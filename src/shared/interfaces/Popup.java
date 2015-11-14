package shared.interfaces;

import java.util.List;

import shared.*;
import shared.enums.*;
import transducer.Transducer;

public interface Popup {

	// from conveyor to popup
	public void msgGlassIsReadyFromConveyor(Recipe r);
	// from conveyor to popup
	public void msgGlassConveyorToPopup(Glass g);
	// from robot to popup
	public void msgGlassIsReadyFromRobot(Robot r);
	// from robot to popup
	public void msgGlassRobotToPopup(Robot r, Glass g);
	// from conveyor family to popup
	public void msgNextStatusUpdated(Status s);
	
	// initialization functions
	public void setConveyorFamily(ConveyorFamily c);
	//public void setConveyor(Conveyor c);
	public void addRobot(Robot r);
		
		
	
}
