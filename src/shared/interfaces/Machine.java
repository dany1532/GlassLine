package shared.interfaces;

import shared.Glass;

public interface Machine {

	// From robot to machine
	public void msgGlassRobotToMachine(Glass g);
	
	//From Robot to machine
		//Used to do the release glass animation (Dany)
	public void msgPermissionToReleaseGlass();
	
	
}
