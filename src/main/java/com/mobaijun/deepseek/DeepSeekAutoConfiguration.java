package com.mobaijun.deepseek;

import com.mobaijun.deepseek.properties.DeepSeekProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Description: [DeepSeek自动配置类，负责配置和初始化与DeepSeek相关的服务及组件。]
 * Author: [mobaijun]
 * Date: [2025/2/6 14:55]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Configuration
@EnableConfigurationProperties(DeepSeekProperties.class)
public class DeepSeekAutoConfiguration {
}
