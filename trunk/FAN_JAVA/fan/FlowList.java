package fan;

import java.util.HashMap;
import java.util.*;

/**
 * The class represents PFL, which stands after Protected Flow List, mere,
 * a list of flows' identifiers which are kept as long as they do not get
 * outdated. In such case they are removed. Removal occurs if Packet p
 * latest activity Time t is older than Time timeout, according to Time 
 * current time. Packets with Flow Identifiers on PFL are guaranteed to
 * be served.
 * 
 * TODO:
 * Test 'unregisterInactiveFlows' functionality
 * 
 * TODO:
 * Removal might be altered by introducing a bit more advanced
 * algorithm of removing Packets. Why not??
 * 
 * TODO:
 * It is taking as granted that time of checking protected  flows takes no time.
 * That should be fixed. Or shouldn't??
 * What could be examined is PFL's length influence on router efficiency - if any.
 * 
 * TODO:
 * Timeout checking should be done every Packet arrival or every Time interval.
 * That is to be discussed.
 * 
 * @see FlowIdentifier
 * @author Mumin
 */

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
	
	// If possible, register a new flow if flow is new or update its time of occurance
	public boolean registerFlow(Packet p, Time currentTime){
		if (getLength() == getMaxLength())
			return false;
		protectedList.put(p.getFlowIdentifier(), currentTime);
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
		// exceeds timeout it is discarded.
		for (Iterator<FlowIdentifier> it = protectedList.keySet().iterator(); it.hasNext(); ){			
			if ( currentTime.substract( protectedList.get(it) ).compareTo(timeout) > 0 ){		
				protectedList.remove(it);
		    }
		}
	}

	
}
