package org.mymmsc.asio.samples;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.mymmsc.api.assembly.Api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 批量调用http接口
 *
 * Created by wangfeng on 2016/9/25.
 */
public class HttpBench<T extends List>{
    private int concurrency = 100;
    private List<String> list = null;
    private CloseableHttpAsyncClient httpclient = null;

    public HttpBench(T list) throws IOReactorException {
        this(list, 100);
    }

    public HttpBench(T list, int concurrency) throws IOReactorException {
        this.list = list;
        this.concurrency = concurrency;
        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor();
        PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
        cm.setMaxTotal(this.concurrency);

        this.httpclient = HttpAsyncClients.custom().setConnectionManager(cm).build();
    }

    public void post(String url, int timeout) {
        String result = "";
        String appKey = "fb371c48e9a9b2a1174ed729ae888513";

        httpclient.start();
        try {
            List<Future<HttpResponse>> respList = new LinkedList<Future<HttpResponse>>();
            for (int i = 0; i < list.size(); i++) {
                String userId = list.get(i);
                HttpPost httpRequst = new HttpPost(url);
                httpRequst.setHeader("Connection", "close");
                List <NameValuePair> params = new ArrayList<NameValuePair>();

                long ts = System.currentTimeMillis();
                String secert = "RRX-PROMOTION-SECRET";

                TreeMap<String, Object> params1 = new TreeMap<>();
                params1.put("appKey", appKey);
                params.add(new BasicNameValuePair("appKey", appKey));
                params1.put("user_id", userId);
                params.add(new BasicNameValuePair("user_id", userId));
                params1.put("ts", ts);
                params.add(new BasicNameValuePair("ts", Api.toString(ts)));

                StringBuilder _preSign = new StringBuilder();
                for (Map.Entry<String, Object> entry: params1.entrySet()) {
                    _preSign.append(entry.getValue()).append("|");
                }
                //_preSign.append('|');
                _preSign.append("RRX-PROMOTION-SECRET");
                String _sign = Api.md5(_preSign.toString());
                params.add(new BasicNameValuePair("sign",_sign));
                httpRequst.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                respList.add(httpclient.execute(httpRequst, null));
            }

            // Print response code
            for (Future<HttpResponse> response : respList) {
                try {
                    HttpResponse httpResponse = response.get();
                    if(httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        HttpEntity httpEntity = httpResponse.getEntity();
                        result = EntityUtils.toString(httpEntity);//取出应答字符串
                        System.out.println(result);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                // System.out.println(response.get().getStatusLine());
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] argv) {
        String url = "http://100.73.18.11/mybankv21/phppassport/v2/passport/inner/get-user-basic-info";
        String userId = "538522734200627281";
        int total = 10000;
        float n = total;
        long tm = System.currentTimeMillis();
        List list = new ArrayList();
        for (int i = 0; i < total; i++) {
            list.add(userId);
        }
        HttpBench hb = null;
        try {
            hb = new HttpBench(list, 200);
            hb.post(url, 30);
            long ums = (System.currentTimeMillis() - tm);

            System.out.println("use                  : " + ums + "ms");
            System.out.println("Time taken for tests : " + (ums/1000) +" seconds");
            System.out.println("process              : " + (ums/n) + "ms/peer");
            System.out.println("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
        } catch (IOReactorException e) {
            e.printStackTrace();
        }

    }
}
