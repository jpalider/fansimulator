package fan;

public class Flow {
	private FlowIdentifier flowID;
	private long finishTag;
	public long backlog;
	public long bytes;
	
//	public int compareTo(Flow flow){
//		if (flowID == flow.getFlowID()) return 0;
////		if (flowID > flow.getFlowID()) return 1;
////		if (flowID < flow.getFlowID()) return -1;		
//	}
	
	public FlowIdentifier getFlowID() {
		return flowID;
	}
	
	public Flow(FlowIdentifier newFlowID) {
		this.flowID = newFlowID;
		this.finishTag = 0;
	}
	
	public long getFinishTag() {
		return finishTag;
	}
	
	public void setFinishTag(long fTag) {
		if ( this.finishTag < fTag )
			this.finishTag = fTag;
	}
}
