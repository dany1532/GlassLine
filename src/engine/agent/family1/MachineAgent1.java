package engine.agent.family1;

import engine.agent.*;
import shared.*;
import shared.interfaces.*;
import transducer.*;

public class MachineAgent1 extends Agent implements Machine {

	enum GlassState { NEW, LOADING, LOADED, BEING_TREATED, BEING_BROKEN, BROKEN, TREATMENT_DONE, GIVEN_BACK }
	class MyGlass {
		Glass glass;
		GlassState state;
		
		public MyGlass(Glass g){
			glass = g;
			state = GlassState.NEW;
		}
	}

	Robot robot;
	MyGlass currentGlass;// = new MyGlass(null); //Kevin added
	TChannel myChannel;
	Integer machineIndex;
	boolean canRelease;
	boolean turnedOn; //For NON-Norm: works on only 0,1,2 sides
	boolean malfunction; //For NON-Norm: workstation doesn't treat glass
	boolean breakGlass; //For Non-Norm: break glass (it fixes itself when it breaks)
	//boolean broken;

	public MachineAgent1(String name, Transducer t, TChannel c, Robot r, Integer index){
		super(name,t);
		myChannel = c;
		transducer.register(this, myChannel);
		robot = r;
		machineIndex = index;
		turnedOn = true;
		malfunction = false;
		print("Initialized");
	}
	
	public void setRobot(Robot r){
		robot = r;
	}
	
	/**
	 * Message Robot sends glass to machine
	 */
	@Override
	public void msgGlassRobotToMachine(Glass g) {
		if (currentGlass != null) {
			System.err.println("Already have glass. Bad");
		}
		print("Received Glass from Robot");
		currentGlass = new MyGlass(g);
		stateChanged();
	}
	
	//Robot message, can do release glass animation
	public void msgPermissionToReleaseGlass(){
		print("Received Permission to release Glass");
		canRelease = true;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: Break Glass (fixes itself)
	 */
	public void msgBreakGlass(){
		print("Set to break glass");
		breakGlass = true;
		stateChanged();
	}
	
   /**
    * For Non_Norm: fix break glass state
    */
	public void msgFixBreakGlassStatus(){
		print("Machine is Fixed: won't break glass");
		breakGlass = false;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: only works 0,1,2 sides
	 * msg to turn on machine
	 */
	public void msgTurnOn(){
		print("Turning On");
		turnedOn = true;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: only works 0,1,2 sides
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
		print("is malfunctioning");
		malfunction = true;
		stateChanged();
	}
	
	/**
	 * For Non_Norm: machine doesn't treat glass
	 * msg to repair the machine malfunction
	 */
	public void msgMachineMalfunctionFixed(){
		print("is now working");
		malfunction = false;
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
				print("loadedFinished");
				currentGlass.state = GlassState.LOADED;
				stateChanged();
			}
			
			if(event == TEvent.WORKSTATION_FINISHED_BREAKING_GLASS){
				print("Glass is Broken");
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
				/*if (currentGlass.state != GlassState.GIVEN_BACK) {
					System.err.println("Release finished mismatch");
				}*/
				print("Released Glass");
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

		if(turnedOn){
			if (currentGlass.state == GlassState.LOADED && !malfunction && !breakGlass){
				treatGlass();
				return true;
			}
			
			if(currentGlass.state == GlassState.LOADED && !malfunction && breakGlass){
				breakGlass();
				return true;
			}
			
			if(currentGlass.state == GlassState.BROKEN){
				tellRobotToClearGlass();
				return true;
			}
				
			if (currentGlass.state == GlassState.TREATMENT_DONE){
				releaseGlass();
				return true;
			}
			
			if(canRelease){
				doReleaseAnimation();
				return true;
			}
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
	
	/**
	 * Makes GUI break the glass for several seconds
	 */
	private void breakGlass(){
		print("Breaking Glass");
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_DO_BREAK_GLASS, args);
		currentGlass.state = GlassState.BEING_BROKEN;
	}
	
	private void releaseGlass(){
		print("Glass Is Ready");
		currentGlass.state = GlassState.GIVEN_BACK;
		robot.msgGlassMachineToRobot(this,currentGlass.glass);
	}
	
	private void tellRobotToClearGlass(){
		print("Telling Robot to Clear Glass");
		//breakGlass = false;
		robot.msgGlassGotBroken(this,currentGlass.glass);
		currentGlass = null;
	}
	
	private void doReleaseAnimation(){
		print("Releasing Glass");
		currentGlass = null;
		canRelease = false;
		Object[] args = new Object[1];
		args[0] = new Integer(machineIndex);
		transducer.fireEvent(myChannel, TEvent.WORKSTATION_RELEASE_GLASS, args);
	}
	public void tracePrint(String msg){
		printTrace(msg);
	}
}
