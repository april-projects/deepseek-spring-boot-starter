package com.mobaijun.deepseek.entity;

import com.mobaijun.deepseek.entity.chat.Choice;
import com.mobaijun.deepseek.entity.usage.Usage;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Description: []
 * Author: [mobaijun]
 * Date: [2025/2/6 11:38]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
@NoArgsConstructor()
@AllArgsConstructor()
public class DeepSeekChatCompletionResponse {

    /**
     * 该对话的唯一标识符。
     */
    private String id;

    /**
     * 对象的类型, 其值为 chat.completion 或 chat.completion.chunk
     */
    private String object;

    /**
     * 创建聊天完成时的 Unix 时间戳（以秒为单位）。
     */
    private Long created;

    /**
     * 生成该 completion 的模型名。
     */
    private String model;

    /**
     * 模型生成的 completion 的选择列表。
     */
    private List<Choice> choices;

    /**
     * 该对话补全请求的用量信息。
     */
    private Usage usage;

    /**
     * 该指纹代表模型运行时使用的后端配置。
     */
    private String systemFingerprint;
}
