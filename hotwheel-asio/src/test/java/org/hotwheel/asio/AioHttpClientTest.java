package org.hotwheel.asio;

import org.hotwheel.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AioHttpClientTest {
    //private final static String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";
    private final static String url = "https://blog.csdn.net/q";
    private final List<String> list = new ArrayList<>();
    private AioHttpClient<String> httpClient = null;
    private long tm = System.currentTimeMillis();
    private long count = 1 * 5;
    private int concurrency = 1;
    @org.junit.Before
    public void setUp() throws Exception {
        list.add("sh600001");
        list.add("sh600002");
        for (int i = 0; i < count; i++) {
            list.add("" + i);
        }
        httpClient = new AioHttpClient<>(list, concurrency);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        httpClient.close();
        long ums = System.currentTimeMillis() - tm;
    }

    @org.junit.Test
    public void post() {
    }

    @org.junit.Test
    public void start() {
        tm = System.currentTimeMillis();
        httpClient.post(url, new HttpCallBack<String>() {
            @Override
            public void finished(ScoreBoard scoreBoard) {
                //
            }

            @Override
            public Map<String, Object> getParams(String ct) {
                TreeMap<String, Object> params = new TreeMap<>();
                params.put("daima", StringUtils.collectionToCommaDelimitedString(list));
                return params;
            }

            @Override
            public void completed(HttpContext ctx) {
                String body = ctx.getBody().toString();
                //System.out.println(body);
            }

            @Override
            public void failed(int sequeueId, Exception e) {
                if (e instanceof java.nio.channels.UnresolvedAddressException) {
                    System.out.println("服务器地址错误");
                } else if (e instanceof java.net.ConnectException) {
                    System.out.println("服务器拒绝连接" + e.getMessage());
                }
                e.printStackTrace();
            }
        });
        httpClient.start();
        long ums = (System.currentTimeMillis() - tm);
        httpClient.close();
        System.out.println("Concurrency Level    : " + concurrency);
        System.out.println("Complete requests    : " + count);
        System.out.println("use                  : " + ums + "ms");
        System.out.println("Time taken for tests : " + (ums / 1000) + " seconds");
        System.out.println("process              : " + (ums / count) + "ms/peer");
        System.out.println("Requests per second  : " + (count * 1000 / ums) + " [#/sec] (mean)");
    }

    @org.junit.Test
    public void close() {
    }
}