package engine.agent.family2;

import shared.Glass;
import shared.enums.Status;
import shared.interfaces.AgentInterface;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import engine.agent.family2.interfaces.InTheFamilyInterface2;

import java.awt.Container;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import javax.management.Query;

import engine.util.StringUtil;

public class InTheFamilyAgent2 extends Agent implements AgentInterface, InTheFamilyInterface2{

	//public String name;
	
	public KStatus2 myStatus = new KStatus2();
	public KStatus2 nextStatus = new KStatus2();
	public KStatus2 prevStatus = new KStatus2();
	
	public int guiIndex = 0;
	
	public Boolean glassAtEnd = false;
	public Boolean newMyStatus = false;
	public Boolean newNextStatus = false;
	public Boolean newPrevStatus = false;
	
	//public Boolean have
	
	public InTheFamilyAgent2 thingAhead = null;
	public InTheFamilyAgent2 thingBehind = null;
	
	public ConveyorFamilyAgent2 cf;
	
	public ArrayList<KGlass> myGlass = new ArrayList<KGlass>();
	
	
	public int myCapacity = 3;
	

	
	public InTheFamilyAgent2(String myName, Transducer t) {
		super(myName, t);
	
	}
	
	public void setConveyorFamily(ConveyorFamilyAgent2 c){
		cf = c;
	}

	public void setNextThing(InTheFamilyAgent2 next){
		thingAhead = next;
	}
	
	public void setPrevThing(InTheFamilyAgent2 prev){
		thingBehind = prev;
	}
	
	/**** Messages ***/
	
	public void msgStatusFromAhead(KStatus2 s) {
		print("Got status "+s.state.toString()+" from ");
		if (thingAhead != null)
			print(thingAhead.getName());
		else
			print("null, conveyor family");
		nextStatus = s;
		newNextStatus = true;
		stateChanged();
	}

	public void msgHereIsGlass(Glass g) {
		if (myStatus.state == Status.READY)
			gotGlass(g);
		else print("Oops, glass collided at me");
		stateChanged();
	}
	
	public void msgGuiGotGlass(){
		print("Just got a glass");
	}
	
	public void msgGlassAtEnd(){
		glassAtEnd = true;
		stateChanged();
	}
	
	/*** End of Messages ***/
	
	
	public void gotGlass(Glass g) {
		synchronized(myGlass){
			print("I was passed some glass and I was ready");
			myGlass.add(new KGlass(g));
			if (myGlass.size() == myCapacity){
				print("That hits my capacity, so hang on!");
				myStatus.setBusy();
				newMyStatus = true;
				//stateChanged();
			}
			stateChanged();
		}
	}

	/*** Actions ***/
	public void sendStatusBack() {
		thingBehind.msgStatusFromAhead(myStatus);
	}
	
	public void shutDown(){
		myStatus.setBusy();
		newMyStatus = true;
		
	}
	public void nextBecameBusy() {
		if (myGlass.size() < myCapacity){
			shutDown(); //TODO
		}
		else{ //Stop immediately
			shutDown();
		}
	}
	
	public void nextBecameReady() {
//		if (myGlass.size() < myCapacity){
			newMyStatus = true;
			myStatus.setReady();
			stateChanged();
	//	}
	}
	
	public void iBecameReady() {
		passGlassForward(); //?
	}
	
	public void makeMeBusy() {
		myStatus.setBusy();
		newMyStatus = true;
	}
	
	public boolean passGlassForward() {
		if (nextStatus.state == Status.BUSY){
			makeMeBusy();
			return false;
		}
		else{
			if (myGlass.isEmpty()){
				noGlassToPass();
				return false;
			}
			else{
				KGlass k = getGlassToSend();
				if (k != null){
					print("passing glass ahead");
					thingAhead.msgHereIsGlass(k.g);
				}
				else{
					print("glass not at end");
					return false;
				}

			}
			if (myStatus.state == Status.BUSY){
				myStatus.setReady();
				newMyStatus = true;
				stateChanged();
			}
			return true;
		}
		
	}
	
	
	public void noGlassToPass() {
		print("no glass to pass");		
	}

	public KGlass getGlassToSend(){
		KGlass k = null;
		synchronized (myGlass){
			if (!myGlass.isEmpty()){
					k = (myGlass.remove(0));
					print("removed glass to send");
			}
		}
		return k;
	}
	
	/*** End of Actions ***/

/*** Scheduler ***/

		/** Agents must implement this scheduler to perform any actions appropriate for the
		 * current state.  Will be called whenever a state change has occurred,
		 * and will be called repeated as long as it returns true.
		 * @return true iff some action was executed that might have changed the
		 * state.
		 */
		public boolean morePickingAndExecuting(){
			//expect this to be overridden if more actions need to be taken for particular agents
			return false;
		}
		public boolean pickAndExecuteAnAction(){
			//gives the particular agent priority to pick and execute before general stuff
			if (morePickingAndExecuting()){
				return true;
			}
			if (glassAtEnd){
				if (nextStatus.state == Status.BUSY){
					makeMeBusy();
				}
			}
			if (newNextStatus){
				newNextStatus = false;
				if (nextStatus.state == Status.BUSY){
					print("next became busy");
					nextBecameBusy();
					return true;
				}
				if (nextStatus.state == Status.READY){
					print("next became ready");
					nextBecameReady();
					return true;
				}
			}
			if (newPrevStatus){
				newPrevStatus = false;
				if (prevStatus.state == Status.BUSY){
					//TODO don't really care...
					print("was told previous is busy");
					return true;
				}
				else{
					print("was told previous became ready");
					return true; //still don't really care...
				}
			}
			if (newMyStatus){
				newMyStatus = false;
				print("Sending status back "+myStatus.state);
				if (thingBehind == null){
					print("Front thing, alerting CF to send status back");
					cf.myStatus = new KStatus2(myStatus.state);
					cf.sendStatus();
					return true;
				}
				if (myStatus.state == Status.READY)
					iBecameReady();
				//else
					//iBecameBusy(); don't do this
				
				sendStatusBack();
				return true;
			}
			
			if (passGlassForward()) return true;
			
			//if no actions were taken, return false
			print("no action taken");
			return false;
		}

		
		
		
		/******The following draws HEAVILY on code previously used in this class for basic agent design*********/
			
			


			/** This should be called whenever state has changed that might cause
			 * the agent to do something. */
			protected void stateChanged() {
				stateChange.release(); 
			}
			
			Semaphore stateChange = new Semaphore(1,true);//binary semaphore, fair
			private AgentThread agentThread;

			
			
			
			
		/** Return agent name for messages.  Default is to return java instance
		 * name. */
		public String getName() {
			//return StringUtil.shortName(this); //Kevin huge problem with recursion
			return this.name;
		}

		/** The simulated action code */
		protected void Do(String msg) {
		    print(msg, null);
		}
		/** Print message */
		protected void print(String msg) {
			print(msg, null);
		}

		/** Print message with exception stack trace */
		protected void print(String msg, Throwable e) {
			StringBuffer sb = new StringBuffer();
			sb.append(getName());
			sb.append(": ");
			sb.append(msg);
			sb.append("\n");
			if (e != null) {
				sb.append(StringUtil.stackTraceString(e));
			}
			System.out.print(sb.toString());
		}

		/** Start agent scheduler thread.  Should be called once at init time. */
		public synchronized void startThread() {
			print("Starting thread for "+ name);
			if (agentThread == null) {
				agentThread = new AgentThread(name);
				agentThread.start(); // causes the run method to execute in the AgentThread below
			} else {
				agentThread.interrupt();//don't worry about this for now
			}
		}

		/** Stop agent scheduler thread. */
		//In this implementation, nothing calls stopThread().
		//When we have a user interface to agents, this can be called.
		public void stopThread() {
			if (agentThread != null) {
				agentThread.stopAgent();
				agentThread = null;
			}
		}

		/** Agent scheduler thread, calls respondToStateChange() whenever a state
		 * change has been signalled. */
		private class AgentThread extends Thread {
			private volatile boolean goOn = false;

			private AgentThread(String name) {
				super(name);
			}

			public void run() {
				goOn = true;

				while (goOn) {
					try {
					    // The agent sleeps here until someone calls, stateChanged(),
					    // which causes a call to stateChange.give(), which wakes up agent.
						stateChange.acquire();
							//The next while clause is the key to the control flow.
							//When the agent wakes up it will call respondToStateChange()
							//repeatedly until it returns FALSE.
							//You will see that pickAndExecuteAnAction() is the agent scheduler.
						while (pickAndExecuteAnAction());
					} catch (InterruptedException e) {
						// no action - expected when stopping or when deadline changed
					} catch (Exception e) {
						print("Unexpected exception caught in Agent thread:", e);
					}
				}
			}

			private void stopAgent() {
				goOn = false;
				this.interrupt();
			}
		}

		@Override
		public void eventFired(TChannel channel, TEvent event, Object[] args) {
			// TODO Auto-generated method stub
			
		}

	}
