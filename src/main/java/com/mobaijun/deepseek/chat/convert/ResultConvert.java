package com.mobaijun.deepseek.chat.convert;

import com.mobaijun.deepseek.entity.ChatCompletionResponse;
import com.mobaijun.deepseek.listener.SseListener;
import okhttp3.sse.EventSourceListener;

/**
 * Description: [泛型接口，用于定义不同类型的结果转换方法。]
 * Author: [mobaijun]
 * Date: [2025/2/6 11:35]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 *
 * @param <T> 泛型类型，表示需要转换的原始数据类型。
 */
public interface ResultConvert<T> {

    /**
     * 将 {@link SseListener} 转换为 {@link EventSourceListener}。
     *
     * @param eventSourceListener 需要转换的 {@link SseListener} 实例。
     * @return 转换后的 {@link EventSourceListener} 实例。
     */
    EventSourceListener convertEventSource(SseListener eventSourceListener);

    /**
     * 将泛型类型 {@link T} 转换为 {@link ChatCompletionResponse}。
     *
     * @param t 需要转换的原始数据，类型为 {@link T}。
     * @return 转换后的 {@link ChatCompletionResponse} 实例。
     */
    ChatCompletionResponse convertChatCompletionResponse(T t);
}
