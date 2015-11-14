package test.mocks;

import shared.interfaces.AgentInterface;
import engine.util.*;


/**
 * This is the base class for a mock agent. It only defines that an agent should
 * contain a name.
 * 
 * @author Justin Walz
 * 
 */
public class MockAgent implements AgentInterface {
	protected String name;

	public MockAgent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "MockAgent: " + name;
	}

	@Override
	public void startThread() {
		print("MockAgent.startThread() called. No Action Made");
		
	}

	@Override
	public void stopThread() {
		print("MockAgent.stopThread() called. No Action Made");
	}
	
	/** Print message */
	protected void print(String msg) {
		print(msg, null);
	}

	/** Print message with exception stack trace */
	protected void print(String msg, Throwable e) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(": ");
		sb.append(msg);
		sb.append("\n");
		if (e != null) {
			sb.append(StringUtil.stackTraceString(e));
		}
		System.out.print(sb.toString());
	}

}
