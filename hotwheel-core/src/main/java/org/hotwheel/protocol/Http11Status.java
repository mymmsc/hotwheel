/**
 *
 */
package org.hotwheel.protocol;

/**
 * HTTP协议
 *
 * @author wangfeng
 * @date 2016年2月21日 下午9:46:30
 */
public final class Http11Status {
    public static final String CONTINUE = "100 Continue";
    public static final String SWITCHING_PROTOCOLS = "101 Switching Protocols";
    public static final String OK = "200 OK";
    public static final String CREATED = "201 Created";
    public static final String ACCEPTED = "202 Accepted";
    public static final String NON_AUTHORITATIVE_INFORMATION = "203 Non-Authoritative Information";
    public static final String NO_CONTENT = "204 No Content";
    public static final String RESET_CONTENT = "205 Reset Content";
    public static final String PARTIAL_CONTENT = "206 Partial Content";
    public static final String MULTIPLE_CHOICES = "300 Multiple Choices";
    public static final String MOVED_PERMANENTLY = "301 Moved Permanently";
    public static final String FOUND = "302 Found";
    public static final String SEE_OTHER = "303 See Other";
    public static final String NOT_MODIFIED = "304 Not Modified";
    public static final String USE_PROXY = "305 Use Proxy";
    public static final String TEMPORARY_REDIRECT = "307 Temporary Redirect";
    public static final String BAD_REQUEST = "400 Bad Request";
    public static final String UNAUTHORIZED = "401 Unauthorized";
    public static final String PAYMENT_REQUIRED = "402 Payment Required";
    public static final String FORBIDDEN = "403 Forbidden";
    public static final String NOT_FOUND = "404 Not Found";
    public static final String METHOD_NOT_ALLOWED = "405 Method Not Allowed";
    public static final String NOT_ACCEPTABLE = "406 Not Acceptable";
    public static final String PROXY_AUTHENTICATION_REQUIRED = "407 Proxy Authentication Required";
    public static final String REQUEST_TIMEOUT = "408 Request Timeout";
    public static final String CONFLICT = "409 Conflict";
    public static final String GONE = "410 Gone";
    public static final String LENGTH_REQUIRED = "411 Length Required";
    public static final String PRECONDITION_FAILED = "412 Precondition Failed";
    public static final String REQUEST_ENTITY_TOO_LARGE = "413 Request Entity Too Large";
    public static final String REQUEST_URI_TOO_LONG = "414 Request-URI Too Long";
    public static final String UNSUPPORTED_MEDIA_TYPE = "415 Unsupported Media Type";
    public static final String REQUESTED_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable";
    public static final String EXPECTATION_FAILED = "417 Expectation Failed";
    public static final String INTERNAL_ERROR = "500 Internal Server Error";
    public static final String NOT_IMPLEMENTED = "501 Not Implemented";
    public static final String BAD_GATEWAY = "502 Bad Gateway";
    public static final String SERVICE_UNAVAILABLE = "503 Service Unavailable";
    public static final String GATEWAY_TIMEOUT = "504 Gateway Timeout";
    public static final String HTTP_VERSION_NOT_SUPPORTED = "505 HTTP Version Not Supported";
}
