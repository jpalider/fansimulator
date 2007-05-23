package fan;

/**
 * This is the class that represents Packets in fan simulator.
 * @author Mumin
 *
 */
public class Packet {
	// Under construction...
	private class Header{
		class Address{
			char A;
			char B;
			char C;
			char D;
			public Address(char a, char b , char c, char d){}
		}
		int getVersion(){ return 0; }
		int getIHL(){ return 0; }
		int getTOS(){ return 0; }
		int getLength(){ return 0; }
		int getIdentification(){ return 0; }
		int getFlags(){ return 0; }
		int getFragmentOffset(){ return 0; }
		int getTTL(){ return 0; }
		int getProtocol(){ return 0; }
		int getHeaderChecksum(){ return 0; }
		Address getSourceAddress(){ return new Address((char)0,(char)0,(char)0,(char)0) ;}
		Address getDestinationAddress(){return new Address((char)0,(char)0,(char)0,(char)0);}
		
		
		
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

