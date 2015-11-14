package engine.agent.family4;

import engine.agent.*;
import shared.enums.Status;
import shared.Glass;
import transducer.*;

public class MachineAgent4 extends Agent {

	enum GlassState { NEW, LOADING, LOADED, BEING_TREATED, BEING_BROKEN, BROKEN, TREATMENT_DONE, GIVEN_BACK }
	class MyGlass {
		Glass glass;
		GlassState state;
		
		public MyGlass(Glass g){
			glass = g;
			state = GlassState.NEW;
		}
	}

	ConveyorFamily4 conveyorFamily;
	ConveyorAgent4 conveyor;
	MyGlass currentGlass;
	TChannel myChannel;
	int machineIndex;
	shared.enums.Status myStatus;
	boolean canRelease;
	boolean turnedOn; //For NON-Norm
	boolean malfunction; //For NON-Norm: workstation doesn't treat glass
	boolean breakGlass; //For Non-Norm: break glass (it fixes itself when it breaks)

	public MachineAgent4(String name, ConveyorAgent4 cA4, int index, Transducer t, TChannel c){
		super(name,t);
		conveyor = cA4;
		machineIndex = index;
		myChannel = c;
		transducer.register(this, myChannel);
		myStatus = Status.READY;
		turnedOn = true;
		malfunction = false;
	}
	
	public void setConveyorFamily(ConveyorFamily4 c){
		conveyorFamily = c;
	}
	public void setConveyor(ConveyorAgent4 c){
		conveyor = c;
	}
	
	public void msgHereIsGlass(Glass g) {
		if (currentGlass != null) {
			print("Received msgHereIsGlass for " + this.name + " with glass crash into another glass");
		}
		currentGlass = new MyGlass(g);
		myStatus = shared.enums.Status.BUSY;
		conveyor.msgStatus(myStatus);
		stateChanged();
	}
	
	public void msgNextStatusChanged(){
		stateChanged();
	}
	
	/**
	 * For Non_Norm: Break Machine
	 */
	public void msgBreakMachine(){
		print("Machine broken.");
		breakGlass = true;
		stateChanged();
	}
	
	public void msgFixMachine(){
		print("Machine fixed.");
		breakGlass = false;
		stateChanged();
	}
	
	/**
	 * For Non_Norm
	 * msg to turn on machine
	 */
	public void msgTurnOn(){
		print("Turning On");
		turnedOn = true;
		stateChanged();
	}
	
	/**
	 * For Non_Norm
	 * msg to turn off machine
	 */
	public void msgTurnOff(){
		print("Turning Off");
		turnedOn = false;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: machine doesn't treat glass
	 * msg to make it malfunction
	 */
	public void msgMachineMalfunction(){
		print("Malfunctioning");
		malfunction = true;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: machine doesn't treat glass
	 * msg to repair the machine malfunction
	 */
	public void msgMachineMalfunctionFixed(){
		print("Is now working");
		malfunction = false;
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		
		if (channel == myChannel && currentGlass != null){
			
			if (event == TEvent.WORKSTATION_LOAD_FINISHED){
				if (currentGlass.state != GlassState.LOADING) {
					System.err.println("Load finished mismatch");
				}
				currentGlass.state = GlassState.LOADED;
				stateChanged();
			}
			
			if(event == TEvent.WORKSTATION_FINISHED_BREAKING_GLASS){
				print("Glass is Broken");
				currentGlass.state = GlassState.BROKEN;
				myStatus = Status.READY;
				conveyor.msgStatus(myStatus);
				stateChanged();
			}
			
		
			if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED){
				if (currentGlass.state != GlassState.BEING_TREATED) {
					System.err.println("Action finished mismatch");
				}
				currentGlass.state = GlassState.TREATMENT_DONE;
				stateChanged();
			}
			
			
			if (event == TEvent.WORKSTATION_RELEASE_FINISHED){
				if (currentGlass.state != GlassState.GIVEN_BACK) {
					System.err.println("Release finished mismatch");
				}
				currentGlass = null;
				myStatus = Status.READY;
				conveyor.msgStatus(myStatus);
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

		if(turnedOn){
			if (currentGlass.state == GlassState.LOADED && !malfunction && !breakGlass){
				// current glass does not need treatment
				if (currentGlass.glass.getRecipe().recipe[machineIndex] == false){
					releaseGlass();
					return true;
				}
				
				treatGlass();
				return true;
			}
			
			if(currentGlass.state == GlassState.LOADED && !malfunction && breakGlass){
				breakGlass();
				return true;
			}
			
			if(currentGlass.state == GlassState.BROKEN){
				clearGlass();
				return true;
			}
				
			if (currentGlass.state == GlassState.TREATMENT_DONE && conveyorFamily.conveyorFamilyAfterStatus == Status.READY){
				releaseGlass();
				return true;
			}
			
			if(canRelease){
				doReleaseAnimation();
				return true;
			}
		}
		else{
			print("Turn Off");
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
	
	private void breakGlass(){
		print("Breaking Glass");
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_BREAK_GLASS, null);
		currentGlass.state = GlassState.BEING_BROKEN;
	}
	
	private void releaseGlass(){
		print("Sending glass to next conveyor family");
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, null);
		currentGlass.state = GlassState.GIVEN_BACK;
		conveyorFamily.msgGlassFamilyToFamily(currentGlass.glass);
	}
	
	private void clearGlass(){
		print("Clear Glass");
		currentGlass = null;
	}
	
	private void doReleaseAnimation(){
		print("Releasing Glass");
		currentGlass = null;
		canRelease = false;
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, null);
	}

	
}
