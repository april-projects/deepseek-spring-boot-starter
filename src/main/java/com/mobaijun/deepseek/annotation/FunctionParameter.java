package com.mobaijun.deepseek.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description: [用于标注函数参数的元数据，包括参数描述信息和是否必填。]
 * Author: [mobaijun]
 * Date: [2025/2/6 11:35]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionParameter {

    /**
     * 参数的描述信息。
     *
     * @return 参数的描述信息。
     */
    String description();

    /**
     * 参数是否为必填项，默认为 true。
     *
     * @return 如果参数必填，则返回 true；否则返回 false。
     */
    boolean required() default true;
}