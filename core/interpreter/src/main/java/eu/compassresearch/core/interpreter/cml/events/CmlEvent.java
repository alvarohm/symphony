package eu.compassresearch.core.interpreter.cml.events;

public abstract class CmlEvent {
	
	
	@Override 
	public abstract int hashCode();
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object obj) ;
}
