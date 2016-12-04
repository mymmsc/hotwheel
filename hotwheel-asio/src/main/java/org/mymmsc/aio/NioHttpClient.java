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
import java.util.List;
import java.util.Map;

/**
 * 异步并发http客户端
 */
public class NioHttpClient<T> extends Asio<HttpContext>{
    private List<T> list = null;
    //private int sequeueId = 0;
    private long beginTime = System.currentTimeMillis();
    private HttpCallBack<T> callBack = null;

    private URL httpUrl = null;
    private String host = null;
    private int port = 80;
    private String path = null;

    private int connectTimeout = 30 * 1000;
    private int readTimeout = 30 * 1000;

    private final static String CRLF = "\r\n";

    private boolean debug = false;

    public NioHttpClient(List<T> list) throws IOException {
        this(list, 500);
    }

    public NioHttpClient(List<T> list, int concurrency) throws IOException {
        this(list.size(), concurrency);
        this.list = list;
        //number = this.list.size();

    }
    public NioHttpClient(int number, int concurrency) throws IOException {
        super(number, concurrency);
    }

    @Override
    public void onClosed(HttpContext context) {
        scoreBoard.closed ++;
        scoreBoard.requests --;
        logger.debug("{} closed", context.getClass().getSimpleName());
    }

    @Override
    public void onCompleted(HttpContext context) {
        scoreBoard.good ++;
        logger.debug("{} Completed", context.getClass().getSimpleName());
        callBack.completed(context);
    }

    @Override
    public void onAccepted(HttpContext context) {
        logger.debug("{} Accepted", context.getClass().getSimpleName());
    }

    @Override
    public void onError(HttpContext context) {
        scoreBoard.bad ++;
        logger.debug("{} Error", context.getClass().getSimpleName());
    }

    @Override
    public void onTimeout(HttpContext context) {
        // 超时后, 失败请求数+1
        scoreBoard.bad++;
        logger.debug("{} Timeout", context.getClass().getSimpleName());
    }

    @Override
    public void onConnected(HttpContext context) {
        // HTTP-Body区域的二进制数据
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        int index = context.index;
        logger.debug("list.index=" + index);
        //System.out.println("list.index=" + index);
        Map<String, Object> params = callBack.getParams(list.get(index));
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = Api.toString(entry.getValue());
            String str = String.format("&%s=%s", key, value);
            addField(data, key, entry.getValue());
        }
        context.setParams(params);

        int postlen = data.size();
        int posting = 0;
        String request = null;
        boolean keepalive = false;
        boolean isproxy = false;
        String fullurl = "";
        String cookie = "";
        String auth = "";

        String hdrs = "application/x-www-form-urlencoded; charset=utf-8" + CRLF;
        hdrs += String.format("Host: %s", host) + CRLF;
        hdrs += "User-Agent: HttpBench/1.0.1" + CRLF;
        //hdrs += "Accept: */*" + CRLF;

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

    private void bufferRead(HttpContext context) {
        // 解析http-header
        while(!context.hasHeader/* && buffer.hasRemaining()*/) {
            byte[] lines = context.readLine();
            if (lines == null) {
                //buffer.reset();
                break;
            } else if (lines.length == 0) {
                //context.readLine();
                //if(context.eof) {
                    context.hasHeader = true;
                //} else {
                    //context.reset();
                    //context.hasHeader = true;
                    context.eof = true;
                    if (context.eof) {
                        //return;
                    }
                //}

                break;
            } else if(lines.length == 2 && lines[0] == '\r' && lines[1] == '\n') {
                context.hasHeader = true;
            } else {
                try {
                    String tmp = new String(lines, UTF8);
                    //System.out.println(tmp);
                    context.addHeader(tmp);
                    if (context.contentLength == 0) {
                        String cl = context.getHeader("Content-Length");
                        int len = Api.valueOf(int.class, cl);
                        context.contentLength = len;
                    }
                } catch (UnsupportedEncodingException e) {
                    //
                }
            }
        }
        //buffer.compact();
        if (context.hasHeader && context.eof) {
            //buffer.flip();
            if (!context.chunked) {
                String tmp = new String(context.array(), context.position() , context.limit());
                StringBuffer body = context.getBody();
                body.append(tmp);
                //buffer.reset();
                //System.out.println(response);
            } else {
                // chunked编码
                //ByteBuffer content = ByteBuffer.allocate(1024 * 1024 * 64);
                //int begin = buffer.position();
                //int end = buffer.limit();
                //byte[] data = buffer.array();
                //line.setLength(0);
                //pos = 0;
                //logger.debug("beigin={}, end={}...start", begin, end);
                while (context.hasRemaining()) { // 封包循环
                    if (context.chunkState == HttpContext.CHUNK_LEN) {
                        byte[] nums = context.readLine();
                        if (nums == null) {
                            break;
                        }
                        String lineBuffer = new String(nums);
                        int separator = lineBuffer.indexOf(';');
                        if (separator < 0) {
                            separator = lineBuffer.length();
                        }
                        lineBuffer = lineBuffer.substring(0, separator);
                        int num = -1;
                        try {
                            num = Integer.parseInt(lineBuffer, 16);
                            if (num == 0) {
                                context.chunkState = HttpContext.CHUNK_LAST;
                            } else {
                                context.chunkState = HttpContext.CHUNK_DATA;
                            }
                            context.chunkSize = num;
                            context.contentLength += num;
                        } catch (final NumberFormatException e) {
                            logger.error("Bad chunk header: " + lineBuffer);
                        }
                    } else if (context.chunkState == HttpContext.CHUNK_DATA) {
                        if (context.position() + context.chunkSize <= context.limit()) {
                            byte[] strs = new byte[context.chunkSize];
                            context.get(strs);
                            //content.put(strs);
                            context.chunkState = HttpContext.CHUNK_CRLF;

                            //logger.debug("beigin={}, end={}...stop", begin, end);
                            //content.flip();
                            String tmp = null;
                            try {
                                tmp = new String(strs, context.getCharset());
                                StringBuffer body = context.getBody();
                                body.append(tmp);
                            } catch (UnsupportedEncodingException e) {
                                logger.error("chunked encode failed: ", e);
                            }
                        } else {
                            break;
                        }
                    } else if (context.chunkState == HttpContext.CHUNK_CRLF) {
                        if (context.position() + 2 <= context.limit()) {
                            context.get(new byte[2]);
                            context.chunkState = HttpContext.CHUNK_LEN;
                        } else {
                            break;
                        }
                    } else if (context.chunkState == HttpContext.CHUNK_LAST) {
                        if (context.position() + 2 <= context.limit()) {
                            context.get(new byte[2]);
                            context.chunkState = HttpContext.CHUNK_LEN;
                        }
                        break;
                    }
                }
            }
        }
        /*
        byte[] ac = Arrays.copyOfRange(buffer.array(), 0 , buffer.position());
        String response = new String(ac);
        System.out.println(response);
        cl = buffer.position();
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
        }*/
        //logger.debug("----------------------------------");
    }

    /*
    private void streamRead(HttpContext context) throws IOException {
        ByteArrayOutputStream outputStream = context.getOutputStream();
        byte[] data = outputStream.toByteArray();
        IoBuffer byteBuffer = IoBuffer.allocate(data.length);
        byteBuffer.put(data);
        outputStream.reset();
        bufferRead(context, byteBuffer);
    }
    */

    @Override
    public void onRead(HttpContext context) {
        try {
            //streamRead(context);
            bufferRead(context);
        } catch (Exception e) {
            logger.error("read failed: ", e);
        }
    }

    @Override
    public void onWrite(HttpContext context) {
        logger.debug("{} Write", context.getClass().getSimpleName());
    }

    @Override
    public void onCompact(HttpContext context) {
        //logger.debug("channel-number=" + selector.keys().size());
        if (debug) System.out.println("channel-number=" + selector.keys().size());
        //logger.debug("Compact: number={},request={},good={},bad={}.", number,requests, good, bad);
        if (debug) System.out.println(String.format("Compact: number=%d,request=%d,good=%d,bad=%d.", number,scoreBoard.requests, scoreBoard.good, scoreBoard.bad));
        while(scoreBoard.sequeueId < number && (/*number < 0 || */number > scoreBoard.good + scoreBoard.bad + scoreBoard.requests) && concurrency > scoreBoard.requests) {
            // 如果未达到并发限制数量, 新增加一个请求
            try {
                SocketChannel sc = SocketChannel.open();
                sc.configureBlocking(false);
                sc.setOption(StandardSocketOptions.SO_RCVBUF, kBufferSize);
                sc.setOption(StandardSocketOptions.SO_SNDBUF, kBufferSize);
                //sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                sc.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                //sc.socket().setSoTimeout(10 * 1000);
                sc.setOption(StandardSocketOptions.TCP_NODELAY, true);
                // SO_LINGGER参数在java不能使用,
                //sc.setOption(StandardSocketOptions.SO_LINGER, 10 * 1000);
                //socket.setSoTimeout(connectTimeout);
                sc.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
                InetSocketAddress sa = new InetSocketAddress(host, port);
                boolean ret = sc.connect(sa);
                if (ret) {
                    //
                } else {
                    //
                }
                HttpContext ctx = new HttpContext(sc, connectTimeout);
                ctx.index = scoreBoard.sequeueId ++;
                sc.register(selector,
                        SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT,
                        ctx);
                scoreBoard.requests ++;
                ctx.setUrl(httpUrl.toExternalForm());
            } catch (IOException e) {
                logger.error("SocketChannel.connect failed: ", e);
            }
        }/* else */
        if(number <= scoreBoard.good + scoreBoard.bad) {
            done = false;
            logger.debug("number={},request={},good={},bad={}.", number, scoreBoard.requests, scoreBoard.good, scoreBoard.bad);
            scoreBoard.acrossTime = System.currentTimeMillis() - beginTime;
            scoreBoard.number = number;
            callBack.finished(scoreBoard);
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

    public void post(String url, HttpCallBack<T> callBack) throws MalformedURLException {
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
}
