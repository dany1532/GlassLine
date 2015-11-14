package engine.agent.family0;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;

public class ConveyorFamily0 implements ConveyorFamily {

	/** Pointer to previous and next conveyor families */
	public ConveyorFamily previous, next;
	
	/** Status of next Conveyor Family */
	public Status nextStatus;	
	
	/** Pointer to own conveyor */
	public ConveyorAgent0Base conveyor;
	public MachineAgent0 machine;
	
	/** Index of current machine */
	public int conveyorFamilyIndex;

	/** Set all local variables */
	public void setNeighbors(ConveyorFamily p, ConveyorFamily n){
		previous = p;
		next = n;
	}
	public void setConveyor(ConveyorAgent0Base c){
		conveyor = c;
	}
	public void setMachine(MachineAgent0 m){
		machine = m;
	}
	
	public ConveyorFamily0(int i){
		conveyorFamilyIndex = i;
		nextStatus = Status.READY;
	}
	
	/** Passes glass to conveyor upon initial entrance to the conveyor family */
	@Override
	public void msgHereIsGlass(Glass g) {
		conveyor.msgGlassConveyorFamilyToConveyor(g);
		
	}

	/** Logs the state of the next component for use of inner agents */
	@Override
	public void msgStatus(Status s) {
		nextStatus = s;
		if (machine != null){
			machine.msgNextStatusChanged();
		} else {
			conveyor.msgNextStatusChanged();
		}
	}
	
	/** Allows inner component (machine or conveyor) to pass glass to next family once done */
	public void msgGiveGlassToNext(Glass g){
		if (next != null){
			next.msgHereIsGlass(g);
		} else {
			System.out.println("ConveyorFamily " + conveyorFamilyIndex + ": Didn't give next glass. Next == null");
		}
		
	}
	
	/** Allows inner compnent (conveyor) to pass status to previous family when it changes */
	public void msgGiveStatusToPrevious(Status s){
		if (previous != null){
			previous.msgStatus(s);
		} else {
			System.out.println("ConveyorFamily " + conveyorFamilyIndex + ": Didn't pass status to previous. Previous == null");
		}
	}
	@Override
	public void msgStopConveyor() {
		System.out.println("msgStopConveyor HEARD!");
		conveyor.msgNonNormStopConveyor();
	}
	@Override
	public void msgStartConveyor() {
		System.out.println("msgStartConveyor HEARD!");
		conveyor.msgNonNormStartConveyor();
	}
	@Override
	public void msgStopPopup() {
		// NA
		return;
	}
	@Override
	public void msgStartPopup() {
		// NA
		return;	
	}
	@Override
	public void msgStopOnlineMachine() {
		System.out.println("msgStopOnlineMachine HEARD!");
		conveyor.msgNonNormBreakMachine();
	}
	@Override
	public void msgStartOnlineMachine() {
		System.out.println("msgStartOnlineMachine HEARD!");
		conveyor.msgNonNormFixMachine();
	}
	@Override
	public void msgOnlineMachineExpectationFailure() {
		System.out.println("msgInlineMachineExcpectationFailue HEARD!");
		conveyor.msgNonNormInlineMachineExpectationFailure();
	}
	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		// NA
		return;	
	}
	@Override
	public void msgSetConveyorTooFull() {
		System.out.println("msgSetConveyorTooFull HEARD!");
		conveyor.msgNonNormSetConveyorFull();
	}
	@Override
	public void msgOfflineMachineExpectationFailure() {
		// NA
		return;
	}
	@Override
	public void msgOfflineMachineFixExpectationFailure() {
		// NA
		return;
	}
	@Override
	public void msgTopOfflineMachineTurnOff() {
		// NA
		return;
	}
	@Override
	public void msgBottomOfflineMachineTurnOff() {
		// NA
		return;
	}
	@Override
	public void msgTopOfflineMachineTurnOn() {
		// NA
		return;
	}
	@Override
	public void msgBottomOfflineMachineTurnOn() {
		// NA
		return;
	}
	@Override
	public void msgTopMachineBreaksGlass() {
		// NA
		return;
	}
	@Override
	public void msgBottomMachineBreaksGlass() {
		// NA
		return;
	}
	@Override
	public void msgTopMachineFixGlass() {
		// NA
		return;
	}
	@Override
	public void msgBottomMachineFixGlass() {
		// NA
		return;
	}



}
