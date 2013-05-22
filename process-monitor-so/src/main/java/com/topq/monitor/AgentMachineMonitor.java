package com.topq.monitor;

import jsystem.framework.report.ExtendTestListener;
import jsystem.framework.report.TestInfo;
import jsystem.framework.scenario.JTestContainer;
import jsystem.framework.scenario.flow_control.AntForLoop;
import jsystem.runner.agent.server.RunnerEngine;
import jsystem.treeui.client.JSystemAgentClientsPool;
import junit.framework.Test;

public class AgentMachineMonitor extends MachineMonitor implements ExtendTestListener {

	public void addAgent() throws Exception {
		JSystemAgentClientsPool.initPoolFromRepositoryFile();
		RunnerEngine engine = JSystemAgentClientsPool.getClient(getHost() + ":8999");
		if (engine != null) {
			engine.addListener(this);
		}
	}

	/**
	 * Replace with {@link #startTest(TestInfo)}
	 */
	@Override
	public void startTest(Test test) {
	}

	/*
	 * ExtendedTestListener interface implementation.
	 */

	@Override
	public void startTest(TestInfo testInfo) {
		try {
			testsReport += getTimeStamp() + "," + testInfo.className + "." + testInfo.methodName + "\n";
		} catch (Exception e) {
			report.report("Unexpected exception: " + e, false);
		}
	}

	@Override
	public void addWarning(Test test) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endRun() {
		// TODO Auto-generated method stub
	}

	@Override
	public void startLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endLoop(AntForLoop loop, int count) {
		// TODO Auto-generated method stub
	}

	@Override
	public void startContainer(JTestContainer container) {
		// TODO Auto-generated method stub
	}

	@Override
	public void endContainer(JTestContainer container) {
		// TODO Auto-generated method stub
	}

	/*
	 * Getters and Setters.
	 */

}
