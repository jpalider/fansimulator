package fan;

/**
 * This is the class that represents Packets in fan simulator.
 * @author  Mumin
 */
public class Packet {
	// Under construction...
	/**
	 * @author  dodek
	 */
	public enum Protocol { TCP, UDP }

	/**
	 * Describes two types of considered traffic in FAN architecture.
	 */
	public enum FlowType {STREAM, ELASTIC};
	private int length;
	private FlowType type;
	private FlowIdentifier flowID;
	
	/**
	 * Variable for holding information about the Time in which the Server registered this packet;
	 */
	private Time serviceStartTime;
	
	/**
	 * Constructor.
	 * @see Packet(FlowIdentifier id, FlowType ftype, int l)
	 */
	public Packet(FlowIdentifier id, FlowType ftype) {
		this(id, ftype, 1500);
	}
	/**
	 * Constructor.
	 * @param id Flow identifier. In early stages od deveopment they are
	 * explicitly assigned in generation procedure.
	 * TODO: Flow identifier should be replaced by typical IP packet header
	 * and on that basis Servers will generate packets' identifiers
	 * @param ftype Flow type. In early stages od deveopment they are
	 * explicitly assigned in generation procedure.
	 * TODO: Flow types should be discovered by Servers.
	 * @param l Definition of packet length 
	 */
	public Packet(FlowIdentifier id, FlowType ftype, int l) {
		length = l;
		flowID = id;
		type = ftype;
	}
	
	/**
	 * @return  the length
	 * @uml.property  name="length"
	 */
	public int getLength(){
		return length;
	}
	
	public void setServiceStartTime(Time t) {
		this.serviceStartTime = t;
	}
	
	public Time getServiceStartTime() {
		return serviceStartTime;
	}
	
	public FlowIdentifier getFlowIdentifier(){
		return flowID;
	}
}

