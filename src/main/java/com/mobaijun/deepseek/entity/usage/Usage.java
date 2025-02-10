package com.mobaijun.deepseek.entity.usage;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: [表示使用情况，包含关于提示和完成阶段的Token计数。]
 * Author: [mobaijun]
 * Date: [2025/2/6 13:47]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
public class Usage implements Serializable {

    /**
     * 提示阶段的Token数量，表示输入给模型的所有Token数量。
     */
    private long promptTokens = 0L;

    /**
     * 完成阶段的Token数量，表示模型生成的Token数量。
     */
    private long completionTokens = 0L;

    /**
     * 总Token数量，包括提示和完成阶段的Token总和。
     */
    private long totalTokens = 0L;
}