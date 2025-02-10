package com.mobaijun.deepseek.entity.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [表示流选项配置，包含是否包含使用情况的选项。]
 * Author: [mobaijun]
 * Date: [2025/2/6 11:47]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor()
@AllArgsConstructor()
public class StreamOptions {

    /**
     * 是否包含使用情况。默认为 true，表示在流式响应中包含使用情况数据。
     */
    private Boolean includeUsage = true;
}