package test.org.hotwheel.asio;

import org.hotwheel.asio.HttpCallBack;
import org.hotwheel.asio.HttpContext;
import org.hotwheel.asio.NioHttpClient;
import org.hotwheel.asio.ScoreBoard;
import org.hotwheel.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 测试ASIO
 *
 * Created by wangfeng on 2017/9/13.
 * @version 5.1.7
 */
public class TestNio {

    private final static String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/MoneyFlow.ssi_ssfx_flzjtj";

    public static void main(String[] args) {
        try {
            long tm = System.currentTimeMillis();
            final List<String> list = new ArrayList<>();
            list.add("sh600001");
            list.add("sh600002");
            NioHttpClient<String> httpClient = new NioHttpClient<>(list, 2000);
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
                    }
                    e.printStackTrace();
                }
            });
            httpClient.start();

            httpClient.close();
            long ums = System.currentTimeMillis() - tm;
        } catch (IOException e) {
            //System.out.println(e.getMessage());
        }
    }
}
