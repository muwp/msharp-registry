package com.xxl.registry.common.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import java.net.URI;
import java.util.Map;

/**
 * 具有http连接池管理的线程安全的http post client
 * <p>
 * http post client helper
 *
 * @author mwup
 * @version 1.0
 * @created 2018/1/6 03:19
 **/
public class HttpPostClientHelper extends AbstractHttpClientHelper {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    public HttpPostClientHelper() {
    }

    public HttpPostClientHelper(RequestConfig config) {
        super(config);
    }

    public HttpPostClientHelper(RequestConfig config, PoolingHttpClientConnectionManager manager) {
        super(config, manager);
    }

    public byte[] post(final String uri) throws Exception {
        return post(URI.create(uri), null, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] post(final String uri, final int timeout) throws Exception {
        return post(URI.create(uri), null, (Header[]) null, timeout);
    }

    public byte[] post(final String uri, final HttpEntity entity) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] post(final String uri, final HttpEntity entity, final int timeout) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, timeout);
    }

    public byte[] post(final String uri, final HttpEntity entity, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, config);
    }

    public byte[] post(String uri, final HttpEntity entity, final Map<String, String> headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] post(final String uri, final HttpEntity entity, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, headerMap, config);

    }

    public byte[] post(final String uri, final HttpEntity entity, final Header[] headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] post(final String uri, final HttpEntity entity, final Header[] headers, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, headers, config);
    }

    public byte[] post(final URI uri) throws Exception {
        return post(uri, null, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] post(final URI uri, final int timeout) throws Exception {
        return post(uri, null, (Header[]) null, timeout);
    }

    public byte[] post(final URI uri, final HttpEntity entity) throws Exception {
        return post(uri, entity, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] post(final URI uri, final HttpEntity entity, final int timeout) throws Exception {
        return post(uri, entity, (Header[]) null, timeout);
    }

    public byte[] post(final URI uri, final HttpEntity entity, final RequestConfig config) throws Exception {
        return post(uri, entity, (Header[]) null, config);
    }

    public byte[] post(URI uri, final HttpEntity entity, final Map<String, String> headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] post(final URI uri, final HttpEntity entity, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        if (null == headerMap) {
            return post(uri, null, config);
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return post(uri, entity, headers, config);
    }

    public byte[] post(final URI uri, final HttpEntity entity, final Header[] headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] post(final URI uri, final HttpEntity entity, final Header[] headers, final RequestConfig config) throws Exception {
        // get httpPost
        final HttpPost httpPost = new HttpPost(uri);
        //set config
        setConfig(httpPost, config);
        // set headers
        setHeaders(httpPost, headers);
        // set entity
        httpPost.setEntity(entity);
        //execute
        return execute(httpPost);
    }

    public static Builder custom() {
        return new Builder();
    }

    public static class Builder {

        private RequestConfig config;

        private PoolingHttpClientConnectionManager manager;

        public Builder setConfig(RequestConfig config) {
            this.config = config;
            return this;
        }

        public Builder setManager(PoolingHttpClientConnectionManager manager) {
            this.manager = manager;
            return this;
        }

        public HttpPostClientHelper build() {
            return new HttpPostClientHelper(config, manager);
        }
    }
}
