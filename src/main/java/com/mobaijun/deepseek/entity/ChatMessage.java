package com.mobaijun.deepseek.entity;

import com.mobaijun.deepseek.enums.ChatMessageType;
import com.mobaijun.deepseek.tool.ToolCall;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [表示对话消息对象，包含消息内容、角色、工具调用等信息。]
 * Author: [mobaijun]
 * Date: [2025/1/16 11:03]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage {

    /**
     * 消息内容，包含实际的文本信息或指令。
     */
    private String content;

    /**
     * 消息发送者的角色，通常为“user”或“assistant”。
     */
    private String role;

    /**
     * 发送消息的名称，用于标识发信人。
     */
    private String name;

    /**
     * 拒绝理由，通常在消息被拒绝时包含拒绝的说明。
     */
    private String refusal;

    /**
     * 工具调用ID，标识当前消息是否与特定工具调用相关联。
     */
    private String toolCallId;

    /**
     * 工具调用列表，用于包含该消息中的所有工具调用信息。
     */
    private List<ToolCall> toolCalls;

    /**
     * 创建一个代表助手角色的消息对象。
     *
     * @param toolCalls 工具调用列表
     * @return 返回构建好的 ChatMessage 对象
     */
    public static ChatMessage withAssistant(List<ToolCall> toolCalls) {
        return ChatMessage.builder()
                .role(ChatMessageType.ASSISTANT.getRole())
                .toolCalls(toolCalls)
                .build();
    }

    /**
     * 创建一个代表工具角色的消息对象。
     *
     * @param content    消息内容
     * @param toolCallId 工具调用ID
     * @return 返回构建好的 ChatMessage 对象
     */
    public static ChatMessage withTool(String content, String toolCallId) {
        return ChatMessage.builder()
                .role(ChatMessageType.TOOL.getRole())
                .content(content)
                .toolCallId(toolCallId)
                .build();
    }
}
