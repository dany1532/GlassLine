package engine.agent.family2;

import engine.agent.family0.ConveyorAgent0Base.MovingState;
import engine.agent.family2.interfaces.ConveyorBelt2;
import shared.Glass;
import shared.enums.Status;
import shared.interfaces.AgentInterface;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class ConveyorBeltAgent2 extends InTheFamilyAgent2 implements ConveyorBelt2 { // implements ConveyorBelt2{

	public ConveyorBeltAgent2(String name, Transducer t,
			int myIndex) {
		super(name, t);
		this.guiIndex = myIndex;
		getTransducer().register(this, TChannel.CONVEYOR);
		startConveyor();
		

	}
	
	public void shutDown(){
		makeMeBusy();
	}
	public void makeMeBusy(){
		myStatus.setBusy();
		newMyStatus = true;
		stateChanged();
		stopConveyor();
	}

	public void startUp(){
		myStatus.setReady();
		newMyStatus = true;
		stateChanged();
		startConveyor();
	}
	
	@Override
	public void nextBecameReady(){
			startUp();
	}
	protected void startConveyor(){
		print("Starting Conveyor");
		Object[] args = new Object[1];
		args[0] = new Integer(guiIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
	}
	
	private void stopConveyor(){
		print("Stopping Conveyor");
		Object[] args = new Object[1];
		args[0] = new Integer(guiIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
}


}
