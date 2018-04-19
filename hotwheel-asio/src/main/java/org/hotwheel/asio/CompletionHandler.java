package org.hotwheel.asio;

/**
 * 时间完成后回调接口
 * 
 * @author wangfeng
 * @date 2016年2月7日 下午12:02:27
 */
public abstract interface CompletionHandler<T extends AioContext> {

	/**
	 * 关闭事件
	 * @param context
	 */
	public abstract void onClosed(T context);

	/**
	 * 业务处理完成事件
	 * @param context
	 */
	public abstract void onCompleted(T context);

	/**
	 * 完成新连接接收事件
	 * @param context
	 */
	public abstract void onAccepted(T context);

	/**
	 * 连接成功事件
	 * @param context
	 */
	public abstract void onConnected(T context);

	/**
	 * 网络操作异常事件
	 * @param context
	 * @param e
	 */
	public abstract void onError(T context, Exception e);

	/**
	 * 有数据到达
	 * @param context
	 */
	public abstract void onRead(T context);

	/**
	 * socket可写
	 * @param context
	 */
	public abstract void onWrite(T context);

	/**
	 * 兼容类事件, 一个大循环完成后触发的事件
	 * @param context
	 */
	public abstract void onCompact(T context);

	/**
	 * 超时事件
	 * @param context
	 */
	public abstract void onTimeout(T context);
}
