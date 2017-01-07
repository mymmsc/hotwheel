package org.hotwheel.utils;

import org.hotwheel.asio.HttpCallBack;
import org.hotwheel.asio.HttpContext;
import org.hotwheel.asio.NioHttpClient;
import org.hotwheel.asio.ScoreBoard;
import org.mymmsc.api.context.JsonAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangfeng on 2016/12/9.
 */
public class TestNotice {
    private static Logger logger = LoggerFactory.getLogger(TestNotice.class);
    protected static String notice = "http://notice.test.jiedaibao.com/notice/notify/notify";

    public static void main(String[] args) {
        int concurrency = 100;
        String debtor = "657991218449808466";
        String creditor = "657991218449808418";
        List<RrcCollectionTimes> list = new ArrayList<>();
        RrcCollectionTimes rct = new RrcCollectionTimes();
        rct.collectorId = creditor;
        list.add(rct);
        try {
            long tm = System.currentTimeMillis();
            NioHttpClient<RrcCollectionTimes> httpClient = new NioHttpClient<RrcCollectionTimes>(list, concurrency);
            httpClient.post(notice, new HttpCallBack<RrcCollectionTimes>() {
                @Override
                public void finished(ScoreBoard scoreBoard) {

                }

                @Override
                public Map<String, Object> getParams(RrcCollectionTimes ct) {
                    String collectorId = ct.collectorId;
                    String title = "填写催记提醒";
                    String message = "您有催收单应于今日填写催记，请及时填写。两次填写催记时间间隔最长3天，否则订单可能会被取消哦。";
                    String pushMessage = "";
                    // jdbclient://rrc/h5rrc?type=taskList
                    // jdbclient://rrc/h5rrc?type=myCollection
                    String messageUrl = "jdbclient://rrc/h5rrc?type=myCollection";
                    String iconUrl = "http://jdbserver.b0.upaiyun.com/images/576401521424470016";

                    TreeMap<String, Object> params = new TreeMap<String, Object>();
                    params.put("type", 1);
                    params.put("bType", 100);
                    params.put("userId", collectorId);

                    params.put("showTitle", title);
                    params.put("showContent", message);
                    params.put("pushMsg", pushMessage);

                    NoticeH5 h5 = new NoticeH5();
                    h5.iconUrl =  iconUrl;
                    h5.messageUrl = messageUrl;

                    //String notifyExtData = String.format("{\"messageUrl\":\"%s\",\"iconUrl\":\"%s\"}", messageUrl, iconUrl);
                    //params.put("notifyExtData", notifyExtData);
                    params.put("notifyExtData", JsonAdapter.get(h5, false));
                    //{"\ext\":{\"subtype\":\"\"}}
                    params.put("pushExtData", "{}");
                    params.put("showTime", 0);
                    return params;
                }

                @Override
                public void completed(HttpContext ctx) {

                }

                @Override
                public void failed(int sequeueId, Exception e) {

                }
            });
            httpClient.start();

            httpClient.close();
            long n = list.size();
            long ums =(System.currentTimeMillis() - tm);
            logger.info("use                  : " + ums + "ms");
            logger.info("Time taken for tests : " + (ums/1000) +" seconds");
            logger.info("process              : " + (ums/n) + "ms/peer");
            logger.info("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
            //Helper.notice(taskName + " Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");

        } catch (IOException e) {
            logger.error("", e);
        }
    }
}
