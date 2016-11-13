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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Launch a daemon process in the background and create a FIFO
 * used to send commands to the daemon.
 * 
 * @author David Hovemeyer
 */
public class DaemonLauncher {
	public static final String DEFAULT_STDOUT_LOG_FILE = "log.txt";
	
	private String stdoutLogFile;
	private String jvmOptions;
	
	/**
	 * Constructor.
	 */
	public DaemonLauncher() {
		this.stdoutLogFile = DEFAULT_STDOUT_LOG_FILE;
	}
	
	/**
	 * Set the name of the file to which the stdout/stderr of the daemon will
	 * be redirected.
	 * 
	 * @param stdoutLogFile name of stdout/stderr log file
	 */
	public void setStdoutLogFile(String stdoutLogFile) {
		if (Util.hasShellMetaCharacters(stdoutLogFile)) {
			throw new IllegalArgumentException("Stdout log file name may not contain shell metacharacters");
		}
		this.stdoutLogFile = stdoutLogFile;
	}

	/**
	 * Set options to be passed to the JVM
	 * (e.g., "-Xmx1024m -Djavax.net.debug=all").
	 * 
	 * @param jvmOptions options to be passed to the JVM
	 */
	public void setJvmOptions(String jvmOptions) {
		this.jvmOptions = jvmOptions;
	}

	/**
	 * Launch the daemon as a background process (with a FIFO for communication).
	 * 
	 * @param instanceName    the instance name to use
	 * @param daemonClass     the daemon class
	 * @throws DaemonException
	 */
	public void launch(String instanceName, Class<?> daemonClass) throws DaemonException {
		// Check to see if the instance is already running
		Integer pid;
		pid = Util.readPid(instanceName);
		if (pid != null) {
			// Is the instance still running?
			if (Util.isRunning(pid)) {
				throw new DaemonException("Process " + pid + " is still running");
			}
			
			// Instance is not still running, so delete pid file and FIFO
			Util.deleteFile(Util.getPidFileName(instanceName));
			Util.deleteFile(Util.getFifoName(instanceName, pid));
		}
		
		// Start the process
		String codeBase = Util.findCodeBase(this.getClass());
		//System.out.println("Codebase is " + codeBase);
		
		// Build a classpath in which the codebase of this class is first.
		StringBuilder classPathBuilder = new StringBuilder();
		classPathBuilder.append(codeBase);
		classPathBuilder.append(File.pathSeparator);
		classPathBuilder.append(System.getProperty("java.class.path"));

		String classPath = classPathBuilder.toString();
		
		if (Util.hasShellMetaCharacters(classPath)) {
			throw new IllegalArgumentException("Classpath has shell metacharacters");
		}
		
		List<String> cmd = new ArrayList<String>();
		cmd.add(Util.SH_PATH);
		cmd.add("-c");
		
		// Generate the shell command that will launch the DaemonLauncher main method
		// as a background process
		StringBuilder launchCmdBuilder = new StringBuilder();
		launchCmdBuilder.append("( exec '");
		launchCmdBuilder.append(Util.getJvmExecutablePath());
		launchCmdBuilder.append("' -classpath '");
		launchCmdBuilder.append(classPath);
		launchCmdBuilder.append("' ");
		if (jvmOptions != null) {
			launchCmdBuilder.append(jvmOptions);
			launchCmdBuilder.append(" ");
		}
		launchCmdBuilder.append("'" + DaemonLauncher.class.getName() + "' ");
		launchCmdBuilder.append(instanceName);
		launchCmdBuilder.append(" '");
		launchCmdBuilder.append(daemonClass.getName());
		launchCmdBuilder.append("' < /dev/null >> '");
		launchCmdBuilder.append(stdoutLogFile);
		launchCmdBuilder.append("' 2>&1 ) &");
		String launchCmd = launchCmdBuilder.toString();
		//System.out.println("launchCmd=" + launchCmd);
		
		// Make sure that the directory for the stdout log file exists.
		// If this directory doesn't exist, then the launch command will
		// silently fail.
		File stdoutLogFileDir = new File(stdoutLogFile).getParentFile();
		if (stdoutLogFileDir != null) {
			// There is at least one explicit directory component in the
			// name of the stdout log file.
			if (stdoutLogFileDir.exists() && !stdoutLogFileDir.isDirectory()) {
				// Parent directory exists, but isn't a directory.
				throw new DaemonException("Parent directory for stdout log " + stdoutLogFile + " exists but is not a directory");
			}
			if (!stdoutLogFileDir.exists() && !stdoutLogFileDir.mkdirs()) {
				// Parent directory doesn't exist, but we couldn't create it.
				throw new DaemonException("Could not create directory for stdout log " + stdoutLogFile);
			}
		}
		
		cmd.add(launchCmd);
		
		int exitCode = Util.exec(cmd.toArray(new String[cmd.size()]));
		if (exitCode != 0) {
			throw new DaemonException("Error launching daemon: shell exited with code " + exitCode);
		}
	}

	/**
	 * This is the main method invoked by the shell command used
	 * to start the background process.  This should not be called
	 * directly.
	 * 
	 * @param args arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		String instanceName = args[0];
		String daemonClassName = args[1];
		
		// Find out our pid
		Integer pid = Util.getPid();
		
		// Write pid file
		Util.writePid(instanceName, pid);
		
		// Create FIFO
		Util.exec(Util.MKFIFO_PATH, Util.getFifoName(instanceName, pid));
		
		// Instantiate the daemon
		Class<?> daemonClass = Class.forName(daemonClassName);
		IDaemon daemon = (IDaemon) daemonClass.newInstance();
		
		// Start the daemon!
		// Note that exceptions are treated as fatal, and will result
		// in a message to stdout (which should be captured in the stdout
		// log) and cleanup of the FIFO and pid file.
		try {
			daemon.start(instanceName);
		} catch (Throwable e) {
			System.out.println("Exception starting daemon");
			e.printStackTrace(System.out);
			System.out.flush();
			Util.deleteFile(Util.getFifoName(instanceName, pid));
			Util.deleteFile(Util.getPidFileName(instanceName));
			System.exit(1);
		}
		
		// Read commands (issued by the DaemonController) from the FIFO
		String fifoName = Util.getFifoName(instanceName, pid);
		BufferedReader reader = null;
		boolean shutdown = false;
		while (!shutdown) {
			if (reader == null) {
				// open the FIFO: will block until a process writes to it
				reader = new BufferedReader(new FileReader(fifoName));
			}
			
			// Read a command from the FIFO
			String line = reader.readLine();
			
			if (line == null) {
				// EOF on FIFO
				IOUtil.closeQuietly(reader);
				reader = null;
			} else {
				// Process the command
				line = line.trim();
				if (!line.equals("")) {
					if (line.equals("shutdown")) {
						shutdown = true;
						IOUtil.closeQuietly(reader);
						reader = null;
					} else {
						// have the daemon handle the command
						daemon.handleCommand(line);
					}
				}
			}
		}
		
		// Shut down the daemon
		daemon.shutdown();
	}
}
