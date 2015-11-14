package engine.agent.family4;

import shared.enums.*;
import shared.*;
import shared.interfaces.ConveyorFamily;

public class ConveyorFamily4 implements ConveyorFamily {

	public Status conveyorFamilyAfterStatus;
	public ConveyorFamily before;
	public ConveyorFamily after;
	public ConveyorAgent4 conveyor;
	public MachineAgent4 machine;
	public int index;
	
	public ConveyorFamily4(int index) {
		this.index = index;
		conveyorFamilyAfterStatus = Status.READY;
	}
	
	@Override
	public void msgHereIsGlass(Glass g) {
		System.out.println("ConveyorFamily " + index + ": msgHereIsGlass received.");
		conveyor.msgGlassConveyorFamilyToConveyor(g);
	}

	@Override
	public void msgStatus(Status s) {
		this.conveyorFamilyAfterStatus = s;
		conveyor.msgStatus(s);
		if (machine != null) {
			machine.msgNextStatusChanged();
		}
	}
	
	public void msgGlassFamilyToFamily(Glass g) {
		if (after == null){
			System.out.println("ConveyorFamily " + index + ": Glass not send to the next conveyor family");
		}
		else{
			after.msgHereIsGlass(g);
		}
	}
	
	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		conveyor.msgTurnOff();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		conveyor.msgTurnOn();
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
		machine.msgTurnOff();
	}
	
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		machine.msgTurnOn();
	}
	
	public void setBefore(ConveyorFamily before){
		this.before = before;
		this.conveyor.conveyorFamilyBegin = before;
	}
	
	public void setAfter(ConveyorFamily after){
		this.after = after;
		this.conveyor.conveyorFamilyEnd = after;
	}

	public void setConveyor(ConveyorAgent4 conveyor){
		this.conveyor = conveyor;
	}

	public void setMachine(MachineAgent4 machine){
		this.machine = machine;
	}

	@Override
	public void msgOnlineMachineExpectationFailure() {
		machine.msgBreakMachine();
	}

	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		machine.msgFixMachine();
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

	@Override
	public void msgTopMachineFixGlass() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomMachineFixGlass() {
		// TODO Auto-generated method stub
		
	}
}
