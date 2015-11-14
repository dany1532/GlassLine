package engine.agent.family5;

import shared.enums.*;
import engine.agent.family5.*;


public class SensorEvent {
	
	SensorPosition position;
	SensorEventType event;
	
	/* CONSTRUCTOR */
	public SensorEvent( SensorPosition position, SensorEventType event ){
		this.position = position;
		this.event = event;
	}
	
	/* Getter */
	public SensorPosition getType(){
		return position;
	}
	
	/* Getter */
	public SensorEventType  getEvent(){
		return event;
	}
}
