package com.mobaijun.deepseek.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: [自定义注解，用于标记类级别的函数请求元数据，提供函数描述信息。]
 * Author: [mobaijun]
 * Date: [2025/2/6 11:35]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionRequest {

    /**
     * 参数描述信息，通常用于说明该函数的功能或用途。
     *
     * @return 函数的描述信息，默认为空字符串
     */
    String description() default "";
}