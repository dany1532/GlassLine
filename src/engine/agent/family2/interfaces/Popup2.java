package engine.agent.family2.interfaces;

import shared.Glass;
import shared.interfaces.Robot;

public interface Popup2 {
	
	public abstract void msgAreYouAvailable();
	
	public abstract void msgWantToLowerGlass(Robot rb);
	
	public abstract void msgGlassSensorToPopUp(Glass g);
	
	public abstract void msgGlassRobotToPopUp(Glass g, Robot rb);
	
	public abstract void msgFinishedLoadingGlass();
	
	public abstract void msgFinishedReleasingGlass();
	
	public abstract void msgFinishedMovingUp();
	
	public abstract void msgFinishedMovingDown();
	
	public abstract void msgGlassGotBroken(Robot m);

}
