package engine.agent.family5.conveyorFamilyUVLAMP;

import engine.agent.Agent;




//import engine.agent.family0.MachineAgent0.GlassState;
import engine.agent.family5.interfaces.Oven;
import shared.*;
import shared.enums.Status;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class UVLampAgent6 extends Agent implements Oven{

	Glass tempGlass, glassOnUVLamp;
	ConveyorAgent6 conveyor11;
	ConveyorAgent7 conveyor12;
	
	
	ConveyorFamily6 nextConveyorFamily;
	private Transducer transducer;
	
	boolean nextConveyorBusy = false;
	boolean glassLoaded = false;
	boolean uvFinished = false;
	boolean glassReleased = false;
	boolean turnedOn; 
	boolean malfunction;
	boolean breakGlass; 
	boolean canRelease;
	
	int index;
	
	public UVLampAgent6(String name, int index) {
		this.name = name;
		this.index = index;
		// TODO Auto-generated constructor stub
	}
	
	
	public enum GlassStateOnUVLamp { NEW, LOADING, LOADED, BEING_TREATED, TREATMENT_DONE, GIVEN_BACK, BROKEN, BEING_BROKEN }
	
	
	class MyGlass{
		Glass glass;
		GlassStateOnUVLamp gState;
		
		public MyGlass(Glass g){
			glass = g;
			gState = GlassStateOnUVLamp.NEW;
		}
		
		
	}
	
	MyGlass currentGlass = new MyGlass(tempGlass);
	 
	
	public void setComposition(ConveyorAgent6 conveyor11, ConveyorAgent7 conveyor12,
			Transducer transducer) {
		// TODO Auto-generated method stub
		this.conveyor11 = conveyor11;
		this.conveyor12 = conveyor12;
		this.transducer = transducer;
	
		transducer.register(this, TChannel.UV_LAMP);
	}
	
	
	
	
	
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		tempGlass = glass;
		currentGlass = new MyGlass(glass);
		conveyor11.msgBusyToGetGlass(Status.BUSY);
		this.turnedOn = true;
		stateChanged();
	}
	
	/*private void msgUVLampLoaded() {
		// TODO Auto-generated method stub
		glassOnUVLamp = tempGlass;
		currentGlass = new MyGlass(glassOnUVLamp);
		conveyor11.msgBusyToGetGlass(Status.BUSY);
		//stateChanged();
		
		
		tempGlass = null;
		glassLoaded = true;
		stateChanged();	
	} */
	
	public void msgUVFinished(){
		uvFinished = true;
		stateChanged();
	}
	
	public void msgGlassReleased(){
		glassReleased = false;
		stateChanged();
	}
	
	
	

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
		
		if (currentGlass == null){
			return false; 
		}
		/* if (currentGlass.gState == GlassStateOnUVLamp.NONE){
			if(turnedOn){
				currentGlass.gState = GlassStateOnUVLamp.NEW;
			}
			
			return true;
		}	*/
		if (currentGlass.gState == GlassStateOnUVLamp.NEW){
			loadGlass();
			return true;
		}
		if(turnedOn){
			if (currentGlass.gState == GlassStateOnUVLamp.LOADED){
				if(!malfunction && breakGlass){
					breakGlass();
				
				}
				if(!malfunction && !breakGlass)
				{
					if (currentGlass.glass.getRecipe().recipe[this.index] == false){
						releaseGlass();
						return true;
					}
					
					
					treatGlass();
				
				}
				return true;
			}
			
			if(currentGlass.gState == GlassStateOnUVLamp.BROKEN){
				clearGlass();
				return true;
			}
			
			if (currentGlass.gState == GlassStateOnUVLamp.TREATMENT_DONE  && nextConveyorBusy == false){
			
				releaseGlass();
		
				return true;
			}
			
			
		}	
		return false;
		
		
		
		
		
		
		
	}

	





	private void loadGlass(){
		print("I'm loading glass");
		transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_DO_LOAD_GLASS, null);
		currentGlass.gState = GlassStateOnUVLamp.LOADING;
		
	}
	
	private void treatGlass(){
		print("I'm treating glass");
		transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_DO_ACTION, null);
		currentGlass.gState = GlassStateOnUVLamp.BEING_TREATED;
	}
	
	private void releaseGlass(){
		print("Sending glass to next conveyor family");
		transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_RELEASE_GLASS, null);
		currentGlass.gState = GlassStateOnUVLamp.GIVEN_BACK;
		conveyor12.msgHereIsGlass(tempGlass);
		conveyor11.msgReadyToGetGlass(Status.READY);
		
	}
	
	private void breakGlass(){
		print("Breaking Glass");
		transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_DO_BREAK_GLASS, null);
		currentGlass.gState = GlassStateOnUVLamp.BEING_BROKEN;
	}
	
	private void clearGlass(){
		print("Clear Glass");
		breakGlass = false;
		currentGlass = null;
	}
	
	



	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
		
		if (channel == TChannel.UV_LAMP){
			
			if (event == TEvent.WORKSTATION_LOAD_FINISHED){
				if (currentGlass.gState != GlassStateOnUVLamp.LOADING) {
					System.err.println("Load finished mismatch. state = " + currentGlass.gState);
				} 
				currentGlass.gState = GlassStateOnUVLamp.LOADED;
				//msgUVLampLoaded();
				stateChanged();
			}
			
			if(event == TEvent.WORKSTATION_FINISHED_BREAKING_GLASS){
				print("Glass is Broken");
				currentGlass.gState = GlassStateOnUVLamp.BROKEN;
				//myStatus = Status.READY;
				conveyor11.msgReadyToGetGlass(Status.READY);
				stateChanged();
			}
			if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED){
				if (currentGlass.gState != GlassStateOnUVLamp.BEING_TREATED) {
					System.err.println("Action finished mismatch. state = " + currentGlass.gState);
				}
				currentGlass.gState = GlassStateOnUVLamp.TREATMENT_DONE;
				stateChanged();
			}
			
			
			if (event == TEvent.WORKSTATION_RELEASE_FINISHED){
				if (currentGlass.gState != GlassStateOnUVLamp.GIVEN_BACK) {
					System.err.println("Release finished mismatch. state = " + currentGlass.gState);
				}
				conveyor12.msgHereIsGlass(tempGlass);
				//currentGlass = null;
				//conveyor11.msgReadyToGetGlass(Status.READY); //).msgMachineStatusChanged(myStatus);
				stateChanged();
			}
			
	
		}
		
		
	}

	public void msgBreakGlass(){
		print("Set to break glass");
		breakGlass = true;
		stateChanged();
	}
	
	
	public void msgMachineMalfunction(){
		print("Malfunctioning");
		malfunction = true;
		stateChanged();
	}
	
	
	public void msgMachineMalfunctionFixed(){
		print("Is now working");
		malfunction = false;
		stateChanged();
	}


	public void msgTurnOff() {
		// TODO Auto-generated method stub
		print("Turning Off");
		turnedOn = false;
		stateChanged();
	}





	public void msgTurnOn() {
		// TODO Auto-generated method stub
		print("Turning On");
		turnedOn = true;
		stateChanged();
	}





	public void msgBreakUVLamp() {
		// TODO Auto-generated method stub
		print("UVLamp broken.");
		breakGlass = true;
		stateChanged();
	}





	public void msgFixUVLamp() {
		// TODO Auto-generated method stub
		print("UVLamp fixed.");
		breakGlass = false;
		stateChanged();
	}

	

}
