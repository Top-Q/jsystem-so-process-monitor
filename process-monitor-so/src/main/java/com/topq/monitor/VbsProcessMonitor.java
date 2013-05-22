package com.topq.monitor;

import java.io.File;
import java.util.ArrayList;

import jsystem.framework.IgnoreMethod;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.exec.Command;
import jsystem.utils.exec.Execute;

public class VbsProcessMonitor extends SystemObjectImpl implements Runnable {

	private final int CPU = 0;
	private final int PRIVATE_BYTES = 1;
	private final int MEMORY = 2;
	private final int VIRTUAL_MEMORY = 3;
	private final int THREAD_COUNT = 4;
	private final int HANDLE_COUNT = 5;
	
	private Command cmd = null;
	private File statsScript;
	private int sleepTime = 250;
	private boolean debug = false;
	
	private boolean done = false;
	
	private ArrayList<Integer> cpuList;
	private ArrayList<Integer> memList;
	private ArrayList<Integer> virtualMemList;
	private ArrayList<Integer> privateBytesList;
	private ArrayList<Integer> threadCountList;
	private ArrayList<Integer> handleCountList;
	
	@Override
	public void init() throws Exception {
		super.init();
	}
	
	@Override
	public void run() {				
		if (debug) {
			return;
		}
		
		try {
			runMonitorCommand();
			
			while (!done) {				
				Thread.sleep(sleepTime);
			}
			
			stopMonitorCommand();			
			readMonitorResults();
		} catch (Exception ignore) {}
	}
		
	private void runMonitorCommand() {
		cmd = new Command();
		cmd.setCmd(new String[]{"cscript.exe", statsScript.getAbsolutePath()});
		try {
			Execute.execute(cmd, false);
		} catch (Exception ignore) {}
	}
	
	private void stopMonitorCommand() {
		cmd.getProcess().destroy();
	}
	
	private void readMonitorResults() {
		cpuList = new ArrayList<Integer>();
		memList = new ArrayList<Integer>();
		virtualMemList = new ArrayList<Integer>();
		privateBytesList = new ArrayList<Integer>();
		threadCountList = new ArrayList<Integer>();
		handleCountList = new ArrayList<Integer>();
		
		try {
			String result = cmd.getStdout().toString().trim();
			String[] lines = result.split("\n");
			
			for (String line : lines) {
				if (line.startsWith("CPU")) {
					String[] counters = line.trim().split(";");			
					cpuList.add(new Integer(counters[CPU].split(":")[1]));
					memList.add(new Integer(counters[MEMORY].split(":")[1]));
					virtualMemList.add(new Integer(counters[VIRTUAL_MEMORY].split(":")[1]));
					privateBytesList.add(new Integer(counters[PRIVATE_BYTES].split(":")[1]));
					threadCountList.add(new Integer(counters[THREAD_COUNT].split(":")[1]));
					handleCountList.add(new Integer(counters[HANDLE_COUNT].split(":")[1]));
				}
			}
		} catch (Exception ignore) {}
	}
	
	public int getCpu() {
		int cpu = cpuList.get(cpuList.size()-1); 
		report.report("CPU - " + cpu);
		return cpu;
	}

	public int getMem() {
		int mem = memList.get(memList.size()-1); 
		report.report("Memory - " + mem);
		return mem;
	}

	public int getVirtualMem() {
		int virtualMem = virtualMemList.get(virtualMemList.size()-1);
		report.report("Virtual Memory - " + virtualMem);
		return virtualMem; 
	}

	public int getPrivateBytes() {
		int privateBytes = privateBytesList.get(privateBytesList.size()-1);
		report.report("Private Bytes - " + privateBytes);
		return privateBytes;
	}

	public int getThreadCount() {
		int threadCount = threadCountList.get(threadCountList.size()-1);
		report.report("Thread Count - " + threadCount);
		return threadCount;
	}

	public int getHandleCount() {
		int handleCount = handleCountList.get(handleCountList.size()-1);
		report.report("Handle Count - " + handleCount);
		return handleCount;
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
	
	public int getAvgVirtualMem(){
		if (debug) {
			return 0;
		}

		int virtualMemAvg = 0;
		
		for (Integer virt : virtualMemList){
			virtualMemAvg += virt.intValue();
		}
		virtualMemAvg /= virtualMemList.size();
		
		report.report("Avg. Virtual Memory - " + virtualMemAvg);
		return virtualMemAvg;		
	}
	
	public int getAvgPrivateBytes(){
		if (debug) {
			return 0;
		}

		int privateBytesAvg = 0;
		
		for (Integer pb : privateBytesList){
			privateBytesAvg += pb.intValue();
		}
		privateBytesAvg /= privateBytesList.size();
		
		report.report("Avg. Private Bytes - " + privateBytesAvg);
		return privateBytesAvg;		
	}
	
	public int getAvgThreadCount(){
		if (debug) {
			return 0;
		}

		int threadCountAvg = 0;
		
		for (Integer tc : threadCountList){
			threadCountAvg += tc.intValue();
		}
		threadCountAvg /= threadCountList.size();
		
		report.report("Avg. Thread Count - " + threadCountAvg);
		return threadCountAvg;		
	}
	
	public int getAvgHandleCount(){
		if (debug) {
			return 0;
		}

		int handleCountAvg = 0;
		
		for (Integer hc : handleCountList){
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

	public File getStatsScript() {
		return statsScript;
	}

	/**
	 * Full path to Monitor.py 
	 */
	public void setStatsScript(File statsScript) {
		this.statsScript = statsScript;
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
