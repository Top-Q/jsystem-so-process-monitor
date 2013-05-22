package com.topq.monitor;

import junit.framework.SystemTestCase4;

import org.junit.Before;
import org.junit.Test;

public class MonitorSamples4 extends SystemTestCase4 {

	private MachineMonitor monitor;
	
	@Before
	public void getMonitor() throws Exception {
		monitor = (MachineMonitor)system.getSystemObject("machineMonitor");
	}

	@Test
	public void startMonitor() throws Exception {
		monitor.setInterval(500);
		monitor.start();
	}

	@Test
	public void helloWorld() throws Exception {
		report.report("hello world");
		sleep(4000);
	}

	@Test
	public void stopMonitor() throws Exception {		
		monitor.stop();
		monitor.saveToCsv();
	}

	/*
	 * Standard Getters and Setters.
	 */

}
