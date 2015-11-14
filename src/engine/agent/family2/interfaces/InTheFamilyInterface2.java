package engine.agent.family2.interfaces;

import engine.agent.family2.KStatus2;
import shared.Glass;

public interface InTheFamilyInterface2 {

	
	public void sendStatusBack();
	public void shutDown();
	public void msgStatusFromAhead(KStatus2 s);
	public void msgHereIsGlass(Glass g);
	public boolean passGlassForward();
	public void msgGuiGotGlass();

}
