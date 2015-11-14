package engine.agent.family3;

import engine.agent.Agent;
import shared.Glass;
import shared.interfaces.ConveyorFamily;
import engine.agent.family3.interfaces.Sensor;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class SensorAgent3 extends Agent implements Sensor, TReceiver{
	
	//OBJECTS
	int index;
	public ConveyorFamilyAgent3 confam;
	public Glass glass; //glass class imported from Github.
	public enum State {pressed,released,stopping};
	public State state;
	public enum FactoryState {stop, go};
	public FactoryState factoryState;
	
	public SensorAgent3(){
	factoryState = FactoryState.go;
	name = "Sensor";
	};
	
	public SensorAgent3(ConveyorFamilyAgent3 cf, Transducer t, int i){
		super("Sensor", t);
		confam = cf;
		factoryState = FactoryState.go;
        index = i;
        System.out.println("Sensor 3 Initialized!");
        
        transducer.register(this, TChannel.SENSOR);
        transducer.register(this, TChannel.POPUP);
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	//MESSAGES
	public void msgHereIsGlass(Glass g){
		glass = g;
		if (glass != null)
			System.out.println("SENSOR 3 GLASS NOT NULL");
		//State changed Glass Pressed was here
		stateChanged();
	}
	
	public void msgReleaseGlass(Glass g){
		if (glass == g){
		state = State.released;
		stateChanged();
			
		}
	}
	
	public void msgPressState(){
		state = State.pressed;
		print("Sensor 3 in Pressed State");
		stateChanged();
	}
	
	public void haltLine(){
		factoryState = FactoryState.stop;
		stateChanged();
	}
	
	public void msgReady(){
		factoryState = FactoryState.go;
		stateChanged();
	}
	
	//FINITE STATE MACHINE
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (glass != null)
		print("INSIDE SCHEDULER" + index);
		else
			print ("Glass Null");
		if (factoryState == FactoryState.go){
			if (state == State.pressed){
				//print("I'M IN THE RIGHT PLACE SENSOR 3 NO ACTION BEING CALLED");
				doReleaseGlass();
				return true;
			}
			if (state == State.released && glass != null){
				//doReleaseGlass();
				return true;
			}
		}
		if (factoryState == FactoryState.stop){
			doHaltFactory();
			return true;
		}	
		return false;
	}
	
	
	//ACTIONS
	public void doBringGlass(){
	}
	public void doReleaseGlass(){
		if (confam.sensor1.index == this.index && glass != null){
			confam.conveyor.msgHereIsGlass(glass);
			glass = null;
			state = State.released;
		}
		else if (confam.sensor1.index != this.index && glass != null) {
			confam.popup.msgHereIsGlass(glass);
			glass = null;
			state = State.released;
		}
		
		stateChanged();
	}
	
	public void doHaltFactory(){
		confam.previous.msgStatus(confam.nextStatus.BUSY);
		stateChanged();
	};
	
	
	//TRANSDUCER MESSAGES
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
		if (TChannel.SENSOR == channel){
			if (index == (Integer)args[0]){
				if (TEvent.SENSOR_GUI_PRESSED == event){
					this.msgPressState();
					System.out.println("SENSOR 3 ENTRY PRESSED" + " " + index);
					//stateChanged();
				}
				if (TEvent.SENSOR_GUI_RELEASED == event){
					state = State.released;
					doReleaseGlass();
					System.out.println("SENSOR 3 EXIT IS GO" + " " + index);
					stateChanged();
				}
			}
		}
		
		//Sensor will tell agent when glass is released. Then you change state with a message.
		//state == released
		
	}

}
