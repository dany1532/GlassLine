package engine.agent.family5.conveyorFamilyUVLAMP;


import java.util.ArrayList;
import java.util.List;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.agent.Agent;
import shared.*;
import shared.enums.*;
import shared.interfaces.ConveyorFamily;


import engine.agent.family5.interfaces.Conveyor;
import engine.agent.family5.*;


public class ConveyorAgent6 extends Agent implements Conveyor, TReceiver{
	
	private ConveyorFamily previousConveyorFamily;
	private Transducer transducer;
	private UVLampAgent6 uvLamp;
	public int guiSensorIndex;
	//private CopyOnWriteArrayList<SensorEvent> sensorEvents = new CopyOnWriteArrayList<SensorEvent>();
	//private CopyOnWriteArrayList<Glass> glassesOnConveyor = new CopyOnWriteArrayList<Glass>();
	public int guiConveyorIndex;
	
	private List<Glass> glassesOnConveyor = new ArrayList<Glass>();
	private List<SensorEvent> sensorEvents = new ArrayList<SensorEvent>();
	Glass tempGlass, glassOnStartSensor,glassOnEndSensor;
	String name;
	
	boolean uvLampIsReadyToGetGlass = true;
	
	
	public ConveyorAgent6(String name, int guiSensorIndex, int guiConveyorIndex){
		//super();
		this.name = name;
		this.guiSensorIndex = guiSensorIndex;
		this.guiConveyorIndex = guiConveyorIndex;
	}
	
	
	
	public void setComposition
	(		
			ConveyorFamily previousFamily, 
			UVLampAgent6 uvLamp,
			Transducer transducer 
									) {
		this.previousConveyorFamily = previousFamily;
		this.uvLamp  = uvLamp;
		this.transducer= transducer;
	
		transducer.register(this, TChannel.SENSOR);
	}
	
	private void msgSensorReleased(SensorPosition p) {
		// TODO Auto-generated method stub
		sensorEvents.add( new SensorEvent( p, SensorEventType.SENSOR_RELEASED ) );
		
		if( p == SensorPosition.START ){
			glassesOnConveyor.add( glassOnStartSensor );
			glassOnStartSensor = null;
		}
		else if( p == SensorPosition.END ){
			glassOnEndSensor = null;
			//glassSetupReady = true;
		}
		stateChanged();
		
	}

	private void msgSensorPressed(SensorPosition position) {
		// TODO Auto-generated method stub
		sensorEvents.add( new SensorEvent( position, SensorEventType.SENSOR_PRESSED ) );
		//print("sensor event added");
		if( position == SensorPosition.START ){
			glassOnStartSensor = tempGlass;
			print("glass on start sensor");
			tempGlass = null;
			//transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, null);   // ANIMATION
			//transducer.fireEvent(TChannel.SENSOR, TEvent.SENSOR_GUI_PRESSED, null);
		}
		else if( position == SensorPosition.END ){
			glassOnEndSensor = glassesOnConveyor.remove(0);
		}
		stateChanged();
		
		
		
	}
	
	//public void msgStatus
	
	//From oven
	public void msgReadyToGetGlass(Status status) {
		// TODO Auto-generated method stub
		uvLampIsReadyToGetGlass = true;
		stateChanged();
	}
	
	public void msgBusyToGetGlass(Status status) {
		// TODO Auto-generated method stub
		uvLampIsReadyToGetGlass = false;
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		//print("get msgHereIsGlass");
		
		tempGlass = glass;
		//print(""+guiSensorIndex);
		//transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, null);
		stateChanged();

	}
	
	@Override
	public void stopThread() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		//print("test1");
		if( !sensorEvents.isEmpty() ){
			//print("test2");
			if( sensorEvents.get(0).getEvent() == SensorEventType.SENSOR_PRESSED ){
				SensorPosition p = sensorEvents.remove(0).getType();
				if( p == SensorPosition.START ){
					if( glassOnEndSensor == null && 
					( glassesOnConveyor.size() != 0 || 
						glassOnStartSensor != null ) ){
						startConveyor();	
					}
					else
						stopConveyor();
				
				
				
				}
				else if( p == SensorPosition.END ){
					print("test " + uvLampIsReadyToGetGlass);
					if(!uvLampIsReadyToGetGlass){
						stopConveyor();
					}else{
						
						giveGlassToUVLamp();
					}
					
					
				}
				
				
			}
			else if( sensorEvents.get(0).getEvent() == SensorEventType.SENSOR_RELEASED ){
				
				SensorPosition p = sensorEvents.remove(0).getType();
				
				if( p == SensorPosition.START ){
					previousConveyorFamily.msgStatus(Status.READY);
				}
				else if( p == SensorPosition.END ){
					
				}
				
			}
			return true;
		}
		
		
		return false;
	}



	private void giveGlassToUVLamp() {
		// TODO Auto-generated method stub
		uvLamp.msgHereIsGlass(glassOnEndSensor);
		print("here is glass");
		
	}



	





	public void stopConveyor() {
		// TODO Auto-generated method stub
		
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);   // ANIMATION
		print("conveyor stoped");
	}

	public void startConveyor() {
		// TODO Auto-generated method stub
		
		Object[] args = new Object[1];
		args[0] = new Integer(guiConveyorIndex);
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		print("conveyor start");
	}

	
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if(channel == TChannel.SENSOR){
			if(guiSensorIndex == (Integer)args[0]){
				if (guiSensorIndex == 22){
					if (event == TEvent.SENSOR_GUI_PRESSED)
					{
						msgSensorPressed( SensorPosition.START );
						print("start sensor pressed " + guiSensorIndex);
						
					}
					else if (event == TEvent.SENSOR_GUI_RELEASED)
					{
						print("start sensor released " + guiSensorIndex );
						msgSensorReleased( SensorPosition.START);
						
						guiSensorIndex++;
					}
					
				}
				else if(guiSensorIndex == 23)
				{
					if (event == TEvent.SENSOR_GUI_PRESSED)
					{
						msgSensorPressed( SensorPosition.END );
						print("end sensor pressed " + guiSensorIndex); 
						
					}
					else if (event == TEvent.SENSOR_GUI_RELEASED)
					{
						msgSensorReleased( SensorPosition.END);
						print("end sensor released " + guiSensorIndex);
						
						guiSensorIndex++;
					}
				}
				
				
				
				
				
			}
			
			
			
		}
		
		
		

	}

	

	public String getName(){
		return name;
	}



	
	
	
	
	
	
	
}