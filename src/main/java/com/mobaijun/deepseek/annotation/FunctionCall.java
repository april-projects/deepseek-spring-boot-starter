package com.mobaijun.deepseek.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: [用于标注函数调用相关的元数据，包括函数名称和描述信息。]
 * Author: [mobaijun]
 * Date: [2025/2/6 11:35]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionCall {

    /**
     * 函数名称。
     *
     * @return 函数的名称。
     */
    String name();

    /**
     * 函数描述信息。
     *
     * @return 函数的描述信息。
     */
    String description();
}
