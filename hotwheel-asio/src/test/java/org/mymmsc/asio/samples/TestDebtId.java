package org.mymmsc.asio.samples;

import org.mymmsc.aio.HttpCallBack;
import org.mymmsc.aio.HttpContext;
import org.mymmsc.aio.NioHttpClient;
import org.mymmsc.aio.ScoreBoard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangfeng on 2016/12/6.
 */
public class TestDebtId {
    private static final String productId = "5413757966488781180001";
    private static final String userId = "538522734200627281";
    public static void main(String[] args) {
        String url = "http://100.73.16.32:5021/id";
        final int concurrency = 500;
        int total = 10000;
        float n = total;
        long tm = System.currentTimeMillis();
        List<String> list = new ArrayList();
        for (int i = 0; i < total; i++) {
            list.add(userId);
        }
        try {
            final Integer[] number = {0};
            tm = System.currentTimeMillis();
            NioHttpClient<String> httpClient = new NioHttpClient<String>(list, 100);
            httpClient.post(url, new HttpCallBack<String>() {
                @Override
                public void finished(ScoreBoard ntc) {
                    System.out.println("number="+ntc.number+",request="+ntc.requests+",good="+ntc.good+",bad="+ntc.bad +".");
                }

                @Override
                public Map<String, Object> getParams(String obj) {
                    TreeMap<String, Object> params = new TreeMap<String, Object>();
                    params.put("type", "outdebt-dev");
                    return params;
                }

                @Override
                public void completed(HttpContext ctx) {
                    number[0] += 1;
                    String body = ctx.getBody().toString();
                    System.out.println(body);
                }

                @Override
                public void failed(int sequeueId, Exception e) {

                }
            });
            httpClient.start();
            long ums = (System.currentTimeMillis() - tm);
            httpClient.close();
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
