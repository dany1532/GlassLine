package engine.agent.family5.interfaces;

import shared.Glass;
//import shared.Status;

public interface ConveyorFamily {

	// glass from source (Bin, Machine, Other Conveyor Family) to Conveyor Family
	public void msgHereIsGlass(Glass g);
	// status from next component (Machine, Other Conveyor Family) to Conveyor Family
	//public void msgStatus(Status s);
	
}
