package engine.agent.family2;

import shared.enums.Status;

public class KStatus2 {

	public Status state = Status.READY;
	
	public KStatus2(){
		state = Status.READY;
	}
	
	public KStatus2(Status s){
		state = s;
	}
	
	public void setReady(){
		state = Status.READY;
	}
	public void setBusy(){
		state = Status.BUSY;
	}
	
}