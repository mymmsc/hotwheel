package org.hotwheel.asio;

import org.hotwheel.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;

/**
 * HTTP协议上下文
 *
 * @author wangfeng
 * @since 2016年2月15日 上午10:43:32
 */
public class HttpContext extends AioContext {
    private static Logger logger = LoggerFactory.getLogger(HttpContext.class);
    public static final int CHUNK_UNKNOW = -1;
    public static final int CHUNK_LEN = 1;
    public static final int CHUNK_DATA = 2;
    public static final int CHUNK_CRLF = 3;
    public static final int CHUNK_LAST = 4;
    public static final int CHUNK_INVALID = Integer.MAX_VALUE;

    public int index = -1;
    private String url = null;
    // 状态码
    private int status = 900;

    // 是否完成http头部解析
    public boolean hasHeader = false;
    public boolean eof = false;
    public boolean chunked = false;
    public boolean chunkedFinished = false;
    public int chunkState = CHUNK_UNKNOW;
    public int chunkSize = 0;

    private int headerCount;
    private String[] headers;
    // body长度
    public int contentLength = 0;
    // HTTP body域, 暂时未用到
    private StringBuffer body = new StringBuffer();
    private String contentType = null;
    private String charset = null;
    private String transferEncoding = null;
    // 请求参数
    private StringBuffer params = new StringBuffer();

    public HttpContext() {
        super();
        //
    }

    public HttpContext(SocketChannel channel, int timeout) throws IOException {
        super(channel, timeout);
    }

    @Override
    public boolean completed() {
        // 修复非chunked编码获取body长度不准确的bug
        // 第一种情况, body有长度, 头信息中长度和body长度相仿
        boolean b1 = !chunked && eof && contentLength > 0 && body.length() > 0 && contentLength >= body.length();
        // 第二种情况, body为空的正常响应
        boolean b2 = !chunked && eof && contentLength == 0 && body.length() == 0;
        // 第三种情况, chuanked长度相仿
        boolean c1 = eof && contentLength > 0 && body.length() > 0 && contentLength >= body.length();
        return b1 || b2 || c1;
    }

    /**
     * 检出 指定的header域的值
     *
     * @param header
     * @param key
     * @return
     */
    private String checkoutHeader(String header, String key) {
        String sRet = null;
        if (header.regionMatches(true, 0, key, 0, key.length())) {
            String value = header.substring(key.length());
            if (value.charAt(0) == ':') {
                value = value.substring(1);
            }
            sRet = value.trim();
        }
        return sRet;
    }

    public void addHeader(String header) {
        if (headers == null) {
            headers = new String[1];
        } else if (headerCount >= headers.length) {
            headers = Arrays.copyOf(headers, headers.length + 1);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(header);
        }
        headers[headerCount++] = header;

        if (status == 900) {
            String key = "HTTP/1.1";
            int pos = header.indexOf(key);
            if (pos >= 0) {
                String str = header.substring(pos + key.length());
                str = str.trim();
                pos = str.indexOf(' ');
                if (pos > 0) {
                    str = str.substring(0, pos);
                }
                status = Api.valueOf(int.class, str);
                if (logger.isDebugEnabled()) {
                    logger.debug("Status={}", status);
                }
            }
        }
        if (contentType == null) {
            String key = "Content-Type";
            contentType = checkoutHeader(header, key);
        }
        if (charset == null && contentType != null) {
            String key = "charset=";
            int pos = contentType.indexOf(key);
            if (pos >= 0) {
                charset = contentType.substring(pos + key.length());
                charset = charset.trim();
                if (logger.isDebugEnabled()) {
                    logger.debug("Content-Type={}, charset={}", contentType, charset);
                }
            }
        }
        if (transferEncoding == null) {
            String key = "Transfer-Encoding";
            transferEncoding = checkoutHeader(header, key);
            if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {
                chunked = true;
                chunkState = CHUNK_LEN;
                if (logger.isDebugEnabled()) {
                    logger.debug("{} is chunked", key);
                }
            }
        }
    }

    public String getHeader(String key) {
        int keyLength = key.length();
        for (int i = 1; i < headerCount; i++) {
            if (headers[i].regionMatches(true, 0, key, 0, keyLength)) {
                return headers[i].substring(keyLength + 1);
            }
        }
        return null;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public StringBuffer getBody() {
        return body;
    }

    public void setBody(StringBuffer body) {
        this.body = body;
    }

    public void setBody(String body) {
        this.body.setLength(0);
        this.body.append(body);
    }

    public void setParams(String params) {
        this.params.append(params);
    }

    public String getParams() {
        return params.toString();
    }

    public void setParams(Map<String, Object> params) {
        String request = HttpApi.getParams(params);
        this.params.append(request);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
