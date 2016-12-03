package org.mymmsc.asio.samples;

import org.mymmsc.aio.AioHttpClient;
import org.mymmsc.aio.HttpCallBack;
import org.mymmsc.aio.ScoreBoard;
import org.mymmsc.api.assembly.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangfeng on 2016/11/29.
 */
public class NtcUserInfo {

    public static void main(String[] args){
        String url = "http://100.73.18.11/mybankv21/phppassport/v2/passport/inner/get-user-basic-info";
        final String productId = "5413757966488781180001";
        final String userId = "538522734200627281";
        int concurrency = 20;
        int total = 100;
        float n = total;

        final TreeMap<String, Object> params0 = new TreeMap<>();
        //String userId = "538522734200627281";
        String appKey = "fb371c48e9a9b2a1174ed729ae888513";

        long ts = System.currentTimeMillis();
        //RRX-PROMOTION-SECRET

        params0.put("appKey", appKey);
        params0.put("user_id", userId);
        params0.put("ts", ts);

        StringBuilder _preSign = new StringBuilder();
        for (Map.Entry<String, Object> entry: params0.entrySet()) {
            _preSign.append(entry.getValue()).append("|");
        }
        //_preSign.append('|');
        _preSign.append("RRX-PROMOTION-SECRET");
        String _sign = Api.md5(_preSign.toString());
        params0.put("sign", _sign);

        long tm = System.currentTimeMillis();
        List list = new ArrayList();
        for (int i = 0; i < total; i++) {
            list.add(userId);
        }
        long ums = (System.currentTimeMillis() - tm);
        System.out.println("ready data use    : " + ums + "ms");

        try {
            final Integer[] number = {0};
            tm = System.currentTimeMillis();
            AioHttpClient<String> httpClient = new AioHttpClient<String>(list, concurrency);
            httpClient.post(url, new HttpCallBack<String>() {
                @Override
                public void finished(ScoreBoard ntc) {
                    System.out.println("number="+ntc.number+",request="+ntc.requests+",good="+ntc.good+",bad="+ntc.bad +".");
                }

                @Override
                public Map<String, Object> getParams(String obj) {
                    String productId = obj;
                    TreeMap<String, Object> params = new TreeMap<>();
                    String userId = "538522734200627281";
                    String appKey = "fb371c48e9a9b2a1174ed729ae888513";

                    long ts = System.currentTimeMillis();
                    //RRX-PROMOTION-SECRET

                    params.put("appKey", appKey);
                    params.put("user_id", userId);
                    params.put("ts", ts);

                    StringBuilder _preSign = new StringBuilder();
                    for (Map.Entry<String, Object> entry: params.entrySet()) {
                        _preSign.append(entry.getValue()).append("|");
                    }
                    //_preSign.append('|');
                    _preSign.append("RRX-PROMOTION-SECRET");
                    String _sign = Api.md5(_preSign.toString());
                    params.put("sign", _sign);
                    return params;
                }

                @Override
                public void completed(int sequeueId, int status, String message, String body) {
                    synchronized (number[0]) {
                        number[0] += 1;
                    }
                    System.out.println(body);
                }

                @Override
                public void failed(int sequeueId, Exception e) {

                }
            });

            ums = (System.currentTimeMillis() - tm);
            System.out.println("number = " + number[0]);
            System.out.println("use                  : " + ums + "ms");
            System.out.println("Time taken for tests : " + (ums/1000) +" seconds");
            System.out.println("process              : " + (ums/n) + "ms/peer");
            System.out.println("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
