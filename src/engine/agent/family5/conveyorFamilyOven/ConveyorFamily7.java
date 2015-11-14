package engine.agent.family5.conveyorFamilyOven;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.Transducer;
import engine.agent.family4.ConveyorFamily4;
import engine.agent.family5.conveyorFamilyUVLAMP.ConveyorFamily6;
public class ConveyorFamily7 implements ConveyorFamily {

	
	private ConveyorFamily previousFamily;
	//private ConveyorFamily nextFamily;
	private Transducer transducer;
	private ConveyorAgent8 conveyor13;
	private OvenAgent6 oven;
	
	private ConveyorAgent9 conveyor14;
	
	public Status conveyorFamilyStatus;
	public int index;
	
	/* CONSTRUCTOR */
	public ConveyorFamily7(int index, Transducer transducer){
		
		this.index = index;
		this.transducer = transducer;
		conveyorFamilyStatus = Status.READY;
		
		
	}
	
	

	
	
	
	public void setConveyorAndOven(ConveyorAgent8 conveyor13,ConveyorAgent9 conveyor14, OvenAgent6 oven) {
		// TODO Auto-generated method stub
		this.conveyor13 = conveyor13;
		this.conveyor14 = conveyor14;
		this.oven = oven;
	}
	
	/* Setter */
	public void setAgentsComps(){
		conveyor13.setComposition(previousFamily, oven, transducer);
		oven.setComposition(conveyor13,conveyor14,  transducer);
		
	}
	
	/* Getter */
	public ConveyorAgent8 getConveyorFirst(){
		return conveyor13;
	}
	
	public ConveyorAgent9 getConveyorSecond(){
		return conveyor14;
	} 
	
	/* Getter */
	public OvenAgent6 getOven(){
		return oven;
	}
	
	/* public ConveyorFamily getNextConveyorFamily(){
		return nextFamily;
	} */
    
	@Override
	public void msgHereIsGlass( Glass glass ) {
		//System.out.println("HereIsGlass");
		conveyor13.msgHereIsGlass( glass );
	}
	
	
	
	@Override
	public void msgStatus(Status s) {
		// TODO Auto-generated method stub
		this.conveyorFamilyStatus = s;
		conveyor14.msgStatus(s);
	}
	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		//TODO
		conveyor13.stopConveyor();
		conveyor14.stopConveyor();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
		conveyor13.startConveyor();
		conveyor14.startConveyor();
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
		oven.msgBreakOven();
	}
	
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		//TODO
		oven.msgFixOven();
	}



	public void setPreviousConveyorFamily(ConveyorFamily6 conveyorFamily6) {
		// TODO Auto-generated method stub
		this.previousFamily = conveyorFamily6;
	}
	public void setTruck() {
		// TODO Auto-generated method stub
		//this.nextFamily = nextFamily;
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
