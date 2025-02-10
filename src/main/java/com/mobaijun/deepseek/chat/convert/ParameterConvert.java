package com.mobaijun.deepseek.chat.convert;

import com.mobaijun.deepseek.entity.ChatCompletion;

/**
 * 泛型接口，用于定义将 {@link com.mobaijun.deepseek.entity.ChatCompletion} 对象转换为指定类型 {@link T} 的方法。
 *
 * @param <T> 泛型类型，表示转换后的目标数据类型。
 */
public interface ParameterConvert<T> {

    /**
     * 将 {@link com.mobaijun.deepseek.entity.ChatCompletion} 对象转换为指定类型 {@link T}。
     *
     * @param chatCompletion 需要转换的 {@link com.mobaijun.deepseek.entity.ChatCompletion} 对象。
     * @return 转换后的目标类型 {@link T} 的实例。
     */
    T convertChatCompletionObject(ChatCompletion chatCompletion);
}
