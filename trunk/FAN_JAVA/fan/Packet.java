package fan;

public class Packet {
	public enum FlowType {STREAM, ELASTIC};
	private int length;
	private FlowType type;
	private FlowIdentifier flowID;
	
	public Packet(FlowIdentifier id, FlowType ftype) {
		new Packet(id, ftype, 65535);
	}
	
	public Packet(FlowIdentifier id, FlowType ftype, int l) {
		length = l;
		flowID = id;
		type = ftype;
	}
	
	public int getLength() {
		return length;
	}
	
	
}
