/*
 * @(#)AioContext.java	6.3.9 09/09/25
 *
 * Copyright 2009 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MSF PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.aio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 异步IO关联上下文
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public abstract class AioContext {
	private final static boolean haveBuffer = false;
	private final static int kBufferSize = 128 * 1024;
	private SocketChannel channel = null;
	private int timeout = 0;
	private long startTime = 0;
	private ByteBuffer buffer = null;
	public int length = 0;

	private ByteArrayOutputStream data = new ByteArrayOutputStream();

	/**
	 * 创建一个AIO上下文
	 *
	 * @param channel
	 * @param timeout
	 * @throws IOException
     */
	public AioContext(SocketChannel channel, int timeout) throws IOException {
		this.channel = channel;
		this.timeout = timeout;
		this.startTime = System.currentTimeMillis();
		this.channel.configureBlocking(false);
		this.buffer = ByteBuffer.allocate(kBufferSize);
		this.buffer.clear();
	}

	/**
	 * 关闭 socket及清理缓存
     */
	public void close() {
		try {
			buffer.clear();
			channel.close();
			data.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int add(ByteBuffer buf) {
		if (buf != null) {
			int len = buf.limit();
			if(haveBuffer) {
				buffer.put(buf);
			} else {
				data.write(buf.array(), 0, buf.limit());
			}
			length += len;
		}
		return buffer.position();
	}

	public int add(ByteBuffer buf, int len) {
		if (buf != null) {
			if (haveBuffer) {
				buffer.put(buf.array(), 0, len);
			} else {
				data.write(buf.array(), 0, len);
			}
			length += len;
		}
		return buffer.position();
	}

	/**
	 * AioContext is time out ?
	 * 
	 * @return
	 */
	public boolean isTimeout() {
		return isTimeout(timeout);
	}
	
	public boolean isTimeout(long timeout) {
		return (System.currentTimeMillis() - startTime) >= timeout;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(SocketChannel channel) {
		this.channel = channel;
	}

	/**
	 * @return the channel
	 */
	public SocketChannel getChannel() {
		return channel;
	}

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	public ByteArrayOutputStream getOutputStream() {
		return data;
	}

	public abstract boolean completed();
}
