package com.mobaijun.deepseek.service;

import com.mobaijun.deepseek.entity.ChatCompletion;
import com.mobaijun.deepseek.entity.ChatCompletionResponse;
import com.mobaijun.deepseek.entity.ModelsInfo;
import com.mobaijun.deepseek.listener.SseListener;

/**
 * Description: [deepseek 业务接口]
 * Author: [mobaijun]
 * Date: [2025/1/16 11:12]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
public interface DeepSeekChatService {

    /**
     * 执行聊天补全请求，并返回结果。
     *
     * @param baseUrl        服务的基础 URL。
     * @param apiKey         用于身份验证的 API Key。
     * @param chatCompletion 包含聊天补全请求数据的对象。
     * @return ChatCompletionResponse 聊天补全的响应对象。
     */
    ChatCompletionResponse chatCompletion(String baseUrl, String apiKey, ChatCompletion chatCompletion);

    /**
     * 执行聊天补全请求，使用默认的 Base URL 和 API Key。
     *
     * @param chatCompletion 包含聊天补全请求数据的对象。
     * @return ChatCompletionResponse 聊天补全的响应对象。
     */
    ChatCompletionResponse chatCompletion(ChatCompletion chatCompletion);

    /**
     * 执行聊天补全请求，并通过流（SSE）接收实时响应。
     *
     * @param baseUrl             服务的基础 URL。
     * @param apiKey              用于身份验证的 API Key。
     * @param chatCompletion      包含聊天补全请求数据的对象。
     * @param eventSourceListener 监听服务器发送事件（SSE）的回调接口。
     */
    void chatCompletionStream(String baseUrl, String apiKey, ChatCompletion chatCompletion, SseListener eventSourceListener);

    /**
     * 执行聊天补全请求，通过流（SSE）接收实时响应，使用默认的 Base URL 和 API Key。
     *
     * @param chatCompletion      包含聊天补全请求数据的对象。
     * @param eventSourceListener 监听服务器发送事件（SSE）的回调接口。
     */
    void chatCompletionStream(ChatCompletion chatCompletion, SseListener eventSourceListener);

    /**
     * 获取模型列表
     *
     * @param baseUrl 基础url
     * @param apiKey  api key
     * @return 模型列表
     */
    ModelsInfo getModelsInfo(String baseUrl, String apiKey);
}
