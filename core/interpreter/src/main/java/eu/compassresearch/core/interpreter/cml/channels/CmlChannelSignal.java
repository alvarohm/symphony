package eu.compassresearch.core.interpreter.cml.channels;

import eu.compassresearch.core.interpreter.events.CmlChannelEvent;
import eu.compassresearch.core.interpreter.events.EventObserver;
import eu.compassresearch.core.interpreter.events.EventSource;

/**
 * A Channel that are able to signal, thus only synchronization
 * @author akm
 *
 */
public interface CmlChannelSignal extends CmlChannel {

	/**
	 * Signals to the channel listeners that they can now proceed 
	 */
	public void signal();
	
	/**
	 * Register or unregister for the onChannelSignal event
	 * @return The appropriate EventSource for event registration
	 */
	public EventSource<CmlChannelEvent> onChannelSignal();

}
