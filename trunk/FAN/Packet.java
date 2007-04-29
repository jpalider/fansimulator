
public class Packet {
	/**
	 * int with the selected flow type 
	 */
	public short flowType;
	
	public Packet(short type) {
		flowType = type;
	}
	
	/**
	 * Method for moving packet to the end of the queue of selected nextServer
	 * @param nextServer Server which is the target of the Packet
	 */
	public void moveToServer(Server nextServer) {
		;
	}
}
