package engine.agent.family3;

import engine.agent.*;
import shared.*;
import shared.interfaces.*;
import transducer.*;

public class MachineAgent3 extends Agent implements Machine {

	enum GlassState { NEW, LOADING, LOADED, BEING_TREATED, TREATMENT_DONE, GIVEN_BACK, BROKEN, FIXING }
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
	boolean brokenGlass, brokenMachine;

	public MachineAgent3(String name, Transducer t, TChannel c, Robot r, int i){
		super(name,t);
		myChannel = c;
		transducer.register(this, myChannel);
		robot = r;
		machineIndex = i;
		brokenGlass = false;
		brokenMachine = false;
	}
	
	public void setRobot(Robot r){
		robot = r;
	}
	
	public void msgBreakGlass(){
		brokenGlass = true;
		stateChanged();
	}
	
	public void msgFixGlass(){
		currentGlass.state = GlassState.FIXING;
		stateChanged();
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
			
			if(event == TEvent.WORKSTATION_FINISHED_BREAKING_GLASS){
				print("GLASS BROKE");
				currentGlass.state = GlassState.BROKEN;
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

		if (currentGlass.state == GlassState.LOADED && !brokenGlass && !brokenMachine ){
			treatGlass();
			return true;
		}
		
		if (currentGlass.state == GlassState.LOADED && brokenGlass && !brokenMachine){
			breakGlass();
			return true;
		}
			
		if (currentGlass.state == GlassState.TREATMENT_DONE){
			releaseGlass();
			return true;
		}
		
		if (currentGlass.state == GlassState.FIXING){ //changed from BROKEN
			reportGlassBreak();
		}
			
		return false;
	}
	
	private void reportGlassBreak(){
		RobotAgent3 temp = (RobotAgent3)robot;
		temp.msgGlassBroken(this,currentGlass.glass);
		brokenGlass = false;
		currentGlass = null;
	}
	
	private void breakGlass(){
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_BREAK_GLASS, args);
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
