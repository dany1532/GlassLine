package engine.agent.family3;

import java.util.ArrayList;

import engine.agent.Agent;
import shared.interfaces.ConveyorFamily;
import shared.interfaces.Machine;
import shared.interfaces.Robot;
import shared.Glass;
import shared.enums.Status;
import engine.agent.family3.interfaces.Popup;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopupAgent3 extends Agent implements Popup{
	
	public ConveyorFamilyAgent3 confam;
	public int index;
	public Glass glass;
	enum GlassState {fromMachine, fromConveyor, neither, ready};
	GlassState glassState;
	enum PopupState {up, down, loaded};
	PopupState popupState;
	ArrayList<RobotAgent3> myRobots;
	enum RobotState {none, oneMachine, twoMachine};
	RobotState robotState;
	
	public PopupAgent3(){
		confam = null;
		glass = null;
		popupState = PopupState.down;
	}
	
	public PopupAgent3(ConveyorFamilyAgent3 cf, Transducer t, RobotAgent3 r, RobotAgent3 r2, int i){ //insert Robot later
		super("Popup", t);
		myRobots = new ArrayList<RobotAgent3>();
		index = i;
		confam = cf;
		glass = null;
		myRobots.add(r);
		myRobots.add(r2);
		popupState = PopupState.down;
		
		robotState = RobotState.none;
		
		getTransducer().register(this, TChannel.POPUP);
	}
	
	
	
	public void setIndex(int i){
		index = i;
	}
	

	//MESSAGES
	public void msgGlassBroken(RobotAgent3 r){
		
	}
	
	public void msgFullyLoaded(){
		print("I'M FULLY LOADED");
		popupState = PopupState.loaded;
		stateChanged();
	}
	
	public void msgMovedUp(){
		print("I'M ALL THE WAY UP");
		popupState = PopupState.up;
		stateChanged();
	}
	
	public void msgMovedDown(){
		popupState = PopupState.down;
		print("I'M DOWN. GUI");
		confam.sensor2.msgReady();
		//confam.conveyor.msgContinueTheLine();
		stateChanged();
	}
	
	@Override
	public void msgHereIsGlass(Glass g) {
		glass = g;
		glassState = GlassState.fromConveyor;	
		print("POPUP GOT THE PIECE!");
		stateChanged();
	}
	
	@Override
	public void msgGlassRobotToPopup(Glass g) {
		glass = g;
		glassState = GlassState.fromMachine;
		print("GOT GLASS FROM MACHINE");
		stateChanged();
		
	}
	
	public void msgOperationDone(){
		popupState = PopupState.up;
		confam.conveyor.msgStopTheLine();
		stateChanged();
	}

	public void msgGlassFromMachineReady() {
		glassState = GlassState.fromMachine;
		stateChanged();
	}
	
	public void msgGlassFromMachine(Glass g){
		glass = g;
		//glassState = GlassState.fromMachine;	
	}
	
	public void msgTopMachineBreakGlass(){
		myRobots.get(0).msgMachineBreakGlass();
	}
	
	public void msgBottomMachineBreakGlass(){
		myRobots.get(1).msgMachineBreakGlass();
	}
	
	public void msgTopFixGlass(){
		myRobots.get(0).msgMachineFixGlass();
	}
	
	public void msgBottomFixGlass(){
		myRobots.get(1).msgMachineFixGlass();
	}

	@Override
	public boolean pickAndExecuteAnAction() { //redo the pick and execute action.
		if (glass != null){
			if (glassState == GlassState.fromConveyor){
				if (glass.getRecipe().doesMachineNeedToDoJob(confam.index)== true){
					if (popupState == PopupState.down){
						if(robotState == RobotState.none || robotState == RobotState.oneMachine){
							print("Send Glass Up");
							doSendGlassUp();
							return true;
						}
					}
				}
				
				else if (glass.getRecipe().doesMachineNeedToDoJob(confam.index)== false){
					doSendOff();
				}
					if (popupState == PopupState.loaded){
						if (robotState == RobotState.none || robotState == RobotState.oneMachine){
							print("Sending Glass to Robot");
							doSendGlassToRobot();
							return true;
						}
					}
			}
			
			if (glassState == GlassState.fromMachine){
				print("From Machine. Almost Done");
				if (popupState == PopupState.loaded){
					print("Poppin' down");
					doPopDown();
					//return true;
				}
				else if (popupState == PopupState.down){
					doSendOff();
					return true;
				}
			}
		}
		
		if (glass == null){
			print("Glass gone to machine");
			if (glassState == GlassState.neither){
				print("Glass State is Neither");
				if (popupState == PopupState.up){
					doPopDown();
					print("Glass in Down");
					return true;
				}
			}
			
			if (glassState == GlassState.fromMachine){
				print("Glass back from Machine");
				/*for (RobotAgent3 r : myRobots){
					if (r.waitingGlass == true){
						print("I'M IN THE RIGHT PLACE AKSDJG;A WAITING GLASS");
						doRequestGlass(r);
						return true;
					}
				}*/
				
				if (popupState == PopupState.down){
					print ("Popping up!");
					doPopUp();
					//return true;
				}
				if (popupState == PopupState.loaded){
					for (RobotAgent3 r : myRobots){
						if (r.waitingGlass == true){
							print("I'M IN THE RIGHT PLACE AKSDJG;A WAITING GLASS");
							doRequestGlass(r);
							//return true;
						}
					}
				}
			}
		}
		print("Returned False");
		return false;
	}

	
	//ACTIONS
	public void doStopConveyor(){
		confam.conveyor.msgStopTheLine();
		stateChanged();
	}
	
	public void doRequestGlass(RobotAgent3 r){
		r.msgPopupReadyForGlass();
	}
	
	public void doSendGlassUp(){	
		confam.sensor2.haltLine();
		doMoveUp();
		/*if (robotState == robotState.none){
			glassState = GlassState.neither;
			print("I'M SENDING THE GLASS UP POPUP3");
			myRobots.get(0).msgGlassPopupToRobot(glass);
			robotState = RobotState.oneMachine;
			glass = null;
			}
		else if (robotState == robotState.oneMachine){
			glassState = GlassState.neither;
			print("I'M SENDING THE GLASS UP POPUP3");
			myRobots.get(1).msgGlassPopupToRobot(glass);
			robotState = RobotState.twoMachine;
			glass = null;
			}*/
	}
	
	public void doSendGlassToRobot(){
		print("Picking how many robots");
		if (robotState == RobotState.none){
			glassState = GlassState.neither;
			myRobots.get(0).msgGlassPopupToRobot(glass);
			robotState = RobotState.oneMachine;
			glass = null;
			print("picked first machine");
		}
		else if (robotState == RobotState.oneMachine){
			glassState = GlassState.neither;
			myRobots.get(1).msgGlassPopupToRobot(glass);
			robotState = RobotState.twoMachine;
			glass = null;
			print("Picked second machine");
		}
	}
	
	public void doPopUp(){
		doMoveUp();
		//popupState = PopupState.up;
		print("I'M MOVING UP");
		//stateChanged();
	}
	
	public void doMoveUp(){
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
		print("I'M ANIMATING POPUP");
	}
	
	public void doPopDown(){ 
		doMoveDown();
		//popupState = PopupState.down;
		//print("I'M MOVING DOWN AGAIN");
		//stateChanged();
	}
	
	public void doMoveDown(){
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
	}
	
	public void doSendOff(){
		confam.next.msgHereIsGlass(glass);
		
		//make available a robot Perhaps move this to the TChannel
		if (robotState == RobotState.oneMachine)
			robotState = RobotState.none;
		if (robotState == RobotState.twoMachine)
			robotState = RobotState.oneMachine;
		//
		
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args);
		doMoveDown();

		glass = null;
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if (TChannel.POPUP == channel){
			if (TEvent.POPUP_GUI_LOAD_FINISHED == event){ //SENSOR SHOULD LISTEN FOR THIS EVENT
				if (index == (Integer)args[0])
				msgFullyLoaded();
				}
				
			if (TEvent.POPUP_GUI_MOVED_UP == event){
				if (index == (Integer)args[0])
					msgMovedUp();				
			}
				
			if (TEvent.POPUP_GUI_MOVED_DOWN == event){
				if (index == (Integer)args[0]){
					msgMovedDown();
				}
			}
		}
	}

	
}
