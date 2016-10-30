package org.mymmsc.asio.samples;

import org.mymmsc.aio.Asio;
import org.mymmsc.aio.HttpContext;
import org.mymmsc.api.assembly.Api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTTP 批量请求
 * 设计原型从Apache HTTP Server的ab(ApacheBench而来)
 * 主线程完成所有client的连接、发送、接收和资源销毁工作
 *
 * @author wangfeng
 * @date 2016年1月10日 上午7:57:42
 */
public class HttpBatch extends Asio<HttpContext> {
    private String host = "100.73.17.2";

    private int port = 80;
    private String path = "/dsmp/api/v2/collection/collectRecord.cgi";
    @SuppressWarnings("unused")
    private String postdata = "memberId=12345678901";
    private int postlen = 0;
    private int connectTimeout = 30 * 1000;
    private int readTimeout = 30 * 1000;

    /** 并发数 */
    //private int concurrency = 100;
    /** 总请求数, -1为无限制 */
    //private int number = 10000;

    private int good = 0;
    private int bad = 0;
    private int requests = 0;

    public HttpBatch(int number) throws  IOException {
        this(number, kConcurrency);
    }

    /**
     * @throws IOException
     *
     */
    public HttpBatch(int number, int concurrency) throws IOException {
        super(number, concurrency);
        this.number = number;
        this.concurrency = concurrency;
        host = "100.73.18.11";
        path = "/mybankv21/phppassport/v2/passport/inner/get-user-basic-info";

        String userId = "538522734200627281";
        String appKey = "fb371c48e9a9b2a1174ed729ae888513";

        long ts = System.currentTimeMillis();
        //RRX-PROMOTION-SECRET

        TreeMap<String, Object> params = new TreeMap<>();
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

        StringBuilder body = new StringBuilder();
        for (Map.Entry<String, Object> entry: params.entrySet()) {
            String key = entry.getKey();
            String value = Api.toString(entry.getValue());
            body.append('&' + key + '=' + value);
        }
        postdata = body.toString().substring(1);
		/*
		host = "100.73.17.2";
		path = "/dsmp/api/v2/collection/collectRecordDetail.cgi";
		postdata = "memberId=538522734200627221&collectId=" + Api.Utf8URLencode("催零五(593591397050305377)");
		*/
    }

    @Override
    public void onAccepted(HttpContext context) {
        //
    }

    @Override
    public void onConnected(HttpContext context) {
        int posting = 0;
        String request = null;
        boolean keepalive = false;
        boolean isproxy = false;
        String fullurl = "";
        String cookie = "";
        String auth = "";

        String hdrs = "application/x-www-form-urlencoded; charset=utf-8\r\n" + String.format("Host: %s\r\n", host);
        hdrs += "User-Agent: HttpBench/1.0.1\r\n";
        //hdrs += "Accept: */*\r\n";

        // 已连接server端, 超时改用读写超时参数
        context.setTimeout(readTimeout);

        if(!Api.isEmpty(postdata)) {
            postdata = postdata.trim();
            postlen = postdata.length();
        }

        if(postlen <= 0) {
            posting = 0;
        } else {
            posting = 1;
        }
		/* setup request */
        if (posting <= 0) {
            request = String.format("%s %s HTTP/1.1\r\n%s%s%s%s\r\n",
                    (posting == 0) ? "GET" : "HEAD",
                    (isproxy) ? fullurl : path,
                    keepalive ? "Connection: Keep-Alive\r\n" : "Connection: close\r\n",
                    cookie, auth, hdrs);
        } else {
            request = String.format("POST %s HTTP/1.1\r\n%s%s%sContent-length: %d\r\nContent-type: %s\r\n%s",
                    (isproxy) ? fullurl : path,
                    keepalive ? "Connection: Keep-Alive\r\n" : "Connection: close\r\n",
                    cookie, auth,
                    postlen,
                    hdrs, postdata);
        }
        try {
            SocketChannel sc = null;
            sc = context.getChannel();
            sc.write(ByteBuffer.wrap(request.getBytes(UTF8)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClosed(HttpContext context) {
        //requests --;
        //good++;
        System.out.print('C');
    }

    @Override
    public void onCompleted(HttpContext context) {
        good ++;
        requests --;
        System.out.print('F');
    }

    @Override
    public void onError(HttpContext context) {
        requests --;
        bad ++;
        //good --;
        System.out.print('E');
    }

    @Override
    public void onTimeout(HttpContext context) {
        requests --;
        // 超时后, 失败请求数+1
        bad++;
        //good --;
        System.out.print('O');
    }

    @Override
    public void onWrite(HttpContext context) {
        //
        System.out.print(">");
    }

    @Override
    public void onCompact(HttpContext context) {
        //System.out.println("channel-number=" + selector.keys().size());
        System.out.println(String.format("number=%d,request=%d,good=%d,bad=%d.", number,requests, good, bad));
        while((number < 0 || number > good + bad + requests) && concurrency > requests) {
            // 如果未达到并发限制数量, 新增加一个请求
            try {
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.setOption(StandardSocketOptions.SO_RCVBUF, 128 * 1024);
                sc.setOption(
                        StandardSocketOptions.SO_SNDBUF, 128 * 1024);
                //sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                sc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //sc.socket().setSoTimeout(10 * 1000);
                sc.setOption(StandardSocketOptions.TCP_NODELAY, true);
                // SO_LINGGER参数在java不能使用,
                //sc.setOption(StandardSocketOptions.SO_LINGER, 10 * 1000);
                //socket.setSoTimeout(connectTimeout);
                InetSocketAddress sa = new InetSocketAddress(host, port);
                @SuppressWarnings("unused")
                boolean ret = sc.connect(sa);
                HttpContext ctx = new HttpContext(sc, connectTimeout);
                sc.register(selector,
                        SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT,
                        ctx);
                requests ++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }/* else */
        if(number <= good + bad) {
            done = false;
            System.out.println(String.format("number=%d,request=%d,good=%d,bad=%d.", number,requests, good, bad));
        }
    }

    @Override
    public void onRead(HttpContext context) {
        ByteBuffer buffer = context.getBuffer();
        buffer.flip();
        int pos = context.getReadpos();
        StringBuffer line = new StringBuffer();
        int start = pos;
        int stop = start;
        //int dupCRLF = 0;
        // 解析http-header
        while(!context.hasHeader && buffer.hasRemaining()) {
            byte ch = buffer.get();
            switch (ch) {
                case '\r':
                    // 跳过
                    //dupCRLF ++;
                    break;
                case '\n':
                    //dupCRLF ++;
                    // 处理内容
                    if(start == stop) {
                        // header域结束, 下面是body
                        System.out.println("Body start...");
                        context.hasHeader = true;
                        String cl = context.getHeader("Content-Length");
                        int len = Api.valueOf(int.class, cl);
                        context.contentLength = len;
                        break;
                    } else {
                        // header域
                        //dupCRLF = 0;
                        context.addHeader(line.toString());
                        line.setLength(0);
                        stop = start = 0;
                    }
                    break;
                default:
                    // 默认追加内容
                    stop ++;
                    line.append((char)ch);
                    break;
            }
            pos++;
        }

        buffer.compact();
        if(context.hasHeader && context.chunked) {
            // chunked编码
            buffer.flip();
            ByteBuffer content = ByteBuffer.allocate(1024);
            int begin = buffer.position();
            int end = buffer.limit();
            while (true) { // 封包循环
                for (int i = begin; i < end - 1; i++) {
                    if (buffer.get(i) == 0x0D && buffer.get(i + 1) == 0x0A) {
                        byte[] nums = new byte[i - begin];
                        buffer.get(nums);
                        // 丢弃\r\n
                        buffer.get(new byte[2]);
                        int num = Integer.parseInt(new String(nums), 16);
                        byte[] strs = new byte[num];
                        buffer.get(strs);
                        content.put(strs);
                        // 丢弃\r\n
                        buffer.get(new byte[2]);
                        begin = i + 4 + num;
                        context.contentLength += num;
                        break;
                    }
                }
                if (buffer.get(begin) == 0x30 && buffer.get(begin + 1) == 0x0D && buffer.get(begin + 2) == 0x0A && buffer.get(begin + 3) == 0x0D && buffer.get(begin + 4) == 0x0A) {
                    content.flip();
                    buffer.get(new byte[5]);
                    context.chunkedFinished = true;
                    break;
                }
            }
            System.out.println(new String(content.array(), 0, content.limit()));
        }

        byte[] ac = Arrays.copyOfRange(buffer.array(), 0 , buffer.position());
        String response = new String(ac);
        System.out.println(response);
        int cl = buffer.position();
        try {
            cl = response.getBytes("UTF-8").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if((!context.chunked // 非chunked编码
                || ( context.chunked && context.chunkedFinished)) // chunked编码且已经结束
                && cl >= context.contentLength) {
            //handleClosed(context.getChannel());
            onCompleted(context);
            // 数据处理完毕, 关闭socket
            //onClosed(context);
        }
        System.out.println("----------------------------------");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String str = "a1";
        int pageSize = Api.valueOf(int.class, str);
        HttpBatch hb = null;
        try {
            int total = 10000;
            int concurrency = 500;
            float n = total;
            hb = new HttpBatch(total, concurrency);
            long tm = System.currentTimeMillis();
            hb.start();
            long ums = (System.currentTimeMillis() - tm);
            hb.close();
            System.out.println("use                  : " + ums + "ms");
            System.out.println("Time taken for tests : " + (ums/1000) +" seconds");
            System.out.println("process              : " + (ums/n) + "ms/peer");
            System.out.println("Requests per second  : " + (n * 1000/ ums) + " [#/sec] (mean)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
