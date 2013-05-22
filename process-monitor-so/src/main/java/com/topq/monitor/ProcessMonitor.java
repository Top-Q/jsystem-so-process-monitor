package com.topq.monitor;

import java.io.File;
import java.text.SimpleDateFormat;

import com.topq.monitor.LogAnalyzer.LogStatus;

import jsystem.framework.report.ReporterHelper;
import jsystem.framework.system.SystemObjectImpl;
import jsystem.utils.DateUtils;
import jsystem.utils.FileUtils;

public class ProcessMonitor extends SystemObjectImpl {

	private String processName;

	private String processFields = "Description";

	private String monitorReport;

	public Wmic wmic;

	public LogFile logFile;

	public LogAnalyzer[] logAnalyzerArray;

	@Override
	public void init() throws Exception {
		super.init();
		/*
		 * If we use ProcessMonitor directly, we want to add wmic clause to the SUT to configure the host, user name and password so wmic
		 * will be created and set automatically. However, if we use ProcessMonitor as part of MachineMonitor then host, user name and
		 * password are configured via MachineMonitor clause and we want to avoid an empty Wmic clause so we add it here.
		 */
		if (wmic == null) {
			wmic = new Wmic();
			wmic.init();
		}

		if (null == logAnalyzerArray) {
			// No log analyzer was defined in SUT. Creating the default one
			logAnalyzerArray = new LogAnalyzer[2];
			LogAnalyzer analyzer = new LogAnalyzer();
			analyzer.setMessage("ERROR");
			analyzer.setStatus(LogStatus.ERROR);
			logAnalyzerArray[0] = analyzer;
			analyzer = new LogAnalyzer();
			analyzer.setMessage("WARNING");
			analyzer.setStatus(LogStatus.WARNING);
			logAnalyzerArray[1] = analyzer;
		}

	}

	public void startReport() throws Exception {
		monitorReport = "TimeStamp," + wmic.handleGetProcessCommand(getProcessName(), getProcessFields()).split("\n")[5] + "\n";
	}

	public void monitor() throws Exception {
		monitorReport += getTimeStamp() + ","
				+ wmic.handleGetProcessCommand(getProcessName(), getProcessFields()).split("\n")[6].replaceFirst("\r", "");
	}

	private String getTimeStamp() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return DateUtils.getDate(System.currentTimeMillis(), sdf) + ": ";
	}

	public void startTest() throws Exception {
		if (logFile != null) {
			logFile.stampStartTime();
		}
	}

	public void endTest() throws Exception {
		if (logFile != null) {
			logFile.stampEndTime();
			File file = File.createTempFile("PartialLogFile", ".txt");
			String content = logFile.readTail();
			FileUtils.write(file, content, false);
			ReporterHelper.copyFileToReporterAndAddLink(report, file, wmic.getHost() + ": Log File for " + processName);
			file.delete();
			logFile.analyze(logAnalyzerArray);
		}
	}

	/*
	 * Getters and Setters.
	 */

	public String getMonitorReport() {
		return monitorReport;
	}

	public String getProcessFields() {
		return processFields;
	}

	public void setProcessFields(String processFields) {
		this.processFields = processFields;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}
