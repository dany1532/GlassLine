package engine.agent.family3.interfaces;
import java.util.ArrayList;

import engine.agent.Agent;

import shared.Glass;
import shared.enums.Status;
import transducer.Transducer;

public interface ConveyorFamily {
	public ArrayList <Agent> agents =  new ArrayList<Agent>();
	public ArrayList<ConveyorFamily> conveyorFamilies = new ArrayList<ConveyorFamily>();
	public Transducer transducer = new Transducer();
	int index = 5; //conveyor family 5

	//Routing Message
	public void msgHereIsGlass(Glass g);
	
	public void msgStatus(Status s);

	
}
