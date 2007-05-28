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
	 * @author  dodek
	 */
	private class Header{
		class Address{
			char A;
			char B;
			char C;
			char D;
			public Address(char a, char b , char c, char d){}
		}
		
			
		private int version;	
		private int ihl;	
		private int tos;	
		private int length;	
		private int identification;	
		private int flags;	
		private int fragmentOffset;
		private int ttl;	
		private int protocol;	
		private int headerChecksum;	
		private Address sourceAddress;	
		private Address destinationAddress;	
	
	
		/**
		 * @return  the version
		 * @uml.property  name="version"
		 */
		public int getVersion(){ return version; }
		public int getIHL(){ return ihl; }
		public int getTOS(){ return tos; }
		/**
		 * @return  the length
		 * @uml.property  name="length"
		 */
		public int getLength(){ return length; }
		/**
		 * @return  the identification
		 * @uml.property  name="identification"
		 */
		public int getIdentification(){ return identification; }
		/**
		 * @return  the flags
		 * @uml.property  name="flags"
		 */
		public int getFlags(){ return flags; }
		/**
		 * @return  the fragmentOffset
		 * @uml.property  name="fragmentOffset"
		 */
		public int getFragmentOffset(){ return fragmentOffset; }
		public int getTTL(){ return ttl; }
		/**
		 * @return  the protocol
		 * @uml.property  name="protocol"
		 */
		public int getProtocol(){ return protocol; }
		/**
		 * @return  the headerChecksum
		 * @uml.property  name="headerChecksum"
		 */
		public int getHeaderChecksum(){ return headerChecksum; }
		/**
		 * @return  the sourceAddress
		 * @uml.property  name="sourceAddress"
		 */
		public Address getSourceAddress(){ return sourceAddress; }
		/**
		 * @return  the destinationAddress
		 * @uml.property  name="destinationAddress"
		 */
		public Address getDestinationAddress(){return destinationAddress; }
		
		// Only the most important fields in IP Packet
		private Header(Address source, Address destination, int length){
			
		}
		private Header(Address source, Address destination, int length, int protocol){			
		}
		
	}
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
		this(id, ftype, 35);
	}
	/**
	 * Constructor.
	 * @param id Flow identifier. In early stages od deveopment they are
	 * explicitly assigned in generation procedure. TODO: Flow identifier
	 * should be replaced by typical IP packet header and on that basis 
	 * Servers will generate packets' identifiers
	 * @param ftype Flow type. In early stages od deveopment they are
	 * explicitly assigned in generation procedure. TODO: Flow types should
	 * be discovered by Servers.
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
}

