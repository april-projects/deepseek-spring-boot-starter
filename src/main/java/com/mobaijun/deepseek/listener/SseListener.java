package com.mobaijun.deepseek.listener;

import cn.hutool.json.JSONUtil;
import com.mobaijun.deepseek.entity.ChatCompletionResponse;
import com.mobaijun.deepseek.entity.ChatMessage;
import com.mobaijun.deepseek.entity.chat.Choice;
import com.mobaijun.deepseek.entity.usage.Usage;
import com.mobaijun.deepseek.enums.ChatMessageType;
import com.mobaijun.deepseek.tool.ToolCall;
import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Description: []
 * Author: [mobaijun]
 * Date: [2025/1/16 11:34]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Slf4j
public abstract class SseListener extends EventSourceListener {

    /**
     * 最终的消息输出
     */
    @Getter
    private final StringBuilder output = new StringBuilder();

    /**
     * 花费token
     */
    @Getter
    private final Usage usage = new Usage();

    /**
     * 最终的函数调用参数
     */
    private final StringBuilder argument = new StringBuilder();

    /**
     * 流式输出，当前消息的内容(回答消息、函数参数)
     */
    @Getter
    private String currStr = "";

    /**
     * 流式输出，当前单条SSE消息对象，即ChatCompletionResponse对象
     */
    @Getter
    private String currData = "";

    /**
     * 记录当前所调用函数工具的名称
     */
    @Getter
    private String currToolName = "";

    /**
     * 是否显示每个函数调用输出的参数文本
     */
    @Getter
    @Setter
    private boolean showToolArgs = false;

    @Setter
    @Getter
    private List<ToolCall> toolCalls = new ArrayList<>();

    @Setter
    @Getter
    private ToolCall toolCall;

    @Getter
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Getter
    private String finishReason = null;

    private boolean ollamaToolCall = false;

    protected abstract void send();

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
        countDownLatch.countDown();
    }

    @Override
    public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        // 封装SSE消息对象
        currData = data;
        if ("[DONE]".equalsIgnoreCase(data)) {
            // 整个对话结束，结束前将SSE最后一条“DONE”消息发送出去
            currStr = "";
            this.send();

            return;
        }
        ChatCompletionResponse chatCompletionResponse = JSONUtil.toBean(data, ChatCompletionResponse.class);
        // 统计token，当设置include_usage = true时，最后一条消息会携带usage, 其他消息中usage为null
        Usage currUsage = chatCompletionResponse.getUsage();
        if (currUsage != null) {
            usage.setPromptTokens(usage.getPromptTokens() + currUsage.getPromptTokens());
            usage.setCompletionTokens(usage.getCompletionTokens() + currUsage.getCompletionTokens());
            usage.setTotalTokens(usage.getTotalTokens() + currUsage.getTotalTokens());
        }
        List<Choice> choices = chatCompletionResponse.getChoices();
        if (choices == null || choices.isEmpty()) {
            return;
        }
        ChatMessage responseMessage = choices.get(0).getDelta();
        finishReason = choices.get(0).getFinishReason();

        if ("stop".equals(finishReason) && ollamaToolCall) {
            ollamaToolCall = false;
            finishReason = "tool_calls";
        }
        // tool_calls回答已经结束
        if ("tool_calls".equals(finishReason)) {
            if (toolCall == null && responseMessage.getToolCalls() != null) {
                toolCalls = responseMessage.getToolCalls();
                if (showToolArgs) {
                    this.currStr = responseMessage.getToolCalls().get(0).getFunction().getArguments();
                    this.send();
                }
                return;
            }

            if (toolCall != null) {
                toolCall.getFunction().setArguments(argument.toString());
                toolCalls.add(toolCall);
            }
            argument.setLength(0);
            currToolName = "";
            return;
        }
        // 消息回答完毕
        if ("stop".equals(finishReason)) {

            return;
        }
        if (ChatMessageType.ASSISTANT.getRole().equals(responseMessage.getRole())
                && StringUtils.isBlank(responseMessage.getContent())
                && responseMessage.getToolCalls() == null) {
            // OPENAI 第一条消息
            return;
        }
        if (responseMessage.getToolCalls() == null) {
            // 判断是否为混元的tool最后一条说明性content
            if (toolCall != null && StringUtils.isNotBlank(String.valueOf(argument)) && "assistant".equals(responseMessage.getRole()) && StringUtils.isNotBlank(responseMessage.getContent())) {
                return;
            }
            if ("<tool_call>".equals(responseMessage.getContent())) {
                // ollama的tool_call
                ollamaToolCall = true;
                return;
            }
            if (ollamaToolCall) {
                if ("</tool_call>".equals(responseMessage.getContent())) {
                    // ollama的tool_call
                    ToolCall.Function function = JSONUtil.toBean(argument.toString(), ToolCall.Function.class);
                    toolCall = new ToolCall();
                    toolCall.setFunction(function);
                    currToolName = function.getName();
                    argument.setLength(0);
                    argument.append(function.getArguments());
                    return;
                }
                argument.append(responseMessage.getContent());
                if (showToolArgs) {
                    this.currStr = responseMessage.getContent();
                    this.send();
                }
                return;
            }
            // 普通响应回答
            output.append(responseMessage.getContent());
            currStr = responseMessage.getContent();
            this.send();
            // 函数调用回答
        } else {
            // 第一条ToolCall表示，不含参数信息
            if (responseMessage.getToolCalls().get(0).getId() != null) {
                if (toolCall == null) {
                    // 第一个函数
                    toolCall = responseMessage.getToolCalls().get(0);
                } else {
                    toolCall.getFunction().setArguments(argument.toString());
                    argument.setLength(0);
                    toolCalls.add(toolCall);
                    toolCall = responseMessage.getToolCalls().get(0);
                }
                currToolName = responseMessage.getToolCalls().get(0).getFunction().getName();
            } else {
                argument.append(responseMessage.getToolCalls().get(0).getFunction().getArguments());
                if (showToolArgs) {
                    this.currStr = responseMessage.getToolCalls().get(0).getFunction().getArguments();
                    this.send();
                }
            }
        }
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        countDownLatch.countDown();
        countDownLatch = new CountDownLatch(1);
    }
}
