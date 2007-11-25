package fan;
/**
 * This is the class representing a Flow, which in nature is an abstract form of 
 * a sequence of packets with identical FlowIdentifier appearing in a router
 * within certain timespace. Its purpose is to keep track of various information
 * updated by the sequence packets. 
 * @see FlowIdentifier
 */
public class Flow {
	private FlowIdentifier flowID;
	private long finishTag;
	/**
	 * Amount of backlogged information 
	 */
	public long backlog;
	/**
	 * Transmitted bytes
	 */
	public long bytes;
	
	/**
	 * Method to get flow identifier
	 * @return Flow identifier
	 */
	public FlowIdentifier getFlowID() {
		return flowID;
	}
	/**
	 * Constructor for this class
	 * @param newFlowID Flow identifier for new flow
	 */
	public Flow(FlowIdentifier newFlowID) {
		this.flowID = newFlowID;
		this.finishTag = 0;
	}
	/**
	 * Method to get finish tag of this flow 
	 * @return Finish tag
	 */
	public long getFinishTag() {
		return finishTag;
	}
	/**
	 * Method to set finish tag of this flow 
	 * @param fTag Finish tag to set set for this flow
	 */
	public void setFinishTag(long fTag) {
		if ( this.finishTag < fTag )
			this.finishTag = fTag;
	}
}
