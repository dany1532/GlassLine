package engine.agent.family5.conveyorFamilyOven;

import engine.agent.Agent;
import engine.agent.family5.interfaces.Oven;
import shared.*;
import shared.enums.Status;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class OvenAgent6 extends Agent implements Oven{

	Glass tempGlass, glassOnOven;
	ConveyorAgent8 conveyor13;
	ConveyorAgent9 conveyor14;
	
	
	//ConveyorFamily nextConveyorFamily;
	private Transducer transducer;
	
	boolean nextConveyorBusy = false;
	boolean glassLoaded = false;
	boolean uvFinished = false;
	boolean glassReleased = false;
	boolean turnedOn; 
	boolean malfunction;
	boolean breakGlass; 
	
	int index;
	public OvenAgent6(String name, int index) {
		this.name = name;
		this.index = index;
		// TODO Auto-generated constructor stub
	}
	
	
	public enum GlassStateOnOven { NEW, LOADING, LOADED, BEING_TREATED, TREATMENT_DONE, GIVEN_BACK, BEING_BROKEN, BROKEN }
	
	
	class MyGlass{
		Glass glass;
		GlassStateOnOven gState;
		
		public MyGlass(Glass g){
			glass = g;
			gState = GlassStateOnOven.NEW;
		}
		
		
	}
	
	MyGlass currentGlass = new MyGlass(tempGlass);
	 
	
	public void setComposition(ConveyorAgent8 conveyor13, ConveyorAgent9 conveyor14,
			Transducer transducer) {
		// TODO Auto-generated method stub
		this.conveyor13 = conveyor13;
		this.conveyor14 = conveyor14;
		this.transducer = transducer;
	
		transducer.register(this, TChannel.OVEN);
	}
	
	
	
	
	
	public void msgHereIsGlass(Glass glass) {
		// TODO Auto-generated method stub
		tempGlass = glass;
		currentGlass = new MyGlass(glass);
		conveyor13.msgBusyToGetGlass(Status.BUSY);
		this.turnedOn = true;
		stateChanged();
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
		
		if (currentGlass == null){
			return false; 
		}
			
		if (currentGlass.gState == GlassStateOnOven.NEW){
			loadGlass();
			return true;
		}

		if(turnedOn){
			if (currentGlass.gState == GlassStateOnOven.LOADED){
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
			
			if(currentGlass.gState == GlassStateOnOven.BROKEN){
				clearGlass();
				return true;
			}
			
			if (currentGlass.gState == GlassStateOnOven.TREATMENT_DONE  && nextConveyorBusy == false){
			
				releaseGlass();
		
				return true;
			}
		}	
		return false;
		
		
		
		
		
		
		
		
		
	}

	private void loadGlass(){
		print("I'm loading glass");
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_LOAD_GLASS, null);
		currentGlass.gState = GlassStateOnOven.LOADING;
	}
	
	private void treatGlass(){
		print("I'm treating glass");
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_ACTION, null);
		currentGlass.gState = GlassStateOnOven.BEING_TREATED;
	}
	
	private void releaseGlass(){
		print("Sending glass to next conveyor family");
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_RELEASE_GLASS, null);
		currentGlass.gState = GlassStateOnOven.GIVEN_BACK;
		//container.msgGiveGlassToNext(currentGlass.glass);
		conveyor14.msgHereIsGlass(tempGlass);
		conveyor13.msgReadyToGetGlass(Status.READY);
	}
	
	private void breakGlass(){
		print("Breaking Glass");
		transducer.fireEvent(TChannel.UV_LAMP, TEvent.WORKSTATION_DO_BREAK_GLASS, null);
		currentGlass.gState = GlassStateOnOven.BEING_BROKEN;
	}
	
	private void clearGlass(){
		print("Clear Glass");
		breakGlass = false;
		currentGlass = null;
	}
	

	



	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
		
		if (channel == TChannel.OVEN){
			
			if (event == TEvent.WORKSTATION_LOAD_FINISHED){
				if (currentGlass.gState != GlassStateOnOven.LOADING) {
					System.err.println("Load finished mismatch. state = " + currentGlass.gState);
				}
				currentGlass.gState = GlassStateOnOven.LOADED;
				stateChanged();
			}
			
			if(event == TEvent.WORKSTATION_FINISHED_BREAKING_GLASS){
				print("Glass is Broken");
				currentGlass.gState = GlassStateOnOven.BROKEN;
				//myStatus = Status.READY;
				conveyor13.msgReadyToGetGlass(Status.READY);
				stateChanged();
			}
			if (event == TEvent.WORKSTATION_GUI_ACTION_FINISHED){
				if (currentGlass.gState != GlassStateOnOven.BEING_TREATED) {
					System.err.println("Action finished mismatch. state = " + currentGlass.gState);
				}
				currentGlass.gState = GlassStateOnOven.TREATMENT_DONE;
				stateChanged();
			}
			
			
			if (event == TEvent.WORKSTATION_RELEASE_FINISHED){
				if (currentGlass.gState != GlassStateOnOven.GIVEN_BACK) {
					System.err.println("Release finished mismatch. state = " + currentGlass.gState);
				}
				conveyor14.msgHereIsGlass(tempGlass);
				//conveyor13.msgReadyToGetGlass(Status.READY); 
				stateChanged();
			}
			
	
		}
		
		
	}





	public void msgBreakOven() {
		// TODO Auto-generated method stub
		print("Oven broken.");
		breakGlass = true;
		stateChanged();
	}





	public void msgFixOven() {
		// TODO Auto-generated method stub
		print("Oven fixed.");
		breakGlass = false;
		stateChanged();
	}

	

}
