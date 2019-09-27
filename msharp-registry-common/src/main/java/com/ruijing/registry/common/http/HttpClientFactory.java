package com.ruijing.registry.common.http;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * HttpClient Factory
 *
 * @author mwup
 * @version 1.0
 * @created 2018/1/7 15:15
 **/
public class HttpClientFactory {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 1020;

    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 20;

    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = 5 * 1000;

    public static HttpClientFactory FACTORY = new HttpClientFactory();

    public CloseableHttpClient getDefaultHttpClient() {
        return getHttpClient(getDefaultRequestConfig(), getDefaultPoolingHttpClientConnectionManager(), null, getSSLConnectionSocketFactory());
    }

    public CloseableHttpClient getHttpClient(RequestConfig config) {
        //set https post
        return getHttpClient(config, getDefaultPoolingHttpClientConnectionManager(), null, getSSLConnectionSocketFactory());
    }

    public CloseableHttpClient getHttpClient(HttpRequestRetryHandler requestRetryHandler) {
        return getHttpClient(getDefaultRequestConfig(), getDefaultPoolingHttpClientConnectionManager(), requestRetryHandler, getSSLConnectionSocketFactory());
    }

    public CloseableHttpClient getHttpClient(RequestConfig config, HttpRequestRetryHandler requestRetryHandler) {
        return getHttpClient(config, getDefaultPoolingHttpClientConnectionManager(), requestRetryHandler, getSSLConnectionSocketFactory());
    }

    public CloseableHttpClient getHttpClient(RequestConfig config, PoolingHttpClientConnectionManager manager, HttpRequestRetryHandler handler) {
        return getHttpClient(config, manager, handler, getSSLConnectionSocketFactory());
    }

    private CloseableHttpClient getHttpClient(RequestConfig config, HttpClientConnectionManager manager, HttpRequestRetryHandler requestRetryHandler, LayeredConnectionSocketFactory factory) {
        // Build the client.
        HttpClientBuilder builder = HttpClients.custom()
                .setUserAgent("MOBILE_SERVER")
                //http异常重试操作交给客户端开发者
                .setDefaultRequestConfig(config)
                .setSSLSocketFactory(factory)
                .setConnectionManager(manager);
        if (requestRetryHandler != null) {
            builder.setRetryHandler(requestRetryHandler);
        }
        return builder.build();
    }

    private SSLConnectionSocketFactory getSSLConnectionSocketFactory() {
        SSLConnectionSocketFactory sslsf;
        try {
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return sslsf;
    }

    private RequestConfig getDefaultRequestConfig() {
        final RequestConfig defaultConfig = RequestConfig.custom()
                // httpLog.connection.timeout
                .setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                // httpLog.connection-manager.timeout
                .setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                //http.connection.timeout/http.socket.timeout default config as 2000ms(2s)
                .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .build(); // httpLog.socket.timeout
        return defaultConfig;
    }

    private PoolingHttpClientConnectionManager getDefaultPoolingHttpClientConnectionManager() {
        final PoolingHttpClientConnectionManager manger = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 1020
        manger.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        // Increase default max connection per route to 20
        manger.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
        return manger;
    }

    public CloseableHttpClient newInstance() {
        return getDefaultHttpClient();
    }
}
