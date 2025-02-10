package com.mobaijun.deepseek.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: [deepseek api 类型]
 * Author: [mobaijun]
 * Date: [2025/1/16 11:15]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@AllArgsConstructor
public enum ApiType {

    /**
     * 对话补全
     */
    CHAT_COMPLETIONS("/chat/completions"),

    /**
     * 对话补全 FIM 补全（Beta）
     */
    BETA_COMPLETIONS("/beta/completions"),

    /**
     * 列出模型
     */
    MODELS("/models"),

    /**
     * 查询余额
     */
    USER_BALANCE("/user/balance");

    /**
     * api 类型
     */
    private final String type;
}
