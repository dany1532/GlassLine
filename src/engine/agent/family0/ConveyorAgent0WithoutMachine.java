package engine.agent.family0;

import shared.enums.Status;
import transducer.Transducer;

public class ConveyorAgent0WithoutMachine extends ConveyorAgent0Base {

	public ConveyorAgent0WithoutMachine(String name, Transducer t, int gci) {
		super(name, t, gci);
	}

	@Override
	public void msgNonNormBreakMachine() {
		// No Machine, nothing to break
		return;
	}

	@Override
	public void msgNonNormFixMachine() {
		// No Machine, nothing to fix
		return;
	}	
	
	@Override
	public void msgNonNormInlineMachineExpectationFailure(){
		// No Machine, nothing to fill
		return;
	}

	
	protected void sendGlassToNext(){
		
		print("Sending glass to next conveyor family");
		
		if (conveyorFull){
			conveyorFull = false;
		}
		
		// This function should only get called if myGlass is not empty. Therefore, allow it to crash otherwise.
		container.msgGiveGlassToNext(myGlass.remove(0));

		glassState = GlassState.GLASS_LOADING; 
		
	}

	protected Status getNextComponentStatus(){
		return container.nextStatus;
	}	
}
