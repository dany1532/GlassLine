package engine.agent.family0;

import shared.enums.Status;
import transducer.Transducer;

public class ConveyorAgent0WithMachine extends ConveyorAgent0Base {

	public ConveyorAgent0WithMachine(String name, Transducer t, int gci) {
		super(name, t, gci);
	}
	public void setMachine(MachineAgent0 m){
		machine = m;
	}
	
	MachineAgent0 machine;
	Status machineStatus = Status.READY;
	
	public void msgMachineStatusChanged(Status s){
		machineStatus = s;
		stateChanged();
	}
	
	@Override
	public void msgNonNormBreakMachine() {
		machine.msgBreakMachine();
	}
	@Override
	public void msgNonNormFixMachine() {
		machine.msgFixMachine();
	}
	
	@Override
	public void msgNonNormInlineMachineExpectationFailure(){
		machine.msgDontTreatNextGlass();
	}
	
	protected void sendGlassToNext(){
		
		print("Sending glass to " + machine);
		
		if (conveyorFull){
			conveyorFull = false;
		}
		
		// This function should only get called if myGlass is not empty. Therefore, allow it to crash otherwise.
		machine.msgHereIsGlass(myGlass.remove(0));

		glassState = GlassState.GLASS_LOADING; 

	}
	
	protected Status getNextComponentStatus(){
		return machineStatus;
	}
	
}
