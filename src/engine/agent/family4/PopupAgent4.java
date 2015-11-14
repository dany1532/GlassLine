package engine.agent.family4;

import engine.agent.*;
import engine.agent.family4.enums.*;
import engine.agent.family4.interfaces.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import shared.*;
import shared.enums.*;
import shared.interfaces.*;
import test.mocks.*;
import transducer.*;

public class PopupAgent4 extends Agent implements PopupInterface {

	public MockRobot mockRobot1;
	public MockRobot mockRobot2;
	public MockConveyorFamily mockConveyorFamily;
	public ConveyorAgent4 conveyorAgent;
	//public MockTransducer mockTransducer;
	public Glass glass;
	
	public PopupState state;
    List<AgentEvent> events = Collections.synchronizedList(new ArrayList<AgentEvent>());

	public AgentState mockRobot1State;
	public AgentState mockRobot2State;
	public AgentState mockConveyorFamilyState;
	
	public enum PopupState
	{
		UP,
		DOWN,
		ERROR
	}
	
    /** Constructor for PopupAgent class
     * @param name name of popup */
    public PopupAgent4(String name, ConveyorAgent4 ca) {
		super();
	
		this.name = name;
		this.state = PopupState.DOWN;
		this.conveyorAgent = ca;
    } 

	/** MESSAGES */
	// glass from Conveyor to Popup
	public void msgGlassConveyorToPopup(Glass g){
		this.glass = g;
		events.add(AgentEvent.LoadGlassConveyorToPopup);
		stateChanged();
	}
	// glass from Robot to Popup
	public void msgGlassRobotToPopup(Glass g){
		this.glass = g;
		events.add(AgentEvent.LoadGlassRobotToPopup);
		stateChanged();
	}
	// status from next component (Offline Machine) to Popup
	public void msgMachineStatus(MockRobot mR, AgentState s){
		if (mR.getName().equals(mockRobot1.getName())) { mockRobot1State = s; }
		if (mR.getName().equals(mockRobot2.getName())) { mockRobot2State = s; }
		stateChanged();
	}
	// status from next component (Online Machine, Other Conveyor Family) to Popup
	public void msgStatus(AgentState s){

		stateChanged();
	}
	// popup is down
	public void msgPopupDown(){
		events.add(AgentEvent.MoveGlassPopupDown);
		stateChanged();
	}
	// popup is up
	public void msgPopupUp(){
		events.add(AgentEvent.MoveGlassPopupUp);
		stateChanged();
	}
    
    /** SCHEDULER.  Determine what action is called for, and do it. */
    public boolean pickAndExecuteAnAction() {
	
    	if (!events.isEmpty()){
    		AgentEvent event = events.remove(0); //pop first element

		    if (event == AgentEvent.LoadGlassConveyorToPopup) {
		    	if (glass.getRecipe().doesMachineNeedToDoJob(3)){
		    		moveGlassUp();
		    	}
		    	else{
		    		glassIsDown();
		    	}
				return true;
		    }
		    else if (event == AgentEvent.LoadGlassRobotToPopup) {
				moveGlassDown();
				return true;
		    }
		    else if (state == PopupState.DOWN) {
			    if (event == AgentEvent.MoveGlassPopupUp) {
					glassIsUp();
					return true;
			    }
    		}
		    else if (state == PopupState.UP) {
		    	if (event == AgentEvent.MoveGlassPopupDown) {
					glassIsDown();
					return true;
			    }
		    }
    	}
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
    
    /** ACTIONS */
    private void moveGlassUp(){
		//print("Move up. Send message POPUP_DO_MOVE_UP to " + mockTransducer.getName() + ".");
		//mockTransducer.POPUP_DO_MOVE_UP(0);
    	stateChanged();
    }
    private void moveGlassDown(){
		//print("Move down. Send message POPUP_DO_MOVE_DOWN to " + mockTransducer.getName() + ".");
		//mockTransducer.POPUP_DO_MOVE_DOWN(0);
    	stateChanged();
    }
    private void glassIsUp(){
    	state = PopupState.UP;
    	MockRobot mR;
    	if (mockRobot1State == AgentState.READY){
    		mR = mockRobot1;
    		mockRobot1State = AgentState.BUSY;
    	}
    	else{
    		mR = mockRobot2;
    		mockRobot2State = AgentState.BUSY;
    	}
		print("Glass is up. Send message msgGlassPopupToRobot to " + mR.getName() + ".");
		//mR.msgGlassPopupToRobot(this.recipe);
		this.glass = null;
    	stateChanged();
    }
    private void glassIsDown(){
    	state = PopupState.DOWN;
		//print("Glass is down. Send message msgGlassPopupToConveyor to " + mockConveyorFamily.getName() + ".");
		//mockConveyorFamily.msgGlassPopupToConveyor(this.recipe);
		this.glass = null;
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
		
	}
	
	/** EXTRA */
	public void setMockRobot1(MockRobot mR){
		this.mockRobot1 = mR;
		this.mockRobot1State = AgentState.READY;
	}
	public void setMockRobot2(MockRobot mR){
		this.mockRobot2 = mR;
		this.mockRobot2State = AgentState.READY;
	}
	public void setMockConveyorFamily(MockConveyorFamily mCF){
		this.mockConveyorFamily = mCF;
	}
	/*
	public void setMockTransducer(MockTransducer mT){
		this.mockTransducer = mT;
	}
	*/
	
}
