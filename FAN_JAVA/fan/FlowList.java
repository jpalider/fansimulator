package fan;

import java.util.HashMap;

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
	
	public void unregisterInactiveFlows(Time currentTime){
		
	}
	
	public void unregisterInactiveFlows(Time currentTime, Time timeout){
		
	}

	
}
