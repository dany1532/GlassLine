package engine.agent.family1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import shared.*;
import engine.agent.family1.interfaces.*;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import gui.panels.subcontrolpanels.TracePanel;

public class SensorAgent1 extends Agent implements Sensor {
	Conveyor myConveyor;
	enum Type { entrance, exit };
	public boolean glassReachedSensor;
	Type myType;
	public enum GlassState { notReachedSensor, reachedSensor, leaving, leftSensor, waitingPopUp, 
	                  waitingPopUpResponse, movingToPopUp, idle}
	//State myState;
	PopUp myPopUp;
	ConveyorFamily1 CF;
	public List <MyGlass> myGlasses;
	int myIndex;
	
	private class MyGlass{
		   Glass glass;
		   GlassState glassState;
		   
		   public MyGlass(Glass g){
			   glass = g;
			   glassState = GlassState.notReachedSensor;
		   }
		   
	}
	
	public SensorAgent1(String name, boolean isEntrance){
		super(name);
		
		if(isEntrance){
			myType = Type.entrance;
			myIndex = 10;
			
		}
		else{
			myType = Type.exit;
			myIndex = 11;
		}
		
		myGlasses = new ArrayList<MyGlass>();
		glassReachedSensor = false;
	}
	
	//Constructor
	public SensorAgent1(String name, boolean isEntrance, Transducer t, TracePanel p){
		super(name, t);
		

		setTracePanel(p);
		getTransducer().register(this, TChannel.SENSOR);
		
		
		if(isEntrance){
			myType = Type.entrance;
			myIndex = 10;
		}
		else{
			myType = Type.exit;
			myIndex = 11;
		}
		
		myGlasses = Collections.synchronizedList(new ArrayList<MyGlass>());
		glassReachedSensor = false;
		print("Initialized");
	}
	
	// *** MESSAGES ***
	
/**
 * Message from Conveyor or ConveyorFamily, giving requested glass
 */
	public void msgHereIsGlassFromConveyor1(Glass g) {
		print("Received Glass from Conveyor1");
		synchronized(myGlasses){
			MyGlass newG = new MyGlass(g);
			newG.glassState = GlassState.reachedSensor;
			myGlasses.add(newG);
			stateChanged();
		}
	}
	
/**
 * Message from previous conveyor family. Sends a glass
 * @param g Glass that is to be processed
 */
	public void msgHereIsGlassFromCF0(Glass g){
		print("Received Glass from ConveyorFamily0");
		synchronized(myGlasses){
			MyGlass newG = new MyGlass(g);
			myGlasses.add(newG);
			stateChanged();
		}
	}

/**
 * Message from animation when a glass has reached a sensor
 */
	public void msgReachedSensor() {
		print("Glass reached Sensor");
		glassReachedSensor = true;
		stateChanged();
	}

/**
 * Message from animation when a glass has left a sensor
 */
	public void msgLeftSensor() {
		synchronized(myGlasses){
			for(MyGlass g: myGlasses){
			    if(g.glassState == GlassState.leaving){
				    g.glassState = GlassState.leftSensor;
				    stateChanged();
			    }
			}
		}
	}

/**
 * Message that pop up is unavailable. Wait for it to be available
 */
	public void msgUnavailable() {
		synchronized(myGlasses){
			for(MyGlass g: myGlasses){
			    if(g.glassState == GlassState.waitingPopUpResponse){
				    g.glassState = GlassState.waitingPopUp;
				    stateChanged();
			    }
			}
		}
	}

/**
 * Message that pop up is available
 */
	public void msgAvailable() {
		print("Received Popup1 availability message");
		synchronized(myGlasses){
			for(MyGlass g: myGlasses){
			    if(g.glassState == GlassState.waitingPopUp ||
			    		g.glassState == GlassState.waitingPopUpResponse){
			    	
				    g.glassState = GlassState.movingToPopUp;
				    stateChanged();
			    }
			}
		}
	}

	// *** SCHEDULER ***
	public boolean pickAndExecuteAnAction() {
		
	//For sensor entrance
		if(myType == Type.entrance){
			
		//Glass has reached the sensor change glass state
			if(glassReachedSensor){
				changeGlassState();
				return true;
			}
			
		//Give glass to conveyor
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.reachedSensor){
					    giveConveyorGlass(g);
					    return true;
				    }
				}
			}
			  
		//Remove glass from sensor list
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.leftSensor){
					    turnOffSensor(g);
					    return true;
				    }
				}
			}
		}
		
	//Only for sensor exit
		else if(myType == Type.exit){
			
		//Ask conveyor for glass
			if(glassReachedSensor){
			   askForGlass();
			   return true;
			}
			
		//Ask the pop up if it is available
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.reachedSensor){
					    checkPopUpAvailability(g);
					    return true;
				    }
				}
			}
			
		//Give status to conveyor and stop it
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.waitingPopUp){
					    tellConveyorToStop(g);
					    return true;
				    }
				}
			}
			
		//Give the glass to pop up and start conveyor if stopped
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.movingToPopUp){
					    giveGlassToPopUp(g);
					    startConveyor();
					    return true;
				    }
				}
			}
			
		//Remove glass from sensor's list
			synchronized(myGlasses){
				for(MyGlass g: myGlasses){
				    if(g.glassState == GlassState.leftSensor){
					    turnOffSensor(g);
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
	
	
//Send message to conveyor to start moving
	private void startConveyor() {
		myConveyor.msgPermissionToMove();
		
	}

//Send glass to popUp
	private void giveGlassToPopUp(MyGlass g) {
		print("Sending Glass to popUp1");
		g.glassState = GlassState.leaving;
		myPopUp.msgGlassSensorToPopUp(g.glass);
	    stateChanged();
		
	}

//Tell conveyor to stop
	private void tellConveyorToStop(MyGlass g) {
		print("Telling Conveyor1 to stop");
		g.glassState = GlassState.waitingPopUpResponse;
	    myConveyor.msgPleaseWait();
	    stateChanged();
		
	}

//Check if the pop up is available
	private void checkPopUpAvailability(MyGlass g) {
		print("Asking PopUp if available");
		g.glassState = GlassState.waitingPopUpResponse;
	    myPopUp.msgAreYouAvailable();
	    stateChanged();
		
	}

//Remove glass from sensor's list
	private void turnOffSensor(MyGlass g) {
		print("Removing Glass from Sensor");
		myGlasses.remove(g);
		stateChanged();
	}

//Give glass to conveyor
	private void giveConveyorGlass(MyGlass g) {
		print("Giving Glass to Conveyor");
		g.glassState = GlassState.leaving;
		myConveyor.msgHereIsGlass(g.glass);
		stateChanged();
		
	}

//Change state of glass for next processing
	private void changeGlassState() {
		
		glassReachedSensor = false;
		if(myType == Type.entrance){
			//print("Asking CF for Glass");
			//CF.msgRequestingGlass();
			myGlasses.get(0).glassState = GlassState.reachedSensor;
		}
		   
		stateChanged();
	}
	
//Ask the conveyor for glass
	private void askForGlass() {
		
		glassReachedSensor = false;

		if(myType == Type.exit){
			print("Requesting Conveyor1 for Glass");
		     myConveyor.msgRequestingGlass();
		}
		   
		stateChanged();
	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
	//GUI glass has reached sensor
		if (channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED)
		{
			if(myIndex == (Integer)args[0])
				this.msgReachedSensor();
		}
		
	}
	

	public GlassState getGlassStateFromFirstInList(){
		return myGlasses.get(0).glassState;
	}
	
	public void setConveyorFamily(ConveyorFamily1 cf){
		CF = cf;
	}
	
	public void setPopUp(PopUp popUp){
		myPopUp = popUp;
	}
	
	public void setConveyor(Conveyor c){
		myConveyor = c;
	}
	
	public void tracePrint(String msg){
		printTrace(msg);
	}



}
