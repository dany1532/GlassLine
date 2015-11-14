package test.mocks;

import shared.Glass;
import shared.interfaces.Machine;
import shared.interfaces.Robot;

public class MockRobot extends MockAgent implements Robot {

	public MockRobot(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

/*	@Override
	public void msgGlassConveyorToRobot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGlassMachineToRobot() {
		// TODO Auto-generated method stub
		
	}
	*/ //Kevin commented these out because they caused errors, and added what's below

	@Override
	public void msgGlassPopupToRobot(Glass g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPopupReadyForGlass() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGlassMachineToRobot(Machine m, Glass g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGlassGotBroken(Machine m, Glass g) {
		// TODO Auto-generated method stub
		
	}

	
	
}
