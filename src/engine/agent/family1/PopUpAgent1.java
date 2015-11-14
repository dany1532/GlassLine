package engine.agent.family1;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import engine.agent.Agent;
import gui.panels.subcontrolpanels.TracePanel;
import shared.Glass;
import shared.interfaces.ConveyorFamily;
import engine.agent.family1.interfaces.*;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopUpAgent1 extends Agent implements PopUp {
	Robot upRobot;
	Robot downRobot;
	MySensor mySensor;
	
	ConveyorFamily1 CF;
	
	ArrayList<MyRobot> myRobots;
	
	public enum PopUpState {raised, lowered, moving};
	public enum AnimState {loading, releasing, waitingCF, done};
	public enum SensorState {idle, awaitingResponse, waiting, sentGlass};
	public enum RobotState {idle, working, finishedGlass};
	public PopUpState myState;
	public AnimState animState;
	boolean waitingForGlass = false;
	
	boolean allRobotsBusy = false;
	public boolean carryingGlass = false;
	public boolean waitingCF2 = false;
	public boolean turnedOn = true;
	int robotWorkingCount = 0;
	Integer myIndex;
	
	
	Timer timer = new Timer(); //temp
	
	public MyGlass popUpGlass;
	
	private class MyRobot{
		Robot robot;
		RobotState robotState;
		boolean isOnline;
		
		public MyRobot(Robot rb){
			robot = rb;
			robotState = RobotState.idle;
			isOnline = true;
		}
		
	}

	private class MySensor{
		Sensor sensor;
		SensorState sensorState;
		
		public MySensor(Sensor s){
			sensor = s;
			sensorState = SensorState.idle;
		}
	}

	private class MyGlass{
		Glass glass;
		boolean needProcess;
		
		public MyGlass(Glass g){
			glass = g;
			needProcess = false;
		}
	}
	
	public PopUpAgent1(String name){
		super(name);
		
		allRobotsBusy = false;
		carryingGlass = false;
		waitingCF2 = false;
		
		myState = PopUpState.lowered;
		animState = AnimState.done;
		
		myRobots = new ArrayList<MyRobot>();
		
	}
	
	//Constructor
	public PopUpAgent1(String name, Transducer t, TracePanel p){
		super(name,t);
		
		setTracePanel(p);
		getTransducer().register(this, TChannel.POPUP);
		myIndex = 0;
		allRobotsBusy = false;
		carryingGlass = false;
		waitingCF2 = false;
		
		myState = PopUpState.lowered;
		animState = AnimState.done;
		
		myRobots = new ArrayList<MyRobot>();
		
		print("Initialized");
		
	}
	
	// *** MESSAGES ***

	/**
	 * Message from Sensor to check availability
	 */
	public void msgAreYouAvailable() {
		print("ExitSensor1 asking if Available");
		mySensor.sensorState = SensorState.awaitingResponse;
		stateChanged();
	}
	
	//Non-Norm Gui message: glass got broken robot is available
	public void msgGlassGotBroken(Robot rb){
		for(MyRobot r: myRobots){
		    if(r.robot == rb){
		    	print(r.robot+" says glass got broken");
		    	r.robotState = RobotState.idle;
		    	if(robotWorkingCount != 0)
		    		robotWorkingCount--;
		    	checkIfAllRobotsBusy();
		    	break;
		    }
		}
	}
	
// Non_norm: message that top machine stopped working
	public void msgTopMachineStoppedWorking(){
		print("Top Machine1 stopped working");
		myRobots.get(0).isOnline = false;
		if(robotWorkingCount < 2)
			robotWorkingCount++;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
//Non-Norm: message that bottom machine stopped working
	public void msgBottomMachineStoppedWorking(){
		print("BottomMachine1 stopped working");
		myRobots.get(1).isOnline = false;
		if(robotWorkingCount < 2)
			robotWorkingCount++;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
//Non_Norm: message that top machine is working again
	public void msgTopMachineStartedWorking(){
		myRobots.get(0).isOnline = true;
		if(myRobots.get(0).robotState == RobotState.idle && robotWorkingCount > 0)
			robotWorkingCount--;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
//Non_Norm: message that bottom machine is working again
	public void msgBottomMachineStartedWorking(){
		myRobots.get(1).isOnline = true;
		if(myRobots.get(1).robotState == RobotState.idle && robotWorkingCount > 0)
			robotWorkingCount--;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
	/**
	 *  Message from Client to stop the popUp
	 */
	public void msgTurnOff(){
		print("Turned Off");
		turnedOn = false;
		stateChanged();
	}
	
	/**
	 * Message from Client to turn on the popUp
	 */
	public void msgTurnOn(){
		print("Turned On");
		turnedOn = true;
		stateChanged();
	}

	/**
	 * Message of robot saying that it finished with glass
	 */
	public void msgWantToLowerGlass(Robot rb) {
		for(MyRobot r: myRobots){
		    if(r.robot == rb){
		    	print(r.robot+" wants to lower glass");
		    	r.robotState = RobotState.finishedGlass;
		    	break;
		    }
		}
		stateChanged();
	}

	/**
	 * Message from Sensor, it gives him the glass when available
	 */
	public void msgGlassSensorToPopUp(Glass g) {
		//popUpGlass.glass = g;
		print("Received glass from ExitSensor1");
		popUpGlass = new MyGlass(g);
		mySensor.sensorState = SensorState.sentGlass;
		stateChanged();
	}

	/**
	 * Message from Robot, giving requested Glass
	 */
	public void msgGlassRobotToPopUp(Glass g, Robot rb) {
		print("Received Glass from Robot");
    	
		for(MyRobot r: myRobots){
		    if(r.robot == rb){
		    	//popUpGlass.glass = g;
		    	popUpGlass = new MyGlass(g);
		    	popUpGlass.needProcess = false;
		    	r.robotState = RobotState.idle;
		    	if(robotWorkingCount != 0)
		    		robotWorkingCount--;
		    	checkIfAllRobotsBusy();
		    	stateChanged();
		    	break;
		    }
		}
	}

	/**
	 * Animation message when glass finished loading into pop up
	 */
	public void msgFinishedLoadingGlass() {
		print("Finished Loading");
		animState = AnimState.done;
		carryingGlass = true;
		waitingForGlass = false;
		stateChanged();
	}

	/**
	 * Animation Message when glass has been fully released
	 */
	public void msgFinishedReleasingGlass() {
		print("Finished releasing glass");
		//animState = AnimState.done;
		//carryingGlass = false;
		//stateChanged();
	}

	/**
	 * Animation Message when glass finished moving up
	 */
	public void msgFinishedMovingUp() {
		print("Finished moving up");
		myState = PopUpState.raised;
		stateChanged();
	}

	/**
	 * Animation message when glass finished moving down
	 */
	public void msgFinishedMovingDown() {
		print("Finished moving down");
		myState = PopUpState.lowered;
		stateChanged();
	}
	
	/**
	 * CF2 message that he is unavailable
	 */
	public void msgCF2Unavailable(){
		print("Received CF2 unavailability");
		waitingCF2 = true;
		stateChanged();
	}
	
	/**
	 * CF2 message that he is available
	 */
	public void msgCF2Available(){
		print("Received CF2 availability");
		waitingCF2 = false;
		stateChanged();
	}

	// *** SCHEDULER ***
	
	public boolean pickAndExecuteAnAction() {
		
	//Check and send message if unavailable
		if(mySensor.sensorState == SensorState.awaitingResponse){
		      sendIfUnavailable();
		      return true;
		}
		

	//do actions only if it not currently loading, releasing or waiting CF2
		if(animState == AnimState.done && turnedOn){ 
			//only if lowered
			if(myState == PopUpState.lowered){
				//only if not all robots are busy
				if(!allRobotsBusy){
					
					//Request glass from sensor
					if(!carryingGlass && mySensor.sensorState == SensorState.waiting ||
							mySensor.sensorState == SensorState.awaitingResponse ){
						requestGlassFromSensor();
						return true;
					}
		        
					//Check the recipe of glass and if it needs processing
					if(mySensor.sensorState == SensorState.sentGlass){
						checkGlassForProcessing();
						return true;
					}

					//Give the glass to the next conveyor
					if(carryingGlass && !popUpGlass.needProcess && !waitingCF2){
						giveGlassToNextConveyor();
						return true;
					}
		        
					//Raise the pop up if glass requires it
					else if(carryingGlass && popUpGlass.needProcess){
						raisePopUp();
						return true;
					}
		        
		           //Raise the pop up if robot requires it
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.finishedGlass && !carryingGlass){
							raisePopUp();
							return true;
						}
					}

				}//end allRobotsBusy

				else if(allRobotsBusy){
					//Raise the pop up if robot requires it
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.finishedGlass && !carryingGlass){
							raisePopUp();
							return true;
						}
					}
				}// end allRobotsBusy
			} //end lowered

		//only if raised
			else if(myState == PopUpState.raised){
				
				//Give glass to the robot
				if(carryingGlass && popUpGlass.needProcess){
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.idle && r.isOnline){
					    	giveGlassToRobot(r);
					    	return true;
					    }
					}
				}
				
				//Lower the pop up to give to next conveyor
				if(carryingGlass && !popUpGlass.needProcess){
					lowerPopUp();
					return true;
				}
				
				//lower the pop up, sensor requires it
				if(mySensor.sensorState == SensorState.waiting &&
						//!waitingForGlass &&
						 !carryingGlass && !allRobotsBusy){
						lowerPopUp();
						return true;
					}
		      
			//Request glass from robot
				for(MyRobot r: myRobots){
				    if(r.robotState == RobotState.finishedGlass && !carryingGlass){
				    	requestFinishedGlass(r);
				    	return true;
				    }
				}
				
				


			} //end of State.raised


		}//end of animationState
		
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
	}

	/**
	 * Request finished glass from Robot
	 * @param r
	 */
	private void requestFinishedGlass(MyRobot r) {
		print("Requesting Glass from Robot");
		animState = AnimState.loading;
		r.robotState = RobotState.idle;
		r.robot.msgPopupReadyForGlass();
		stateChanged();
	}

	/**
	 * Lowers the popUp
	 */
	private void lowerPopUp() {
		print("lowering PopUp");
		myState = PopUpState.moving;
		doMoveDown(); //animation
		stateChanged();		
	}

	/**
	 * Gives Glass to robot, increases the working robots and checks if all busy
	 * @param r
	 */
	private void giveGlassToRobot(MyRobot r) {
		print("Giving Glass to Robot");
		//animState = AnimState.releasing;
		carryingGlass = false;
		r.robotState = RobotState.working;
		r.robot.msgGlassPopupToRobot(popUpGlass.glass); //will send the glass
		robotWorkingCount++;
		checkIfAllRobotsBusy();
		stateChanged();
		
	}

	/**
	 * Raises the pop up
	 */
	private void raisePopUp() {
		print("Raising PopUp");
		myState = PopUpState.moving;
		doMoveUp(); //animation
		stateChanged();
	}

	/**
	 * Releases glass to next Conveyor family
	 */
	private void giveGlassToNextConveyor() {
		print("GLASS JUST LEFT SECTION 2 OF GLASSLINE (Dany's part)");
		carryingGlass = false;
		//animState = AnimState.releasing;
		CF.msgGiveGlassToNext(popUpGlass.glass);
		doReleaseGlass(); //animation
		stateChanged();
		
	}

	/**
	 * Checks if the glass need to be processed
	 */
	private void checkGlassForProcessing() {
		mySensor.sensorState = SensorState.idle;
		if(popUpGlass.glass.getRecipe().doesMachineNeedToDoJob(3)){
			popUpGlass.needProcess = true;
			print("Glass needs processing");
		}
		else
			print("Glass doesn't need processing");
		
		stateChanged();
		
	}

	/**
	 * Tells sensor that it is available and it requests the glass
	 */
	private void requestGlassFromSensor() {
		print("Requesting Glass from sensorExit1");
		animState = AnimState.loading;
		mySensor.sensorState = SensorState.idle;
		mySensor.sensor.msgAvailable();
		stateChanged();
		
	}

	//Check if pop up is unavailable and tell the sensor
	private void sendIfUnavailable() {
		if(myState != PopUpState.lowered || carryingGlass || waitingCF2 || !turnedOn || allRobotsBusy){
			print("not available");
		      mySensor.sensorState = SensorState.waiting;
		      mySensor.sensor.msgUnavailable();
		   }
		mySensor.sensorState = SensorState.waiting;
		stateChanged();
		
	}
	
//check if all robots are busy
	private void checkIfAllRobotsBusy(){
		print("Robot Working Count is: "+robotWorkingCount);
		if(robotWorkingCount == 2){
			allRobotsBusy = true;
		}
		else
			allRobotsBusy = false;
	}
	
//animation: move up
	private void doMoveUp(){
		Integer[] args = new Integer[1];
		args[0] = myIndex;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
	}
	
//animation: move down
	private void doMoveDown(){
		Integer[] args = new Integer[1];
		args[0] = myIndex;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
	}
	
//animation: release glass
	private void doReleaseGlass(){
		Integer[] args = new Integer[1];
		args[0] = myIndex;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args);
	}
	

		 
	
/*//Simulating robot agent (temp)
	private void doLoadRobot(){
		print("Giving Glass to Robot");
		animState = AnimState.releasing;
		Integer[] newArgs = new Integer[1];
		newArgs[0] = 1;
		getTransducer().fireEvent(TChannel.DRILL, TEvent.WORKSTATION_DO_LOAD_GLASS, newArgs);
		stateChanged();
	} */
	
/*
	private void doRequestGlass(final PopUpAgent1 pop){
		print("Requesting Glass from Robot");
		myRobots.get(0).robotState = RobotState.idle;
		timer.schedule(new TimerTask(){
		    public void run(){//this routine is like a message reception 
		    	print("Robot sending glass");
		    	doReleaseRobot();
		    	pop.msgGlassRobotToPopUp(pop.popUpGlass.glass, null);
		    	stateChanged();
		    }
		}, 1000);
	} */
	
	/*//Simulating robot agent (temp)
		private void doReleaseRobot(){
			Integer[] newArgs = new Integer[1];
			newArgs[0] = 1;
			getTransducer().fireEvent(TChannel.DRILL, TEvent.WORKSTATION_RELEASE_GLASS, newArgs);
			stateChanged();
		} */
	
	
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(myIndex == (Integer) args[0]){
	
			if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_LOAD_FINISHED)
			{
				msgFinishedLoadingGlass();
			}
		
			else if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_MOVED_UP)
			{
				msgFinishedMovingUp();
			}
		
			else if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_MOVED_DOWN)
			{
				msgFinishedMovingDown();
			}
			
			else if (channel == TChannel.POPUP && event == TEvent.POPUP_GUI_RELEASE_FINISHED){
				msgFinishedReleasingGlass();
			}
			
			
		}
		
		/*if (channel == TChannel.DRILL && event == TEvent.WORKSTATION_LOAD_FINISHED)
		{
			msgFinishedReleasingGlass();
			getTransducer().fireEvent(TChannel.DRILL, TEvent.WORKSTATION_DO_ACTION, args);
		}
		
		
		else if (channel == TChannel.DRILL && event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
		{
			//getTransducer().fireEvent(TChannel.DRILL, TEvent.WORKSTATION_RELEASE_GLASS, args);
			this.msgWantToLowerGlass(null);

		}*/
	}
	
	public SensorState sendSensorState(){
		return mySensor.sensorState;
	}
	
	public void setSensor(Sensor s){
		mySensor = new MySensor(s);
	}
	
	public void setRobot(Robot rb){
		myRobots.add(new MyRobot(rb));
	}
	
	public void setConveyorFamily(ConveyorFamily1 cf){
		CF = cf;
	}
	
	public boolean doesGlassNeedProcess(){
		return popUpGlass.needProcess;
	}
	
	public void setDoesNotNeedProcessing(){
		popUpGlass.needProcess = false;
	}
	
	public void setRobotToFinished(){
		myRobots.get(0).robotState = RobotState.finishedGlass;
	}
	
	public RobotState getRobotState(Robot rb){
		for(MyRobot r: myRobots){
		    if(r.robot == rb){
		    	return r.robotState;
		    }
		}
		return null;
	}
	
	public void setRobotStateToWorking(Robot rb){
		for(MyRobot r: myRobots){
		    if(r.robot == rb){
		    	r.robotState = RobotState.working;
		    }
		}
	}
	
	public void setPopUpToRaised(){
		myState = PopUpState.raised;
	}
	
	public void tracePrint(String msg){
		printTrace(msg);
	}



}
