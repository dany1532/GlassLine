package shared.interfaces;

/** Base implementation that all agent interfaces should extend */

public interface AgentInterface {
	
	/** Start agent scheduler thread.  Should be called once at init time. */
	public void startThread();

	/** Stop agent scheduler thread. */
	public void stopThread();
	
	public String getName();
	public String toString();
	
	
}

