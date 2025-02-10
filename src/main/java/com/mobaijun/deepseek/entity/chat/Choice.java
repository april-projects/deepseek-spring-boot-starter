package com.mobaijun.deepseek.entity.chat;

import com.mobaijun.deepseek.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [选择对象]
 * Author: [mobaijun]
 * Date: [2025/2/6 13:47]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Choice {

    /**
     * 选择的索引，用于标识该选择的编号。
     */
    private Integer index;

    /**
     * Delta信息，通常用于表示模型生成的增量信息。
     */
    private ChatMessage delta;

    /**
     * 消息内容，表示完整的聊天消息。
     */
    private ChatMessage message;

    /**
     * 模型生成过程中的logprobs信息，通常用于调试和分析模型生成概率分布。
     */
    private Object logprobs;

    /**
     * 模型停止生成 token 的原因。
     * <p>
     * [stop, length, content_filter, tool_calls, insufficient_system_resource]
     * <p>
     * stop：模型自然停止生成，或遇到 stop 序列中列出的字符串。
     * length：输出长度达到了模型上下文长度限制，或达到了 max_tokens 的限制。
     * content_filter：输出内容因触发过滤策略而被过滤。
     * tool_calls：函数调用。
     * insufficient_system_resource：系统推理资源不足，生成被打断。
     */
    private String finishReason;
}