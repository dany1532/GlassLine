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


import engine.agent.family5.conveyorFamilyOven.ConveyorFamily7;
import engine.agent.family5.interfaces.Conveyor;
import engine.agent.family5.*;


public class ConveyorAgent7 extends Agent implements Conveyor, TReceiver{
	
	
	private Transducer transducer;
	private UVLampAgent6 uvLamp;
	public int guiSensorIndex;
	//private CopyOnWriteArrayList<SensorEvent> sensorEvents = new CopyOnWriteArrayList<SensorEvent>();
	//private CopyOnWriteArrayList<Glass> glassesOnConveyor = new CopyOnWriteArrayList<Glass>();
	public int guiConveyorIndex;
	private ConveyorFamily7 nextFamily;
	private List<Glass> glassesOnConveyor = new ArrayList<Glass>();
	private List<SensorEvent> sensorEvents = new ArrayList<SensorEvent>();
	Glass tempGlass, glassOnStartSensor,glassOnEndSensor;
	String name;
	
	boolean nextConveyorFamilyIsReadyToGetGlass = true;
	
	
	
	public ConveyorAgent7(String name, int guiSensorIndex, int guiConveyorIndex){
		//super();
		this.name = name;
		this.guiSensorIndex = guiSensorIndex;
		this.guiConveyorIndex = guiConveyorIndex;
	}
	
	
	
	public void setComposition
	(		
			ConveyorFamily7 nextFamily, 
			UVLampAgent6 uvLamp,
			Transducer transducer 
									) {
		this.nextFamily = nextFamily;
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
		}
		else if( position == SensorPosition.END ){
			glassOnEndSensor = glassesOnConveyor.remove(0);
		}
		stateChanged();
		
		
		
	}
	

	
	
	public void msgHereIsGlass(Glass glass){
		//print("get msgHereIsGlass");
		
		tempGlass = glass;
		//print(""+guiSensorIndex);
		//transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, null);
		stateChanged();

	}
	
	public void msgStatus(Status s) {
		// TODO Auto-generated method stub
		
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
					//next conveyor family
					
					
				}
				
				
				if( nextConveyorFamilyIsReadyToGetGlass){
					GiveGlassToNextFamily();
					return true;
				}
				
				//pressSensor();
			}
			else if( sensorEvents.get(0).getEvent() == SensorEventType.SENSOR_RELEASED ){
				
				SensorPosition p = sensorEvents.remove(0).getType();
				
				if( p == SensorPosition.START ){
					//previousConveyorFamily.msgStatus(null);
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



	private void GiveGlassToNextFamily() {
		// TODO Auto-generated method stub
		nextFamily.msgHereIsGlass(glassOnEndSensor);
		
		
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
				if (guiSensorIndex == 24){
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
				else if(guiSensorIndex == 25)
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