package com.topq.monitor;

import jsystem.framework.system.SystemObjectImpl;

public class LogAnalyzer extends SystemObjectImpl {

	public enum LogStatus {
		ERROR, WARNING
	}

	private LogStatus status;

	private String message;

	public LogStatus getStatus() {
		return status;
	}

	public void setStatus(LogStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
