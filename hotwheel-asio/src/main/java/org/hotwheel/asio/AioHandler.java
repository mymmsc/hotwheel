/**
 *
 */
package org.hotwheel.asio;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * AIO事件接口
 *
 * @author wangfeng
 * @since 2016年2月7日 上午1:23:23
 */
public abstract interface AioHandler {
    /**
     * 兼容模式, 常规事件之外的处理过程
     *
     * @param selector
     */
    public abstract void handleCompact(Selector selector);

    /**
     * 接受一个新连接
     * @param sc
     */
    public abstract void handleAccepted(SocketChannel sc);

    /**
     * 连接到远端服务器
     * @param sc
     */
    public abstract void handleConnected(SocketChannel sc);

    /**
     * 关闭socket
     * @param sc
     */
    public abstract void handleClosed(SocketChannel sc);

    /**
     * socket发生错误
     * @param sc
     */
    public abstract void handleError(SocketChannel sc, Exception e);

    /**
     * 读事件处理
     * @param sc
     */
    public abstract void handleRead(SocketChannel sc);

    /**
     * socket可写
     * @param sc
     */
    public abstract void handleWrite(SocketChannel sc);

    /**
     * 通道超时
     * @param sc
     */
    public abstract void handleTimeout(SocketChannel sc);
}
