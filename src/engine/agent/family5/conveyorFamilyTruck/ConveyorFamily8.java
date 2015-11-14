package engine.agent.family5.conveyorFamilyTruck;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.Transducer;
import engine.agent.family4.ConveyorFamily4;
import engine.agent.family5.conveyorFamilyOven.ConveyorFamily7;

public class ConveyorFamily8 implements ConveyorFamily {

	
	private ConveyorFamily previousFamily;
	//private ConveyorFamily nextFamily;
	private Transducer transducer;
	//private ConveyorAgent8 conveyor13;
	private TruckAgent6 truck;
	
	//private ConveyorAgent9 conveyor14;
	
	
	
	/* CONSTRUCTOR */
	public ConveyorFamily8(Transducer transducer){
		
		//this.nextFamily = nextFamily;
		this.transducer = transducer;
		
		
		
	}
	
	

	
	@Override
	public void msgHereIsGlass( Glass glass ) {
		System.out.println("HereIsGlass");
		
		truck.msgHereIsGlass( glass );
	}
	
	
	/*
	public void setConveyorAndOven(ConveyorAgent8 conveyor13,ConveyorAgent9 conveyor14, OvenAgent6 oven) {
		// TODO Auto-generated method stub
		this.conveyor13 = conveyor13;
		this.conveyor14 = conveyor14;
		this.oven = oven;
	} */
	
	public void setTruck(TruckAgent6 truck){
		
		this.truck = truck;
	}
	
	/* Setter */
	public void setAgentsComps(){
		//conveyor13.setComposition(previousFamily, oven, transducer);
		//oven.setComposition(conveyor13,conveyor14,  transducer);
		truck.setComposition(previousFamily, transducer);
	}
	
	/* Getter */
	/*public ConveyorAgent8 getConveyorFirst(){
		return conveyor13;
	} 
	
	public ConveyorAgent9 getConveyorSecond(){
		return conveyor14;
	}  */
	
	/* Getter */
	public TruckAgent6 getTruck(){
		return truck;
	}
	
	/* public ConveyorFamily getNextConveyorFamily(){
		return nextFamily;
	} */

	@Override
	public void msgStatus(Status s) {
		// TODO Auto-generated method stub
		
		//conveyor14.msgReadyToGetGlass();
	}
	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		//TODO
		truck.msgStopTruck();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
		truck.msgStartTruck();
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



	public void setPreviousConveyorFamily(ConveyorFamily7 conveyorFamily7) {
		// TODO Auto-generated method stub
		this.previousFamily = conveyorFamily7;
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
		// TODO Auto-generated method stub
		
	}




	@Override
	public void msgBottomMachineBreaksGlass() {
		// TODO Auto-generated method stub
		
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
		
	}




	@Override
	public void msgBottomMachineFixGlass() {
		// TODO Auto-generated method stub
		
	}
	



	

	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
