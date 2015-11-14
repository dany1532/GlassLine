package engine.agent.family2;

import engine.agent.family2.interfaces.Sensor2;
import shared.Glass;

import shared.enums.SensorPosition;
import shared.enums.Status;
import shared.interfaces.*;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class SensorAgent2 extends InTheFamilyAgent2 implements Sensor2{

	public SensorPosition pos = SensorPosition.MIDDLE;
	
	public SensorAgent2(String name, Transducer t, int myIndex){
		super(name, t);
		//this.cf = cf;
		myCapacity = 2;
		this.guiIndex = myIndex;
		getTransducer().register(this, TChannel.SENSOR);

	}
	
	public ConveyorFamily cf;
	public Glass g;


	@Override
	public void setPos(SensorPosition position) {
		pos = position;
	}


	@Override
	public SensorPosition getPos() {
		return pos;
	}

//	@Override
//	public boolean passGlassForward(){
//		if (nextStatus.state == Status.BUSY)
//			return false;
//		else{
//			if (myGlass.isEmpty()){
//				noGlassToPass();
//				return false;
//			}
//			else{
//				print("waiting to release gui before passing glass ahead");
//				thingAhead.msgHereIsGlass(getGlassToSend());
//			}
//			if (myStatus.state == Status.BUSY){
//				myStatus.setReady();
//				newMyStatus = true;
//				stateChanged();
//			}
//			return true;
//		}
//		
//	}
	
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		
		if (event == TEvent.SENSOR_GUI_PRESSED)
		{
			if(guiIndex == (Integer)args[0]){
				print("I was pressed in GUI");
				this.msgGuiGotGlass();
				if (thingBehind != null)
					thingBehind.msgGlassAtEnd();
			}
		
		}
		if (event == TEvent.SENSOR_GUI_RELEASED)
		{
			if(guiIndex == (Integer)args[0]){
				print("I was released in GUI");
				if (thingAhead != null)
					thingAhead.msgGuiGotGlass();
				this.msgGlassAtEnd();
			}
		
		}
		
	}

}
