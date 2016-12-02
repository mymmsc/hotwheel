package org.mymmsc.aio;

import org.mymmsc.api.assembly.Api;
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
 * @date 2016年2月15日 上午10:43:32
 */
public class HttpContext extends AioContext {
	private static Logger logger = LoggerFactory.getLogger(HttpContext.class);
	public static final int CHUNK_UNKNOW  = -1;
	public static final int CHUNK_LEN     = 1;
	public static final int CHUNK_DATA    = 2;
	public static final int CHUNK_CRLF    = 3;
	public static final int CHUNK_LAST    = 4;
	public static final int CHUNK_INVALID = Integer.MAX_VALUE;

	public int index = -1;
	private String url = null;
	// 状态码
	private int status = 900;
	// 接收到字节数
	private int recviced = 0;
	private int readpos = 0;
	//
	public boolean hasHeader = false;
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

	public HttpContext(SocketChannel channel, int timeout) throws IOException {
		super(channel, timeout);
	}

	@Override
	public boolean completed() {
		return contentLength > 0 && contentLength >= body.length();
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
		logger.debug(header);
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
				logger.debug("Status={}", status);
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
				logger.debug("Content-Type={}, charset={}", contentType, charset);
			}
		}
		if (transferEncoding == null) {
			String key = "Transfer-Encoding";
			transferEncoding = checkoutHeader(header, key);
			if (transferEncoding != null && transferEncoding.equalsIgnoreCase("chunked")) {
				chunked = true;
				chunkState = CHUNK_LEN;
				logger.debug("{} is chunked", key);
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

	public int getRecviced() {
		return recviced;
	}

	public int getReadpos() {
		return readpos;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setRecviced(int recviced) {
		this.recviced = recviced;
	}

	public void setReadpos(int readpos) {
		this.readpos = readpos;
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

	public void setParams(String params) {
		this.params.append(params);
	}

	public String getParams() {
		return params.toString();
	}

	public static String getParams(Map<String, Object> params) {
		String sRet = "{";
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();

			String value = Api.toString(entry.getValue());
			String str = String.format("&%s=%s", key, value);
			sb.append(str);
		}
		if (sb.length() > 0) {

			sRet += sb.substring(1);
		}
		sRet += "}";
		return sRet;
	}

	public void setParams(Map<String, Object> params) {
		String request = getParams(params);
		this.params.append(request);
	}
}
