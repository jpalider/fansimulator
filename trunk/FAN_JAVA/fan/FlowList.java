package fan;

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
 * It is taking as granted that time of checking protected  flows takes no time.
 * That should be fixed. Or shouldn't??
 * What could be examined is PFL's length influence on router efficiency - if any.
 * 
 * @see FlowIdentifier
 * @author Mumin
 */

public class FlowList {
	
	private LinkedList<Flow> protectedList;
	private int maxLength;
	
	public FlowList(int maxLength){		
		protectedList = new LinkedList<Flow>();
		this.maxLength = maxLength;		
	}	
		
	public int getLength(){
		return protectedList.size();
	}
		
	public int getMaxLength(){
		return maxLength;
	}
	
	public boolean registerNewFlow(Flow flow){
		if (getLength() >= getMaxLength())
			return false;
		protectedList.add(flow);
		return true;
	}
	
	public boolean unregisterFlow(Flow flow){
		protectedList.remove(flow);
		return true;
	}
	
	public boolean contains(FlowIdentifier flowID){
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			if( element.getFlowID().equals(flowID) )
				return true;
		}
		return false;
	}
	
	public boolean contains(Flow flow) {
		if(protectedList.contains(flow))
			return true;
		else
			return false;
	}
	
	public Flow getFlow(FlowIdentifier flowID) {
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			if( element.getFlowID().equals(flowID) )
				return element;
		}
		return null;
	}

	public void cleanFlows(long virtualTime){
		for (int i = 0; i < protectedList.size(); i++) {
			Flow element = protectedList.get( i );
			if( element.getFinishTag() <= virtualTime ){
				protectedList.remove(element);
			}
		}		
	}
	
	public void cleanAllFlows() {
		protectedList.clear();
	}
}
