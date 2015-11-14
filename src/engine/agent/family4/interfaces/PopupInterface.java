package engine.agent.family4.interfaces;

import engine.agent.family4.enums.*;
import shared.*;
import shared.enums.*;
import test.mocks.*;

public interface PopupInterface {

	// glass from Conveyor to Popup
	public void msgGlassConveyorToPopup(Glass g);
	// glass from Robot to Popup
	public void msgGlassRobotToPopup(Glass g);
	// status from next component (Offline Machine) to Popup
	public void msgMachineStatus(MockRobot mR, AgentState s);
	// status from next component (Online Machine, Other Conveyor Family) to Popup
	public void msgStatus(AgentState s);
	// popup is down
	public void msgPopupDown();
	// popup is up
	public void msgPopupUp();

}
