package engine.agent.family3.interfaces;

import shared.Glass;

public interface Sensor {
		
		public void setIndex(int i);
		//MESSAGES
		public void msgHereIsGlass(Glass g);
		
		public void haltLine();
		
		//ACTIONS
		public void doBringGlass(); //dunno what this does yet.
		
		public void doReleaseGlass();
		
		public void doHaltFactory();

}