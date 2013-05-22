package com.topq.monitor;

import junit.framework.SystemTestCase4;

import org.junit.Before;
import org.junit.Test;

public class VbsProcessMonitorOperations extends SystemTestCase4 {
	
	private VbsProcessMonitor monitor;
	
	@Before
	public void getMonitor() throws Exception {
		monitor = (VbsProcessMonitor)system.getSystemObject("monitor");
	}
	
	@Test
	public void gatherStatistics() throws Exception {
		
		report.report("First time");
		monitor.setDone(false);
		Thread thread = new Thread(monitor);
		thread.start();
		Thread.sleep(10000);
		monitor.setDone(true);
		thread.join();
		
		monitor.getAvgCpu();
		monitor.getAvgMem();
		monitor.getAvgVirtualMem();
		monitor.getAvgPrivateBytes();
		monitor.getAvgThreadCount();
		monitor.getAvgHandleCount();
		
		monitor.setDone(false);
		thread = new Thread(monitor);
		thread.start();
		Thread.sleep(2000);
		monitor.setDone(true);
		thread.join();
		
		monitor.getCpu();
		monitor.getMem();
		monitor.getVirtualMem();
		monitor.getPrivateBytes();
		monitor.getThreadCount();
		monitor.getHandleCount();
	}
}
