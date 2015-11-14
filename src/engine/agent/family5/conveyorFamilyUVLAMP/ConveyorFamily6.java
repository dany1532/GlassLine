package engine.agent.family5.conveyorFamilyUVLAMP;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.Transducer;
import engine.agent.family4.ConveyorFamily4;
import engine.agent.family5.conveyorFamilyOven.ConveyorFamily7;
public class ConveyorFamily6 implements ConveyorFamily {

	
	private ConveyorFamily previousFamily;
	private ConveyorFamily7 nextFamily;
	private Transducer transducer;
	private ConveyorAgent6 conveyor11;
	private UVLampAgent6 uvLamp;
	
	private ConveyorAgent7 conveyor12;
	
	
	
	public Status conveyorFamilyStatus;
	public int index;
	/* CONSTRUCTOR */
	public ConveyorFamily6(int index, Transducer transducer){
		this.index = index;
		this.transducer = transducer;
		conveyorFamilyStatus = Status.READY;
		
		
	}
	
	
	
	
	
	public void setConveyorAndUVLamp(ConveyorAgent6 conveyor11,ConveyorAgent7 conveyor12, UVLampAgent6 uvLamp) {
		// TODO Auto-generated method stub
		this.conveyor11 = conveyor11;
		this.conveyor12 = conveyor12;
		this.uvLamp = uvLamp;
	}
	
	/* Setter */
	public void setAgentsComps(){
		conveyor11.setComposition(previousFamily, uvLamp, transducer);
		uvLamp.setComposition(conveyor11,conveyor12,  transducer);
		conveyor12.setComposition(nextFamily,uvLamp, transducer);
		
	}
	
	/* Getter */
	public ConveyorAgent6 getConveyorFirst(){
		return conveyor11;
	}
	
	public ConveyorAgent7 getConveyorSecond(){
		return conveyor12;
	} 
	
	/* Getter */
	public UVLampAgent6 getUVLamp(){
		return uvLamp;
	}
	
	public ConveyorFamily getNextConveyorFamily(){
		return nextFamily;
	}

	@Override
	public void msgHereIsGlass( Glass glass ) {
		//System.out.println("HereIsGlass");
		conveyor11.msgHereIsGlass( glass );
	}
	
	
	
	@Override
	public void msgStatus(Status s) {
		// TODO Auto-generated method stub
		//conveyor12.msgReadyToGetGlass();
		this.conveyorFamilyStatus = s;
		conveyor12.msgStatus(s);
		
	}
	// GUI stop conveyor for non-norms
	@Override
	public void msgStopConveyor(){
		//TODO
		conveyor11.stopConveyor();
		conveyor12.stopConveyor();
	}
	
	// GUI start conveyor for non-norms
	@Override
	public void msgStartConveyor(){
		//TODO
		conveyor11.startConveyor();
		conveyor12.startConveyor();
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
		uvLamp.msgTurnOff();
	}
	
	// GUI start online machine for non-norms
	@Override
	public void msgStartOnlineMachine(){
		//TODO
		uvLamp.msgTurnOn();
	}



	public void setPreviousConveyorFamily(ConveyorFamily4 previousFamily) {
		// TODO Auto-generated method stub
		this.previousFamily = previousFamily;
	}
	public void setNextConveyorFamily(ConveyorFamily7 nextFamily) {
		// TODO Auto-generated method stub
		this.nextFamily = nextFamily;
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
		uvLamp.msgBreakUVLamp();
	}


	@Override
	public void msgOnlineMachineFixExpectationFailure() {
		// TODO Auto-generated method stub
		uvLamp.msgFixUVLamp();
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
