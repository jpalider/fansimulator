package fan;

/**
 * This is the class that represents Packets in fan simulator.
 * @author Mumin
 *
 */
public class Packet {
	// Under construction...
	public enum Protocol { TCP, UDP }
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
	
	
		public int getVersion(){ return version; }
		public int getIHL(){ return ihl; }
		public int getTOS(){ return tos; }
		public int getLength(){ return length; }
		public int getIdentification(){ return identification; }
		public int getFlags(){ return flags; }
		public int getFragmentOffset(){ return fragmentOffset; }
		public int getTTL(){ return ttl; }
		public int getProtocol(){ return protocol; }
		public int getHeaderChecksum(){ return headerChecksum; }
		public Address getSourceAddress(){ return sourceAddress; }
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
	 * Constructor.
	 * @see Packet(FlowIdentifier id, FlowType ftype, int l)
	 */
	public Packet(FlowIdentifier id, FlowType ftype) {
		new Packet(id, ftype, 65535);
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
	
	public int getLength(){
		return length;
	}
}

