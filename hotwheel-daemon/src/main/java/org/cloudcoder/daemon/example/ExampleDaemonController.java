// Copyright (c) 2012, David H. Hovemeyer <david.hovemeyer@gmail.com>
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

package org.cloudcoder.daemon.example;

import org.cloudcoder.daemon.DaemonController;
import org.cloudcoder.daemon.IDaemon;

/**
 * An example main class to start, shut down, and control
 * your daemon process.  If you are creating an executable jar file,
 * a class like this one should be the main class.
 * 
 * @author David Hovemeyer
 */
public class ExampleDaemonController extends DaemonController {
	@Override
	public String getDefaultInstanceName() {
		return "default";
	}

	@Override
	public Class<? extends IDaemon> getDaemonClass() {
		return ExampleDaemon.class;
	}

	public static void main(String[] args) {
		ExampleDaemonController controller = new ExampleDaemonController();
		controller.exec(args);
	}
}
