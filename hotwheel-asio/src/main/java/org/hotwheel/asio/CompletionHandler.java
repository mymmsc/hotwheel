/**
 * 
 */
package org.hotwheel.asio;

/**
 * 时间完成后回调接口
 * 
 * @author wangfeng
 * @date 2016年2月7日 下午12:02:27
 */
public abstract interface CompletionHandler<T extends AioContext> {
	public abstract void onClosed(T context);

	public abstract void onCompleted(T context);

	public abstract void onAccepted(T context);

	public abstract void onConnected(T context);

	public abstract void onError(T context, Exception e);

	public abstract void onRead(T context);

	public abstract void onWrite(T context);

	public abstract void onCompact(T context);

	public abstract void onTimeout(T context);
}
