package shared.agents;

import engine.agent.*;
import shared.*;
import shared.interfaces.*;
import transducer.*;

public class MachineAgent extends Agent implements Machine {

	enum GlassState { NEW, LOADING, LOADED, BEING_TREATED, TREATMENT_DONE, GIVEN_BACK }
	class MyGlass {
		Glass glass;
		GlassState state;
		
		public MyGlass(Glass g){
			glass = g;
			state = GlassState.NEW;
		}
	}

	Robot robot;
	MyGlass currentGlass;
	TChannel myChannel;
	int machineIndex;

	public MachineAgent(String name, Transducer t, TChannel c, Robot r){
		super(name,t);
		myChannel = c;
		transducer.register(this, myChannel);
		robot = r;
	}
	
	public void setRobot(Robot r){
		robot = r;
	}
	
	@Override
	public void msgGlassRobotToMachine(Glass g) {
		if (currentGlass != null) {
			System.err.println("Already have glass. Bad");
		}
		currentGlass = new MyGlass(g);
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		
		if (channel == myChannel){
			
			int destinationIndex = ((Integer)args[0]).intValue();
			
			if (destinationIndex != machineIndex){
				return;
			}
			
			if (event == TEvent.WORKSTATION_LOAD_FINISHED){
				if (currentGlass.state != GlassState.LOADING) {
					System.err.println("Load finished mismatch");
				}
				currentGlass.state = GlassState.LOADED;
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
				stateChanged();
			}
			
	
		}
	}
	
	
	@Override
	public boolean pickAndExecuteAnAction() {
		if (currentGlass == null)
			return false;
			
		if (currentGlass.state == GlassState.NEW){
			loadGlass();
			return true;
		}

		if (currentGlass.state == GlassState.LOADED){
			treatGlass();
			return true;
		}
			
		if (currentGlass.state == GlassState.TREATMENT_DONE){
			releaseGlass();
			return true;
		}
			
		return false;
	}
	
	private void loadGlass(){
		print("I'm loading glass");
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_LOAD_GLASS, args);
		currentGlass.state = GlassState.LOADING;
	}
	
	private void treatGlass(){
		print("I'm treating glass");
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_ACTION, args);
		currentGlass.state = GlassState.BEING_TREATED;
	}
	
	private void releaseGlass(){
		print("I'm releasing glass to " + robot);
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, args);
		currentGlass.state = GlassState.GIVEN_BACK;
		robot.msgGlassMachineToRobot(this,currentGlass.glass);
	}

	//Ignore, used for Dany code...
	@Override
	public void msgPermissionToReleaseGlass() {
		// TODO Auto-generated method stub
		
	}



}
