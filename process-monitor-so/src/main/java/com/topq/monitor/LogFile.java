package com.topq.monitor;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.topq.monitor.LogAnalyzer.LogStatus;

import jsystem.extensions.analyzers.text.TextNotFound;
import jsystem.framework.system.SystemObjectImpl;

public class LogFile extends SystemObjectImpl {

	private static final String TIME_STAMP_REGEX = "(\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2},\\d{3})\\s";

	private String fileName;

	private long startTimeStamp = 0;

	private long endTimeStamp = 0;
	
	private boolean analyze = false;

	public LogFile(String fileName) {
		this.fileName = fileName;
	}

	public void stampStartTime() {
		startTimeStamp = System.currentTimeMillis();
	}

	public void stampEndTime() throws Exception {
		endTimeStamp = System.currentTimeMillis();
	}

	public String readTail() throws Exception {
		if (0 == startTimeStamp || 0 == endTimeStamp) {
			return "Start time stamp or end time stamp was not marked.";
		}

		File file = new File(getFileName());
		if (!file.exists()) {
			return "Log file " + file.getAbsolutePath() + " was not found";
		}

		Scanner scanner = new Scanner(file);
		StringBuilder result = new StringBuilder();
		boolean inBetweenTimeStamps = false;
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			long timeStamp = parseTimeStamp(line);
			if (timeStamp != 0) {
				if (timeStamp > startTimeStamp) {
					inBetweenTimeStamps = true;
				}
				if (timeStamp > endTimeStamp) {
					break;
				}
			}
			if (inBetweenTimeStamps) {
				result.append(line + System.getProperty("line.separator"));
			}
		}
		setTestAgainstObject(result.toString());
		return result.toString();
	}

	private long parseTimeStamp(String line) {
		Pattern pattern = Pattern.compile(TIME_STAMP_REGEX);
		Matcher matcher = pattern.matcher(line);
		long timeStamp = 0;
		if (matcher.find()) {
			String timeStampStr = matcher.group(1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			try {
				timeStamp = ((Date) sdf.parse(timeStampStr)).getTime();
			} catch (ParseException e) {
			}
		}
		return timeStamp;
	}

	/*
	 * Standard Getters and Setters.
	 */

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void analyze(LogAnalyzer[] logAnalyzerArray) {
		if (!analyze) {
			return;
		}
		if (null == getTestAgainstObject()) {
			return;
		}
		for (LogAnalyzer analyzer : logAnalyzerArray) {
			if (LogStatus.ERROR == analyzer.getStatus()) {
				analyze(new TextNotFound(analyzer.getMessage()), true);
			}
		}
		for (LogAnalyzer analyzer : logAnalyzerArray) {
			if (LogStatus.WARNING == analyzer.getStatus()) {
				TextNotFound textNotFound = new TextNotFound(analyzer.getMessage());
				boolean success = isAnalyzeSuccess(textNotFound);
				if (!success) {
					report(textNotFound.getTitle(), textNotFound.getMessage(), 2);
				}
			}
		}
	}

	public boolean isAnalyze() {
		return analyze;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}

}
