package eu.compassresearch.core.interpreter.cml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import eu.compassresearch.core.interpreter.cml.events.ChannelEvent;
import eu.compassresearch.core.interpreter.cml.events.CmlEvent;

public class CmlTrace {

	private final List<CmlEvent> trace;
	
	public CmlTrace()
	{
		trace = new LinkedList<CmlEvent>();
	}
	
	public CmlTrace(CmlTrace other)
	{
		trace = new LinkedList<CmlEvent>(other.trace);
	}
	
	public void addEvent(CmlEvent event)
	{
		trace.add(event);
	}
	
	public CmlEvent getLastEvent()
	{
		if(trace.size() > 0)
			return trace.get(trace.size()-1);
		else 
			return null;
	}
	
	@Override
	public String toString() {
		
		
		StringBuilder strbuilder = new StringBuilder();
		strbuilder.append("<");
		for(Iterator<CmlEvent> iterator = trace.iterator(); iterator.hasNext();)
		{
			strbuilder.append(iterator.next());
			if(iterator.hasNext())
				strbuilder.append(",");
		}
		strbuilder.append(">");
		
		return strbuilder.toString();
	}
	
	public List<CmlEvent> getTrace()
	{
		return new LinkedList<CmlEvent>(trace);
	}
	
	public List<CmlEvent> getVisibleTrace()
	{
		List<CmlEvent> visibleEvents = new LinkedList<CmlEvent>();
			
		for(CmlEvent e : trace)
		{
			if(e instanceof ChannelEvent)
				visibleEvents.add(e);
		}
		
		return visibleEvents;
	}
	
	public static boolean isObservableEvent(CmlEvent event)
	{
		return event instanceof ChannelEvent;
	}
	
	public boolean equals(Object obj) { 
	
		if(!(obj instanceof CmlTrace))
			return false;
				
		CmlTrace otherTraceObj = (CmlTrace)obj;
		
		return otherTraceObj.trace.equals(trace);
	};
	
	@Override
	public int hashCode() {
		return trace.hashCode();
	}
}
