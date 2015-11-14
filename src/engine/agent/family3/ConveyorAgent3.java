package engine.agent.family3;

import java.util.ArrayList;

import engine.agent.Agent;

import shared.Glass;
import shared.enums.Status;
import engine.agent.family3.interfaces.Conveyor;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class ConveyorAgent3 extends Agent implements Conveyor {
	
	int index; //Conveyor, Sensor, and Popups all have an index for args[].
	public ConveyorFamilyAgent3 confam;
	public  ArrayList<Glass> glass;
	public enum State {stopped, go, active};
	public State state;
	
	public ConveyorAgent3(){
		confam = null;
		name = "Conveyor";
		state = state.go;
		glass = new ArrayList<Glass>();
	}
	
	public ConveyorAgent3(ConveyorFamilyAgent3 cf, Transducer t, int i){
		super("Conveyor", t);
		confam = cf;
		state = state.go;
		glass = new ArrayList<Glass>();
		index = i;
		
		//Listen to these channels
		getTransducer().register(this, TChannel.CONVEYOR);
		getTransducer().register(this, TChannel.SENSOR);
		
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
	
		
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	//MESSAGES
	public void msgHereIsGlass(Glass g){
		glass.add(g);
		state = State.go;
		print("WHAT AM I DOING, CONVEYOR3");
		
		
		
		stateChanged();
	}
	
	@Override
	public void msgStopTheLine() {
		state = State.stopped;
		stateChanged();
	}

	@Override
	public void msgContinueTheLine() {
		state = State.go;
		stateChanged();
	}

	//FINITE STATE MACHINE
	@Override
	public boolean pickAndExecuteAnAction() {
		if(state == state.go && !glass.isEmpty()){
			doContinueLine();
			return true;
		}
		if (state == state.stopped){
			doHaltLine();
			return true;
		}
		return false;
	}
	
	//ACTIONS
	public void doContinueLine(){
		//animation call;
		Status status = Status.READY;
		confam.sendMessageBack(status);
		
		state = state.active;
		
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
	}
	
	public void doHaltLine(){
		//animation call;
		Status status = Status.BUSY;
		confam.sendMessageBack(status);
		
		Integer[] args = new Integer[1];
		args[0] = index;
		getTransducer().fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
	}

	public void sendGlassOf(){
		confam.sensor2.msgHereIsGlass(glass.get(0));
		glass.remove(0);
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (TChannel.SENSOR == channel){
			if (confam.sensor2.index == (Integer)args[0]){
				if (TEvent.SENSOR_GUI_PRESSED == event){
					sendGlassOf();
				}
			}
			
			if (confam.sensor1.index == (Integer)args[0]){
				if (TEvent.SENSOR_GUI_PRESSED == event){
					//state = State.stopped;
				}
				if (TEvent.SENSOR_GUI_RELEASED == event){
					state = State.go;
				}
			}
		}
		
		
		
	}

}
