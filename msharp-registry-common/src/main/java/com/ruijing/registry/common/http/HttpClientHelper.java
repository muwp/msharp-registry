package com.ruijing.registry.common.http;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * 具有http连接池管理的线程安全的httpclient
 * <p>
 * http client helper
 * {@link org.apache.http.client.HttpClient}
 * {@link CloseableHttpClient}
 *
 * @author mwup
 * @version 1.0
 * @created 2018/1/6 03:19
 **/
public class HttpClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientHelper.class);

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    private static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    private CloseableHttpClient httpClient;

    /**
     * default singleton HttpClient
     */
    public static HttpClientHelper INSTANCE = new HttpClientHelper();

    public HttpClientHelper() {
        httpClient = HttpClientFactory.FACTORY.getDefaultHttpClient();
    }

    private HttpClientHelper(final RequestConfig config) {
        this.httpClient = HttpClientFactory.FACTORY.getHttpClient(config, null);
    }

    public String get(final String uri) throws Exception {
        return get(uri, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final String uri, final int timeout) throws Exception {
        return get(uri, (Header[]) null, timeout);
    }

    public String get(final String uri, final RequestConfig config) throws Exception {
        return get(uri, (Header[]) null, config);
    }

    public String get(String uri, final Header[] headers) throws Exception {
        return get(uri, headers, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final String uri, final Header[] headers, final int timeout) throws Exception {
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String get(final String uri, final Map<String, String> headerMap) throws Exception {
        return get(uri, headerMap, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final String uri, final Map<String, String> headerMap, final int timeout) throws Exception {
        return get(URI.create(uri), headerMap, timeout);
    }

    public String get(final String uri, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        return get(URI.create(uri), headerMap, config);
    }

    public String get(final String uri, final Header[] headers, final RequestConfig config) throws Exception {
        if (StringUtils.isBlank(uri)) {
            return StringUtils.EMPTY;
        }
        return get(URI.create(uri), headers, config);
    }

    public String get(final URI uri) throws Exception {
        return get(uri, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final URI uri, final int timeout) throws Exception {
        return get(uri, (Header[]) null, timeout);
    }

    public String get(final URI uri, final RequestConfig config) throws Exception {
        return get(uri, (Header[]) null, config);
    }

    public String get(URI uri, final Header[] headers) throws Exception {
        return get(uri, headers, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final URI uri, final Header[] headers, final int timeout) throws Exception {
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String get(final URI uri, final Map<String, String> headerMap) throws Exception {
        return get(uri, headerMap, DEFAULT_SOCKET_TIMEOUT);
    }

    public String get(final URI uri, final Map<String, String> headerMap, final int timeout) throws Exception {
        if (MapUtils.isEmpty(headerMap)) {
            return get(uri, (Header[]) null, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return get(uri, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String get(final URI uri, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        if (MapUtils.isNotEmpty(headerMap)) {
            return get(uri, (Header[]) null, config);
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (final Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return get(uri, headers, config);
    }

    public String get(final URI uri, final Header[] headers, final RequestConfig config) throws Exception {
        //create http get
        final HttpGet httpGet = new HttpGet(uri);
        // set headers
        setHeaders(httpGet, headers);
        //set config
        setConfig(httpGet, config);
        //execute
        return execute(httpGet);
    }

    public String post(final String uri) throws Exception {
        return post(URI.create(uri), null, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public String post(final String uri, final int timeout) throws Exception {
        return post(URI.create(uri), null, (Header[]) null, timeout);
    }

    public String post(final String uri, final HttpEntity entity) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public String post(final String uri, final HttpEntity entity, final int timeout) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, timeout);
    }

    public String post(final String uri, final HttpEntity entity, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, (Header[]) null, config);
    }

    public String post(String uri, final HttpEntity entity, final Map<String, String> headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String post(final String uri, final HttpEntity entity, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, headerMap, config);

    }

    public String post(final String uri, final HttpEntity entity, final Header[] headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String post(final String uri, final HttpEntity entity, final Header[] headers, final RequestConfig config) throws Exception {
        return post(URI.create(uri), entity, headers, config);
    }

    public String post(final URI uri) throws Exception {
        return post(uri, null, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public String post(final URI uri, final int timeout) throws Exception {
        return post(uri, null, (Header[]) null, timeout);
    }

    public String post(final URI uri, final HttpEntity entity) throws Exception {
        return post(uri, entity, (Header[]) null, DEFAULT_SOCKET_TIMEOUT);
    }

    public String post(final URI uri, final HttpEntity entity, final int timeout) throws Exception {
        return post(uri, entity, (Header[]) null, timeout);
    }

    public String post(final URI uri, final HttpEntity entity, final RequestConfig config) throws Exception {
        return post(uri, entity, (Header[]) null, config);
    }

    public String post(URI uri, final HttpEntity entity, final Map<String, String> headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String post(final URI uri, final HttpEntity entity, final Map<String, String> headerMap, final RequestConfig config) throws Exception {
        if (MapUtils.isEmpty(headerMap)) {
            return post(uri, null, config);
        }
        final Header[] headers = new Header[headerMap.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            headers[i++] = new BasicHeader(entry.getKey(), entry.getValue());
        }
        return post(uri, entity, headers, config);
    }

    public String post(final URI uri, final HttpEntity entity, final Header[] headers, final int timeout) throws Exception {
        return post(uri, entity, headers, getRequestConfig(DEFAULT_CONNECTION_TIMEOUT, timeout));
    }

    public String post(final URI uri, final HttpEntity entity, final Header[] headers, final RequestConfig config) throws Exception {
        // create httpPost
        final HttpPost httpPost = new HttpPost(uri);
        // set headers
        setHeaders(httpPost, headers);
        //set config
        setConfig(httpPost, config);
        // set entity
        httpPost.setEntity(entity);
        //execute
        return execute(httpPost);
    }

    private void setHeaders(final HttpRequestBase httpRequest, final Header[] headers) {
        if (null != headers) {
            httpRequest.setHeaders(headers);
        }
    }

    private void setConfig(final HttpRequestBase httpRequest, final RequestConfig config) {
        if (null != config) {
            httpRequest.setConfig(config);
        }
    }

    private RequestConfig getRequestConfig(final int connectTimeout, final int timeout) {
        return RequestConfig
                .custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(timeout)
                .build();
    }

    /**
     * uniform execute method for GET and POST
     *
     * @param request
     * @return
     * @throws IOException
     */
    private String execute(final HttpRequestBase request) throws Exception {
        final StringBuilder sb = new StringBuilder(256);
        int statusCode = -1;
        long start = System.currentTimeMillis();
        try {
            sb.append(request.getMethod());
            sb.append(Separator.BLANK).append(request.getURI());
            sb.append(Separator.BLANK).append(request.getProtocolVersion());
            final HttpResponse response = httpClient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= HttpStatus.ERROR_MIN_STATUS_CODE) {
                throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + statusCode + ", status message = [" + response.getStatusLine().getReasonPhrase() + "]");
            }
            final HttpEntity entity = response.getEntity();
            return entity == null ? StringUtils.EMPTY : EntityUtils.toString(entity);
        } catch (Exception ex) {
            sb.append(Separator.BLANK).append(statusCode);
            sb.append(Separator.BLANK).append(System.currentTimeMillis() - start);
            LOGGER.error(sb.toString(), ex);
            throw convertHttpInvokerAccessException(ex, sb.toString());
        } finally {
            if (null != request) {
                request.releaseConnection();
            }
        }
    }

    private static Exception convertHttpInvokerAccessException(Exception ex, String message) throws Exception {
        if (ex instanceof ConnectException) {
            return new RemoteException("Could not connect to HTTP invoker remote url at [" + message + "]", ex);
        }
        throw ex;
    }
}
