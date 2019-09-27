package com.ruijing.registry.common.http;

/**
 * HTTP状态码
 * <p>
 * 代码	消息	描述
 * 100	Continue	只有请求的一部分已经被服务器接收，但只要它没有被拒绝，客户端应继续该请求。
 * 101	Switching Protocols	服务器切换协议。
 * 200	OK	请求成功。
 * 201	Created	该请求是完整的，并创建一个新的资源。
 * 202	Accepted	该请求被接受处理，但是该处理是不完整的。
 * 203	Non-authoritative Information
 * 204	No Content
 * 205	Reset Content
 * 206	Partial Content
 * 300	Multiple Choices	链接列表。用户可以选择一个链接，进入到该位置。最多五个地址。
 * 301	Moved Permanently	所请求的页面已经转移到一个新的 URL。
 * 302	Found	所请求的页面已经临时转移到一个新的 URL。
 * 303	See Other	所请求的页面可以在另一个不同的 URL 下被找到。
 * 304	Not Modified
 * 305	Use Proxy
 * 306	Unused	在以前的版本中使用该代码。现在已不再使用它，但代码仍被保留。
 * 307	Temporary Redirect	所请求的页面已经临时转移到一个新的 URL。
 * 400	Bad Request	服务器不理解请求。
 * 401	Unauthorized	所请求的页面需要用户名和密码。
 * 402	Payment Required	您还不能使用该代码。
 * 403	Forbidden	禁止访问所请求的页面。
 * 404	Not Found	服务器无法找到所请求的页面。.
 * 405	Method Not Allowed	在请求中指定的方法是不允许的。
 * 406	Not Acceptable	服务器只生成一个不被客户端接受的响应。
 * 407	Proxy Authentication Required	在请求送达之前，您必须使用代理服务器的验证。
 * 408	Request Timeout	请求需要的时间比服务器能够等待的时间长，超时。
 * 409	Conflict	请求因为冲突无法完成。
 * 410	Gone	所请求的页面不再可用。
 * 411	Length Required	"Content-Length" 未定义。服务器无法处理客户端发送的不带 Content-Length 的请求信息。
 * 412	Precondition Failed	请求中给出的先决条件被服务器评估为 false。
 * 413	Request Entity Too Large	服务器不接受该请求，因为请求实体过大。
 * 414	Request-url Too Long	服务器不接受该请求，因为 URL 太长。当您转换一个 "post" 请求为一个带有长的查询信息的 "get" 请求时发生。
 * 415	Unsupported Media Type	服务器不接受该请求，因为媒体类型不被支持。
 * 417	Expectation Failed
 * 500	Internal Server Error	未完成的请求。服务器遇到了一个意外的情况。
 * 501	Not Implemented	未完成的请求。服务器不支持所需的功能。
 * 502	Bad Gateway	未完成的请求。服务器从上游服务器收到无效响应。
 * 503	Service Unavailable	未完成的请求。服务器暂时超载或死机。
 * 504	Gateway Timeout	网关超时。
 * 505	HTTP Version Not Supported	服务器不支持"HTTP协议"版本。
 *
 * @author mwup
 * @version 1.0
 * @created 2018/1/6 02:16
 **/
public final class HttpStatus {

    public static final int NOT_SET_SERIALIZE_VALUE = 1001;


    /* 2XX: generally "OK" */

    /**
     * HTTP Status-Code 200: OK.
     */
    public static final int HTTP_OK = 200;

    /**
     * HTTP Status-Code 201: Created.
     */
    public static final int HTTP_CREATED = 201;

    /**
     * HTTP Status-Code 202: Accepted.
     */
    public static final int HTTP_ACCEPTED = 202;

    /**
     * HTTP Status-Code 203: Non-Authoritative Information.
     */
    public static final int HTTP_NOT_AUTHORITATIVE = 203;

    /**
     * HTTP Status-Code 204: No Content.
     */
    public static final int HTTP_NO_CONTENT = 204;

    /**
     * HTTP Status-Code 205: Reset Content.
     */
    public static final int HTTP_RESET = 205;

    /**
     * HTTP Status-Code 206: Partial Content.
     */
    public static final int HTTP_PARTIAL = 206;

    /* 3XX: relocation/redirect */

    /**
     * HTTP Status-Code 300: Multiple Choices.
     */
    public static final int HTTP_MULT_CHOICE = 300;

    /**
     * HTTP Status-Code 301: Moved Permanently.
     */
    public static final int HTTP_MOVED_PERM = 301;

    /**
     * HTTP Status-Code 302: Temporary Redirect.
     */
    public static final int HTTP_MOVED_TEMP = 302;

    /**
     * HTTP Status-Code 303: See Other.
     */
    public static final int HTTP_SEE_OTHER = 303;

    /**
     * HTTP Status-Code 304: Not Modified.
     */
    public static final int HTTP_NOT_MODIFIED = 304;

    /**
     * HTTP Status-Code 305: Use Proxy.
     */
    public static final int HTTP_USE_PROXY = 305;

    /* 4XX: client error */

    public static final int ERROR_MIN_STATUS_CODE = 400;
    /**
     * HTTP Status-Code 400: Bad Request.
     */
    public static final int HTTP_BAD_REQUEST = 400;

    /**
     * HTTP Status-Code 401: Unauthorized.
     */
    public static final int HTTP_UNAUTHORIZED = 401;

    /**
     * HTTP Status-Code 402: Payment Required.
     */
    public static final int HTTP_PAYMENT_REQUIRED = 402;

    /**
     * HTTP Status-Code 403: Forbidden.
     */
    public static final int HTTP_FORBIDDEN = 403;

    /**
     * HTTP Status-Code 404: Not Found.
     */
    public static final int HTTP_NOT_FOUND = 404;

    /**
     * HTTP Status-Code 405: Method Not Allowed.
     */
    public static final int HTTP_BAD_METHOD = 405;

    /**
     * HTTP Status-Code 406: Not Acceptable.
     */
    public static final int HTTP_NOT_ACCEPTABLE = 406;

    /**
     * HTTP Status-Code 407: Proxy Authentication Required.
     */
    public static final int HTTP_PROXY_AUTH = 407;

    /**
     * HTTP Status-Code 408: Request Time-Out.
     */
    public static final int HTTP_CLIENT_TIMEOUT = 408;

    /**
     * HTTP Status-Code 409: Conflict.
     */
    public static final int HTTP_CONFLICT = 409;

    /**
     * HTTP Status-Code 410: Gone.
     */
    public static final int HTTP_GONE = 410;

    /**
     * HTTP Status-Code 411: Length Required.
     */
    public static final int HTTP_LENGTH_REQUIRED = 411;

    /**
     * HTTP Status-Code 412: Precondition Failed.
     */
    public static final int HTTP_PRECON_FAILED = 412;

    /**
     * HTTP Status-Code 413: Request Entity Too Large.
     */
    public static final int HTTP_ENTITY_TOO_LARGE = 413;

    /**
     * HTTP Status-Code 414: Request-URI Too Large.
     */
    public static final int HTTP_REQ_TOO_LONG = 414;

    /**
     * HTTP Status-Code 415: Unsupported Media Type.
     */
    public static final int HTTP_UNSUPPORTED_TYPE = 415;

    /* 5XX: server error */

    /**
     * HTTP Status-Code 500: Internal Server Error.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * HTTP Status-Code 501: Not Implemented.
     */
    public static final int HTTP_NOT_IMPLEMENTED = 501;

    /**
     * HTTP Status-Code 502: Bad Gateway.
     */
    public static final int HTTP_BAD_GATEWAY = 502;

    /**
     * HTTP Status-Code 503: Service Unavailable.
     */
    public static final int HTTP_UNAVAILABLE = 503;

    /**
     * HTTP Status-Code 504: Gateway Timeout.
     */
    public static final int HTTP_GATEWAY_TIMEOUT = 504;

    /**
     * HTTP Status-Code 505: HTTP Version Not Supported.
     */
    public static final int HTTP_VERSION = 505;

}
