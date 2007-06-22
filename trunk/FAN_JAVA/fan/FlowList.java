package fan;

import java.util.HashMap;
import java.util.*;

public class FlowList {
	
	private HashMap<FlowIdentifier,Time> protectedList;
	private int maxLength;
	private Time timeout;
	
	public FlowList(int maxLength, Time timeout){		
		protectedList = new HashMap<FlowIdentifier, Time>();
		this.maxLength = maxLength;		
		this.timeout = timeout;
	}	
	
	public FlowList(){
		this(50, new Time(10));
	}
	
	public int getLength(){
		return protectedList.size();
	}
		
	public int getMaxLength(){
		return maxLength;
	}
	
	public Time getTimeout(){
		return timeout;		
	}
	
	// If possible, register a new flow if flow is new or update its time of occurance
	public boolean registerFlow(FlowIdentifier identifier, Time currentTime){
		if (getLength() == getMaxLength())
			return false;
		protectedList.put(identifier, currentTime);
		return true;
	}
	
	public boolean unregisterFlow(FlowIdentifier identifier){
		protectedList.remove(identifier);
		return true;
	}
	
	//that needs some fix as it won't work fast
	// mayby by value not key??
	public void unregisterInactiveFlows(Time currentTime, Time timeout){
		// For each Flow in PFL we check wheather it is outdated or not. If that flow 
		// expands (not this word) timeout it is discarded.
		for (Iterator<FlowIdentifier> it = protectedList.keySet().iterator(); it.hasNext(); ){			
			if ( currentTime.substract( protectedList.get(it) ).compareTo(timeout) > 0 ){		
				protectedList.remove(it);
		    }
		}
	}

	
}
