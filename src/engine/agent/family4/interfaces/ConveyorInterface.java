package engine.agent.family4.interfaces;

import shared.*;
import shared.enums.*;

public interface ConveyorInterface {

	// glass from Conveyor Family to Conveyor
	public void msgGlassConveyorFamilyToConveyor(Glass g);
	// status from next component (Machine, Other Conveyor Family) to Conveyor
	public void msgStatus(Status s);

}