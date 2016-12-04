/*
 * @(#)AioContext.java	6.3.9 09/09/25
 *
 * Copyright 2009 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MSF PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 异步IO关联上下文
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @since 2.1.0
 */
public abstract class AioContext {
	private final static int kBufferSize = 128 * 1024;
	private SocketChannel channel = null;
	private int timeout = 0;
	private long startTime = 0;
	//private IoBuffer buffer = null;
	private ByteBuffer buffer = null;
	public int length = 0;
	// 接收到字节数
	private int recviced = 0;
	private int readpos = 0;

	public AioContext() {
		//
	}

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
		if (this.channel != null) {
			this.channel.configureBlocking(false);
		}
		//this.buffer = IoBuffer.allocate(kBufferSize);
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
		} catch (IOException e) {
			//
		}
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public int add(ByteBuffer src) {
		if (src != null) {
			int pos = recviced;
			int limit = buffer.capacity() - recviced;
			if(limit > 0) {
				buffer.limit(limit);
				buffer.position(recviced);
			}
			//buffer.rewind();
			int len = src.remaining();
			//buffer.add(buf);
			buffer.put(src);
			recviced += len;
			length += len;

		}
		return buffer.position();
	}

	public int add(ByteBuffer buf, int len) {
		if (buf != null) {
			buffer.put(buf.array(), 0, len);
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

	public abstract boolean completed();

	/**
	 * 读取一行
	 * @return
	 */
	public byte[] readLine() {
		byte[] bRet = null;
		int begin = readpos;
		int pos = begin;
		byte[] data = buffer.array();
		while(pos < buffer.remaining()) {
			/*if (pos + 1 < buffer.remaining() && data[pos] == '\r' && data[pos + 1] == '\n'
					&& data[pos + 2] == '\r' && data[pos + 3] == '\n') {
				//readpos += pos - begin;
				bRet = new byte[pos - begin];
				buffer.get(bRet);
				recviced -= pos - begin;
				buffer.get(new byte[2]);
				recviced -= 2;
				//readpos += 2;
				break;
			} else*/ if (pos + 1 < buffer.remaining() && data[pos] == '\r' && data[pos + 1] == '\n') {
				//readpos += pos - begin;
				/*if (pos == begin) {
					break;
				}*/
				bRet = new byte[pos - begin];
				buffer.get(bRet);
				recviced -= pos - begin;
				buffer.get(new byte[2]);
				recviced -= 2;
				//readpos += 2;
				break;
			} else {
				pos++;
			}
		}

		return bRet;
	}

	public void reset() {
		int limit = buffer.capacity() - recviced;
		if(limit > 0) {
			buffer.limit(limit);
			buffer.position(recviced);
		}
	}
}
