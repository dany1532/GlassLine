package engine.agent.family2;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import engine.agent.Agent;
import gui.panels.subcontrolpanels.TracePanel;
import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import engine.agent.family2.interfaces.*;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopupAgent2 extends InTheFamilyAgent2 implements Popup2 {
	Robot upRobot;
	Robot downRobot;
	MySensor2 mySensor2;
	
	ConveyorFamilyAgent2 CF;
	
	ArrayList<MyRobot> myRobots;
	
	public enum PopUpState {raised, lowered, moving};
	public enum AnimState {loading, releasing, waitingCF, done};
	public enum Sensor2State {idle, awaitingResponse, waiting, sentGlass};
	public enum RobotState {idle, working, finishedGlass};
	public PopUpState myState;
	public AnimState animState;
	boolean waitingForGlass = false;
	
	boolean allRobotsBusy = false;
	public boolean carryingGlass = false;
	public boolean waitingCF3 = false;
	public boolean turnedOn = true;
	public boolean expecting = true;
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

	private class MySensor2{
		Sensor2 sensor;
		Sensor2State sensorState;
		
		public MySensor2(Sensor2 s){
			sensor = s;
			sensorState = Sensor2State.idle;
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
	
	public PopupAgent2(String name, Transducer t){
		super(name, t);
		
		allRobotsBusy = false;
		carryingGlass = false;
		waitingCF3 = false;
		
		myState = PopUpState.lowered;
		animState = AnimState.done;
		
		myRobots = new ArrayList<MyRobot>();
		
	}
	
	public PopupAgent2(String name, Transducer t, TracePanel p, int index){//Kevin added int
		super(name,t);
		
		setTracePanel(p);
		getTransducer().register(this, TChannel.POPUP);
		myIndex = index; //kevin changed from 1
		allRobotsBusy = false;
		carryingGlass = false;
		waitingCF3 = false;
		
		myState = PopUpState.lowered;
		animState = AnimState.done;
		
		myRobots = new ArrayList<MyRobot>();
		
		print("Initialized");
		
	}
	
	// *** MESSAGES ***

	/**
	 * Message from Sensor2 to check availability
	 */
	public void msgAreYouAvailable() {
		print("ExitSensor asking if available");
		mySensor2.sensorState = Sensor2State.awaitingResponse;
		stateChanged();
	}
	
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
	public void msgTurnTopMachineOff(){
		print("Telling top machine (robot) it stopped working");
		//myRobots.get(0).msgStopWorking();//Kevin
		myRobots.get(0).isOnline = false;
		if(robotWorkingCount < 2)
			robotWorkingCount++;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
	public void msgTurnBottomMachineOff(){
		print("Telling top machine (robot) it stopped working");
		myRobots.get(1).isOnline = false;
		if(robotWorkingCount < 2)
			robotWorkingCount++;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
	public void msgTurnTopMachineOn(){
		myRobots.get(0).isOnline = true;
		if(myRobots.get(0).robotState == RobotState.idle && robotWorkingCount < 0)
			robotWorkingCount--;
		checkIfAllRobotsBusy();
		stateChanged();
	}
	
	public void msgTurnBottomMachineOn(){
		myRobots.get(1).isOnline = true;
		if(myRobots.get(1).robotState == RobotState.idle && robotWorkingCount < 0)
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
			thingBehind.msgStatusFromAhead(new KStatus2(Status.BUSY));
		stateChanged();
	}
	
	/**
	 * Message from Client to turn on the popUp
	 */
	public void msgTurnOn(){
		print("Turned On");
		turnedOn = true;
			thingBehind.msgStatusFromAhead(new KStatus2(Status.READY));
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
	 * Message from Sensor2, it gives him the glass when available
	 */
	@Override
	public void msgHereIsGlass(Glass g) {
		print("Received glass from ExitSensor2 and was READY(?)");
		popUpGlass = new MyGlass(g);
		mySensor2.sensorState = Sensor2State.sentGlass;
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

	
	public void msgFinishedMovingDown() {
		print("Finished moving down");
		myState = PopUpState.lowered;
		stateChanged();
	}
	
	public void msgCF3Unavailable(){
		print("Received CF3 unavailability");
		waitingCF3 = true;
		stateChanged();
	}
	
	public void msgCF3Available(){
		print("Received CF3 availability");
		waitingCF3 = false;
		stateChanged();
	}

	// *** SCHEDULER ***
	
	public boolean pickAndExecuteAnAction() {
		if(mySensor2.sensorState == Sensor2State.awaitingResponse){
		      sendIfUnavailable();
		      return true;
		}
		

	//do actions only if it not currently loading, releasing or waiting CF3
		if(animState == AnimState.done && turnedOn){ 
			if(myState == PopUpState.lowered){
				if(!allRobotsBusy){
					if(!carryingGlass && mySensor2.sensorState == Sensor2State.waiting ||
							mySensor2.sensorState == Sensor2State.awaitingResponse ){
						requestGlassFromSensor2();
						return true;
					}
		        
					if(mySensor2.sensorState == Sensor2State.sentGlass){
						checkGlassForProcessing();
						return true;
					}

					System.out.println(carryingGlass);
					if(carryingGlass && !popUpGlass.needProcess && !waitingCF3){
						giveGlassToNextConveyor();
						return true;
					}
		        

					else if(carryingGlass && popUpGlass.needProcess){
						raisePopUp();
						return true;
					}
		        
		        
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.finishedGlass && !carryingGlass){
							raisePopUp();
							return true;
						}
					}

				}//end allRobotsBusy

				else if(allRobotsBusy){
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.finishedGlass && !carryingGlass){
							raisePopUp();
							return true;
						}
					}
				}// end allRobotsBusy
			} //end lowered

			else if(myState == PopUpState.raised){
				
				if(carryingGlass && popUpGlass.needProcess){
					for(MyRobot r: myRobots){
						if(r.robotState == RobotState.idle && r.isOnline){
					    	giveGlassToRobot(r);
					    	return true;
					    }
					}
				}
				if(carryingGlass && !popUpGlass.needProcess){
					lowerPopUp();
					return true;
				}
				
				if(mySensor2.sensorState == Sensor2State.waiting &&
						//!waitingForGlass &&
						 !carryingGlass && !allRobotsBusy){
						lowerPopUp();
						return true;
					}
		      
			//yay
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
		thingBehind.msgStatusFromAhead(new KStatus2(Status.BUSY)); //JKevin
		stateChanged();
	}

	/**
	 * Releases glass to next Conveyor family
	 */
	private void giveGlassToNextConveyor() {
		print("GLASS JUST LEFT SECTION 2 OF GLASSLINE (Kevin's part)");
		carryingGlass = false;
		//animState = AnimState.releasing;
		CF.msgGiveGlassToNext(popUpGlass.glass);//TODO
		thingBehind.msgStatusFromAhead(new KStatus2(Status.READY)); //JKevin
		doReleaseGlass(); //animation
		stateChanged();
		
	}

	/**
	 * Checks if the glass need to be processed
	 */
	private void checkGlassForProcessing() {
		mySensor2.sensorState = Sensor2State.idle;
		if(popUpGlass.glass.getRecipe().doesMachineNeedToDoJob(4) && expecting){ //Kevin added expecting
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
	private void requestGlassFromSensor2() {//TODO
		print("Requesting Glass from sensorExit1; Doesnt actually do anything");
		animState = AnimState.loading;
		mySensor2.sensorState = Sensor2State.idle;
		//mySensor2.sensor.msg
		stateChanged();
		
	}

	private void sendIfUnavailable() {
		if(myState != PopUpState.lowered || carryingGlass || waitingCF3 || !turnedOn){
			print("not available");
		      mySensor2.sensorState = Sensor2State.waiting;
		      mySensor2.sensor.msgStatusFromAhead(new KStatus2(Status.BUSY)); //Kevin changed this
		   }
		mySensor2.sensorState = Sensor2State.waiting;
		stateChanged();
		
	}
	
	private void checkIfAllRobotsBusy(){
		if(robotWorkingCount == 2){
			allRobotsBusy = true;
		}
		else
			allRobotsBusy = false;
	}
	
	private void doMoveUp(){
		Integer[] args = new Integer[1];
		args[0] = myIndex;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
	}
	
	private void doMoveDown(){
		Integer[] args = new Integer[1];
		args[0] = myIndex;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
	}
	
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
	
	public Sensor2State sendSensor2State(){
		return mySensor2.sensorState;
	}
	
	public void setSensor2(Sensor2 s){
		mySensor2 = new MySensor2(s);
	}
	
	public void setRobot(Robot rb){
		myRobots.add(new MyRobot(rb));
	}
	
	public void setConveyorFamily(ConveyorFamilyAgent2 cf){
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

	@Override
	public void msgGlassSensorToPopUp(Glass g) {
		// TODO Auto-generated method stub
		
	}

	public void setExpecting(boolean b) {
		expecting = b;
	}

	public void msgMachineBreaksGlass(String machine, boolean b) {
		if (machine == "top"){
			
		}
		
	}



}
