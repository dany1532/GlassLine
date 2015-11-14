package shared.interfaces;

import shared.Glass;
import shared.enums.Status;

public interface ConveyorFamily {

	// glass from source (Bin, Machine, Other Conveyor Family) to Conveyor Family
	public void msgHereIsGlass(Glass g);
	// status from next component (Machine, Other Conveyor Family) to Conveyor Family
	public void msgStatus(Status s);
	// GUI stop conveyor for non-norms
	public void msgStopConveyor();
	// GUI start conveyor for non-norms
	public void msgStartConveyor();
	// GUI stop popup for non-norms
	public void msgStopPopup();
	// GUI start popup for non-norms
	public void msgStartPopup();
	// GUI stop online machine for non-norms
	public void msgStopOnlineMachine();
	// GUI start online machine for non-norms
	public void msgStartOnlineMachine();
	// GUI set inline machine to have expectation failure -- do not process glass when should
	public void msgOnlineMachineExpectationFailure();
	public void msgOnlineMachineFixExpectationFailure();

	// GUI set conveyor to FULL
	public void msgSetConveyorTooFull();
	//GUI set offline machine to have expectation failure -- do not process glass
	public void msgOfflineMachineExpectationFailure();
	//GUI fix offline machine from the expectation failure
	public void msgOfflineMachineFixExpectationFailure();
	//Top workstation doesn't work. For Non_Norm: offline workstatiosn work on 0,1,2 sides
	public void msgTopOfflineMachineTurnOff();
	//Bottom workstation doesn't work. For Non_Norm: offline workstations work on 0,1,2 sides
	public void msgBottomOfflineMachineTurnOff();
	//Top workstation starts working. For Non_Norm: offline workstatiosn work on 0,1,2 sides
	public void msgTopOfflineMachineTurnOn();
	//Bottom workstation starts working. For Non_Norm: offline workstations work on 0,1,2 sides
	public void msgBottomOfflineMachineTurnOn();
	//Top Machine breaks glass. Non_Norm: glass breaking on offline machine (**At the end it fixes itself**)
	public void msgTopMachineBreaksGlass();
	//Bottom Machine breaks glass. Non_Norm: glass breaking on offline machine(**At the end it fixes itself**)
	public void msgBottomMachineBreaksGlass();
	
	public void msgTopMachineFixGlass();
	//Bottom Machine stopsbreaks glass. Non_Norm: glass breaking on offline machine(**At the end it fixes itself**)
	public void msgBottomMachineFixGlass();
	
}
