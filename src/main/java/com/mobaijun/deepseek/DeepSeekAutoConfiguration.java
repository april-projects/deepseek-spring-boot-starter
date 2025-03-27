package com.mobaijun.deepseek;

import com.mobaijun.deepseek.properties.DeepSeekProperties;
import com.mobaijun.deepseek.properties.DeepSeekProperties.OkHttp;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    private final DeepSeekProperties deepSeekProperties;

    /**
     * 构造函数，接收 {@link DeepSeekProperties} 实例。
     *
     * @param deepSeekProperties {@link DeepSeekProperties} 实例，用于获取配置属性。
     */
    public DeepSeekAutoConfiguration(DeepSeekProperties deepSeekProperties) {
        this.deepSeekProperties = deepSeekProperties;
    }

    @NotNull
    private static Builder getBuilder(OkHttp okHttpConfig) {
        Builder builder = new Builder();

        // 设置连接池
        if (okHttpConfig.getConnectionPoolEnabled() != null && okHttpConfig.getConnectionPoolEnabled()) {
            ConnectionPool connectionPool = okHttpConfig.getConnectionPool() != null
                    ? okHttpConfig.getConnectionPool()
                    : new ConnectionPool(okHttpConfig.getMaxIdleConnections(), okHttpConfig.getKeepAliveDuration(), TimeUnit.MINUTES);

            builder.connectionPool(connectionPool);
        }
        return builder;
    }

    /**
     * 创建并配置 {@link OkHttpClient} 实例。
     * <p>
     * 根据 {@link DeepSeekProperties} 中的配置，设置连接池、超时、重试策略等参数。
     * </p>
     *
     * @return 配置好的 {@link OkHttpClient} 实例。
     */
    @Bean
    public OkHttpClient okHttpClient() {
        DeepSeekProperties.OkHttp okHttpConfig = deepSeekProperties.getOkHttp();

        Builder builder = getBuilder(okHttpConfig);

        // 设置连接、读取、写入超时
        builder.connectTimeout(okHttpConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(okHttpConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(okHttpConfig.getWriteTimeout(), TimeUnit.SECONDS);

        // 设置重试策略
        if (okHttpConfig.getRetryOnConnectionFailure() != null) {
            builder.retryOnConnectionFailure(okHttpConfig.getRetryOnConnectionFailure());
        }

        // 设置自动跟随重定向
        if (okHttpConfig.getFollowRedirects() != null) {
            builder.followRedirects(okHttpConfig.getFollowRedirects());
        }

        // 设置自动跟随 SSL 重定向
        if (okHttpConfig.getFollowSslRedirects() != null) {
            builder.followSslRedirects(okHttpConfig.getFollowSslRedirects());
        }

        // 配置代理
        if (okHttpConfig.getProxyEnabled() != null && okHttpConfig.getProxyEnabled()) {
            String proxyHost = okHttpConfig.getProxyHost();
            int proxyPort = okHttpConfig.getProxyPort();

            // 如果启用代理，构造 Proxy 对象
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

            builder.proxy(proxy);

            if (okHttpConfig.getProxyAuthenticator() != null) {
                builder.proxyAuthenticator(okHttpConfig.getProxyAuthenticator());
            }
        }

        // 配置自定义 DNS
        if (okHttpConfig.getCustomDns() != null) {
            builder.dns(okHttpConfig.getCustomDns());
        }

        // 配置自定义拦截器
        if (okHttpConfig.getInterceptors() != null && !okHttpConfig.getInterceptors().isEmpty()) {
            for (Interceptor interceptor : okHttpConfig.getInterceptors()) {
                builder.addInterceptor(interceptor);
            }
        }

        // 配置自定义网络拦截器
        if (okHttpConfig.getNetworkInterceptors() != null && !okHttpConfig.getNetworkInterceptors().isEmpty()) {
            for (Interceptor networkInterceptor : okHttpConfig.getNetworkInterceptors()) {
                builder.addNetworkInterceptor(networkInterceptor);
            }
        }

        // 配置响应缓存
        if (okHttpConfig.getResponseCacheEnabled() != null && okHttpConfig.getResponseCacheEnabled()) {
            File cacheDirectory = okHttpConfig.getCacheDirectory() != null
                    ? new File(okHttpConfig.getCacheDirectory())
                    : new File(System.getProperty("java.io.tmpdir"), "http_cache");
            Cache cache = new Cache(cacheDirectory, okHttpConfig.getCacheSize());
            builder.cache(cache);
        }

        // 配置 TLS 设置
        if (okHttpConfig.getTlsEnabled() != null && okHttpConfig.getTlsEnabled()) {
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, new SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                builder.sslSocketFactory(sslSocketFactory, okHttpConfig.getTrustManager());
                builder.hostnameVerifier(okHttpConfig.getHostnameVerifier());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException("TLS configuration failed", e);
            }
        }

        // 配置自定义 SocketFactory
        if (okHttpConfig.getSocketFactory() != null) {
            builder.socketFactory(okHttpConfig.getSocketFactory());
        }

        // 配置证书钉扎
        if (okHttpConfig.getCertificatePinner() != null) {
            builder.certificatePinner(okHttpConfig.getCertificatePinner());
        }

        // 配置其他自定义项（如缓存，代理等）
        return builder.build();
    }
}
