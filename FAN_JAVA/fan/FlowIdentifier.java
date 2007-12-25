package fan;

/**
 * This is the class representing a flow identifier, consolidation of some packet fields
 * common to each flow. Here an abstract identifier assigned to packets that serves the 
 * cross-protect router to distinguish packets from different flows. 
 * @see Flow
 */
public class FlowIdentifier {
	/**
	 * Implementation of a flow identifier
	 */
	private int id;
	
	/**
	 * Constructor for this class
	 * @param id Flow identifier
	 */
	public FlowIdentifier(int id) {
		this.id = id;
	}
	
	/**
	 * Compares one flow identifier to another one
	 * @param secondFlow Flow identifier to compare to.
	 * @return True if secondFlow is the same identifier
	 */
	public boolean equals(FlowIdentifier secondFlow) {
		if(this.id == secondFlow.id)
			return true;
		else 
			return false;
	}
	/**
	 * Serves mainly for printing purpose.
	 * @return Integer value of an identifier
	 */
	public int toInt() {
		return this.id;
	}
	
	/**
	 * Returns String with flowId number
	 */
	public String toString() {
		return String.valueOf( id );
	}
}
