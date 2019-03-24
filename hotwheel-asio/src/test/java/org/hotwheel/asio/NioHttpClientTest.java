package org.hotwheel.asio;

import org.hotwheel.util.StringUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class NioHttpClientTest {
    //private final static String url = "http://vip.stock.finance.sina.com.cn:12345/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";
    //private final static String url = "http://127.0.0.1:12345/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";

    private final static String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";
    private final List<String> list = new ArrayList<>();
    private NioHttpClient<String> httpClient = null;
    private long tm = System.currentTimeMillis();

    @org.junit.Before
    public void setUp() throws Exception {
        list.add("sh600001");
        list.add("sh600002");
        httpClient = new NioHttpClient<>(list, 2000);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        httpClient.close();
        long ums = System.currentTimeMillis() - tm;
    }

    @Test
    public void getSelector() {
    }

    @Test
    public void close() {
    }

    @Test
    public void isClosed() {
    }

    @Test
    public void keyFor() {
    }

    @Test
    public void contextFor() {
    }

    @Test
    public void contextFor1() {
    }

    @Test
    public void createSocket() {
    }

    @Test
    public void closeChannel() {
    }

    @Test
    public void handleCompact() {
    }

    @Test
    public void handleAccepted() {
    }

    @Test
    public void handleClosed() {
    }

    @Test
    public void handleError() {
    }

    @Test
    public void handleTimeout() {
    }

    @Test
    public void handleConnected() {
    }

    @Test
    public void handleRead() {
    }

    @Test
    public void handleWrite() {
    }

    @Test
    public void start() throws MalformedURLException {
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
                System.out.println(body);
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
    }

    @Test
    public void getConcurrency() {
    }

    @Test
    public void setConcurrency() {
    }

    @Test
    public void getNumber() {
    }

    @Test
    public void setNumber() {
    }

    @Test
    public void add() {
    }

    @Test
    public void onClosed() {
    }

    @Test
    public void onCompleted() {
    }

    @Test
    public void onAccepted() {
    }

    @Test
    public void onError() {
    }

    @Test
    public void onTimeout() {
    }

    @Test
    public void onConnected() {
    }

    @Test
    public void onRead() {
    }

    @Test
    public void onWrite() {
    }

    @Test
    public void onCompact() {
    }

    @Test
    public void addField() {
    }

    @Test
    public void post() {
    }
}