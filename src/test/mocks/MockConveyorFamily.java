package test.mocks;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;

public class MockConveyorFamily implements ConveyorFamily {

	@Override
	public void msgHereIsGlass(Glass g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgStatus(Status s) {
		// TODO Auto-generated method stub
		
	}

	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		//TODO
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
	}
	
	// GUI stop popup for non-norms
	@Override
	public void msgStopPopup(){
		//TODO
	}
	
	// GUI start popup for non-norms
	@Override
	public void msgStartPopup(){
		//TODO
	}
	
	// GUI stop online machine for non-norms
	@Override
	public void msgStopOnlineMachine(){
		//TODO
	}
	
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		//TODO
	}

	@Override
	public void msgOnlineMachineExpectationFailure() {
		// TODO Auto-generated method stub
	}
	@Override
	public void msgOnlineMachineFixExpectationFailure() {
	}
	@Override
	public void msgTopMachineFixGlass() {
		// TODO Auto-generated method stub
	}
	@Override
	public void msgBottomMachineFixGlass() {
		// TODO Auto-generated method stub
	}

	@Override
	public void msgSetConveyorTooFull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOfflineMachineExpectationFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOfflineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopMachineBreaksGlass() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomMachineBreaksGlass() {
		// TODO Auto-generated method stub
		
	}
}
