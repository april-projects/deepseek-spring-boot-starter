package com.mobaijun.deepseek.constant;

/**
 * Description: [定义常量类，包含常用的静态常量，如SSE内容类型、默认用户代理、JSON类型等。]
 * Author: [mobaijun]
 * Date: [2025/2/6 13:47]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
public class Constants {

    /**
     * JSON媒体类型常量，用于表示请求和响应的数据格式为JSON。
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * 带有字符集信息的JSON类型常量，通常用于标识UTF-8编码的JSON格式。
     */
    public static final String JSON_CONTENT_TYPE = APPLICATION_JSON + "; charset=utf-8";
}