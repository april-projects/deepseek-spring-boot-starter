package com.mobaijun.deepseek.service.impl;

import cn.hutool.json.JSONUtil;
import com.mobaijun.deepseek.chat.convert.ParameterConvert;
import com.mobaijun.deepseek.chat.convert.ResultConvert;
import com.mobaijun.deepseek.constant.Constants;
import com.mobaijun.deepseek.entity.ChatCompletion;
import com.mobaijun.deepseek.entity.ChatCompletionResponse;
import com.mobaijun.deepseek.entity.ChatMessage;
import com.mobaijun.deepseek.entity.DeepSeekChatCompletion;
import com.mobaijun.deepseek.entity.DeepSeekChatCompletionResponse;
import com.mobaijun.deepseek.entity.ModelsInfo;
import com.mobaijun.deepseek.entity.chat.Choice;
import com.mobaijun.deepseek.entity.usage.Usage;
import com.mobaijun.deepseek.enums.ApiType;
import com.mobaijun.deepseek.listener.SseListener;
import com.mobaijun.deepseek.properties.DeepSeekProperties;
import com.mobaijun.deepseek.service.DeepSeekChatService;
import com.mobaijun.deepseek.tool.Tool;
import com.mobaijun.deepseek.tool.ToolCall;
import com.mobaijun.deepseek.util.ToolUtil;
import com.mobaijun.deepseek.util.ValidateUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

/**
 * Description: [deepseek-chat-service-impl]
 * Author: [mobaijun]
 * Date: [2025/1/16 11:13]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Service
@RequiredArgsConstructor
public class DeepSeekChatServiceImpl implements DeepSeekChatService, ParameterConvert<DeepSeekChatCompletion>, ResultConvert<DeepSeekChatCompletionResponse> {

    private final DeepSeekProperties deepSeekProperties;

    private final OkHttpClient client = new OkHttpClient();

    private EventSource.Factory factory;

    /**
     * 执行聊天补全请求，并返回结果。
     *
     * @param baseUrl        服务的基础 URL。
     * @param apiKey         用于身份验证的 API Key。
     * @param chatCompletion 包含聊天补全请求数据的对象。
     * @return ChatCompletionResponse 聊天补全的响应对象。
     */
    @Override
    public ChatCompletionResponse chatCompletion(String baseUrl, String apiKey, ChatCompletion chatCompletion) {
        if (baseUrl == null || baseUrl.isEmpty()) baseUrl = deepSeekProperties.getBaseUrl();
        if (apiKey == null || apiKey.isEmpty()) apiKey = deepSeekProperties.getApiKey();
        chatCompletion.setStream(false);
        chatCompletion.setStreamOptions(null);

        // 转换 请求参数
        DeepSeekChatCompletion deepSeekChatCompletion = this.convertChatCompletionObject(chatCompletion);

        // 如含有function，则添加tool
        if (deepSeekChatCompletion.getFunctions() != null && !deepSeekChatCompletion.getFunctions().isEmpty()) {
            List<Tool> tools = ToolUtil.getAllFunctionTools(deepSeekChatCompletion.getFunctions());
            deepSeekChatCompletion.setTools(tools);
        }

        // 总token消耗
        Usage allUsage = new Usage();
        String finishReason = "first";
        while ("first".equals(finishReason) || "tool_calls".equals(finishReason)) {
            finishReason = null;
            // 构造请求
            String requestString = JSONUtil.toJsonStr(deepSeekChatCompletion);
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + apiKey)
                    .url(ValidateUtil.concatUrl(baseUrl, ApiType.CHAT_COMPLETIONS.getType()))
                    .post(RequestBody.create(MediaType.parse(Constants.JSON_CONTENT_TYPE), requestString))
                    .build();

            Response execute;
            try {
                execute = client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (execute.isSuccessful() && execute.body() != null) {
                DeepSeekChatCompletionResponse deepSeekChatCompletionResponse;
                try {
                    deepSeekChatCompletionResponse = JSONUtil.toBean(execute.body().string(), DeepSeekChatCompletionResponse.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Choice choice = deepSeekChatCompletionResponse.getChoices().get(0);
                finishReason = choice.getFinishReason();

                Usage usage = deepSeekChatCompletionResponse.getUsage();
                allUsage.setCompletionTokens(allUsage.getCompletionTokens() + usage.getCompletionTokens());
                allUsage.setTotalTokens(allUsage.getTotalTokens() + usage.getTotalTokens());
                allUsage.setPromptTokens(allUsage.getPromptTokens() + usage.getPromptTokens());

                // 判断是否为函数调用返回
                if ("tool_calls".equals(finishReason)) {
                    ChatMessage message = choice.getMessage();
                    List<ToolCall> toolCalls = message.getToolCalls();

                    List<ChatMessage> messages = new ArrayList<>(deepSeekChatCompletion.getMessages());
                    messages.add(message);

                    // 添加 tool 消息
                    for (ToolCall toolCall : toolCalls) {
                        String functionName = toolCall.getFunction().getName();
                        String arguments = toolCall.getFunction().getArguments();
                        String functionResponse = ToolUtil.invoke(functionName, arguments);

                        messages.add(ChatMessage.withTool(functionResponse, toolCall.getId()));
                    }
                    deepSeekChatCompletion.setMessages(messages);
                } else {
                    // 设置包含tool的总token数
                    deepSeekChatCompletionResponse.setUsage(allUsage);
                    // 恢复原始请求数据
                    chatCompletion.setMessages(deepSeekChatCompletion.getMessages());
                    chatCompletion.setTools(deepSeekChatCompletion.getTools());
                    return this.convertChatCompletionResponse(deepSeekChatCompletionResponse);
                }
            }
        }
        return null;
    }

    /**
     * 执行聊天补全请求，使用默认的 Base URL 和 API Key。
     *
     * @param chatCompletion 包含聊天补全请求数据的对象。
     * @return ChatCompletionResponse 聊天补全的响应对象。
     */
    @Override
    public ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion) {
        return this.chatCompletion(null, null, chatCompletion);
    }

    /**
     * 执行聊天补全请求，并通过流（SSE）接收实时响应。
     *
     * @param baseUrl             服务的基础 URL。
     * @param apiKey              用于身份验证的 API Key。
     * @param chatCompletion      包含聊天补全请求数据的对象。
     * @param eventSourceListener 监听服务器发送事件（SSE）的回调接口。
     */
    @Override
    public void chatCompletionStream(String baseUrl, String apiKey, ChatCompletion chatCompletion, SseListener eventSourceListener) {
        if (baseUrl == null || baseUrl.isEmpty()) baseUrl = deepSeekProperties.getBaseUrl();
        if (apiKey == null || apiKey.isEmpty()) apiKey = deepSeekProperties.getApiKey();
        chatCompletion.setStream(true);

        // 转换 请求参数
        DeepSeekChatCompletion deepSeekChatCompletion = this.convertChatCompletionObject(chatCompletion);

        // 如含有function，则添加tool
        if (deepSeekChatCompletion.getFunctions() != null && !deepSeekChatCompletion.getFunctions().isEmpty()) {
            List<Tool> tools = ToolUtil.getAllFunctionTools(deepSeekChatCompletion.getFunctions());
            deepSeekChatCompletion.setTools(tools);
        }

        String finishReason = "first";

        while ("first".equals(finishReason) || "tool_calls".equals(finishReason)) {
            String jsonString = JSONUtil.toJsonStr(deepSeekChatCompletion);
            Request request = new Request.Builder()
                    .header("Authorization", "Bearer " + apiKey)
                    .url(ValidateUtil.concatUrl(baseUrl, ApiType.CHAT_COMPLETIONS.getType()))
                    .post(RequestBody.create(MediaType.parse(Constants.APPLICATION_JSON), jsonString))
                    .build();


            factory.newEventSource(request, convertEventSource(eventSourceListener));
            try {
                eventSourceListener.getCountDownLatch().await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            finishReason = eventSourceListener.getFinishReason();
            List<ToolCall> toolCalls = eventSourceListener.getToolCalls();

            // 需要调用函数
            if ("tool_calls".equals(finishReason) && !toolCalls.isEmpty()) {
                // 创建tool响应消息
                ChatMessage responseMessage = ChatMessage.withAssistant(eventSourceListener.getToolCalls());
                List<ChatMessage> messages = new ArrayList<>(deepSeekChatCompletion.getMessages());
                messages.add(responseMessage);

                // 封装tool结果消息
                for (ToolCall toolCall : toolCalls) {
                    String functionName = toolCall.getFunction().getName();
                    String arguments = toolCall.getFunction().getArguments();
                    String functionResponse = ToolUtil.invoke(functionName, arguments);

                    messages.add(ChatMessage.withTool(functionResponse, toolCall.getId()));
                }
                eventSourceListener.setToolCalls(new ArrayList<>());
                eventSourceListener.setToolCall(null);
                deepSeekChatCompletion.setMessages(messages);
            }

        }

        // 补全原始请求
        chatCompletion.setMessages(deepSeekChatCompletion.getMessages());
        chatCompletion.setTools(deepSeekChatCompletion.getTools());
    }

    /**
     * 执行聊天补全请求，通过流（SSE）接收实时响应，使用默认的 Base URL 和 API Key。
     *
     * @param chatCompletion      包含聊天补全请求数据的对象。
     * @param eventSourceListener 监听服务器发送事件（SSE）的回调接口。
     */
    @Override
    public void chatCompletionStream(ChatCompletion chatCompletion, SseListener eventSourceListener) {
        this.chatCompletionStream(null, null, chatCompletion, eventSourceListener);
    }

    /**
     * 获取模型列表
     *
     * @param baseUrl 基础url
     * @param apiKey  api key
     * @return 模型列表
     */
    @Override
    public ModelsInfo getModelsInfo(String baseUrl, String apiKey) {
        String url = baseUrl + ApiType.MODELS;
        Request request = createRequest(url, apiKey);

        try (Response response = client.newCall(request).execute()) {
            // 校验响应状态
            validateResponse(response);
            // 解析成功的响应
            return parseResponse(response);
        } catch (IOException e) {
            throw new RuntimeException("Request failed due to an IOException: " + e.getMessage(), e);
        }
    }

    /**
     * 创建 HTTP 请求。
     *
     * @param url    请求的 URL。
     * @param apiKey 用于身份验证的 API Key。
     * @return 构造完成的 HTTP 请求对象。
     */
    private Request createRequest(String url, String apiKey) {
        return new Request.Builder()
                .url(url)
                // 使用 GET 方法，避免显式空 RequestBody
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    /**
     * 校验 HTTP 响应状态。
     *
     * @param response HTTP 响应对象。
     * @throws RuntimeException 如果响应不成功（非 2xx 状态码）。
     */
    private void validateResponse(Response response) {
        if (!response.isSuccessful()) {
            String errorBody = extractResponseBody(response);
            throw new RuntimeException("Request failed with status: " + response.code() + " and message: " + errorBody);
        }
    }

    /**
     * 解析成功的 HTTP 响应。
     *
     * @param response HTTP 响应对象。
     * @return 解析后的模型信息对象。
     * @throws IOException 如果在读取响应体时发生 IO 错误。
     */
    private ModelsInfo parseResponse(Response response) throws IOException {
        String responseBody = extractResponseBody(response);
        // 使用 Hutool 工具解析 JSON
        return JSONUtil.toBean(responseBody, ModelsInfo.class);
    }

    /**
     * 提取 HTTP 响应的响应体内容。
     *
     * @param response HTTP 响应对象。
     * @return 响应体内容的字符串形式。如果响应体为空，返回 "No content"。
     * @throws RuntimeException 如果在读取响应体时发生 IO 错误。
     */
    private String extractResponseBody(Response response) {
        try {
            return response.body() != null ? response.body().string() : "No content";
        } catch (IOException e) {
            throw new RuntimeException("Failed to read response body: " + e.getMessage(), e);
        }
    }

    @Override
    public DeepSeekChatCompletion convertChatCompletionObject(ChatCompletion chatCompletion) {
        DeepSeekChatCompletion deepSeekChatCompletion = new DeepSeekChatCompletion();
        deepSeekChatCompletion.setModel(chatCompletion.getModel());
        deepSeekChatCompletion.setMessages(chatCompletion.getMessages());
        deepSeekChatCompletion.setFrequencyPenalty(chatCompletion.getFrequencyPenalty());
        deepSeekChatCompletion.setMaxTokens(chatCompletion.getMaxTokens());
        deepSeekChatCompletion.setPresencePenalty(chatCompletion.getPresencePenalty());
        deepSeekChatCompletion.setResponseFormat(chatCompletion.getResponseFormat());
        deepSeekChatCompletion.setStop(chatCompletion.getStop());
        deepSeekChatCompletion.setStream(chatCompletion.getStream());
        deepSeekChatCompletion.setStreamOptions(chatCompletion.getStreamOptions());
        deepSeekChatCompletion.setTemperature(chatCompletion.getTemperature());
        deepSeekChatCompletion.setTopP(chatCompletion.getTopP());
        deepSeekChatCompletion.setTools(chatCompletion.getTools());
        deepSeekChatCompletion.setFunctions(chatCompletion.getFunctions());
        deepSeekChatCompletion.setToolChoice(chatCompletion.getToolChoice());
        deepSeekChatCompletion.setLogprobs(chatCompletion.getLogprobs());
        deepSeekChatCompletion.setTopLogprobs(chatCompletion.getTopLogprobs());
        return deepSeekChatCompletion;
    }

    @Override
    public EventSourceListener convertEventSource(SseListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                eventSourceListener.onOpen(eventSource, response);
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                if ("[DONE]".equalsIgnoreCase(data)) {
                    eventSourceListener.onEvent(eventSource, id, type, data);
                    return;
                }

                DeepSeekChatCompletionResponse chatCompletionResponse = JSONUtil.toBean(data, DeepSeekChatCompletionResponse.class);
                ChatCompletionResponse response = convertChatCompletionResponse(chatCompletionResponse);

                eventSourceListener.onEvent(eventSource, id, type, JSONUtil.toJsonStr(response));
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }
        };
    }

    @Override
    public ChatCompletionResponse convertChatCompletionResponse(DeepSeekChatCompletionResponse deepSeekChatCompletionResponse) {
        ChatCompletionResponse chatCompletionResponse = new ChatCompletionResponse();
        chatCompletionResponse.setId(deepSeekChatCompletionResponse.getId());
        chatCompletionResponse.setObject(deepSeekChatCompletionResponse.getObject());
        chatCompletionResponse.setCreated(deepSeekChatCompletionResponse.getCreated());
        chatCompletionResponse.setModel(deepSeekChatCompletionResponse.getModel());
        chatCompletionResponse.setSystemFingerprint(deepSeekChatCompletionResponse.getSystemFingerprint());
        chatCompletionResponse.setChoices(deepSeekChatCompletionResponse.getChoices());
        chatCompletionResponse.setUsage(deepSeekChatCompletionResponse.getUsage());
        return chatCompletionResponse;
    }
}
