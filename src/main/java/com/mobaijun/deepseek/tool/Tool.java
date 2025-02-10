package com.mobaijun.deepseek.tool;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [表示工具对象，包含工具类型和与工具相关的函数信息。]
 * Author: [mobaijun]
 * Date: [2025/2/6 14:20]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Tool {

    /**
     * 工具类型，当前值为“function”，表示该工具为函数类型。
     */
    private String type;

    /**
     * 函数对象，包含函数的名称、描述和参数等信息。
     */
    private Function function;

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {

        /**
         * 函数名称，标识函数的唯一名称。
         */
        private String name;

        /**
         * 函数描述，简要说明该函数的功能或用途。
         */
        private String description;

        /**
         * 函数的参数，包含参数名称、类型及属性等。
         */
        private Parameter parameters;

        @Getter
        @Setter
        @ToString
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Parameter {

            /**
             * 参数类型，默认为 "object"，表示该参数为对象类型。
             */
            private String type = "object";

            /**
             * 参数属性，key为参数名称，value为参数的具体属性（例如类型、描述等）。
             */
            private Map<String, Property> properties;

            /**
             * 必须的参数列表，包含函数调用时必须提供的参数名称。
             */
            private List<String> required;
        }

        @Getter
        @Setter
        @ToString
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Property {

            /**
             * 属性类型，标识该属性的数据类型（如string, number等）。
             */
            private String type;

            /**
             * 属性描述，简要说明该属性的用途或功能。
             */
            private String description;

            /**
             * 枚举项，表示该属性可能的取值范围，适用于枚举类型的属性。
             */
            private List<String> enumValues;
        }
    }
}
