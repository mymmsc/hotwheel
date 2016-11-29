package org.mymmsc.asio.samples;

import org.mymmsc.aio.HttpContext;
import org.mymmsc.aio.IContextCallBack;
import org.mymmsc.aio.NioHttpClient;
import org.mymmsc.api.assembly.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangfeng on 2016/10/30.
 */
public class TestNioHttpClient {
    private final static String appKey = "fb371c48e9a9b2a1174ed729ae888513";
    private final static String md5Key = "jdbRenRenCui20160328";

    public static void main(String[] args){
        String url = "http://100.73.16.5:8080/innerApi/rrc/getDebtInfoByProduct";
        final String productId = "5413757966488781180001";
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
            final Integer[] number = {0};
            tm = System.currentTimeMillis();
            NioHttpClient<String> httpClient = new NioHttpClient<String>(list, concurrency);
            httpClient.post(url, new IContextCallBack<String>() {
                @Override
                public void finished(NioHttpClient ntc) {
                    System.out.println("number="+ntc.getNumber()+",request="+ntc.getRequests()+",good="+ntc.getGood()+",bad="+ntc.getBad() +".");
                }

                @Override
                public TreeMap<String, Object> getParams(String obj) {
                    String productId = obj;
                    TreeMap<String, Object> params = new TreeMap<>();
                    // 状态值 1计息中（未还，包括逾期） 2部分还款 3已还款 0全部
                    params.put("status", 0);
                    params.put("pageNo", 0);
                    params.put("pageSize", Integer.MAX_VALUE);
                    params.put("productID", productId);

                    long ts = System.currentTimeMillis() / 1000;

                    params.put("appKey", appKey);
                    params.put("ts", ts);

                    StringBuilder preSign = new StringBuilder();
                    for (Map.Entry<String, Object> entry: params.entrySet()) {
                        preSign.append(Api.toString(entry.getValue()));
                        preSign.append('|');
                    }
                    String sRet = Api.md5(preSign.append(md5Key).toString());
                    sRet = sRet.toLowerCase();
                    params.put("sign", sRet);
                    return params;
                }

                @Override
                public void completed(HttpContext ctx) {
                    number[0] += 1;
                    System.out.println(ctx.getBody().toString());
                }
            });

            ums = (System.currentTimeMillis() - tm);
            System.out.println("create NilHttpClient Object use  : " + ums + "ms");
            tm = System.currentTimeMillis();
            httpClient.start();
            ums = (System.currentTimeMillis() - tm);
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
