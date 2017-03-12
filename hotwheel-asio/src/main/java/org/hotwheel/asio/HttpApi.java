package org.hotwheel.asio;

import org.hotwheel.assembly.Api;
import org.hotwheel.io.ActionStatus;
import org.hotwheel.io.HttpClient;
import org.hotwheel.io.HttpResult;
import org.hotwheel.json.JsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * http单例接口封装
 *
 * @author wangfeng
 * @date 2016年1月12日 下午1:52:34
 */
public final class HttpApi {

	private static Logger logger = LoggerFactory.getLogger(HttpApi.class);

	public static String getParams(Map<String, Object> params) {
		String sRet = "{";
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			String value = Api.toString(entry.getValue());
			String str = String.format("&%s=%s", key, value);
			sb.append(str);
		}
		if(sb.length() > 0) {
			sRet += sb.substring(1);
		}
		sRet += "}";
		return sRet;
	}

	/**
	 * 同步方式访问外部接口
	 *
	 * @param clazz
	 * @param url
	 * @param headers
	 * @param body
	 * @return
	 */
	public static <T> T get(Class<T> clazz, String url, Map<String, String> headers, Object body) {
		T tRet = null;
		HttpClient hc = new HttpClient(url, 30);
		HttpResult hRet = hc.post(headers, body);
		System.out.println(
				"http-status=[" + hRet.getStatus() + "], body=[" + hRet.getBody() + "], message=" + hRet.getError());
		if (hRet.getStatus() == 200 && hRet.getBody() != null) {
			JsonAdapter json = JsonAdapter.parse(hRet.getBody());
			if (json != null) {
				tRet = json.get(clazz);
				json.close();
			}
		}
		return tRet;
	}

	public static <T> T request(String url, Map headers, Map object, Class<T> clazz, Class subClass) {
		T obj = null;
		ActionStatus as = new ActionStatus();
		int errCode = 900;
		String params = getParams(object);
		//System.out.println(params);
		logger.info("request={}, params={}", url, params);
		HttpClient hc = new HttpClient(url, 30);
		HttpResult hRet = hc.post(headers, object);

		logger.info("http-status=[" + hRet.getStatus() + "], body=[" + hRet.getBody() + "], message="
				+ hRet.getError());
		if(hRet == null) {
			as.set(errCode + 0, "调用接口失败");
		} else if(hRet.getStatus() >= 400) {
			as.set(errCode + 1, String.format("调用接口失败: %d, %s", hRet.getStatus(), hRet.getError()));
		} else if(hRet.getStatus() != 200) {
			as.set(errCode + 2, String.format("调用接口成功, 但是: %d, %s", hRet.getStatus(), hRet.getError()));
		} else if(hRet.getBody() == null){
			as.set(errCode + 3, "HTTP接口返回BODY为空");
		} else {
			JsonAdapter json = JsonAdapter.parse(hRet.getBody());
			if(json == null) {
				as.set(errCode + 10, "调用接口失败");
			} else {
				try {
				    if (subClass == null) {
				        obj = (T) json.get(clazz);
                    } else {
                        obj = (T) json.get(clazz, subClass);
                    }
					if (obj == null) {
						as.set(errCode + 11, "接口返回内容不能匹配");
					} else {
						as.set(0, "接口成功");
					}
				} catch (Exception e) {
					logger.error("", e);
				} finally {
					json.close();
				}
			}
		}
		logger.info("request={}, result={}", url, JsonAdapter.get(as, false));
		return obj;
	}

	public static <T> T request(String url, Map headers, Map params, Class<T> clazz) {
		return request(url, headers, params, clazz, null);
	}

	public static <T> T requestForSign(String url, TreeMap<String, Object> params,
									   String appKey, String md5Key,
									   Class<T> clazz, Class subClass) {
		if(params != null) {
			params.put("appKey", appKey);
			long ts = System.currentTimeMillis() / 1000;
			params.put("ts", ts);

			StringBuilder preSign = new StringBuilder();
			for (Map.Entry<String, Object> entry: params.entrySet()) {
				preSign.append(Api.toString(entry.getValue()));
				preSign.append('|');
			}
			String _sign = Api.md5(preSign.append(md5Key).toString());
			params.put("sign", _sign.toLowerCase());
		}
		return request(url, null, params, clazz, subClass);
	}
}
