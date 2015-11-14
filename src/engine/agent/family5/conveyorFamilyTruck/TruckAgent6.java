package engine.agent.family5.conveyorFamilyTruck;

import engine.agent.Agent;
import engine.agent.family5.conveyorFamilyOven.ConveyorAgent9;
import engine.agent.family5.conveyorFamilyOven.ConveyorFamily7;
import engine.agent.family5.interfaces.Oven;
import shared.*;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class TruckAgent6 extends Agent implements Oven{

	Glass tempGlass, glassOnTruck;
	//ConveyorAgent8 conveyor13;
	ConveyorAgent9 conveyor14;
	
	
	ConveyorFamily previousConveyorFamily;
	private Transducer transducer;
	boolean truckIsAvailable = true; 
	
	
	
	
	int index;
	public TruckAgent6(String name, int index) {
		this.name = name;
		this.index = index;
		// TODO Auto-generated constructor stub
	}
	
	
	public enum GlassStateOnTruck {NONE, LOADED, DERIVERING, DONE, BROKEN }
	
	class MyGlass{
		Glass glass;
		GlassStateOnTruck gState;
		
		public MyGlass(Glass g){
			glass = g;
			gState = GlassStateOnTruck.NONE;
		}
		
		
	}
	
	MyGlass currentGlass;// = new MyGlass(tempGlass);
	 
	
	public void setComposition(ConveyorFamily previousConveyorFamily,
			Transducer transducer) {
		// TODO Auto-generated method stub
		//this.conveyor13 = conveyor13;
		this.previousConveyorFamily = previousConveyorFamily;
		this.transducer = transducer;
	
		transducer.register(this, TChannel.TRUCK);
	}
	
	public void msgStopTruck(){
		truckIsAvailable = false;
		previousConveyorFamily.msgStatus(Status.BUSY);
		StopTruck();
		//conveyor14.BusyToGetGlass(Status.BUSY);
		//transducer.fireEvent(TChannel.TRUCK, TEvent., null);
	}
	

	public void msgStartTruck(){
		
		truckIsAvailable = false;
		previousConveyorFamily.msgStatus(Status.BUSY);
		StartTruck();
	}
	
	
	
	

	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		tempGlass = glass;

		conveyor14.BusyToGetGlass(Status.BUSY);
		
		stateChanged();
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
		
		if (currentGlass == null){
			
			return false; 
		}
		
		/*if (currentGlass.gState == GlassStateOnTruck.NONE && truckIsAvailable){
			loadGlass();
			//goDelivery();
			return true;
		}*/
		if (currentGlass.gState == GlassStateOnTruck.LOADED){
			goDelivery();
			//waitDeriveryDone();
			return true;
		}
		
		if (currentGlass.gState == GlassStateOnTruck.BROKEN){
			
			return true;
		}
		
		if (currentGlass.gState == GlassStateOnTruck.DERIVERING){
			//treatGlass();
			waitDeriveryDone();
			return true;
		}
		
		if (currentGlass.gState == GlassStateOnTruck.DONE){
			//treatGlass();
			readyToGetGlass();
			return true;
		} 
		
			
		return false;
		
		
		
		
		
		
		
	}

	






	private void readyToGetGlass() {
		// TODO Auto-generated method stub
		currentGlass.gState = GlassStateOnTruck.NONE;
		previousConveyorFamily.msgStatus(Status.READY);
		//conveyor14.ReadyToGetGlass(Status.READY);
	}





	private void waitDeriveryDone() {
		// TODO Auto-generated method stub
		truckIsAvailable = false;
		previousConveyorFamily.msgStatus(Status.BUSY);
		//conveyor14.BusyToGetGlass(Status.BUSY);
	
	}





	//private void treatGlass(){
	private void goDelivery(){
		print("I'm delivering glass");
		transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY, null);
		currentGlass.gState = GlassStateOnTruck.DONE;
		truckIsAvailable = false;
	}
	
	

	private void StopTruck() {
		// TODO Auto-generated method stub
		print("I'm broken");
		transducer.fireEvent(TChannel.TRUCK, TEvent.STOP, null);
		truckIsAvailable = false;
	}
	
	private void StartTruck() {
		// TODO Auto-generated method stub
		print("start again");
		transducer.fireEvent(TChannel.TRUCK, TEvent.START, null);
		truckIsAvailable = false;
	}
	



	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
		
		if (channel == TChannel.TRUCK){
			
			if (event == TEvent.TRUCK_GUI_LOAD_FINISHED){
				
				glassOnTruck = tempGlass;
				currentGlass = new MyGlass(glassOnTruck);
				currentGlass.gState = GlassStateOnTruck.LOADED;
				
				
				stateChanged();
			} 
				/*if (currentGlass.gState != GlassStateOnTruck.DERIVERING) {
				System.err.println("Action finished mismatch. state = " + currentGlass.gState);
			} */
			
			
			if (event == TEvent.TRUCK_GUI_EMPTY_FINISHED){
				if (currentGlass.gState != GlassStateOnTruck.NONE) {
					System.err.println("Load finished mismatch. state = " + currentGlass.gState);
				}
				currentGlass.gState = GlassStateOnTruck.NONE;
				print("ready");
				stateChanged();
			} 
			
			
			
		
		} 
		
		
	}

	

	

}
