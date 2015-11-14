package engine.agent.family2;
import java.util.ArrayList;

import engine.agent.Agent;
import engine.agent.family0.ConveyorAgent0Base.MovingState;
import engine.agent.family2.interfaces.*;

import shared.Glass;
import shared.enums.SensorPosition;
import shared.enums.Status;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;

public class ConveyorFamilyAgent2 implements ConveyorFamily{
	
	public ConveyorFamilyAgent2(){
	}

	public ConveyorFamilyAgent2(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public void setPrev(ConveyorFamily prev){
		previousFam = prev;
	}
	
	public void setNext(ConveyorFamily next){
		nextFam = next;
	}
	
	public ConveyorFamilyAgent2(int currentIndex, ConveyorFamily prev, ConveyorFamily next) {
		this.currentIndex = currentIndex;
		previousFam = prev;
		nextFam = next;
	}

	public int currentIndex;
	public String name;
	//All the belts in this conveyer fam.
	public ArrayList<ConveyorBelt2> belts = new ArrayList<ConveyorBelt2>();
	//All the popups in this conveyer fam
	public ArrayList<PopupAgent2> popups = new ArrayList<PopupAgent2>();
	//All the sensors in this conveyer fam
	public ArrayList<Sensor2> sensors = new ArrayList<Sensor2>();
	
	public ConveyorFamily previousFam;
	public ConveyorFamily nextFam;
	
	public KStatus2 prevStatus = new KStatus2();
	public KStatus2 myStatus = new KStatus2();
	public KStatus2 nextStatus = new KStatus2();
	
	public Glass enteringGlass = null;
	

	
	/* MESSAGES */
	
	@Override
	public void msgHereIsGlass(Glass g) {
		if (enteringGlass != null)
			System.out.println("Oops, conveyor family "+ name +" was given too much glass");
		for (Sensor2 s: sensors){
			if (s.getPos() == SensorPosition.START){
				s.msgHereIsGlass(g);
			}
		}
	}
	@Override
	public void msgStatus(Status s) {
		popups.get(popups.size()-1).msgStatusFromAhead(new KStatus2(s));
		
	}
	
	
	public void sendStatus(){
		previousFam.msgStatus(myStatus.state);
	}

	public void passThisOn(Glass g) {
		if (nextFam == null){
			nextStatus.state = Status.BUSY;
			popups.get(popups.size()-1).msgStatusFromAhead(new KStatus2(Status.BUSY));
			return;
		}
		nextFam.msgHereIsGlass(g);	
	}

	public void addSensor(Sensor2 sensor) {
		sensors.add(sensor);
	}
	
	public void addBelt(ConveyorBelt2 belt) {
		belts.add(belt);
	}
	
	public void addPopups(PopupAgent2 popup) {
		popups.add(popup);
	}

	public void startAllThreads() {
		System.out.println("start all threads");
		for (Object p: popups){
			if (p.getClass() == Agent.class){
				((Agent) p).startThread();
				System.out.println("claims to start a popup");
			}
		}
		for (Object s: sensors){
			if (s.getClass() == Agent.class){
				((Agent) s).startThread();
				System.out.println("claims to start a popup");
			}
		}
		for (Object b: belts){
			if (b.getClass() == Agent.class){
				((Agent) b).startThread();
				System.out.println("claims to start a belt");

			}
		}
		
	}

	
	public void msgGiveGlassToNext(Glass glass) {
		nextFam.msgHereIsGlass(glass);
		
	}
	
	
	// NONNORMS
	@Override
	public void msgStopConveyor(){
		//TODO
		belts.get(belts.size()-1).shutDown();//.msgStatusFromAhead(new KStatus2(Status.BUSY)); //bit of a hack since next isn't actually busy
		
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
		belts.get(belts.size()-1).startUp();//msgStatusFromAhead(new KStatus2(Status.BUSY)); //bit of a hack since next isn't actually busy
	}
	
	
	@Override
	public void msgStopPopup(){
		popups.get(popups.size()-1).msgTurnOff();
	}
	

	@Override
	public void msgStartPopup(){
		popups.get(popups.size()-1).msgTurnOn();
	}
	

	@Override
	public void msgSetConveyorTooFull() {
		// TODO hmmm
		
	}
	
	@Override
	public void msgOfflineMachineExpectationFailure() {
		popups.get(popups.size()-1).setExpecting(false);
		//Kevin.. don't actually tell the machine?
	}
	
	@Override
	public void msgOfflineMachineFixExpectationFailure() {
		popups.get(popups.size()-1).setExpecting(true);
		//Kevin.. don't actually tell the machines for these?
	}
	
	@Override
	public void msgTopOfflineMachineTurnOff() {
		popups.get(popups.size()-1).msgTurnTopMachineOff();
		
		System.out.println("Told popup: "+popups.get(popups.size()-1).getName()+" to turn off top machine");
	}

	@Override
	public void msgBottomOfflineMachineTurnOff() {
		popups.get(popups.size()-1).msgTurnBottomMachineOff();
		
	}

	@Override
	public void msgTopOfflineMachineTurnOn() {
		popups.get(popups.size()-1).msgTurnTopMachineOn();
	}

	@Override
	public void msgBottomOfflineMachineTurnOn() {
		popups.get(popups.size()-1).msgTurnBottomMachineOn();
	}
	@Override
	public void msgTopMachineBreaksGlass() {
		popups.get(popups.size()-1).msgMachineBreaksGlass("top",true);
		
	}
	@Override
	public void msgBottomMachineBreaksGlass() {
		popups.get(popups.size()-1).msgMachineBreaksGlass("bottom", true);

	}
	@Override
	public void msgTopMachineFixGlass() {
		popups.get(popups.size()-1).msgMachineBreaksGlass("top",false);
		
	}
	@Override
	public void msgBottomMachineFixGlass() {
		popups.get(popups.size()-1).msgMachineBreaksGlass("bottom", false);

	}

	

	
	
	
	//Don't care about these
	@Override
	public void msgStopOnlineMachine(){
		//nope
	}
	

	@Override
	public void msgStartOnlineMachine(){
		//nope
	}
	@Override
	public void msgOnlineMachineExpectationFailure() {
		//nope		
	}
	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		
		
	}

}
