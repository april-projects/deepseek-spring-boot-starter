package com.mobaijun.deepseek.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.ui.Model;

/**
 * Description: [表示模型列表，包含模型对象及其元数据。]
 * Author: [mobaijun]
 * Date: [2025/1/16 11:51]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Getter
@Setter
@ToString
public class ModelsInfo {

    /**
     * 对象类型，通常用于标识返回的数据类型或元数据。
     */
    private String object;

    /**
     * 模型列表，包含多个 `Model` 对象，表示可用的模型数据。
     */
    private List<Model> data;
}