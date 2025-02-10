package com.mobaijun.deepseek.tool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [表示工具调用对象，包含工具调用的ID、类型和相关的函数信息。]
 * Author: [mobaijun]
 * Date: [2025/2/6 14:30]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ToolCall {

    /**
     * 工具调用ID，用于唯一标识一个工具调用。
     */
    private String id;

    /**
     * 工具调用类型，表示该调用的类型，例如“function”。
     */
    private String type;

    /**
     * 函数对象，包含函数的名称和参数等信息。
     */
    private Function function;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {

        /**
         * 函数名称，标识该调用的函数。
         */
        private String name;

        /**
         * 函数的参数，通常以JSON字符串表示，包含函数所需的参数。
         */
        private String arguments;
    }
}
