package gui.panels;

import java.util.ArrayList;

import engine.agent.family0.*;
import engine.agent.family1.*;
import engine.agent.family2.*;
import engine.agent.family2.interfaces.Sensor2;
import engine.agent.family3.*;
import engine.agent.family4.ConveyorAgent4;
import engine.agent.family4.ConveyorFamily4;
import engine.agent.family4.MachineAgent4;
import engine.agent.family4.SensorAgent4;
import engine.agent.family4.enums.SensorType;
import engine.agent.family5.conveyorFamilyOven.*;

import engine.agent.family5.conveyorFamilyTruck.ConveyorFamily8;
import engine.agent.family5.conveyorFamilyTruck.TruckAgent6;
import engine.agent.family5.conveyorFamilyUVLAMP.*;


import gui.drivers.FactoryFrame;
import gui.v2.RecipeMaker;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import shared.Glass;
import shared.Recipe;
import shared.enums.SensorPosition;
import shared.interfaces.ConveyorFamily;
import transducer.*;

/**
 * The FactoryPanel is highest level panel in the actual kitting cell. The
 * FactoryPanel makes all the back end components, connects them to the
 * GuiComponents in the DisplayPanel. It is responsible for handing
 * communication between the back and front end.
 */
@SuppressWarnings("serial")
public class FactoryPanel extends JPanel
{
	
	RecipeMaker rm;
	public ArrayList<ConveyorFamily> conveyorFams = new ArrayList <ConveyorFamily>();
	
	
	
	/** The frame connected to the FactoryPanel */
	private FactoryFrame parent;

	/** The control system for the factory, displayed on right */
	private ControlPanel cPanel;//JKEVIN

	/** The graphical representation for the factory, displayed on left */
	private DisplayPanel dPanel;

	/** Allows the control panel to communicate with the back end and give commands */
	private Transducer transducer;

	/**
	 * Constructor links this panel to its frame
	 */
	public FactoryPanel(FactoryFrame fFrame)
	{
		parent = fFrame;

		// initialize transducer
		transducer = new Transducer();
		transducer.startTransducer();

		// use default layout
		// dPanel = new DisplayPanel(this);
		// dPanel.setDefaultLayout();
		// dPanel.setTimerListeners();

		// initialize and run
		this.initialize();
		this.initializeBackEnd();
	}

	/**
	 * Initializes all elements of the front end, including the panels, and lays
	 * them out
	 */
	private void initialize()
	{
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// initialize control panel
		cPanel = new ControlPanel(this, transducer);
		
		
		
		// initialize display panel
		dPanel = new DisplayPanel(this, transducer);

		// add panels in
		// JPanel tempPanel = new JPanel();
		// tempPanel.setPreferredSize(new Dimension(830, 880));
		// this.add(tempPanel);

		
		
		this.add(dPanel);
		//this.add(cPanel);
		
	}

	/**
	 * Feel free to use this method to start all the Agent threads at the same time
	 */
	private void initializeBackEnd()
	{
		// ===========================================================================
		// TODO initialize and start Agent threads here
		// ===========================================================================

		
		// *** OBJECT INSTANTIATION *** // 
		
		// From beginning to Cutter
		int conveyorIndex = 0;
		
		int machineDownIndex = 0;
		int machineUpIndex = 1;

		int popupIndex = 0;
		
		int upIndex = 0;
		int downIndex = 1;
		
		int sensorIndex = 0;
		
		ConveyorFamily0 conveyorFamily00 = new ConveyorFamily0(conveyorIndex);
		ConveyorAgent0WithMachine conveyor00 = new ConveyorAgent0WithMachine("Conveyor00",transducer,conveyorIndex);
		MachineAgent0 machine00 = new MachineAgent0("NCCutter",transducer,TChannel.CUTTER,0);
		conveyorIndex++;
		sensorIndex += 2;
				
		// From Cutter to first shuttle

		ConveyorFamily0 conveyorFamily01 = new ConveyorFamily0(conveyorIndex);
		ConveyorAgent0WithoutMachine conveyor01 = new ConveyorAgent0WithoutMachine("Conveyor01",transducer,conveyorIndex);
		// no machine
		conveyorIndex++;
		sensorIndex += 2;
		
		// From firt shuttle to breakout machine
		ConveyorFamily0 conveyorFamily02 = new ConveyorFamily0(conveyorIndex);
		ConveyorAgent0WithMachine conveyor02 = new ConveyorAgent0WithMachine("Conveyor02",transducer,conveyorIndex);
		MachineAgent0 machine02 = new MachineAgent0("Breakout",transducer,TChannel.BREAKOUT,1);
		conveyorIndex++;
		sensorIndex += 2;
		
		// From breakout machine to manual breakout
		ConveyorFamily0 conveyorFamily03 = new ConveyorFamily0(conveyorIndex);
		ConveyorAgent0WithMachine conveyor03 = new ConveyorAgent0WithMachine("Conveyor03",transducer,conveyorIndex);
		MachineAgent0 machine03 = new MachineAgent0("Manual Breakout",transducer,TChannel.MANUAL_BREAKOUT,2);
		conveyorIndex++;
		sensorIndex += 2;
		
		// From manual breakout to second shuttle
		ConveyorFamily0 conveyorFamily04 = new ConveyorFamily0(conveyorIndex);
		ConveyorAgent0WithoutMachine conveyor04 = new ConveyorAgent0WithoutMachine("Conveyor04",transducer,conveyorIndex);
		conveyorIndex++;
		sensorIndex += 2;
		// no machine
		
		// From second shuttle to Drill
		//ConveyorFamily1 Initialization
		ConveyorFamily1 conveyorFamily1 = new ConveyorFamily1(conveyorIndex);
		ConveyorAgent1 conveyor1 = new ConveyorAgent1("Conveyor1",transducer, cPanel.getTracePanel(),conveyorIndex);
		conveyorIndex++;
		SensorAgent1 sensorEntrance1 = new SensorAgent1("Sensor Entrance 1", true, 
														transducer, cPanel.getTracePanel());
		sensorIndex++;
		SensorAgent1 sensorExit1 = new SensorAgent1("Sensor Exit 1", false, transducer, 
																	cPanel.getTracePanel());
		sensorIndex++;
		
		PopUpAgent1 popUp1 = new PopUpAgent1("PopUp1",transducer, cPanel.getTracePanel());
		popupIndex++;
		RobotAgent1 robotUp1 = new RobotAgent1("RobotUp1", transducer);
		RobotAgent1 robotDown1 = new RobotAgent1("RobotDown1", transducer);
		MachineAgent1 machineUp1 = new MachineAgent1("MachineUp1", transducer, TChannel.DRILL,robotUp1, upIndex);
		MachineAgent1 machineDown1 = new MachineAgent1("MachineDown1", transducer, TChannel.DRILL, robotDown1, downIndex);
		
		//Begin Kevin
		// From Drill to cross seamer
//				ConveyorFamily2 Initialization
				ConveyorFamilyAgent2 conveyorFamily2 = new ConveyorFamilyAgent2(conveyorIndex);
				ConveyorBeltAgent2 conveyor2 = new ConveyorBeltAgent2("Conveyor2",transducer,conveyorIndex);
				conveyorIndex++;
				
				SensorAgent2 sensorEntrance2 = new SensorAgent2("Sensor Entrance 2",transducer, sensorIndex); //, cPanel.getTracePanel());
				sensorIndex++;
				SensorAgent2 sensorExit2 = new SensorAgent2("Sensor Exit 2",transducer, (sensorIndex));
				//															cPanel.getTracePanel());
				sensorIndex++;
				PopupAgent2 popup2 = new PopupAgent2("Popup2",transducer, cPanel.getTracePanel(),popupIndex);
				popupIndex++;
				RobotAgent2 robotUp2 = new RobotAgent2("RobotUp2", transducer); 
				RobotAgent2 robotDown2 = new RobotAgent2("RobotDown2", transducer);
				MachineAgent2 machineUp2 = new MachineAgent2("MachineUp2", transducer, TChannel.CROSS_SEAMER,robotUp2, upIndex); 
				MachineAgent2 machineDown2 = new MachineAgent2("MachineDown2", transducer, TChannel.CROSS_SEAMER,robotDown2, downIndex);

//				// From cross seamer to grinder -- Neetu
//				RobotAgent3 robotUp3 = new RobotAgent3("RobotUp3", transducer); 
//				RobotAgent3 robotDown3 = new RobotAgent3("RobotDown3", transducer);
//				MachineAgent3 machineUp3 = new MachineAgent3("MachineUp3", transducer, TChannel.GRINDER,robotUp3, 0); 
//				MachineAgent3 machineDown3 = new MachineAgent3("MachineDown3", transducer, TChannel.GRINDER,robotDown3, 1);
//				
//				ConveyorFamilyAgent3 conveyorFamily3 = new ConveyorFamilyAgent3(transducer, conveyorIndex);
//				SensorAgent3 sensorEntry3 = new SensorAgent3(conveyorFamily3, transducer, sensorIndex);
//				sensorIndex++;
//				SensorAgent3 sensorExit3 = new SensorAgent3(conveyorFamily3, transducer, sensorIndex);
//				sensorIndex++;
//				ConveyorAgent3 conveyor3 = new ConveyorAgent3(conveyorFamily3, transducer, conveyorIndex);
//				PopupAgent3 popup3 = new PopupAgent3(conveyorFamily3, transducer, robotUp3, robotDown3, popupIndex);
//				popupIndex++;
//				robotUp3.setPopup(popup3);
//				robotDown3.setPopup(popup3);
//				
//				conveyorFamily3.setConveyor(conveyor3);
//				conveyorFamily3.setSensors(sensorEntry3, sensorExit3);
//				conveyorFamily3.setPopup(popup3);
//				
//				conveyorIndex++;
				
				//Begin Kevin
				// From Drill to cross seamer
//						ConveyorFamily3 Initialization
						ConveyorFamilyAgent2 conveyorFamily3 = new ConveyorFamilyAgent2(conveyorIndex);
						ConveyorBeltAgent2 conveyor3 = new ConveyorBeltAgent2("Conveyor3",transducer,conveyorIndex);
						conveyorIndex++;
						
						SensorAgent2 sensorEntrance3 = new SensorAgent2("Sensor Entrance 3",transducer, sensorIndex); //, cPanel.getTracePanel());
						sensorIndex++;
						SensorAgent2 sensorExit3 = new SensorAgent2("Sensor Exit 3",transducer, (sensorIndex));
						//															cPanel.getTracePanel());
						sensorIndex++;
						PopupAgent2 popup3 = new PopupAgent2("Popup3",transducer, cPanel.getTracePanel(),popupIndex);
						popupIndex++;
						RobotAgent2 robotUp3 = new RobotAgent2("RobotUp3", transducer); 
						RobotAgent2 robotDown3 = new RobotAgent2("RobotDown3", transducer);
						MachineAgent2 machineUp3 = new MachineAgent2("MachineUp3", transducer, TChannel.GRINDER,robotUp3, upIndex); 
						MachineAgent2 machineDown3 = new MachineAgent2("MachineDown3", transducer, TChannel.GRINDER,robotDown3, downIndex);


				

		//from jack's code
		ConveyorFamily4 conveyorFamily4_1 = new ConveyorFamily4(conveyorIndex);
		ConveyorAgent4 conveyorAgent4_1 = new ConveyorAgent4("Conveyor " + conveyorIndex, conveyorFamily4_1, transducer, conveyorIndex);
		SensorAgent4 sensorBeginAgent4_1 = new SensorAgent4("Sensor Begin " + conveyorIndex, SensorType.BEGIN, conveyorAgent4_1, transducer, sensorIndex);
		sensorIndex++;
		SensorAgent4 sensorEndAgent4_1 = new SensorAgent4("Sensor End " + conveyorIndex, SensorType.END, conveyorAgent4_1, transducer, sensorIndex);
		sensorIndex++;
		MachineAgent4 machineAgent4_1 = new MachineAgent4("Washer", conveyorAgent4_1, 6, transducer, TChannel.WASHER);
		conveyorIndex++;
		
		ConveyorFamily4 conveyorFamily4_2 = new ConveyorFamily4(conveyorIndex);
		ConveyorAgent4 conveyorAgent4_2 = new ConveyorAgent4("Conveyor " + conveyorIndex, conveyorFamily4_2, transducer, conveyorIndex);
		SensorAgent4 sensorBeginAgent4_2 = new SensorAgent4("Sensor Begin " + conveyorIndex, SensorType.BEGIN, conveyorAgent4_2, transducer, sensorIndex);
		sensorIndex++;
		SensorAgent4 sensorEndAgent4_2 = new SensorAgent4("Sensor End " + conveyorIndex, SensorType.END, conveyorAgent4_2, transducer, sensorIndex);
		sensorIndex++;
		conveyorIndex++;
		
		ConveyorFamily4 conveyorFamily4_3 = new ConveyorFamily4(conveyorIndex);
		ConveyorAgent4 conveyorAgent4_3 = new ConveyorAgent4("Conveyor " + conveyorIndex, conveyorFamily4_3, transducer, conveyorIndex);
		SensorAgent4 sensorBeginAgent4_3 = new SensorAgent4("Sensor Begin " + conveyorIndex, SensorType.BEGIN, conveyorAgent4_3, transducer, sensorIndex);
		sensorIndex++;
		SensorAgent4 sensorEndAgent4_3 = new SensorAgent4("Sensor End " + conveyorIndex, SensorType.END, conveyorAgent4_3, transducer, sensorIndex);
		sensorIndex++;
		MachineAgent4 machineAgent4_3 = new MachineAgent4("Painter", conveyorAgent4_3, 7, transducer, TChannel.PAINTER);
		conveyorIndex++;
		
		
		
		
		ConveyorFamily6 conveyorFamily6 = new ConveyorFamily6(conveyorIndex, transducer);
		ConveyorAgent6 conveyor11 = new ConveyorAgent6("conveyor " + conveyorIndex,sensorIndex, conveyorIndex);
		UVLampAgent6 uvLamp = new UVLampAgent6("UVLamp",8);
		sensorIndex = sensorIndex + 2;
		conveyorIndex++;
		
		//ConveyorFamily6 conveyorFamily7 = new ConveyorFamily6(conveyorIndex, transducer);
		ConveyorAgent7 conveyor12 = new ConveyorAgent7("conveyor " + conveyorIndex,sensorIndex, conveyorIndex);
		
		conveyorIndex++;
		sensorIndex = sensorIndex + 2;
		ConveyorFamily7 conveyorFamily7 = new ConveyorFamily7(conveyorIndex, transducer);
		
		ConveyorAgent8 conveyor13 = new ConveyorAgent8("conveyor "+ conveyorIndex,sensorIndex, conveyorIndex);
		OvenAgent6 oven = new OvenAgent6("Oven",9);
		sensorIndex = sensorIndex + 2;
		conveyorIndex++;
		ConveyorAgent9 conveyor14 = new ConveyorAgent9("conveyor "+ conveyorIndex,sensorIndex, conveyorIndex);
		
		sensorIndex = sensorIndex + 2;
		ConveyorFamily8 conveyorFamily8 = new ConveyorFamily8(transducer);
		TruckAgent6 truck = new TruckAgent6("Truck",10);
		
		
		
		
		
		// *** LINKING UP ALL OBJECTS *** //
		
		// From beginning to Cutter
		conveyorFamily00.setNeighbors(null, conveyorFamily01); // second null will be the next conveyor family
		conveyorFamily00.setConveyor(conveyor00);
		conveyorFamily00.setMachine(machine00);
		conveyor00.setConveyorFamily(conveyorFamily00);
		conveyor00.setMachine(machine00);
		machine00.setConveyorFamily(conveyorFamily00);
		machine00.setConveyor(conveyor00);
		
		// From Cutter to first shuttle
		conveyorFamily01.setNeighbors(conveyorFamily00, conveyorFamily02);
		conveyorFamily01.setConveyor(conveyor01);
		conveyor01.setConveyorFamily(conveyorFamily01);
		
		// From first shuttle to breakout machine
		conveyorFamily02.setNeighbors(conveyorFamily01, conveyorFamily03);
		conveyorFamily02.setConveyor(conveyor02);
		conveyorFamily02.setMachine(machine02);
		conveyor02.setConveyorFamily(conveyorFamily02);
		conveyor02.setMachine(machine02);
		machine02.setConveyorFamily(conveyorFamily02);
		machine02.setConveyor(conveyor02);
		
		// From breakout machine to manual breakout
		conveyorFamily03.setNeighbors(conveyorFamily02, conveyorFamily04);
		conveyorFamily03.setConveyor(conveyor03);
		conveyorFamily03.setMachine(machine03);
		conveyor03.setConveyorFamily(conveyorFamily03);
		conveyor03.setMachine(machine03);
		machine03.setConveyorFamily(conveyorFamily03);
		machine03.setConveyor(conveyor03);
		
		// From manual breakout to second shuttle
		conveyorFamily04.setNeighbors(conveyorFamily03,conveyorFamily1); 
		conveyorFamily04.setConveyor(conveyor04);
		conveyor04.setConveyorFamily(conveyorFamily04);
				
		//Conveyor Family 1 
		conveyorFamily1.setNeighbors(conveyorFamily04, conveyorFamily2); // TODO -- put conveyorFamily2 instead of null
		conveyorFamily1.setSensorEntrance(sensorEntrance1);
		conveyorFamily1.setPopUp(popUp1);
		conveyorFamily1.setConveyor(conveyor1);
		conveyorFamily1.setMachines(machineUp1, machineDown1);
		sensorEntrance1.setConveyorFamily(conveyorFamily1);
		sensorEntrance1.setConveyor(conveyor1);
		conveyor1.setExitSensor(sensorExit1);
		conveyor1.setConveyorFamily(conveyorFamily1);
		sensorExit1.setConveyor(conveyor1);
		sensorExit1.setPopUp(popUp1);
		popUp1.setSensor(sensorExit1);
		popUp1.setRobot(robotUp1);
		popUp1.setRobot(robotDown1);
		popUp1.setConveyorFamily(conveyorFamily1);
		robotUp1.addMachine(machineUp1);
		robotUp1.setPopup(popUp1);
		robotDown1.addMachine(machineDown1);
		robotDown1.setPopup(popUp1);
		
	//For Non_Norm: breaks the glass
		//machineUp1.msgBreakGlass();
		
	//For Non_Norm: workstations work on 0,1,2 sides
		//popUp1.msgTopMachineStoppedWorking();
		//popUp1.msgBottomMachineStoppedWorking();
		
		//Conveyor Family 2 - Kevin
		conveyorFamily2.setNext(conveyorFamily3); 
		conveyorFamily2.setPrev(conveyorFamily1);
		sensorEntrance2.setConveyorFamily(conveyorFamily2);
		sensorEntrance2.setPrevThing(null);
		sensorEntrance2.setNextThing(conveyor2);
		sensorEntrance2.setPos(SensorPosition.START);
		
		conveyor2.setPrevThing(sensorEntrance2);
		conveyor2.setNextThing(sensorExit2);
		
		sensorExit2.setPrevThing(conveyor2);
		sensorExit2.setNextThing(popup2);
		sensorExit2.setPos(SensorPosition.END);

		conveyorFamily2.addSensor(sensorEntrance2);
		conveyorFamily2.addSensor(sensorExit2);
		
		conveyorFamily2.addPopups(popup2);
		conveyorFamily2.addBelt(conveyor2);

		popup2.setSensor2(sensorExit2);
		popup2.setRobot(robotUp2);
		popup2.setConveyorFamily(conveyorFamily2);
		robotUp2.addMachine(machineUp2);
		robotUp2.setPopup(popup2);
		
		popup2.setPrevThing(sensorExit2);
		popup2.setNextThing(null);
		popup2.sendStatusBack();
		
		
		popup2.setRobot(robotDown2);
		robotUp2.addMachine(machineUp2);
		robotUp2.setPopup(popup2);
		robotDown2.addMachine(machineDown2);
		robotDown2.setPopup(popup2);
		
//		//Conveyor Family 3 -- Neetu
//		conveyorFamily3.setNeighbors(conveyorFamily2, conveyorFamily4_1); //put your conveyorFamily 4 here c:
//		robotUp3.addMachine(machineUp3);
//		robotDown3.addMachine(machineDown3);
//		machineUp3.setRobot(robotUp3);
//		machineDown3.setRobot(robotDown3);
		
		//Conveyor Family 3 - KEVIN REPLACEMENT
		conveyorFamily3.setNext(conveyorFamily4_1); 
		conveyorFamily3.setPrev(conveyorFamily2);
		sensorEntrance2.setConveyorFamily(conveyorFamily3);
		sensorEntrance3.setPrevThing(null);
		sensorEntrance3.setNextThing(conveyor3);
		sensorEntrance3.setPos(SensorPosition.START);
		
		conveyor3.setPrevThing(sensorEntrance3);
		conveyor3.setNextThing(sensorExit3);
		
		sensorExit3.setPrevThing(conveyor3);
		sensorExit3.setNextThing(popup3);
		sensorExit3.setPos(SensorPosition.END);

		conveyorFamily3.addSensor(sensorEntrance3);
		conveyorFamily3.addSensor(sensorExit3);
		
		conveyorFamily3.addPopups(popup3);
		conveyorFamily3.addBelt(conveyor3);

		popup3.setSensor2(sensorExit3);
		popup3.setRobot(robotUp3);
		popup3.setConveyorFamily(conveyorFamily3);
		robotUp3.addMachine(machineUp3);
		robotUp3.setPopup(popup3);
		
		popup3.setPrevThing(sensorExit3);
		popup3.setNextThing(null);
		popup3.sendStatusBack();
		
		
		popup3.setRobot(robotDown3);
		robotUp3.addMachine(machineUp3);
		robotUp3.setPopup(popup3);
		robotDown3.addMachine(machineDown3);
		robotDown3.setPopup(popup3);
		
		//KEVIN REPLACEMENT
		
		conveyorFamily4_1.setConveyor(conveyorAgent4_1);
		conveyorFamily4_1.setMachine(machineAgent4_1);
		conveyorFamily4_1.setBefore(conveyorFamily3);
		conveyorFamily4_1.setAfter(conveyorFamily4_2);
		conveyorAgent4_1.setMachine(machineAgent4_1);
		conveyorAgent4_1.setSensorBegin(sensorBeginAgent4_1);
		conveyorAgent4_1.setSensorEnd(sensorEndAgent4_1);
		machineAgent4_1.setConveyorFamily(conveyorFamily4_1);
		//For Non_Norm: workstation doesn't process glass when it should (breaks the glass)
		//machineAgent4_1.msgBreakGlass();

		conveyorFamily4_2.setConveyor(conveyorAgent4_2);
		conveyorFamily4_2.setBefore(conveyorFamily4_1);
		conveyorFamily4_2.setAfter(conveyorFamily4_3);
		conveyorAgent4_2.setSensorBegin(sensorBeginAgent4_2);
		conveyorAgent4_2.setSensorEnd(sensorEndAgent4_2);

		conveyorFamily4_3.setConveyor(conveyorAgent4_3);
		conveyorFamily4_3.setMachine(machineAgent4_3);
		conveyorFamily4_3.setBefore(conveyorFamily4_2);
		conveyorFamily4_3.setAfter(conveyorFamily6);	// cf6 for UVlamp
		conveyorAgent4_3.setMachine(machineAgent4_3);
		conveyorAgent4_3.setSensorBegin(sensorBeginAgent4_3);
		conveyorAgent4_3.setSensorEnd(sensorEndAgent4_3);
		machineAgent4_3.setConveyorFamily(conveyorFamily4_3);
		//For Non_Norm: workstation doesn't process glass when it should (breaks the glass)
		//machineAgent4_3.msgBreakGlass();
		
		
		conveyorFamily6.setPreviousConveyorFamily(conveyorFamily4_3);
		conveyorFamily6.setNextConveyorFamily(conveyorFamily7);
		conveyorFamily6.setConveyorAndUVLamp(conveyor11,conveyor12, uvLamp);
		conveyorFamily6.setAgentsComps();
		uvLamp.setComposition(conveyor11,conveyor12, transducer);
		conveyor12.setComposition(conveyorFamily7, uvLamp, transducer);
		
		conveyorFamily7.setPreviousConveyorFamily(conveyorFamily6);
		conveyorFamily7.setConveyorAndOven(conveyor13, conveyor14, oven);
		conveyorFamily7.setAgentsComps();
		oven.setComposition(conveyor13, conveyor14, transducer);
		conveyor14.setComposition(conveyorFamily8, oven, transducer);
		
		conveyorFamily8.setPreviousConveyorFamily(conveyorFamily7);
		conveyorFamily8.setTruck(truck);
		conveyorFamily8.setAgentsComps();
		truck.setComposition(conveyorFamily7, transducer);
		
		//Kevin Gui stuff
		conveyorFams.add(conveyorFamily00);  //index 0
		conveyorFams.add(conveyorFamily01); //index 1
		conveyorFams.add(conveyorFamily02); //index 2
		conveyorFams.add(conveyorFamily03); //index 3
		conveyorFams.add(conveyorFamily04); //index 4
		conveyorFams.add(conveyorFamily1);//index 5
		conveyorFams.add(conveyorFamily2);//index 6
		conveyorFams.add(conveyorFamily3);//index 7
		conveyorFams.add(conveyorFamily4_1);//index 8
		conveyorFams.add(conveyorFamily4_2);//index 9
		conveyorFams.add(conveyorFamily4_3);//index 10
		conveyorFams.add(conveyorFamily6);//index 11
		conveyorFams.add(conveyorFamily7);//index 12
		conveyorFams.add(conveyorFamily8);//index 12
		
		rm = new RecipeMaker(500, 600, conveyorFams, transducer, this);
		rm.displayClient(500, 600); //JKevin
		//rm.setConveyorFam(conveyorFams); //Dany Hack
		
		
		// *** START ALL THREADS *** //
		conveyor00.startThread();
		machine00.startThread();
		
		conveyor01.startThread();
		
		conveyor02.startThread();
		machine02.startThread();
		
		conveyor03.startThread();
		machine03.startThread();
		
		conveyor04.startThread();
		
		sensorEntrance1.startThread();
		conveyor1.startThread();
		sensorExit1.startThread();
		popUp1.startThread();
		robotUp1.startThread();
		robotDown1.startThread();
		machineUp1.startThread();
		machineDown1.startThread();
		
//		conveyorFamily2.startAllThreads();  Kevin ugh
		
		conveyor2.startThread();
		sensorEntrance2.startThread();
		sensorExit2.startThread();
		popup2.startThread();
		robotUp2.startThread();
		robotDown2.startThread();
		machineUp2.startThread();
		machineDown2.startThread();
		
		//KEVIN REPLACEMENT
		conveyor3.startThread();
		sensorEntrance3.startThread();
		sensorExit3.startThread();
		popup3.startThread();
		robotUp3.startThread();
		robotDown3.startThread();
		machineUp3.startThread();
		machineDown3.startThread();
		//KEVIN REPLACEMENT
		
		//Neetu:
//		conveyorFamily3.sensor1.startThread();
//		conveyorFamily3.conveyor.startThread();
//		conveyorFamily3.sensor2.startThread();
//		conveyorFamily3.popup.startThread();
//		robotUp3.startThread();
//		robotDown3.startThread();
//		machineUp3.startThread();
//		machineDown3.startThread();
		
		
		conveyorAgent4_1.startThread();
		sensorBeginAgent4_1.startThread();
		sensorEndAgent4_1.startThread();
		machineAgent4_1.startThread();
		
		conveyorAgent4_2.startThread();
		sensorBeginAgent4_2.startThread();
		sensorEndAgent4_2.startThread();
		
		conveyorAgent4_3.startThread();
		sensorBeginAgent4_3.startThread();
		sensorEndAgent4_3.startThread();
		machineAgent4_3.startThread();
		
		conveyor11.startThread();
		uvLamp.startThread();
		conveyor12.startThread();
		
		conveyor13.startThread();
		oven.startThread();
		conveyor14.startThread();
		
		truck.startThread();
		
		
		// START THREADS HERE...
	
		
		System.out.println("Back end initialization finished.");
	}

	/**
	 * Returns the parent frame of this panel
	 * 
	 * @return the parent frame
	 */
	public FactoryFrame getGuiParent()
	{
		return parent;
	}

	/**
	 * Returns the control panel
	 * 
	 * @return the control panel
	 */
	public ControlPanel getControlPanel()
	{
		return cPanel;
	}

	/**
	 * Returns the display panel
	 * 
	 * @return the display panel
	 */
	public DisplayPanel getDisplayPanel()
	{
		return dPanel;
	}
}
