package org.mymmsc.aio;

/**
 * 记分牌
 *
 * Created by wangfeng on 2016/11/27.
 * @since 2.1.1
 */
public class ScoreBoard {
    public int sequeueId = 0;
    public int number = 0;
    // 正在进行中的请求数
    public int requests = 0;
    // 完成业务处理的数量
    public int good = 0;
    // 失败的数量
    public int bad = 0;
    // 关闭socket数量, 总体应该和number-good-bad保持一致
    public int closed = 0;
    // 耗时统计
    public long acrossTime = 0;
}
