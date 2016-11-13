// Copyright (c) 2012-2014, David H. Hovemeyer <david.hovemeyer@gmail.com>
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.cloudcoder.daemon;

/**
 * Interface to be implemented by daemon tasks.
 * 
 * @author David Hovemeyer
 */
public interface IDaemon {
	/**
	 * Start the daemon.
	 * If this method returns normally (by not throwing an exception),
	 * then it is assumed that the daemon has started normally.
	 * If this method throws an exception, then it is assumed that
	 * the daemon failed to start properly, and the FIFO and
	 * pid file will be cleaned up, and the daemon process will
	 * exit. 
	 * 
	 * @param instanceName the instance name for this daemon
	 */
	public void start(String instanceName);
	
	/**
	 * Handle a command received via the FIFO.
	 * 
	 * @param command the command to handle
	 */
	public void handleCommand(String command);
	
	/**
	 * Stop the daemon.
	 */
	public void shutdown();
}
