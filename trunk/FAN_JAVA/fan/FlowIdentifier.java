package fan;

public class FlowIdentifier {
	private int id;
	public FlowIdentifier(int id) {
		this.id = id;
	}
	
	public boolean equals(FlowIdentifier secondFlow) {
		if(this.id == secondFlow.id)
			return true;
		else 
			return false;
	}
	
	public int toInt() {
		return this.id;
	}
}
