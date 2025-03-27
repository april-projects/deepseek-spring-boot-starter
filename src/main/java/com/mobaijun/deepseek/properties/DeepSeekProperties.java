package com.mobaijun.deepseek.properties;

import java.util.ArrayList;
import java.util.List;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.Dns;
import okhttp3.Interceptor;
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

    /**
     * OkHttpClient 配置项
     */
    private OkHttp okHttp = new OkHttp();

    /**
     * okhttp 配置
     */
    @Getter
    @Setter
    @ToString
    public static class OkHttp {

        /**
         * 连接超时，默认30秒
         */
        private Integer connectTimeout = 30;

        /**
         * 读取超时，默认30秒
         */
        private Integer readTimeout = 30;
        /**
         * 写入超时，默认30秒
         */
        private Integer writeTimeout = 30;

        /**
         * 最大空闲连接数，默认5
         */
        private Integer maxIdleConnections = 5;

        /**
         * 最大存活时间（分钟），默认5分钟
         */
        private Integer keepAliveDuration = 5;

        /**
         * 是否重试
         */
        private Boolean retryOnConnectionFailure;

        /**
         * 是否自动跟随重定向，默认true
         */
        private Boolean followRedirects = true;

        /**
         * 是否自动跟随SSL重定向，默认true
         */
        private Boolean followSslRedirects = true;

        /**
         * 代理设置地址
         */
        private String proxyHost;

        /**
         * 代理配置端口
         */
        private int proxyPort;

        /**
         * 代理认证
         */
        private Authenticator proxyAuthenticator;

        /**
         * 代理主机名验证
         */
        private HostnameVerifier hostnameVerifier;

        /**
         * 自定义DNS
         */
        private Dns dns;

        /**
         * 自定义连接池
         */
        private ConnectionPool connectionPool;

        /**
         * 自定义缓存
         */
        private Cache cache;

        /**
         * 自定义拦截器
         */
        private List<Interceptor> interceptors = new ArrayList<>();

        /**
         * 自定义网络拦截器
         */
        private List<Interceptor> networkInterceptors = new ArrayList<>();

        /**
         * 是否启用响应缓存，默认true
         */
        private Boolean responseCacheEnabled = true;

        /**
         * 缓存最大大小，默认10MB
         */
        private Long cacheSize = 10L * 1024L * 1024L;

        /**
         * 缓存目录
         */
        private String cacheDirectory;

        /**
         * 是否启用连接池，默认true
         */
        private Boolean connectionPoolEnabled = true;

        /**
         * 是否启用代理，默认false
         */
        private Boolean proxyEnabled = false;

        /**
         * 是否启用TLS，默认true
         */
        private Boolean tlsEnabled = true;

        /**
         * 自定义TLS配置
         */
        private SSLSocketFactory sslSocketFactory;

        /**
         * 自定义信任管理器
         */
        private X509TrustManager trustManager;

        /**
         * 自定义证书钉扎
         */
        private CertificatePinner certificatePinner;

        /**
         * 自定义DNS解析
         */
        private Dns customDns;

        /**
         * 自定义连接工厂
         */
        private SocketFactory socketFactory;
    }
}
