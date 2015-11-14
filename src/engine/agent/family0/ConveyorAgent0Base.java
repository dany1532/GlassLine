package engine.agent.family0;

import java.util.ArrayList;
import java.util.List;

import engine.agent.Agent;
import shared.*;
import shared.enums.Status;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public abstract class ConveyorAgent0Base extends Agent {

	public enum Event { FRONT_SENSOR_FIRED, BACK_SENSOR_FIRED}	
	public enum GlassState { GLASS_LOADING, GLASS_AT_END, GLASS_ON_LINE_NOT_AT_END, NO_GLASS_ON_LINE }
	public enum MovingState { MOVING, STOPPED }

	public GlassState glassState;
	public MovingState movingState;

	public ConveyorFamily0 container;
	
	public List<Glass> myGlass;
	public List<Event> events;
	public int guiConveyorIndex;
	
	
	public int glassCount = 0;
	
	boolean conveyorBroken;
	boolean conveyorFull;

	public ConveyorAgent0Base(String name, Transducer t, int gci){
		super(name,t);		
		guiConveyorIndex = gci;
		
		myGlass = new ArrayList<Glass>();
		events = new ArrayList<Event>();
		
		glassState = GlassState.NO_GLASS_ON_LINE;
		movingState = MovingState.STOPPED;
		
		conveyorBroken = false;
		conveyorFull = false;
		
		transducer.register(this, TChannel.SENSOR);
		if (gci == 0){
			transducer.register(this, TChannel.CUTTER);
			transducer.register(this, TChannel.BIN);
		}
		if (gci == 2){
			transducer.register(this, TChannel.BREAKOUT);
		}
		if (gci == 3){
			transducer.register(this, TChannel.MANUAL_BREAKOUT);
		}
	}
	
	public void setConveyorFamily(ConveyorFamily0 c){
		container = c;
	}

	public void msgGlassConveyorFamilyToConveyor(Glass g) {
		myGlass.add(g);
		stateChanged();
	}
	
	public void msgNextStatusChanged(){
		// next component status is different, check if a new scheudler rule applies
		// (abstract getNextComponentStatus takes care of everything)
		stateChanged();
	}
	
	public void msgNonNormStopConveyor(){
		conveyorBroken = true;
		stateChanged();
	}
	
	public void msgNonNormStartConveyor(){
		conveyorBroken = false;
		stateChanged();
	}
	
	public abstract void msgNonNormBreakMachine();
	
	public abstract void msgNonNormFixMachine();

	public abstract void msgNonNormInlineMachineExpectationFailure();
	
	public void msgNonNormSetConveyorFull(){
		conveyorFull = true;
		container.msgGiveStatusToPrevious(Status.BUSY);
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if (channel == TChannel.SENSOR){
			
			if (event == TEvent.SENSOR_GUI_PRESSED){
				
				// front sensor
				if (((Integer)args[0]).equals(guiConveyorIndex*2)){
					print("My front sensor is pressed.");
					if (glassState == GlassState.NO_GLASS_ON_LINE){
						glassState = GlassState.GLASS_ON_LINE_NOT_AT_END;
					}
					stateChanged();
				}
				
				// back sensor
				if (((Integer)args[0]).equals(guiConveyorIndex*2 + 1)){
					print("My back sensor is pressed.");
					glassState = GlassState.GLASS_AT_END;
					stateChanged();
				}			
				
			}
			
			if (event == TEvent.SENSOR_GUI_RELEASED){
				
				// front sensor
				if (((Integer)args[0]).equals(guiConveyorIndex*2)){
					print("My front sensor is released.");
					stateChanged();
				}
				
				// back sensor
				if (((Integer)args[0]).equals(guiConveyorIndex*2 + 1)){
					print("My back sensor is released.");
					stateChanged();
				}			
			}

			
		}
		
		if (channel == TChannel.BIN){
			
			if (event == TEvent.BIN_CREATE_PART){
				// only the first conveyor can hear this message -- see constructor
				// simulate calling message on self to initiate first glass
				
				Recipe r = (Recipe) args[0];
				
				this.msgGlassConveyorFamilyToConveyor(new Glass(r, glassCount++));
			}
			
		}
		
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {

		if (conveyorBroken){
			
			if (movingState == MovingState.MOVING){
				stopConveyor();
				return true;
			}
			
			// conveyor is broken, nothing to do
			return false;
		}
		
		// Pass any glass that is ready to go
		if (glassState == GlassState.GLASS_AT_END && getNextComponentStatus() == Status.READY){
			
			if (movingState == MovingState.STOPPED){
				startConveyor();
				return true;
			}
			
			sendGlassToNext();
			return true;
		}
		
		// Stop if glass wasnt sent
		if (movingState == MovingState.MOVING && glassState == GlassState.GLASS_AT_END){
			stopConveyor();
			return true;
		}
			
		// Start if glass on line
		if (movingState == MovingState.STOPPED && (glassState == GlassState.GLASS_ON_LINE_NOT_AT_END || glassState == GlassState.GLASS_LOADING)){
			startConveyor();
			return true;
		}
		
		System.out.println("CONVEYOR BASE " + guiConveyorIndex + ": No rule applies. MovingState = " + movingState + ", GlassState = " + glassState);

		return false;
	}

	protected void startConveyor(){
		print("Starting Conveyor");
		movingState = MovingState.MOVING;
		
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		
		container.msgGiveStatusToPrevious(Status.READY);
	}
	
	private void stopConveyor(){
		print("Stopping Conveyor");
		movingState = MovingState.STOPPED;
		
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
	
		container.msgGiveStatusToPrevious(Status.BUSY);
	}
	
	protected abstract void sendGlassToNext();

	protected abstract Status getNextComponentStatus();
}
