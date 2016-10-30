package org.mymmsc.asio.samples;

import org.mymmsc.aio.HttpContext;
import org.mymmsc.aio.IContextCallBack;
import org.mymmsc.aio.NioHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by wangfeng on 2016/10/30.
 */
public class TestNioHttpClient {

    public static void main(String[] args){
        String url = "http://100.73.18.11/mybankv21/phppassport/v2/passport/inner/get-user-basic-info";
        url = "http://100.73.16.32:5021/id?type=outdebt";
        url = "http://100.73.16.32:5021/id";
        //url = "http://127.0.0.1:8080/dsmp/innerApi/v2/status.cgi";
        final String appKey = "fb371c48e9a9b2a1174ed729ae888513";
        final String userId = "538522734200627281";
        int concurrency = 200;
        int total = 1000;
        float n = total;
        long tm = System.currentTimeMillis();
        List list = new ArrayList();
        for (int i = 0; i < total; i++) {
            list.add(userId);
        }
        long ums = (System.currentTimeMillis() - tm);
        System.out.println("ready data use    : " + ums + "ms");

        try {
            tm = System.currentTimeMillis();
            NioHttpClient<String> httpClient = new NioHttpClient<String>(list, concurrency);
            httpClient.post(url, new IContextCallBack<String>() {
                @Override
                public TreeMap<String, Object> getParams(String obj) {
                    TreeMap<String, Object> params1 = new TreeMap<>();
                    params1.put("type","outdebt");
                    /*
                    String userId = obj;
                    long ts = System.currentTimeMillis();
                    String secert = "RRX-PROMOTION-SECRET";

                    params1.put("appKey", appKey);
                    params1.put("user_id", userId);
                    params1.put("ts", ts);

                    StringBuilder _preSign = new StringBuilder();
                    for (Map.Entry<String, Object> entry: params1.entrySet()) {
                        _preSign.append(entry.getValue()).append("|");
                    }
                    //_preSign.append('|');
                    _preSign.append(secert);
                    String _sign = Api.md5(_preSign.toString());
                    params1.put("sign", _sign);
                    */
                    return params1;
                }

                @Override
                public void finishend(HttpContext ctx) {

                }
            });
            ums = (System.currentTimeMillis() - tm);
            System.out.println("create NilHttpClient Object use  : " + ums + "ms");
            tm = System.currentTimeMillis();
            httpClient.start();
            ums = (System.currentTimeMillis() - tm);
            httpClient.close();
            System.out.println("use                  : " + ums + "ms");
            System.out.println("Time taken for tests : " + (ums/1000) +" seconds");
            System.out.println("process              : " + (ums/n) + "ms/peer");
            System.out.println("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
