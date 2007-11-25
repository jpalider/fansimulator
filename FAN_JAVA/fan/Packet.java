package fan;

/**
 * This is the class that represents Packets in fan simulator.
 */
public class Packet {
	
	/**
	 * Enumaratio to specify the protocol - it has 2 possible values:
	 * TCP, UDP. This 
	 */
	public enum Protocol { TCP, UDP }

	
	/**
	 * Describes two types of considered traffic in FAN architecture.
	 */
	public enum FlowType {STREAM, ELASTIC};
	
	
	/**
	 * The packet length
	 */
	private int length;
	
	
	/**
	 * Field that specifies flow type 
	 */
	private FlowType type;
	
	
	/**
	 * Field that specifies id of the flow
	 */
	private FlowIdentifier flowID;
	
	
	/**
	 * Variable for holding information about the Time in which the Server registered this packet;
	 */
	private Time serviceStartTime;
	
	
	/**
	 * Constructor that creates packet of 1500B length
	 * @see Packet(FlowIdentifier id, FlowType ftype, int l)
	 */
	public Packet(FlowIdentifier id, FlowType ftype) {
		this(id, ftype, 1500);
	}
	
	
	/**
	 * Constructor.
	 * @param 	id Flow identifier. In early stages they are
	 * 			explicitly assigned in generation procedure.
	 * @param 	ftype Flow type. In early stages they are
	 * 			explicitly assigned in generation procedure.
	 * @param l Definition of packet length 
	 */
	public Packet(FlowIdentifier id, FlowType ftype, int l) {
		length = l;
		flowID = id;
		type = ftype;
	}
	
	
	/**
	 * Method to return packet length
	 * @return The length of the packet
	 */
	public int getLength(){
		return length;
	}
	
	
	/**
	 * Method to set the service start time (the time when packet
	 * starts it service in the server)
	 * @param t The time when service started
	 */
	public void setServiceStartTime(Time t) {
		this.serviceStartTime = t;
	}
	
	
	/**
	 * Method to get service start time
	 * @return Service start time of the packet
	 */
	public Time getServiceStartTime() {
		return serviceStartTime;
	}
	
	
	/**
	 * Method to get flow identifier of this packet 
	 * @return
	 */
	public FlowIdentifier getFlowIdentifier(){
		return flowID;
	}
	
}

