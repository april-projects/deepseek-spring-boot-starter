package com.mobaijun.deepseek.util;

/**
 * Description: [用于URL拼接的工具类，提供了拼接多个URL部分的方法。]
 * Author: [mobaijun]
 * Date: [2025/2/6 13:45]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
public class ValidateUtil {

    /**
     * 拼接多个URL参数并返回完整的URL字符串。
     *
     * @param params URL参数部分，可以是多个字符串。
     * @return 拼接后的完整URL字符串。
     * @throws IllegalArgumentException 如果传入的URL参数为空，则抛出异常。
     */
    public static String concatUrl(String... params) {
        // 如果没有参数，则抛出异常
        if (params.length == 0) {
            throw new IllegalArgumentException("url params is empty");
        }

        // 使用StringBuilder拼接URL
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            // 去除每个参数的前导“/”
            if (params[i].startsWith("/")) {
                params[i] = params[i].substring(1);
            }
            // 如果参数是“?”或“&”，检查是否需要去掉斜杠
            if (params[i].startsWith("?") || params[i].startsWith("&")) {
                // 如果sb的末尾是“/”，则删除末尾
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
            // 将当前参数拼接到结果中
            sb.append(params[i]);
            // 除非是最后一个参数，其他参数后都加一个“/”
            if (!params[i].endsWith("/")) {
                sb.append('/');
            }
        }

        // 去掉最后一个斜杠（如果有的话）
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
