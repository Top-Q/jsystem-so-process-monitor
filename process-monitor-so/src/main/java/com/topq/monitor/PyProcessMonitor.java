package com.topq.monitor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jsystem.framework.IgnoreMethod;
import jsystem.framework.report.Reporter;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

public class PyProcessMonitor extends SystemObjectImpl implements Runnable {

	private String pathToScripts;
	private String pythonPath;
	
	private String processName;
	private int sleepTime = 250;
	private boolean debug = false;

	private boolean done = false;

	private ArrayList<Integer> cpuList;
	private ArrayList<Integer> memList;
	private ArrayList<Integer> virtualMemList;
	private ArrayList<Integer> privateBytesList;
	private ArrayList<Integer> threadCountList;
	private ArrayList<Integer> handleCountList;

	private int cpu;
	private int mem;
	private int virtualMem;
	private int privateBytes;
	private int threadCount;
	private int handleCount;

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	public void run() {

		cpuList = new ArrayList<Integer>();
		memList = new ArrayList<Integer>();
		virtualMemList = new ArrayList<Integer>();
		privateBytesList = new ArrayList<Integer>();
		threadCountList = new ArrayList<Integer>();
		handleCountList = new ArrayList<Integer>();

		if (debug) {
			return;
		}

		try {
			report.startLevel("Start process monitoring", Reporter.CurrentPlace);
			while (!done) {
				String result = runMonitorCommand();
				cpuList.add(new Integer(getCounter(result, "CPU", false)));
				memList.add(new Integer(getCounter(result, "Memory", false)));
				virtualMemList.add(new Integer(getCounter(result, "Virtual Memory", false)));
				privateBytesList.add(new Integer(getCounter(result, "Private Bytes", false)));
				threadCountList.add(new Integer(getCounter(result, "Thread Count", false)));
				handleCountList.add(new Integer(getCounter(result, "Handle Count", false)));

				Thread.sleep(sleepTime);
			}
			report.stopLevel();
		} catch (Exception ignore) {
			report.report("Unexpected exception", false);
		}
	}

	public void readMonitorCounters() throws Exception {
		String result = runMonitorCommand();

		cpu = Integer.parseInt(getCounter(result, "CPU", false));
		mem = Integer.parseInt(getCounter(result, "Memory", false));
		virtualMem = Integer.parseInt(getCounter(result, "Virtual Memory", false));
		privateBytes = Integer.parseInt(getCounter(result, "Private Bytes", false));
		threadCount = Integer.parseInt(getCounter(result, "Thread Count", false));
		handleCount = Integer.parseInt(getCounter(result, "Handle Count", false));
	}

	private String runMonitorCommand() throws Exception {
		return executeCommand("-p", getProcessName());
	}

	public int getCpu() {
		return cpu;
	}

	public int getMem() {
		return mem;
	}

	public int getVirtualMem() {
		return virtualMem;
	}

	public int getPrivateBytes() {
		return privateBytes;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public int getHandleCount() {
		return handleCount;
	}

	public String getDllVersion(String dllPath) throws Exception {
		String result = executeCommand("-i", dllPath);
		return getCounter(result, "Version", false);
	}

	public boolean isProcessRunning(String processName) throws Exception {
		return !executeCommand("-e", processName).contains("doesn't");
	}

	public boolean isServiceStarted(String serviceName) throws Exception {
		return executeCommand("-s", serviceName).contains("doesn't");
	}

	public String executeCommand(String flag, String argument) throws Exception {
		Command cmd = new Command();
		cmd.setCmd(new String[] { getPythonPath(), getPathToMonitorScript(), flag, argument });
		Execute.execute(cmd, true);
		if (cmd.getReturnCode() != 0) {
			throw new Exception("Python failed. " + cmd.getStderr().toString());
		}
		return cmd.getStdout().toString();
		
	}

	private String getCounter(String testText, String toFind, boolean caseSensitive) throws Exception {
		String counter = "0";
		Pattern p = null;

		if (caseSensitive) {
			p = Pattern.compile(toFind + "\\s*[:\\.]+[ \\t\\x0B\\f]*(.*)");
		} else {
			p = Pattern.compile(toFind + "\\s*[:\\.]+[ \\t\\x0B\\f]*(.*)", Pattern.CASE_INSENSITIVE);
		}
		Matcher m = p.matcher(testText);
		if (m.find()) {
			counter = m.group(1);
		}
		return counter;
	}

	public int getAvgCpu() {
		if (debug) {
			return 0;
		}

		int cpuAvg = 0;

		for (Integer cpu : cpuList) {
			cpuAvg += cpu.intValue();
		}
		cpuAvg /= cpuList.size();

		report.report("Avg. CPU - " + cpuAvg);
		return cpuAvg;
	}

	public int getAvgMem() {
		if (debug) {
			return 0;
		}

		int memAvg = 0;

		for (Integer mem : memList) {
			memAvg += mem.intValue();
		}
		memAvg /= memList.size();

		report.report("Avg. Memory - " + memAvg);
		return memAvg;
	}

	public int getAvgVirtualMem() {
		if (debug) {
			return 0;
		}

		int virtualMemAvg = 0;

		for (Integer virt : virtualMemList) {
			virtualMemAvg += virt.intValue();
		}
		virtualMemAvg /= virtualMemList.size();

		report.report("Avg. Virtual Memory - " + virtualMemAvg);
		return virtualMemAvg;
	}

	public int getAvgPrivateBytes() {
		if (debug) {
			return 0;
		}

		int privateBytesAvg = 0;

		for (Integer pb : privateBytesList) {
			privateBytesAvg += pb.intValue();
		}
		privateBytesAvg /= privateBytesList.size();

		report.report("Avg. Private Bytes - " + privateBytesAvg);
		return privateBytesAvg;
	}

	public int getAvgThreadCount() {
		if (debug) {
			return 0;
		}

		int threadCountAvg = 0;

		for (Integer tc : threadCountList) {
			threadCountAvg += tc.intValue();
		}
		threadCountAvg /= threadCountList.size();

		report.report("Avg. Thread Count - " + threadCountAvg);
		return threadCountAvg;
	}

	public int getAvgHandleCount() {
		if (debug) {
			return 0;
		}

		int handleCountAvg = 0;

		for (Integer hc : handleCountList) {
			handleCountAvg += hc.intValue();
		}
		handleCountAvg /= handleCountList.size();

		report.report("Avg. Handle Count - " + handleCountAvg);
		return handleCountAvg;
	}

	@IgnoreMethod
	public void setDone(boolean done) {
		this.done = done;
	}

	public String getPathToMonitorScript() {
		return  getPathToScripts() + "/monitor.py";
	}
	
	public String getPathToScripts() {
		return pathToScripts;
	}

	public void setPathToScripts(String pathToScripts) {
		this.pathToScripts = pathToScripts;
	}

	public String getPythonPath() {
		return pythonPath;
	}

	/**
	 * Full path to Python executable
	 */
	public void setPythonPath(String pythonPath) {
		this.pythonPath = pythonPath;
	}

	public String getProcessName() {
		return processName;
	}

	/**
	 * Name of MCC process (.exe)
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	/**
	 * Sleep time between monitor operations (ms)
	 */
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean isDebug() {
		return debug;
	}

	/**
	 * Defines whether the module is in debug mode
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
