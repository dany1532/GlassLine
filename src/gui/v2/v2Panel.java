package gui.v2;

import java.awt.Dimension;

import javax.swing.JPanel;

import gui.panels.FactoryPanel;
import gui.panels.subcontrolpanels.GlassInfoPanel;
import gui.panels.subcontrolpanels.GlassSelectPanel;
import gui.panels.subcontrolpanels.LogoPanel;
import gui.panels.subcontrolpanels.NonNormPanel;
import gui.panels.subcontrolpanels.StatePanel;
import gui.panels.subcontrolpanels.TitlePanel;
import gui.panels.subcontrolpanels.TracePanel;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class v2Panel extends JPanel implements TReceiver{

	/**
	 * The parent panel for communication with the display
	 */
	FactoryPanel parent;

	/**
	 * Allows the control panel to communicate with the back end and give commands
	 */
	Transducer transducer;

	/**
	 * The panel containing the title
	 */
	TitlePanel titlePanel;

	/**
	 * The panel that controls whether the factory pauses
	 */
	StatePanel statePanel;

	/**
	 * The panel containing the buttons to control the configuration of glass
	 */
	GlassSelectPanel glassSelectPanel;

	/**
	 * The panel displaying information on current glass production
	 */
	GlassInfoPanel glassInfoPanel;

	/**
	 * The panel handling non-normative events
	 */
	NonNormPanel nonNormPanel;

	/**
	 * Panel holding logo
	 */
	LogoPanel logoPanel;

	/**
	 * A panel for printing backend messages.
	 */
	TracePanel tracePanel;

	public final static Dimension size = new Dimension(250, 880);

	/**
	 * Creates a ControlPanel with no connections. Used only for testing
	 * purposes
	 */
	public v2Panel(FactoryPanel fPanel, Transducer fTransducer)
	{
		this();

		transducer = fTransducer;
		transducer.register(this, TChannel.CONTROL_PANEL);

		parent = fPanel;
	}

	public v2Panel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the parent
	 */
	public void setParent(FactoryPanel fp)
	{
		parent = fp;
	}

	/**
	 * Returns the parent FactoryPanel
	 * @return the parent FactoryPanel
	 */
	public FactoryPanel getGuiParent()
	{
		return parent;
	}

	/**
	 * Listens to events fired on the transducer, especially from Agents
	 */
	public synchronized void eventFired(TChannel channel, TEvent event, Object[] args)
	{
		if (channel == TChannel.CONTROL_PANEL){
			
			if (event == TEvent.START){
				
				// look at 'current recipe'? and make that many new parts?
				//transducer.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);

				
			}
			
			if (event == TEvent.STOP){
				
			}
			
		}
	}

	/**
	 * Sets the transducer
	 * @param newTransducer
	 *        the new transducer to link
	 */
	public void setTransducer(Transducer newTransducer)
	{
		transducer = newTransducer;
		
		// TODO -- register more channels if needed...
		transducer.register(this,TChannel.CONTROL_PANEL);
	}

	/**
	 * Returns the transducer
	 * @return the transducer
	 */
	public Transducer getTransducer()
	{
		return transducer;
	}

	/**
	 * Returns the State Panel
	 * @return the State Panel
	 */
	public StatePanel getStatePanel()
	{
		return statePanel;
	}

	/**
	 * Returns the glass info panel, for ease of printing
	 * @return the glass info panel
	 */
	public GlassInfoPanel getGlassInfoPanel()
	{
		return glassInfoPanel;
	}

	/**
	 * Returns the glass select panel
	 * @return the glass select panel
	 */
	public GlassSelectPanel getGlassSelectPanel()
	{
		return glassSelectPanel;
	}

	/**
	 * Returns the non-norm panel
	 * @return the NonNormPanel
	 */
	public NonNormPanel getNonNormPanel()
	{
		return nonNormPanel;
	}

	/**
	 * Returns an instance of trace panel
	 * @return trace panel
	 */
	public TracePanel getTracePanel()
	{
		return tracePanel;
	}

	/**
	 * Returns the name of the panel
	 */
	public String toString()
	{
		return "Control Panel";
	}
}
