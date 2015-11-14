package shared.interfaces;

import shared.Glass;

public interface Robot {

	// from Popup to Robot
	public void msgGlassPopupToRobot(Glass g);
	// from Popup to Robot
	public void msgPopupReadyForGlass();
	// from Machine to the Robot
	public void msgGlassMachineToRobot(Machine m, Glass g);
	
	public void msgGlassGotBroken(Machine m, Glass g);
	
	
}
