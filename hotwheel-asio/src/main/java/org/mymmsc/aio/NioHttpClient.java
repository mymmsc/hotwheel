package org.mymmsc.aio;

import org.mymmsc.api.assembly.Api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.StandardSocketOptions;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 异步并发http客户端
 */
public class NioHttpClient<T> extends Asio<HttpContext>{

    private List<T> list = null;
    private IContextCallBack<T> callBack = null;

    private URL httpUrl = null;
    private String host = null;
    private int port = 80;
    private String path = null;

    private int connectTimeout = 30 * 1000;
    private int readTimeout = 30 * 1000;

    private int good = 0;
    private int bad = 0;
    private int requests = 0;

    public NioHttpClient(List<T> list) throws IOException {
        this(list, 500);
    }

    public NioHttpClient(List<T> list, int concurrency) throws IOException {
        this(list.size(), concurrency);
        this.list = list;
        number = this.list.size();

    }
    public NioHttpClient(int number, int concurrency) throws IOException {
        super(number, concurrency);
    }

    @Override
    public void onClosed(HttpContext context) {
        logger.debug("{} closed", context.getClass().getSimpleName());
    }

    @Override
    public void onCompleted(HttpContext context) {
        good ++;
        requests --;
        logger.debug("{} Completed", context.getClass().getSimpleName());
        callBack.completed(context);
    }

    @Override
    public void onAccepted(HttpContext context) {
        logger.debug("{} Accepted", context.getClass().getSimpleName());
    }

    @Override
    public void onError(HttpContext context) {
        requests --;
        bad ++;
        //good --;
        logger.debug("{} Error", context.getClass().getSimpleName());
    }

    @Override
    public void onTimeout(HttpContext context) {
        requests --;
        // 超时后, 失败请求数+1
        bad++;
        //good --;
        logger.debug("{} Timeout", context.getClass().getSimpleName());
    }

    @Override
    public void onConnected(HttpContext context) {
        // HTTP-Body区域的二进制数据
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        int index = context.index;
        //logger.info("list.index=" + index);
        TreeMap<String, Object> params = callBack.getParams(list.get(index));
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = Api.toString(entry.getValue());
            String str = String.format("&%s=%s", key, value);
            addField(data, key, entry.getValue());
        }

        int postlen = data.size();
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
            request = String.format("POST %s HTTP/1.1\r\n%s%s%sContent-length: %d\r\nContent-type: %s\r\n",
                    (isproxy) ? fullurl : path,
                    keepalive ? "Connection: Keep-Alive\r\n" : "Connection: close\r\n",
                    cookie, auth,
                    postlen,
                    hdrs);
        }
        try {
            SocketChannel sc = context.getChannel();
            sc.write(ByteBuffer.wrap(request.getBytes(UTF8)));
            sc.write(ByteBuffer.wrap(data.toByteArray()));
            //requests ++;
        } catch (IOException e) {
            logger.error("SocketChannel.write failed: ", e);
        } finally {
            try {
                data.close();
            } catch (IOException e) {
                //
            }
        }
    }

    @Override
    public void onRead(HttpContext context) {
        //logger.debug("{} Read", context.getClass().getSimpleName());
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
                        //logger.info("Body start...");
                        context.hasHeader = true;
                        String cl = context.getHeader("Content-Length");
                        int len = Api.valueOf(int.class, cl);
                        context.contentLength = len;
                        //logger.info("contentLength={}", len);
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
        int cl = 0;
        buffer.compact();
        if(context.hasHeader && context.chunked) {
            // chunked编码
            buffer.flip();
            ByteBuffer content = ByteBuffer.allocate(1024 * 64);
            int begin = buffer.position();
            int end = buffer.limit();
            //logger.info("beigin={}, end={}...start", begin, end);
            while (true) { // 封包循环
                for (int i = begin; i < end - 1; i++) {
                    //logger.info("i={}", i);
                    if (buffer.get(i) == 0x0D && buffer.get(i + 1) == 0x0A) {
                        byte[] nums = new byte[i - begin];
                        buffer.get(nums);
                        // 丢弃\r\n
                        buffer.get(new byte[2]);
                        String lineBuffer = new String(nums);
                        int separator = lineBuffer.indexOf(';');
                        if (separator < 0) {
                            separator = lineBuffer.length();
                        }
                        lineBuffer = lineBuffer.substring(0, separator);
                        int num = Integer.parseInt(lineBuffer, 16);
                        //logger.info("num={}, start", num);
                        byte[] strs = new byte[num];
                        buffer.get(strs);
                        content.put(strs);
                        // 丢弃\r\n
                        buffer.get(new byte[2]);
                        begin = i + 4 + num;
                        context.contentLength += num;

                        //logger.info("num={}, stop", num);
                        break;
                    }
                }
                //logger.info("1");
                if(begin + 4 > end) {
                    break;
                } else if (buffer.get(begin) == 0x30 && buffer.get(begin + 1) == 0x0D && buffer.get(begin + 2) == 0x0A && buffer.get(begin + 3) == 0x0D && buffer.get(begin + 4) == 0x0A) {
                    //logger.info("1-1");
                    content.flip();
                    buffer.get(new byte[5]);
                    context.chunkedFinished = true;
                    //logger.info("1-2");
                    break;
                }
                //logger.info("2");
            }
            //logger.info("beigin={}, end={}...stop", begin, end);
            String tmp = new String(content.array(), 0, content.limit());
            StringBuffer body = context.getBody();
            body.append(tmp);
            cl = body.length();
            logger.info(tmp);
        }

        if (!context.chunked) {
            byte[] ac = Arrays.copyOfRange(buffer.array(), 0, buffer.position());
            String response = new String(ac);
            logger.info(response);
            cl = buffer.position();
            try {
                cl = response.getBytes(UTF8).length;
            } catch (UnsupportedEncodingException e) {
                logger.error("encoding failed: ", e);
            }
        }

        if((!context.chunked // 非chunked编码
                || ( context.chunked && context.chunkedFinished)) // chunked编码且已经结束
                && context.contentLength > 0 && cl >= context.contentLength) {
            // 数据处理完毕, 关闭socket
            //onCompleted(context);
        }
        //logger.info("----------------------------------");
    }

    @Override
    public void onWrite(HttpContext context) {
        logger.debug("{} Write", context.getClass().getSimpleName());
    }

    @Override
    public void onCompact(HttpContext context) {
        //logger.info("channel-number=" + selector.keys().size());
        //logger.info("Compact: number={},request={},good={},bad={}.", number,requests, good, bad);
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
                ctx.index = requests ++;
            } catch (IOException e) {
                logger.error("SocketChannel.connect failed: ", e);
            }
        }/* else */
        if(number <= good + bad) {
            done = false;
            logger.debug("number={},request={},good={},bad={}.", number, requests, good, bad);
            callBack.finished(this);
        }
    }

    /**
     * 增加一个表单字段
     *
     * @param name  字段名
     * @param value 值
     */
    private void addBasePart(ByteArrayOutputStream data, String name, String value) {
        String temp = String.format("%s=%s", name, value);
        try {
            if (data.size() >= 3) {
                data.write('&');
            }
            data.write(temp.getBytes(UTF8));
        } catch (IOException e) {
            logger.error("write failed: ", e);
        }
    }

    /**
     * 增加一个表单字段, 二进制方式
     *
     * @param name  字段名
     * @param value 值
     * @deprecated
     */
    private void addMultiPart(ByteArrayOutputStream data, String name, byte[] value) {
        String boundary = "";
        String temp = String.format(
                "--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n",
                boundary, name);
        try {
            data.write(temp.getBytes(UTF8));
            data.write(value);
            temp = "\r\n";
            data.write(temp.getBytes(UTF8));
        } catch (IOException e) {
            logger.error("write failed: ", e);
        }
    }

    /**
     * 增加一个表单字段
     *
     * @param name  字段名
     * @param value 值, 可以是除了自定义类以外的任何类型的值, 包括基础数据类型或类对象
     */
    private void addMultiPart(ByteArrayOutputStream data, String name, Object value) {
        String temp = Api.toString(value);
        try {
            addMultiPart(data, name, temp.getBytes(UTF8));
        } catch (UnsupportedEncodingException e) {
            logger.error("encoding failed: ", e);
        }
    }

    /**
     * 增加一个表单字段
     *
     * @param name  字段名
     * @param value 值, 可以是除了自定义类以外的任何类型的值, 包括基础数据类型或类对象
     */
    public void addField(ByteArrayOutputStream data, String name, Object value) {
        String temp = Api.toString(value);
        try {
            boolean uploadFile = false;
            if (uploadFile) {
                addMultiPart(data, name, temp.getBytes(UTF8));
            } else {
                addBasePart(data, name, temp);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("encoding failed: ", e);
        }
    }

    public void post(String url, IContextCallBack<T> callBack) throws MalformedURLException {
        httpUrl = new URL(url);
        host = httpUrl.getHost();
        port = httpUrl.getPort();
        if (port < 0) {
            if(httpUrl.getProtocol().equalsIgnoreCase("http")) {
                port = 80;
            }
        }
        path = httpUrl.getFile();
        this.callBack = callBack;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getBad() {
        return bad;
    }

    public void setBad(int bad) {
        this.bad = bad;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }
}
