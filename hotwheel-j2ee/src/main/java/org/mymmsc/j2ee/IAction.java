package org.mymmsc.j2ee;

public interface IAction {

    /**
     * HTTP 响应
     *
     * @return byte[] 输出字节数组
     * @remark 不允许抛出异常, 所有异常必须自己解决
     */
    public abstract byte[] execute();

}