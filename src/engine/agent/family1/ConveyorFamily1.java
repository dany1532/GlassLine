package engine.agent.family1;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;

public class ConveyorFamily1 implements ConveyorFamily {
	/** Pointer to previous and next conveyor families */
	public ConveyorFamily previous, next;
	
	/** Status of next Conveyor Family */
	public Status nextStatus;	
	
	/** Pointer to own conveyor */
	public ConveyorAgent1 conveyor;
	public SensorAgent1 sensorEntrance;
	public PopUpAgent1 popUp;
	public MachineAgent1 topMachine;
	public MachineAgent1 bottomMachine;
	
	/** Index of current machine */
	public int conveyorFamilyIndex;

	/** Set all local variables */
	public void setNeighbors(ConveyorFamily p, ConveyorFamily n){
		previous = p;
		next = n;
	}
	public void setConveyor(ConveyorAgent1 c){
		conveyor = c;
	}
	public void setSensorEntrance(SensorAgent1 s){
		sensorEntrance = s;
	}
	
	public void setMachines(MachineAgent1 top, MachineAgent1 bottom){
		topMachine = top;
		bottomMachine = bottom;
	}
	
	public void setPopUp(PopUpAgent1 p){
		popUp = p;
	}
	
	public ConveyorFamily1(int i){
		conveyorFamilyIndex = i;
		nextStatus = Status.READY;
	}
	
	/** Passes glass to conveyor upon initial entrance to the conveyor family */
	@Override
	public void msgHereIsGlass(Glass g) {
		sensorEntrance.msgHereIsGlassFromCF0(g);
		
	}

	/** Logs the state of the next component for use of inner agents */
	@Override
	public void msgStatus(Status s) {
		nextStatus = s;
		if(s == Status.BUSY){
			System.out.println("CF1: CF2 is unavailable");
			popUp.msgCF2Unavailable();
		}
		else{
			System.out.println("CF1: CF2 is available");
			popUp.msgCF2Available();
		}
	}
	
	/** Allows inner component (popUp) to pass glass to next family once done */
	public void msgGiveGlassToNext(Glass g){
		if (next != null){
			next.msgHereIsGlass(g);
		} else {
			System.out.println("ConveyorFamily " + conveyorFamilyIndex + ": Didn't give next glass. Next == null");
		}
		
	}
	
	/** Allows inner compnent (conveyor) to pass status to previous family when it changes */
	public void msgGiveStatusToPrevious(Status s){
		if (previous != null){
			previous.msgStatus(s);
		} else {
			System.out.println("ConveyorFamily " + conveyorFamilyIndex + ": Didn't pass status to previous. Previous == null");
		}
	}


	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		//TODO
		System.out.println("CF1: Received Message Breaking Conveyor");
		conveyor.msgPleaseWait();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
		System.out.println("CF1: Received Message Starting Conveyor");
		conveyor.msgPermissionToMove();
	}
	
	// GUI stop popup for non-norms
	@Override
	public void msgStopPopup(){
		//TODO
		System.out.println("CF1: Received Message Breaking PopUP");
		popUp.msgTurnOff();
	}
	
	// GUI start popup for non-norms
	@Override
	public void msgStartPopup(){
		//TODO
		System.out.println("CF1: Received Message Fixing PopUp");
		popUp.msgTurnOn();
	}
	
	//Not used by my conveyor family
	// GUI stop online machine for non-norms
	@Override
	public void msgStopOnlineMachine(){
		//TODO
	}
	
	//Not used by my conveyor family
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		//TODO
	}
	/*@Override
	public void msgInlineMachineExcpectationFailue() {
		// TODO Auto-generated method stub
		
	}*/
	@Override
	public void msgSetConveyorTooFull() {
		// TODO Auto-generated method stub
		
	}
	
	//Non_Norm: machines don't treat glass
	@Override
	public void msgOfflineMachineExpectationFailure() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Offline Machine Expectation Failure message");
		topMachine.msgTurnOff();
		bottomMachine.msgTurnOff();
		
	}
	//Non_Norm: machines don't treat glass (fixed)
	@Override
	public void msgOfflineMachineFixExpectationFailure() {
		System.out.println("CF1: Received Fix Offline Machine Expectation Failure message");
		// TODO Auto-generated method stub
		topMachine.msgTurnOn();
		bottomMachine.msgTurnOn();
		
	}
	//Non_Norm: workstations work on 0,1,2 sides
	@Override
	public void msgTopOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Turn off top machine message");
		popUp.msgTopMachineStoppedWorking();
		topMachine.msgTurnOff();
		
	}
	//Non_Norm: workstations work on 0,1,2 sides
	@Override
	public void msgBottomOfflineMachineTurnOff() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Turn off bottom machine message");
		popUp.msgBottomMachineStoppedWorking();
		bottomMachine.msgTurnOff();
		
	}
	//Non_Norm: workstations work on 0,1,2 sides (fix)
	@Override
	public void msgTopOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Turn on top machine message");
		topMachine.msgTurnOn();
		popUp.msgTopMachineStartedWorking();
		
		
	}
	//Non_Norm: workstations work on 0,1,2 sides (fix)
	@Override
	public void msgBottomOfflineMachineTurnOn() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Turn off bottom machine message");
		bottomMachine.msgTurnOn();
		popUp.msgBottomMachineStartedWorking();
		
		
	}
	@Override
	public void msgTopMachineBreaksGlass() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Top Machine break glass message");
		topMachine.msgBreakGlass();
		
	}
	@Override
	public void msgBottomMachineBreaksGlass() {
		// TODO Auto-generated method stub
		System.out.println("CF1: Received Bottom Machine break glass message");
		bottomMachine.msgBreakGlass();
	}
	@Override
	public void msgOnlineMachineExpectationFailure() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgTopMachineFixGlass() {
		// TODO Auto-generated method stub
		System.out.println("Received Fix top machine from break status");
		topMachine.msgFixBreakGlassStatus();
		
	}
	@Override
	public void msgBottomMachineFixGlass() {
		// TODO Auto-generated method stub
		System.out.println("Received Fix bottom machine from break status");
		bottomMachine.msgFixBreakGlassStatus();
	}

}
