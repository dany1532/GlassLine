package engine.agent.family4;

import engine.agent.*;
import engine.agent.family4.enums.*;
import engine.agent.family4.interfaces.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import shared.*;
import shared.agents.*;
import shared.enums.*;
import shared.interfaces.*;
import test.mocks.*;
import transducer.*;

public class ConveyorAgent4 extends Agent implements ConveyorInterface {
	
	public RobotAgent robotAgent1;
	public RobotAgent robotAgent2;
	public SensorAgent4 sensorAgentBegin;
	public SensorAgent4 sensorAgentEnd;
	public SensorState sensorAgentBeginState;
	public SensorState sensorAgentEndState;
	public PopupAgent4 popupAgent;
	public MachineAgent4 machineAgent;
	public ConveyorFamily4 conveyorFamily;
	public ConveyorFamily conveyorFamilyBegin;
	public ConveyorFamily conveyorFamilyEnd;
	public List<ConveyorGlass> glasses = Collections.synchronizedList(new ArrayList<ConveyorGlass>());
	public ConveyorType type;
	public ConveyorState state;
    List<AgentEvent> events = Collections.synchronizedList(new ArrayList<AgentEvent>());
	public int guiConveyorIndex;
	public boolean conveyorMoving = false;
	public boolean conveyorFull = false;
	public int conveyorSize = 2;
	public boolean conveyorOn = true;
	
	public Status robotAgent1State;
	public Status robotAgent2State;
	public Status popupAgentState;
	public Status machineAgentState;
	
	public enum ConveyorState
	{
		ON,
		OFF,
		MOVING,
		ERROR
	}
	
	public enum ConveyorType
	{
		STANDALONE,
		MACHINE,
		POPUP
	}
	
	public enum SensorState
	{
		NOGLASS,
		PRESSEDGLASS,
		RELEASEDGLASS
	}
	
	private class ConveyorGlass{
		boolean passBeginSensor = false;
		boolean passEndSensor = false;
		Glass glass;
		
		public ConveyorGlass(Glass g){
			glass = g;
		}
	}
	
    /** Constructor for ConveyorAgent class
     * @param name name of conveyor */
    public ConveyorAgent4(String name, ConveyorFamily4 cf4, Transducer t, int gci) {
		super(name, t);
	
		this.name = name;
		this.state = ConveyorState.ON;
		this.type = ConveyorType.STANDALONE;
		this.conveyorFamily = cf4;
		guiConveyorIndex = gci;

		transducer.register(this, TChannel.SENSOR);
		if (gci == 8){
			transducer.register(this, TChannel.WASHER);
		}
		if (gci == 10){
			transducer.register(this, TChannel.PAINTER);
		}
    } 

    /** MESSAGES */
    // turn on conveyor
    public void msgTurnOn(){
    	conveyorOn = true;
		this.state = ConveyorState.ON;
    	stateChanged();
    }
    // turn off conveyor
    public void msgTurnOff(){
    	conveyorOn = false;
    	stateChanged();
    }
	// glass from Conveyor Family to Conveyor
	public void msgGlassConveyorFamilyToConveyor(Glass g){
		print("msgGlassConveyorFamilyToConveyor recevied.");
		glasses.add(new ConveyorGlass(g));
		if (glasses.size() > conveyorSize){ conveyorFull = true; }
		stateChanged();
	}
	// status from next component (Machine, Other Conveyor Family) to Conveyor
	public void msgStatus(Status s){
		machineAgentState = s;
		stateChanged();
	}
	// sensor stop conveyor
	public void msgStopConveyor(){
		this.state = ConveyorState.OFF;
		stateChanged();
	}
	// sensor start conveyor
	public void msgStartConveyor(){
		this.state = ConveyorState.ON;
		stateChanged();
	}
	// sensor send glass
	public void msgSendGlass(AgentEvent ae){
		events.add(ae);
		stateChanged();
	}
	// sensor send glass on begin sensor
	public void msgGlassOnBeginSensor(){
		sensorAgentBeginState = SensorState.PRESSEDGLASS;
		stateChanged();
	}
	// sensor send glass off begin sensor
	public void msgGlassOffBeginSensor(){
		sensorAgentBeginState = SensorState.RELEASEDGLASS;
		stateChanged();
	}
	// sensor send glass on end sensor
	public void msgGlassOnEndSensor(){
		sensorAgentEndState = SensorState.PRESSEDGLASS;
		stateChanged();
	}
	// sensor send glass off end sensor
	public void msgGlassOffEndSensor(){
		sensorAgentEndState = SensorState.RELEASEDGLASS;
		stateChanged();
	}
    
    /** SCHEDULER.  Determine what action is called for, and do it. */
    public boolean pickAndExecuteAnAction() {

    	if (conveyorOn){
	    	if (machineAgentState == Status.READY){
	    		this.state = ConveyorState.ON;
	    	}
	    	if (this.state == ConveyorState.OFF){
	    		stopConveyor();
	    	}
	    	else{
	    		if (this.state == ConveyorState.ON){
	    			startConveyor();
	    		}
	    		if (sensorAgentEndState == SensorState.PRESSEDGLASS &&
					((type == ConveyorType.STANDALONE && conveyorFamily.conveyorFamilyAfterStatus == Status.BUSY) ||
					 (type == ConveyorType.MACHINE && machineAgentState == Status.BUSY))){
					stopConveyor();
	    		}
	    		else if (sensorAgentEndState == SensorState.RELEASEDGLASS)
	    		{
	    			if (type == ConveyorType.MACHINE){
	        			sensorAgentEndState = SensorState.NOGLASS;
	    				sendGlassToMachine();
	    			}
	    			if (type == ConveyorType.STANDALONE){
	        			sensorAgentEndState = SensorState.NOGLASS;
	    				sendGlassToConveyor();
	    			}
	    		}
	    	}
	    	if (conveyorFull){
	    		fullConveyor();
	    	}
	    	else{
	    		freeConveyor();
	    	}
    	}
    	else {
    		stopConveyor();
    	}
    	
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
    
    /** ACTIONS */
    private void stopConveyor(){
		print("Stopped. Send message msgStatus to BeforeConveyorFamily that conveyor is busy.");
    	state = ConveyorState.OFF;
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		if (conveyorFamilyBegin != null){
			conveyorFamilyBegin.msgStatus(Status.BUSY);
		}
    }
    
    private void startConveyor(){
    	print("Started. Send message msgStatus to BeforeConveyorFamily that conveyor is ready.");
    	state = ConveyorState.MOVING;
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		if (conveyorFamilyBegin != null){
			conveyorFamilyBegin.msgStatus(Status.READY);
		}
    }
    
    private void sendGlassToPopup(){
    	ConveyorGlass removeGlass = glasses.remove(0);
    	if (glasses.size() <= conveyorSize){ conveyorFull = false; }
		print("Send message msgGlassConveyorToPopup to Popup " + popupAgent.getName() + " for glass.");
		popupAgent.msgGlassConveyorToPopup(removeGlass.glass);
		stateChanged();
    }
    
    private void sendGlassToMachine(){
    	ConveyorGlass removeGlass = glasses.remove(0);
    	if (glasses.size() <= conveyorSize){ conveyorFull = false; }
		print("Send message msgHereIsGlass to Machine " + machineAgent.getName() + " for glass.");
		machineAgent.msgHereIsGlass(removeGlass.glass);
		stateChanged();
    }
    
    private void sendGlassToConveyor(){
    	ConveyorGlass removeGlass = glasses.remove(0);
    	if (glasses.size() <= conveyorSize){ conveyorFull = false; }
		print("Send message msgGlassFamilyToFamily to Conveyor Family for glass.");
		conveyorFamily.msgGlassFamilyToFamily(removeGlass.glass);
		stateChanged();
    }
    
    private void fullConveyor(){
    	//print("Full, tell the conveyor before I am busy");
		if (conveyorFamilyBegin != null){
			//conveyorFamilyBegin.msgStatus(Status.BUSY);
		}
    }
    
    private void freeConveyor(){
    	//print("Not full, tell the conveyor before I am busy");
		if (conveyorFamilyBegin != null){
			//conveyorFamilyBegin.msgStatus(Status.READY);
		}
    }
    
    /**
	 * Agents must implement this method in order to communicate with the transducer.
	 * This allows them to listen to events fired by the front end.
	 * 
	 * NOTE: All implementations of this method should be synchronized!
	 */
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args){
		
	}
	
	/** EXTRA */
	public void setRobot1(RobotAgent mR){
		this.robotAgent1 = mR;
		this.robotAgent1State = Status.READY;
	}
	public void setRobot2(RobotAgent mR){
		this.robotAgent2 = mR;
		this.robotAgent2State = Status.READY;
	}
	public void setPopup(PopupAgent4 pA){
		this.popupAgent = pA;
		this.type = ConveyorType.POPUP;
	}
	public void setMachine(MachineAgent4 mA){
		this.machineAgent = mA;
		this.type = ConveyorType.MACHINE;
	}
	public void setSensorBegin(SensorAgent4 sA){
		this.sensorAgentBegin = sA;
		sensorAgentBeginState = SensorState.NOGLASS;
	}
	public void setSensorEnd(SensorAgent4 sA){
		this.sensorAgentEnd = sA;
		sensorAgentEndState = SensorState.NOGLASS;
	}
	public void setBeforeConveyorFamily(ConveyorFamily cF){
		this.conveyorFamilyBegin = cF;
	}
	public void setMockAfterConveyorFamily(ConveyorFamily cF){
		this.conveyorFamilyEnd = cF;
	}
	
}
