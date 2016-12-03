package org.mymmsc.aio;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.mymmsc.api.assembly.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * 引入Apache 异步组件
 *
 * Created by wangfeng on 2016/12/2.
 * @since 2.0.37
 */
public class AioHttpClient<T>{
    private static Logger logger = LoggerFactory.getLogger(AioHttpClient.class);
    private int connectTimeout = 30 * 1000;
    private int readTimeout = 30 * 1000;
    private int concurrency = 100;
    private List<T> list = null;
    private CloseableHttpAsyncClient httpclient = null;
    private static final String UTF_8 = "UTF-8";
    private HttpCallBack<T> callBack = null;
    private ScoreBoard scoreBoard = new ScoreBoard();
    private List<Future<HttpResponse>> respList = null;

    public AioHttpClient(List<T> list) throws IOReactorException {
        this(list, 100);
    }

    public AioHttpClient(List<T> list, int concurrency) throws IOReactorException {
        this.list = list;
        this.concurrency = concurrency;
        scoreBoard.number = list.size();
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
        //cm.setMaxTotal(this.concurrency * Runtime.getRuntime().availableProcessors() * 2);
        cm.setDefaultMaxPerRoute(this.concurrency);
        HttpAsyncClientBuilder clientBuilder = HttpAsyncClients.custom();
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(connectTimeout);
        requestConfig.setSocketTimeout(readTimeout);
        clientBuilder.setConnectionManager(cm);
        clientBuilder.setDefaultRequestConfig(requestConfig.build());
        this.httpclient = clientBuilder.build();
    }

    public void post(final String url, final HttpCallBack<T> callBack) {
        this.callBack = callBack;
        httpclient.start();
        respList = new LinkedList<Future<HttpResponse>>();
        for (int i = 0; i < list.size(); i++) {
            final int sequeueId = i;
            try {
                scoreBoard.sequeueId = sequeueId;
                final Map<String, Object> map = callBack.getParams((T) list.get(sequeueId));

                final HttpPost httpRequst = new HttpPost(url);
                httpRequst.setHeader("Connection", "close");
                List <NameValuePair> params = new ArrayList<NameValuePair>();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    String value = Api.toString(entry.getValue());
                    params.add(new BasicNameValuePair(entry.getKey(), value));
                }
                httpRequst.setEntity(new UrlEncodedFormEntity(params, UTF_8));

                respList.add(httpclient.execute(httpRequst, new FutureCallback<HttpResponse>(){

                    @Override
                    public void completed(HttpResponse httpResponse) {
                        String body = null;
                        try {
                            if(httpResponse.getStatusLine().getStatusCode() == 200) {
                                scoreBoard.good++;
                                HttpEntity httpEntity = httpResponse.getEntity();
                                body = EntityUtils.toString(httpEntity);//取出应答字符串
                            } else {
                                scoreBoard.bad ++;
                            }
                        } catch (Exception e) {
                            logger.error("process HttpResponse exception:", e);
                            //body = e.getMessage();
                        }
                        StatusLine sl = httpResponse.getStatusLine();
                        HttpContext httpContext = new HttpContext();
                        httpContext.setUrl(url);
                        httpContext.setParams(map);
                        httpContext.index = sequeueId;
                        httpContext.setStatus(sl.getStatusCode());
                        httpContext.setBody(body);
                        callBack.completed(httpContext);
                    }

                    @Override
                    public void failed(Exception e) {
                        scoreBoard.bad ++;
                        callBack.failed(sequeueId, e);
                    }

                    @Override
                    public void cancelled() {
                        scoreBoard.bad ++;
                        callBack.failed(sequeueId, null);
                    }
                }));
            } catch (Exception e) {
                logger.error("post failed: ", e);
            }

        }

    }

    public int start() {
        int iRet = 0;
        for (Future<HttpResponse> response : respList) {
            try {
                HttpResponse httpResponse = response.get();
            } catch (Exception e) {
                logger.error("wait http response failed: ", e);
                iRet = -1;
            }
        }

        return iRet;
    }

    public void close() {
        try {
            httpclient.close();
            callBack.finished(scoreBoard);
        } catch (Exception e) {
            //
        }
    }
}
