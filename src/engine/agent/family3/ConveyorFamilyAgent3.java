package engine.agent.family3;

import java.util.ArrayList;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.Transducer;

public class ConveyorFamilyAgent3 implements ConveyorFamily{
	
	public ConveyorFamily previous, next;
	public SensorAgent3 sensor1;
	public ConveyorAgent3 conveyor;
	public SensorAgent3 sensor2;
	public PopupAgent3 popup;
	
	Status nextStatus;
	Transducer transducer; 
	int index, arrayIndex;
	
	public ConveyorFamilyAgent3(Transducer t, int i){
		
		transducer = t;
		index = i;
		
		//create the Agents
		//SensorAgent3 sensor1 = new SensorAgent3(this, transducer, index*2);
		//System.out.println ("i'm making sensor3 agent 1");
		//ConveyorAgent3 conveyor = new ConveyorAgent3(this, transducer, index);
		//SensorAgent3 sensor2 = new SensorAgent3(this, transducer, ((index*2)+1));
		//PopupAgent3 popup = new PopupAgent3(this, transducer, index);
		
		nextStatus = Status.READY;
	}
	
	//Routing Message
	public void msgHereIsGlass(Glass g){
		sensor1.msgHereIsGlass(g);
		System.out.println("Here is glass CONFAM 3");
	}
	
	public void msgStatus(Status s){
		nextStatus = s;
	};
	
	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		conveyor.msgStopTheLine();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		conveyor.msgContinueTheLine();
	}
	
	// GUI stop popup for non-norms
	@Override
	public void msgStopPopup(){
		//TODO
	}
	
	// GUI start popup for non-norms
	@Override
	public void msgStartPopup(){
		//TODO
	}
	
	// GUI stop online machine for non-norms
	@Override
	public void msgStopOnlineMachine(){
		//TODO
	}
	
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		//TODO
	}
	
	public void sendMessageBack(Status s){
		previous.msgStatus(s);
	}
	
	public void setNeighbors(ConveyorFamily p, ConveyorFamily n){
		previous = p;
		next = n;
	}
	
	public void startAllThreads(){
		sensor1.startThread();
		//conveyor.startThread();
		sensor2.startThread();
		//popup.startThread();
	}
	
	public void setSensors(SensorAgent3 s1, SensorAgent3 s2){
		sensor1 = s1;
		sensor2 = s2;
	}
	
	public void setConveyor(ConveyorAgent3 c){
		conveyor = c;
	}
	
	public void setPopup(PopupAgent3 p){
		popup = p;
	}

	@Override
	public void msgSetConveyorTooFull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOfflineMachineExpectationFailure() {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void msgOfflineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBottomOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTopMachineBreaksGlass() {
		popup.msgTopMachineBreakGlass();
		
	}

	@Override
	public void msgBottomMachineBreaksGlass() {
		popup.msgBottomMachineBreakGlass();
		
	}

	@Override
	public void msgTopMachineFixGlass() {
		popup.msgTopFixGlass();
		
	}

	@Override
	public void msgBottomMachineFixGlass() {
		popup.msgBottomFixGlass();
		
	}

	@Override
	public void msgOnlineMachineExpectationFailure() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		
	}
	
	
//Contains all components of the Conveyor Family
//Transducer reference
}
