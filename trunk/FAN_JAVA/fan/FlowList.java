package fan;

import java.util.*;


/**
 * The class represents PFL, which stands after Protected Flow List, mere,
 * a list of flows' identifiers which are kept as long as they do not get
 * outdated. In such case they are removed. Removal occurs if Packet p
 * latest activity Time t is older than Time timeout, according to Time 
 * current time. Packets with Flow Identifiers on PFL are guaranteed to
 * be served.
 * @see FlowIdentifier
 */

public class FlowList {
	
	/**
	 * Implementation of PFL
	 */
	private LinkedList<Flow> protectedList;
	/**
	 * Maximum number of flows a PFL can hold
	 */
	private int maxLength;
	
	/**
	 * Constructor for this class
	 * @param maxLength Defines maximum number of flows a PFL can hold
	 */
	public FlowList(int maxLength){		
		protectedList = new LinkedList<Flow>();
		this.maxLength = maxLength;		
	}	
	/**
	 * Returns number of currently held flows  
	 * @return Current list size
	 */	
	public int getLength(){
		return protectedList.size();
	}
	/**
	 * Returns maximum number od flows a list can hold  
	 * @return maximum number od flows
	 */	
	public int getMaxLength(){
		return maxLength;
	}
	/**
	 * Registers a new flow to PFL
	 * @param flow Flow to be added to list
	 * @return False if not succeded, otherwise true. The reason a flow cannot be 
	 * registered is that the list is already full.
	 */
	public boolean registerNewFlow(Flow flow){
		if (getLength() >= getMaxLength())
			return false;
		protectedList.add(flow);
		return true;
	}
	/**
	 * Unregisteres a flow from PFL
	 * @param flow Flow to be removed from PFL
	 * @return True
	 */
	public boolean unregisterFlow(Flow flow){
		protectedList.remove(flow);
		return true;
	}
	/**
	 * Checkes whether a flow identifier belongs to any of flows in the PFL
	 * @param flowID A flow identifier to be checked
	 * @return True if appropriate flow is found in PFL, false otherwise
	 */
	public boolean contains(FlowIdentifier flowID){
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			if( element.getFlowID().equals(flowID) )
				return true;
		}
		return false;
	}
	/**
	 * Checkes whether a flow is already in the PFL
	 * @param flowID A flow to be checked
	 * @return True if a given flow is in PFL, false otherwise
	 */	
	public boolean contains(Flow flow) {
		if(protectedList.contains(flow))
			return true;
		else
			return false;
	}
	/**
	 * Finds a flow for a given flow identifier
	 * @param flowID Flow identifier for processing
	 * @return Flow with flow identifier identical to flowID, null if none found
	 */
	public Flow getFlow(FlowIdentifier flowID) {
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			if( element.getFlowID().equals(flowID) )
				return element;
		}
		return null;
	}
	/**
	 * Removes all flows from FPL of which finish tag is smaller that virtualTime 
	 * @param virtualTime Defines the upper limit of flows' finish tag for removing
	 * @see Flow
	 */
	public void cleanFlows(long virtualTime){
		for (int i = 0; i < protectedList.size(); i++) {
			Flow element = protectedList.get( i );
			if( element.getFinishTag() <= virtualTime ){
				protectedList.remove(element);
			}
		}		
	}
	
	/**
	 * Removes all flows from PFL
	 */
	public void cleanAllFlows() {
		protectedList.clear();
	}
	/**
	 * Displayes priopriate information of all flows currently held in PFL.
	 *
	 */
	public void printAllFlows() {
		System.out.println("\nThe AFL contains the following flows:");
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			System.out.println ( element.getFlowID().toInt() + " Finish tag: " + element.getFinishTag() );
		}
	}
	
	/**
	 * Metod to return flow identifier of the most backlogged flow
	 * @return Flow identifier of most backlogged flow.
	 */
	public FlowIdentifier getMostBackloggedFlow() {
		
		long maxBacklog = 0;
		FlowIdentifier backloggedFlowId = null;
		for (Iterator iter = protectedList.iterator(); iter.hasNext();) {
			Flow element = (Flow) iter.next();
			if( element.backlog > maxBacklog ) {
				maxBacklog = element.backlog;
				backloggedFlowId = element.getFlowID();
			}
		}
		return backloggedFlowId;
	}
}
