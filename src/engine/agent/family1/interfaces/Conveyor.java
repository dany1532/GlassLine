package engine.agent.family1.interfaces;

import shared.Glass;

public interface Conveyor {
	
	public abstract void msgHereIsGlass(Glass g);
	
	public abstract void msgPleaseWait();
	
	public abstract void msgPermissionToMove();
	
	public abstract void msgRequestingGlass();

}
