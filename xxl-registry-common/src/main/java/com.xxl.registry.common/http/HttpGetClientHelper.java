package com.xxl.registry.common.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;

import java.net.URI;
import java.util.Map;

/**
 * 具有http连接池管理的线程安全的http get client
 * <p>
 * http get client helper
 *
 * @author mwup
 * @version 1.0
 * @created 2018/1/6 03:19
 **/
public final class HttpGetClientHelper extends AbstractHttpClientHelper {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    /**
     * default singleton HttpClient
     */
    public static HttpGetClientHelper INSTANCE = new HttpGetClientHelper();

    private HttpGetClientHelper() {
    }

    public byte[] get(final String uri, final RequestConfig config) throws Exception {
        return get(uri, (Header[]) null, config);
    }

    public byte[] get(String uri, final Header[] headers) throws Exception {
        return get(uri, headers, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] get(final String uri, final Header[] headers, final int timeout) throws Exception {
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] get(final String uri, final Map<String, String> headerMap) throws Exception {
        return get(uri, headerMap, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] get(final String uri, final Map<String, String> headerMap, final int timeout) throws Exception {
        return get(URI.create(uri), headerMap, timeout);
    }

    public byte[] get(final String uri, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        return get(URI.create(uri), headerMap, config);
    }

    public byte[] get(final String uri, final Header[] headers, final RequestConfig config) throws Exception {
        if (StringUtils.isBlank(uri)) {
            return new byte[0];
        }
        return get(URI.create(uri), headers, config);
    }

    public byte[] get(final URI uri) throws Exception {
        return get(uri, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] get(final URI uri, final int timeout) throws Exception {
        return get(uri, (Header[]) null, timeout);
    }

    public byte[] get(final URI uri, final RequestConfig config) throws Exception {
        return get(uri, (Header[]) null, config);
    }

    public byte[] get(URI uri, final Header[] headers) throws Exception {
        return get(uri, headers, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] get(final URI uri, final Header[] headers, final int timeout) throws Exception {
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] get(final URI uri, final Map<String, String> headerMap) throws Exception {
        return get(uri, headerMap, DEFAULT_SOCKET_TIMEOUT);
    }

    public byte[] get(final URI uri, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        if (null == headerMap) {
            return get(uri, (Header[]) null, config);
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (final Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return get(uri, headers, config);
    }

    public byte[] get(final URI uri, final Map<String, String> headerMap, final int timeout) throws Exception {
        if (null == headerMap) {
            return get(uri, (Header[]) null, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public byte[] get(final URI uri, final Header[] headers, final RequestConfig config) throws Exception {
        //create http get
        final HttpGet httpGet = new HttpGet(uri);
        // set headers
        setHeaders(httpGet, headers);
        //set config
        setConfig(httpGet, config);
        //execute
        return execute(httpGet);
    }
}
