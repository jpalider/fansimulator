package fan;

public class Flow {
	private FlowIdentifier flowID;
	private long finishTag;
	
	public int compareTo(Flow flow){
		//if (flow_id == flow.getFlowId()) return true;
		return 1;
	}
	
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
		this.finishTag = fTag;
	}
}
