package com.topq.monitor;

import java.io.File;
import java.text.SimpleDateFormat;

import jsystem.framework.report.ReporterHelper;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;
import junit.framework.AssertionFailedError;
import junit.framework.JSystemJUnit4ClassRunner;
import junit.framework.SystemTest;
import junit.framework.Test;
import junit.framework.TestListener;

public class MachineMonitor extends SystemObjectImpl implements Runnable, TestListener {

	public ProcessMonitor[] processMonitors;

	private String host;

	private String user;

	private String password;

	private String processFields = "ProcessId";

	private int interval = 500;

	private boolean done = false;

	protected String testsReport;

	private Thread thread;

	@Override
	public void init() throws Exception {
		super.init();
		thread = new Thread(this);
		// If processMonitors was not set by SUT we create an empty one so it will not be null, but an empty one, until it is configured by
		// the test.
		if (processMonitors == null) {
			processMonitors = new ProcessMonitor[0];
		}
		// Workaround - somehow in some scenarios the SO is not registered??? However, worst case we add the SO twice, no harm done.
		setName("MachineMonitor" + System.currentTimeMillis());
		systemManager.addSystemObject(this);
		
	}

	public void start() {
		done = false;
		thread.start();
	}

	public void stop() throws InterruptedException {
		done = true;
		thread.join();
	}

	@Override
	public void run() {

		try {
			testsReport = "TimeStamp,TestName\n\n";
			for (ProcessMonitor process : processMonitors) {
				process.startReport();
			}
			while (!done) {
				monitor();
				Thread.sleep(getInterval());
			}
		} catch (Exception e) {
			report.report("Unexpected monitor exception: " + e, false);
		}

	}

	public void saveToCsv() throws Exception {
		for (ProcessMonitor process : processMonitors) {
			process.monitor();
		}
		String fileName = "c:/monitor" + getHost() + ".csv";
		FileUtils.write(fileName, getMonitorReport() + "\n\n" + getTestsReport());
		ReporterHelper.copyFileToReporterAndAddLink(report, new File(fileName), getHost() + ": Monitor File");
		FileUtils.deleteFile(fileName);
	}

	private void monitor() throws Exception {
		for (ProcessMonitor process : processMonitors) {
			process.monitor();
		}
	}

	protected String getTimeStamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return DateUtils.getDate(System.currentTimeMillis(), sdf) + ": ";
	}

	/*
	 * TestListener interface implementation.
	 */

	@Override
	public void startTest(Test test) {
		SystemTest currentTest = null;
		try {
			if (test instanceof SystemTest) {
				currentTest = (SystemTest) test;
			} else if (test instanceof JSystemJUnit4ClassRunner.TestInfo) {
				currentTest = ((JSystemJUnit4ClassRunner.TestInfo) test).getSystemTest();
			}
			testsReport += getTimeStamp() + "," + currentTest.getClassName() + "." + currentTest.getMethodName() + "\n";
			for (ProcessMonitor process : processMonitors) {
				process.startTest();
			}
		} catch (Exception e) {
			report.report("Unexpected exception: " + e, false);
		}
	}

	@Override
	public void endTest(Test test) {
		for (ProcessMonitor process : processMonitors) {
			try {
				process.endTest();
			} catch (Exception e) {
				report.report("Unexpected exception: " + e, false);
			}
		}
	}

	@Override
	public void addError(Test arg0, Throwable arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addFailure(Test arg0, AssertionFailedError arg1) {
		// TODO Auto-generated method stub
	}

	/*
	 * Getters and Setters.
	 */

	public String getMonitorReport() {
		String monitorReport = "";
		for (ProcessMonitor process : processMonitors) {
			monitorReport += process.getMonitorReport();
			monitorReport += "\n\r";
		}
		return monitorReport;
	}

	public String getTestsReport() {
		return testsReport;
	}

	public int getInterval() {
		return interval;
	}

	/**
	 * Sleep time between monitor operations (ms)
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	public String getProcessFields() {
		return processFields;
	}

	public void setProcessFields(String processFields) {
		this.processFields = processFields;
		for (ProcessMonitor process : processMonitors) {
			process.setProcessFields(processFields);
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
		for (ProcessMonitor process : processMonitors) {
			process.wmic.setHost(getHost());
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		for (ProcessMonitor process : processMonitors) {
			process.wmic.setUser(getUser());
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		for (ProcessMonitor process : processMonitors) {
			process.wmic.setPassword(getPassword());
		}
	}

}
