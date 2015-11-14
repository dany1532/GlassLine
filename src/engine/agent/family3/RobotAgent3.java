package engine.agent.family3;

import java.util.*;

import engine.agent.*;
import shared.*;
import shared.interfaces.ConveyorFamily;
import shared.interfaces.AgentInterface;
import shared.interfaces.Machine;
import shared.interfaces.Robot;
import engine.agent.family3.interfaces.Popup;
import transducer.*;

public class RobotAgent3 extends Agent implements Robot {

	
	enum GlassState { NEW, BEING_TREATED, TREATMENT_DONE, WAITING_FOR_PICKUP_REQUEST, PICKUP_REQUESTED, BROKEN }
	class MyGlass {
		Glass glass;
		GlassState state;
		
		public MyGlass(Glass g){
			glass = g;
			state = GlassState.NEW;
		}
	}
		
	enum MachineState { READY, BUSY }
	class MyMachine {
		Machine machine;
		MachineState state;
		
		public MyMachine(Machine m){
			machine = m;
			state = MachineState.READY;
		}
	}

	Popup myPopup;
	List<MyMachine> myMachines;
	List<MyGlass> myGlass;
	boolean waitingGlass;
	
	public RobotAgent3(String name, Transducer t){
		super(name,t);
		
		myMachines = new ArrayList<MyMachine>();		
		myGlass = new ArrayList<MyGlass>();
		
	}
	
	public void addMachine(Machine m){
		myMachines.add(new MyMachine(m));
	}
	public void setPopup(Popup p){
		myPopup = (PopupAgent3)p;
	}
	
	
	public void msgGlassBroken(MachineAgent3 m3, Glass g){
		for (MyMachine machine : myMachines){
			if (machine.machine == m3){
				machine.state = MachineState.READY;
			}
		}
		
		for (MyGlass glass : myGlass){
			if (glass.glass == g){
				glass.state = GlassState.BROKEN;
			}
		}
		stateChanged();
	}
	
	public void msgMachineBreakGlass(){
		for (MyMachine m : myMachines){
				MachineAgent3 temp = (MachineAgent3)m.machine;
				temp.msgBreakGlass();
			}
	}
	
	public void msgMachineFixGlass(){
		for (MyMachine m: myMachines){
			MachineAgent3 temp = (MachineAgent3)m.machine;
			temp.msgFixGlass();
		}
	}
	
	@Override
	public void msgGlassPopupToRobot(Glass g) {
		if (myGlass.size() >= myMachines.size())
			System.err.println("Received glass when no where for it to go. Bad");
		if (g != null){
		myGlass.add(new MyGlass(g));
		stateChanged();}
		else 
			print("ERROR NULL GLASS SENT TO ROBOT");
	}

	@Override
	public void msgPopupReadyForGlass() {
		MyGlass readyGlass = null;
		for (MyGlass g : myGlass){
			if (g.state == GlassState.WAITING_FOR_PICKUP_REQUEST){
				readyGlass = g;
				break;
			}
		}
		if (readyGlass == null){
			System.err.println("Uknown request for glass. Bad");
		} else {
			readyGlass.state = GlassState.PICKUP_REQUESTED;

		}
		stateChanged();
	}

	@Override
	public void msgGlassMachineToRobot(Machine m, Glass g) {
		for (MyGlass glass : myGlass){
			if (glass.glass == g){
				glass.state = GlassState.TREATMENT_DONE;
			}
		}
		for (MyMachine machine : myMachines){
			if (machine.machine == m){
				machine.state = MachineState.READY;
			}
		}
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {

		// not applicable
		
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		
		if (myGlass.isEmpty()){
			return false;
		}
				
		for (MyGlass g : myGlass){
			if (g.state == GlassState.TREATMENT_DONE){
				alertPopupGlassIsReady(g);
				return true;
			}
		}
		
		for (MyGlass g : myGlass){
			if (g.state == GlassState.NEW){
				for (MyMachine m : myMachines){
					if (m.state == MachineState.READY){
						giveGlassToMachine(g,m);
						return true;
					}
				}
			}
		}

		for (MyGlass g : myGlass){
			if (g.state == GlassState.PICKUP_REQUESTED){
				giveGlassToPopup(g);
				return true;
			}
		}
		
		for (MyGlass g: myGlass){
			if (g.state == GlassState.BROKEN){
				glassBroken(this, g);
				
			}
		}
		
		return false;
	}
	
	public boolean waitingForPickUp(){
		return waitingGlass;
	}
	
	private void glassBroken(RobotAgent3 r, MyGlass g){
		PopupAgent3 temp =(PopupAgent3)myPopup;
		temp.msgGlassBroken(this);
		myGlass.remove(g);
		
	}

	private void giveGlassToMachine(MyGlass g, MyMachine m){
		print("Giving glass to " + m.machine);
		m.machine.msgGlassRobotToMachine(g.glass);
		m.state = MachineState.BUSY;
		g.state = GlassState.BEING_TREATED;
	}
	
	private void alertPopupGlassIsReady(MyGlass g){
		print("Alerting " + myPopup + " that glass is ready");
		if (myPopup != null){
		myPopup.msgGlassFromMachineReady();
		}
		else
			print("POPUP IS NULL, WHAT?");
		g.state = GlassState.WAITING_FOR_PICKUP_REQUEST;
		waitingGlass = true;
	}

	private void giveGlassToPopup(MyGlass g){
		print("Giving glass to " + myPopup);
		myPopup.msgGlassFromMachine(g.glass);
		myGlass.remove(g);
		waitingGlass = false;
	}


	@Override
	public void msgGlassGotBroken(Machine m, Glass g) {
		// TODO Auto-generated method stub
		
	}


}
