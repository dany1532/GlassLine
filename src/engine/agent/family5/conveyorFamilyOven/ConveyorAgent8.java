package engine.agent.family5.conveyorFamilyOven;


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


import engine.agent.family5.SensorEvent;
import engine.agent.family5.SensorEventType;
import engine.agent.family5.interfaces.Conveyor;



public class ConveyorAgent8 extends Agent implements Conveyor, TReceiver{
	
	private ConveyorFamily previousConveyorFamily;
	private Transducer transducer;
	private OvenAgent6 oven;
	public int guiSensorIndex;
	//private CopyOnWriteArrayList<SensorEvent> sensorEvents = new CopyOnWriteArrayList<SensorEvent>();
	//private CopyOnWriteArrayList<Glass> glassesOnConveyor = new CopyOnWriteArrayList<Glass>();
	public int guiConveyorIndex;
	
	private List<Glass> glassesOnConveyor = new ArrayList<Glass>();
	private List<SensorEvent> sensorEvents = new ArrayList<SensorEvent>();
	Glass tempGlass, glassOnStartSensor,glassOnEndSensor;
	String name;
	
	boolean ovenIsReadyToGetGlass = true;
	
	
	public ConveyorAgent8(String name, int guiSensorIndex, int guiConveyorIndex){
		//super();
		this.name = name;
		this.guiSensorIndex = guiSensorIndex;
		this.guiConveyorIndex = guiConveyorIndex;
	}
	
	
	
	public void setComposition
	(		
			ConveyorFamily previousFamily, 
			OvenAgent6 oven,
			Transducer transducer 
									) {
		this.previousConveyorFamily = previousFamily;
		this.oven  = oven;
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
		}
		else if( position == SensorPosition.END ){
			glassOnEndSensor = glassesOnConveyor.remove(0);
		}
		stateChanged();
		
		
		
	}
	
	
	
	//From oven
	public void msgReadyToGetGlass(Status status) {
		// TODO Auto-generated method stub
		ovenIsReadyToGetGlass = true;
		stateChanged();
	}
	
	public void msgBusyToGetGlass(Status status) {
		// TODO Auto-generated method stub
		ovenIsReadyToGetGlass = false;
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass){
		print("get msgHereIsGlass");
		
		tempGlass = glass;
		print(""+ guiSensorIndex);
		stateChanged();

	}
	
	@Override
	public void stopThread() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if( !sensorEvents.isEmpty() ){
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
					//asking to next guy?
					if(!ovenIsReadyToGetGlass){
						stopConveyor();
					}else{
						
						giveGlassToOven();
					}
					
					
				}
				
				
				//if( ovenIsReadyToGetGlass){
				//	GiveGlassToOven();
				//	return true;
				//}
				
				//pressSensor();
			}
			else if( sensorEvents.get(0).getEvent() == SensorEventType.SENSOR_RELEASED ){
				
				SensorPosition p = sensorEvents.remove(0).getType();
				
				if( p == SensorPosition.START ){
					previousConveyorFamily.msgStatus(Status.READY);
				}
				else if( p == SensorPosition.END ){
					/*new Timer().schedule(new TimerTask(){
						public void run(){
							conveyorControlAction();
						}
					}, 200); */
				}
				
			}
			return true;
		}
		
		
		return false;
	}



	private void giveGlassToOven() {
		// TODO Auto-generated method stub
		oven.msgHereIsGlass(glassOnEndSensor);
		
		
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
				if (guiSensorIndex == 26){
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
				else if(guiSensorIndex == 27)
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