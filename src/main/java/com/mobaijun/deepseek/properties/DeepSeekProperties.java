package com.mobaijun.deepseek.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: [deepseek 配置项]
 * Author: [mobaijun]
 * Date: [2025/1/16 10:43]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = DeepSeekProperties.PREFIX)
public class DeepSeekProperties {

    public static final String PREFIX = "deepseek";

    /**
     * deepseek 配置项
     */
    private String baseUrl = "https://api.deepseek.com";

    /**
     * deepseek API key
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String model = "deepseek-chat";

    /**
     * 响应格式，默认 json_object
     */
    private String responseFormat = "json_object";

    /**
     * 是否开启流式输出
     */
    private Boolean stream = false;
}
