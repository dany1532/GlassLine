package engine.agent.family1.interfaces;

import shared.Glass;

public interface Sensor {
	
	public abstract void msgHereIsGlassFromConveyor1(Glass g);
	
	public abstract void msgReachedSensor();
	
	public abstract void msgLeftSensor();
	
	public abstract void msgUnavailable();
	
	public abstract void msgAvailable();
	
}
