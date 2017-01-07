package org.hotwheel.asio;

import java.nio.charset.Charset;

/**
 * HTTP 基准并发
 *
 * Created by wangfeng on 2016/9/25.
 */
public abstract class AioBenchmark {
    protected final static String UTF8 = "utf-8";
    protected final static int kConcurrency = 100;
    /** 并发数 */
    protected int concurrency = kConcurrency;
    /** 总请求数, -1为无限制 */
    protected volatile int number = -1;

    /** 选择器超时时间 */
    protected int timeout = 30 * 1000;

    protected String encoding = System.getProperty("file.encoding");
    protected Charset charset = Charset.forName(encoding);

    public AioBenchmark(int number, int concurrency) {
        this.number = number;
        this.concurrency = concurrency > 0 ? concurrency : kConcurrency;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
