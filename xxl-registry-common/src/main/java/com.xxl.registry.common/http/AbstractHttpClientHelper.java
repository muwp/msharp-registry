package com.xxl.registry.common.http;

import com.xxl.registry.common.util.StreamUtils;
import org.apache.commons.lang3.SerializationException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

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
public abstract class AbstractHttpClientHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpClientHelper.class);

    protected static final String ENCODING_GZIP = "gzip";

    private CloseableHttpClient httpClient;

    private static final String IGNORE_CHECK_STATUS_CODE = "ignore-check-status-code";

    protected AbstractHttpClientHelper() {
        httpClient = HttpClientFactory.FACTORY.getDefaultHttpClient();
    }

    protected AbstractHttpClientHelper(final RequestConfig config) {
        this.httpClient = HttpClientFactory.FACTORY.getHttpClient(config, null);
    }

    public AbstractHttpClientHelper(RequestConfig config, PoolingHttpClientConnectionManager manager) {
        this.httpClient = HttpClientFactory.FACTORY.getHttpClient(config, manager, null);
    }

    protected void setHeaders(final HttpRequestBase httpRequest, final Header[] headers) {
        if (null != headers) {
            httpRequest.setHeaders(headers);
        }
    }

    protected void setConfig(final HttpRequestBase httpRequest, final RequestConfig config) {
        if (null != config) {
            httpRequest.setConfig(config);
        }
    }

    protected RequestConfig getRequestConfig(final int connectTimeout, final int timeout) {
        return RequestConfig
                .custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(timeout).build();
    }

    /**
     * get execute method for GET and POST
     *
     * @param request
     * @return
     * @throws IOException
     */
    public byte[] execute(final HttpRequestBase request) throws Exception {
        int statusCode = -1;
        InputStream inputStream = null;
        final StringBuilder sb = new StringBuilder(256);
        boolean success = false;
        long time = System.currentTimeMillis();
        try {
            sb.append(request.getMethod())
                    .append(Separator.BLANK)
                    .append(request.getURI())
                    .append(Separator.BLANK)
                    .append(request.getProtocolVersion());
            final HttpResponse response = httpClient.execute(request);
            if (request.containsHeader(IGNORE_CHECK_STATUS_CODE)) {
                request.removeHeaders(IGNORE_CHECK_STATUS_CODE);
            } else {
                validateResponse(response);
            }
            statusCode = response.getStatusLine().getStatusCode();
            inputStream = response.getEntity().getContent();
            inputStream = isGzipResponse(response) ? new GZIPInputStream(inputStream) : inputStream;
            byte[] result = StreamUtils.copyToByteArray(inputStream);
            success = true;
            return result;
        } catch (Exception ex) {
            success = false;
            throw convertHttpInvokerAccessException(ex, sb.toString());
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
            if (null != request) {
                request.releaseConnection();
            }
            sb.append(Separator.BLANK)
                    .append(statusCode)
                    .append(Separator.BLANK)
                    .append("success:")
                    .append(success)
                    .append(Separator.BLANK)
                    .append("cost_time:")
                    .append(System.currentTimeMillis() - time)
                    .append("(milliseconds)");
            if (!success) {
                LOGGER.warn(sb.toString());
            }
        }
    }

    private static Exception convertHttpInvokerAccessException(Exception ex, String message) throws Exception {
        throw ex;
    }

    protected void validateResponse(final HttpResponse response) throws IOException {
        final StatusLine statusLine = response.getStatusLine();
        final int status = statusLine.getStatusCode();
        if (status == HttpStatus.NOT_SET_SERIALIZE_VALUE) {
            throw new SerializationException("not set serialize value, status code = " + status + ", status message = [没有设置序列化方式]");
        }
        if (status >= HttpStatus.ERROR_MIN_STATUS_CODE) {
            throw new NoHttpResponseException("Did not receive successful HTTP response: status code = " + status + ", status message = [" + statusLine.getReasonPhrase() + "]");
        }
    }

    private boolean isGzipResponse(HttpResponse httpResponse) {
        final Header encodingHeader = httpResponse.getFirstHeader(HttpConstants.CONTENT_ENCODING);
        if (null == encodingHeader) {
            return false;
        }
        return encodingHeader.getValue() != null && encodingHeader.getValue().toLowerCase().contains(ENCODING_GZIP);
    }
}