package engine.agent.family0;

import engine.agent.Agent;
import shared.Glass;
import shared.enums.Status;
import transducer.*;


public class MachineAgent0 extends Agent {

	enum GlassState { NEW, LOADING, LOADED, BEING_TREATED, TREATMENT_DONE, GIVEN_BACK }
	class MyGlass {
		Glass glass;
		GlassState state;
		
		public MyGlass(Glass g){
			glass = g;
			state = GlassState.NEW;
		}
	}

	ConveyorFamily0 container;
	ConveyorAgent0WithMachine conveyor;
	MyGlass currentGlass;
	TChannel myChannel;
	shared.enums.Status myStatus;
	
	int machineIndex;
	boolean dontTreatNextGlass;
	boolean machineBroken;
	
	public MachineAgent0(String name, Transducer t, TChannel c, int mi){
		super(name,t);
		myChannel = c;
		transducer.register(this, myChannel);
		myStatus = Status.READY;
		machineIndex = mi;
		dontTreatNextGlass = false;
		machineBroken = false;
	}
	
	public void setConveyorFamily(ConveyorFamily0 c){
		container = c;
	}
	public void setConveyor(ConveyorAgent0WithMachine c){
		conveyor = c;
	}
	
	public void msgHereIsGlass(Glass g) {
		if (currentGlass != null) {
			System.err.println("Machine already has glass. Bad");
		}
		currentGlass = new MyGlass(g);
		myStatus = shared.enums.Status.BUSY;
		conveyor.msgMachineStatusChanged(myStatus);
		stateChanged();
	}
	
	public void msgNextStatusChanged(){
		//make gui conveyor stop (dany)
		stateChanged();
	}
	
	public void msgDontTreatNextGlass(){
		// set a bool to not treat the next piece of glass
		dontTreatNextGlass = true;
		stateChanged();
	}
	
	public void msgBreakMachine(){
		machineBroken = true;
		stateChanged();
	}
	
	public void msgFixMachine(){
		machineBroken = false;
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		
		if (channel == myChannel){
			
			if (event == TEvent.WORKSTATION_LOAD_FINISHED){
				if (currentGlass.state != GlassState.LOADING) {
					System.err.println("Load finished mismatch. state = " + currentGlass.state);
				}
				currentGlass.state = GlassState.LOADED;
				stateChanged();
			}
			
		
			if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED){
				if (currentGlass.state != GlassState.BEING_TREATED) {
					System.err.println("Action finished mismatch. state = " + currentGlass.state);
				}
				currentGlass.state = GlassState.TREATMENT_DONE;
				stateChanged();
			}
			
			
			if (event == TEvent.WORKSTATION_RELEASE_FINISHED){
				if (currentGlass.state != GlassState.GIVEN_BACK) {
					System.err.println("Release finished mismatch. state = " + currentGlass.state);
				}
				currentGlass = null;
				myStatus = shared.enums.Status.READY;
				conveyor.msgMachineStatusChanged(myStatus);
				stateChanged();
			}
			
	
		}
	}
	
	
	@Override
	public boolean pickAndExecuteAnAction() {
		if (currentGlass == null){
			return false; 
		}
			
		if (currentGlass.state == GlassState.NEW){
			loadGlass();
			return true;
		}

		if (currentGlass.state == GlassState.LOADED){
			
			// check if machine is broke
			if (machineBroken){
				System.out.println("Machine is Broken... please fix!!");
				return false;
			}
			
			// non norm, expectation failure
			if (dontTreatNextGlass){
				dontTreatNextGlass = false;
				releaseGlass();
				return true;
			}
			
			// current glass does not need treatment
			if (currentGlass.glass.getRecipe().recipe[machineIndex] == false){
				releaseGlass();
				return true;
			}
			
			
			// otherwise, treat glass
			treatGlass();
			return true;
		}
			
		if (currentGlass.state == GlassState.TREATMENT_DONE && container.nextStatus == Status.READY){
			releaseGlass();
			return true;
		}
			
		return false;
	}
	
	private void loadGlass(){
		print("I'm loading glass");
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_LOAD_GLASS, null);
		currentGlass.state = GlassState.LOADING;
	}
	
	private void treatGlass(){
		print("I'm treating glass");
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_ACTION, null);
		currentGlass.state = GlassState.BEING_TREATED;
	}
	
	private void releaseGlass(){
		print("Sending glass to next conveyor family");
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		currentGlass.state = GlassState.GIVEN_BACK;
		container.msgGiveGlassToNext(currentGlass.glass);
	}

	
}
