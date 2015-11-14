package engine.agent.family4;

import engine.agent.*;
import engine.agent.family4.ConveyorAgent4.ConveyorType;
import engine.agent.family4.enums.*;
import engine.agent.family4.interfaces.*;
import java.util.*;
import shared.*;
import shared.enums.*;
import shared.interfaces.*;
import test.mocks.*;
import transducer.*;

public class SensorAgent4 extends Agent implements SensorInterface {

	public Glass glass;
	public SensorState state;
	public SensorType type;
	public ConveyorAgent4 conveyorAgent;
	private Glass sendGlass;
	public int guiSensorIndex;
	
	public enum SensorState
	{
		ON,
		OFF,
		ERROR
	}
	
    /** Constructor for SensorAgent class
     * @param name name of sensor */
    public SensorAgent4(String name, SensorType type, ConveyorAgent4 ca, Transducer t, int gsi) {
		super(name, t);
		
		getTransducer().register(this, TChannel.SENSOR);
	
		this.name = name;
		this.type = type;
		this.state = SensorState.OFF;
		this.conveyorAgent = ca;
		guiSensorIndex = gsi;
    } 
	
	/** MESSAGES */
	// glass on Sensor
	public void msgGlassOnSensor(Glass g){
		if (this.glass == null){
			this.glass = g;
			this.state = SensorState.ON;
			print("Received msgGlassOnSensor for " + this.name + " with recipe.");
		}
		else{
			this.state = SensorState.ERROR;
			print("Received msgGlassOnSensor for " + this.name + " with recipe crash into another recipe");
		}
		stateChanged();
	}
	// glass off Sensor
	public void msgGlassOffSensor(){
		this.sendGlass = this.glass;
		this.glass = null;
		this.state = SensorState.OFF;
		stateChanged();
	}
    
    /** SCHEDULER.  Determine what action is called for, and do it. */
    public boolean pickAndExecuteAnAction() {
    	/*
    	if (conveyorAgent.type == ConveyorType.STANDALONE && conveyorAgent.conveyorFamily.conveyorFamilyAfterStatus == Status.BUSY)
    	{
    		stopConveyor();
    		return true;
    	}
    	else
    	{
    		if (this.sendGlass != null && this.type == SensorType.END){
    			sendGlass();
    		}
    		startConveyor();
    	}
	 	*/
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
	
	/** ACTIONS */
    private void stopConveyor(){
    	print("Send message msgStopConveyor to " + conveyorAgent.toString() + ".");
    	conveyorAgent.msgStopConveyor();
		stateChanged();
    }
    
    private void startConveyor(){
    	print("Send message msgStartConveyor to " + conveyorAgent.toString() + ".");
    	conveyorAgent.msgStartConveyor();
		stateChanged();
    }
    
    private void sendGlass(){
    	print("Send message msgSendGlass to " + conveyorAgent.toString() + ".");
    	if (conveyorAgent.type == ConveyorType.POPUP){
    		conveyorAgent.msgSendGlass(AgentEvent.SendGlassConveyorToPopup);
    	}
    	else if (conveyorAgent.type == ConveyorType.MACHINE){
    		conveyorAgent.msgSendGlass(AgentEvent.SendGlassConveyorToMachine);
    	}
    	else {
    		conveyorAgent.msgSendGlass(AgentEvent.SendGlassConveyorToConveyor);
    	}
    	this.sendGlass = null;
		stateChanged();
    }
    
    /**
	 * Agents must implement this method in order to communicate with the transducer.
	 * This allows them to listen to events fired by the front end.
	 * 
	 * NOTE: All implementations of this method should be synchronized!
	 */
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args){
		if (channel == TChannel.SENSOR)
		{
/*
			if (event == TEvent.SENSOR_GUI_PRESSED)
			{
				if(guiSensorIndex == (Integer)args[0]){
					if (guiSensorIndex % 2 == 0){
						print("Sensor pressed and send msgGlassOnBeginSensor to conveyor.");
						conveyorAgent.msgGlassOnBeginSensor();
					}
					else{
						print("Sensor pressed and send msgGlassOnEndSensor to conveyor.");
						conveyorAgent.msgGlassOnEndSensor();
					}
				}
				
			}
			else if (event == TEvent.SENSOR_GUI_RELEASED)
			{
				if(guiSensorIndex == (Integer)args[0]){
					if (guiSensorIndex % 2 == 0){
						print("Sensor pressed and send msgGlassOffBeginSensor to conveyor.");
						conveyorAgent.msgGlassOffBeginSensor();
					}
					else{
						print("Sensor pressed and send msgGlassOffEndSensor to conveyor.");
						conveyorAgent.msgGlassOffEndSensor();
					}
				}
			}
			*/

			if(guiSensorIndex == (Integer)args[0]){
				if (guiSensorIndex % 2 == 0){
					if (event == TEvent.SENSOR_GUI_PRESSED)
					{
						print("Sensor pressed and send msgGlassOnBeginSensor to conveyor.");
						conveyorAgent.msgGlassOnBeginSensor();
					}
					else if (event == TEvent.SENSOR_GUI_RELEASED)
					{
						print("Sensor pressed and send msgGlassOffBeginSensor to conveyor.");
						conveyorAgent.msgGlassOffBeginSensor();
					}
				}
				else {
					if (event == TEvent.SENSOR_GUI_PRESSED)
					{
						print("Sensor pressed and send msgGlassOnEndSensor to conveyor.");
						conveyorAgent.msgGlassOnEndSensor();
					}
					else if (event == TEvent.SENSOR_GUI_RELEASED)
					{
						print("Sensor pressed and send msgGlassOffEndSensor to conveyor.");
						conveyorAgent.msgGlassOffEndSensor();
					}
				}
			}
		}
	}
	
	/** EXTRA */
	
}
