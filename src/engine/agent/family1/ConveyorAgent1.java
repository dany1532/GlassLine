package engine.agent.family1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shared.Glass;
import shared.enums.*;
import engine.agent.family1.interfaces.*;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import gui.panels.subcontrolpanels.TracePanel;

public class ConveyorAgent1 extends Agent implements Conveyor {
	public boolean isOn;
	public enum State {operational, notOperational};
	public State myState;
	public List <ConveyorGlass> myGlasses;
	//Machine pastMachine;
	public ConveyorFamily1 CF;
	public Integer myIndex;
	//PopUp pastPopUp;
	Sensor exitSensor;
	//enum GlassState {movingInConveyor, reachedSensor};

	private class ConveyorGlass{
		Glass glass;
		boolean reachedSensor;
		
		public ConveyorGlass(Glass g){
			glass = g;
			reachedSensor = false;
		}
		
		public boolean getReachedSensor(){
			return reachedSensor;
		}
	}
	
	public ConveyorAgent1 (String name){
		super(name);
		myState = State.operational;
		myGlasses = Collections.synchronizedList(new ArrayList<ConveyorGlass>());
		isOn = true;
	}
	
	//Constructor
	public ConveyorAgent1 (String name, Transducer t, TracePanel p, Integer index){
		super(name,t);

		myIndex = index;
		setTracePanel(p);
		getTransducer().register(this, TChannel.CONVEYOR);
		myState = State.operational;
		myGlasses = Collections.synchronizedList(new ArrayList<ConveyorGlass>());
		isOn = true;
		doStartConveyor();
		print("Initialized");
	}
	
	// *** MESSAGES ***
	
   //Message from entrance sensor
	public void msgHereIsGlass(Glass g) {
		print("Received Glass");
		myGlasses.add(new ConveyorGlass(g));
		stateChanged();
	}

  //Message from exit sensor/GUI to stop conveyor
	public void msgPleaseWait() {
		isOn = false;
		stateChanged();
	}

  //Message from exit sensor/GUI to start conveyor
	public void msgPermissionToMove() {
		isOn = true;
		stateChanged();
	}

/**
 * Message from exitSensor, requesting the glass that has
 * reached the exit sensor
 */
	public void msgRequestingGlass() {
		print("Received Exit Sensor request");
		synchronized(myGlasses){
			myGlasses.get(0).reachedSensor = true;
			stateChanged();
		}
	}
	
	// *** SCHEDULER ***
	
	public boolean pickAndExecuteAnAction() {
		
	//Conveyor is turn off, tell previous conveyor family
		if(isOn == false && myState == State.operational){
			tellOthersIHaveStopped();
			return true;
		}
		
	//Conveyor is turn on, tell previous conveyor family
		else if( isOn == true && myState == State.notOperational){
			tellOthersIAmOperational();
			return true;
		}
		
	//Conveyor is running, when glass reaches sensor send glass to agent
		else if(myState == State.operational){
			synchronized(myGlasses){
				for(ConveyorGlass g: myGlasses){
				    if(g.reachedSensor == true){
				    	sendGlassToSensor(g);
				    	return true;
				    } 
				}
			}
		}
			
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
	}
	
	// *** ACTIONS ***

	/**
	 * Gives glass to exit sensor and removes glass from list
	 * @param g
	 */
	private void sendGlassToSensor(ConveyorGlass g) {
		print("Sending Glass to exitSensor");
		exitSensor.msgHereIsGlassFromConveyor1(g.glass);
	    myGlasses.remove(g);
	    stateChanged();
	}

	/**
	 * Sends message to CF that conveyor is running. Give status to previous
	 */
	private void tellOthersIAmOperational() {
		print("Telling CF I am operational");
		doStartConveyor();
		myState = State.operational;
		//Status s = new Status();
		//s.setReady();
		CF.msgGiveStatusToPrevious(Status.READY);
		stateChanged();
	}

	/**
	 * Sends message to CF that conveyor stop. Give status to previous
	 */
	private void tellOthersIHaveStopped() {
		print("Telling CF I am stopping");
		doStopConveyor();
		myState = State.notOperational;
		//Status s = new Status();
		//s.setBusy();
		CF.msgGiveStatusToPrevious(Status.BUSY);
		stateChanged();
		
	}
	
	/**
	 * GUI Animation start conveyor
	 */
	private void doStartConveyor(){
		print("Starting Conveyor");
		Object[] args = new Object[1];
		args[0] = new Integer(myIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
	}
	
	/**
	 * GUI Animation stop conveyor
	 */
	private void doStopConveyor(){
		print("Stopping Conveyor");
		Object[] args = new Object[1];
		args[0] = new Integer(myIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
	}
	
	//Sets the reference to conveyor family
	public void setConveyorFamily(ConveyorFamily1 cf){
		CF = cf;
	}
	
//Sets the reference to the exit sensor
	public void setExitSensor(Sensor s){
		exitSensor = s;
	}
	
	//testing purposes: turns it completely off
	public void turnOff(){
		isOn = false;
		myState = State.notOperational;
	}
	
	//testing purposes: turns it completely on
	public void turnOn(){
		isOn = true;
		myState = State.operational;
	}
	
	public boolean didFirstGlassReachedSensor(){
		return myGlasses.get(0).getReachedSensor();
	}
	
	public void tracePrint(String msg){
		printTrace(msg);
	}
	

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		/*if (channel == TChannel.CONVEYOR && event == TEvent.CONVEYOR_DO_START){
			this.msgPermissionToMove();
		}
		
		if (channel == TChannel.CONVEYOR && event == TEvent.CONVEYOR_DO_STOP){
			this.msgPleaseWait();
		}
		*/
	}

}
