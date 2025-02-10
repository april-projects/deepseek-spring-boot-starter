package com.mobaijun.deepseek.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: [表示对话消息类型的枚举，定义了系统、用户、助手和工具的角色。]
 * Author: [mobaijun]
 * Date: [2025/1/16 12:00]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@AllArgsConstructor
public enum ChatMessageType {

    /**
     * 系统消息类型，通常用于标识系统生成的消息。
     */
    SYSTEM("system"),

    /**
     * 用户消息类型，通常用于标识由用户发送的消息。
     */
    USER("user"),

    /**
     * 助手消息类型，通常用于标识由助手生成的消息。
     */
    ASSISTANT("assistant"),

    /**
     * 工具消息类型，通常用于标识与工具调用相关的消息。
     */
    TOOL("tool");

    /**
     * 消息角色，表示该消息的发送者类型。
     */
    private final String role;
}