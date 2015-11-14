package engine.agent.family1;

import java.util.ArrayList;
import java.util.List;

import engine.agent.Agent;

import shared.Glass;
import shared.interfaces.Machine;
import shared.interfaces.Robot;
import engine.agent.family1.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class RobotAgent1 extends Agent implements Robot {
	enum GlassState { NEW, BEING_TREATED, TREATMENT_DONE,BROKEN, WAITING_FOR_PICKUP_REQUEST, PICKUP_REQUESTED }
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

	PopUp myPopup;
	List<MyMachine> myMachines;
	List<MyGlass> myGlass;
	boolean glassGotBroken;
	
	public RobotAgent1(String name, Transducer t){
		super(name,t);
		
		myMachines = new ArrayList<MyMachine>();		
		myGlass = new ArrayList<MyGlass>();
		print("Initialized");
		
	}
	
	public void addMachine(Machine m){
		myMachines.add(new MyMachine(m));
	}
	public void setPopup(PopUp p){
		myPopup = p;
	}
	
	@Override
	public void msgGlassPopupToRobot(Glass g) {
		if (myGlass.size() >= myMachines.size())
			System.err.println("Received glass when no where for it to go. Bad");
		print("Received Glass from PopUp1");
		myGlass.add(new MyGlass(g));
		stateChanged();
	}
	
	@Override
	public void msgGlassGotBroken(Machine m, Glass g) {
		glassGotBroken = true;
		for (MyMachine machine : myMachines){
			if (machine.machine == m){
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
		
		if(glassGotBroken){
			for(MyGlass g : myGlass){
				tellPopUpGlassGotBroken(g);
				return true;
			}
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
				for (MyMachine m : myMachines){
					if (m.state == MachineState.READY){
						giveGlassToPopup(g, m);
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private void giveGlassToMachine(MyGlass g, MyMachine m){
		print("Giving glass to " + m.machine);
		m.machine.msgGlassRobotToMachine(g.glass);
		m.state = MachineState.BUSY;
		g.state = GlassState.BEING_TREATED;
	}
	
	private void tellPopUpGlassGotBroken(MyGlass g){
		print("Telling PopUp glass got Broken");
		glassGotBroken = false;
		myPopup.msgGlassGotBroken(this);
		myGlass.remove(g);
	}
	
	private void alertPopupGlassIsReady(MyGlass g){
		print("Alerting " + myPopup + " that glass is ready");
		myPopup.msgWantToLowerGlass(this);
		g.state = GlassState.WAITING_FOR_PICKUP_REQUEST;
	}

	private void giveGlassToPopup(MyGlass g, MyMachine m){
		print("Giving glass to " + myPopup);
		myPopup.msgGlassRobotToPopUp(g.glass,this);
		m.machine.msgPermissionToReleaseGlass();
		myGlass.remove(g);
	}
	
	public void tracePrint(String msg){
		printTrace(msg);
	}



}
