package fan;
import java.util.LinkedList;


/**
 * This is the class implementing typical FIFO Queue
 */
public class FIFOQueue implements Queue {
	
	/**
	 * The actual queue storing packets
	 */
	protected LinkedList<Packet> fifo;
		
	/**
	 * Maximum size (in Packets) of this fifo queue
	 */
	int maxSize;
	
	/**
	 * Holds the information about the type of the queue - in this case Fifo
	 */
	String type;
	
	/**
	 * Number of recieved by this queue bytes
	 */
	protected long recievedBytes = 0;
	
	/**
	 * Variable to hold previous number of recieved bytes
	 */
	protected long recievedBytesPrev = 0;
	
	/**
	 * Holds information about the time when last packet arrived
	 */
	protected Time lastTime = new Time(0);
	
	/**
	 * Holds information about the load of this queue
	 */
	protected long load = 0;
	
	/**
	 * Holds the information about interface this fifo queue is assigned to
	 */
	protected Interface intfce;
	
	
	/**
	 * Constructor for this class
	 * @param maxSize Maximum size of this queue 
	 * @param intfce The interface which this queue is assigned to
	 */
	public FIFOQueue(int maxSize, Interface intfce) {
		fifo = new LinkedList<Packet>();
		this.maxSize = maxSize;
		type = "FIFO";
		this.intfce = intfce;
	}
	
	
	/**
	 * Checks if this queue is full.
	 * @return True if it is full, false otherwise
	 */
	public boolean isFull() {
		return fifo.size() >= maxSize;
	}

	
	/**
	 * Method to put packet inside this queue
	 * @param p The Packet that will be inserted in this queue
	 * @return 	True if the packet was inserted properly, false if the queue was full and there
	 * 			was no space for this packet.
	 */
	public boolean putPacket(Packet p) {
		if ( this.isFull() )
			return false;
		fifo.offer(p);
		recievedBytes += p.getLength();
		return true;		
	}
	
	
	/**
	 * Remove and return the first packet of this queue
	 * @return First, removed packet of this queue
	 */
	public Packet removeFirst() {
		return fifo.poll();
	}
	
	
	/**
	 * Returns, but doesn't remove the first packet of this queue
	 * @return First packet of this queue
	 */
	public Packet peekFirst() {
		return fifo.peek();
	}
	
	
	/**
	 * Method that checks if this queue is empty
	 * @return True if there are no packets in this queue, false otherwise
	 */
	public boolean isEmpty() {
		return fifo.isEmpty();
	}

	
	/**
	 * Method to get size of the queue (number of packets inside this queue)
	 * @return Number of packets in waiting currently in this queue
	 */
	public int getSize() {
		return fifo.size();
	}
	
	
	/**
	 * Method to set maximum size of this queue
	 * @param packetNumber The maximum number of packets in this queue
	 */
	public void setSize(int packetNumber) {
		this.maxSize = packetNumber;
	}

	
	/**
	 * Get the maximum number of packets in this queue
	 * @return The maximum number of packets in this queue
	 */
	public int getMaxSize() {
		return maxSize;
	}
	
	
	/**
	 * Method to return the type of the queue,
	 * in this case "FIFO"
	 */
	public String getType(){
		return type;
	}
	
	
	/**
	 * Method to return load of this queue
	 * @return Load of this fifo queue
	 */
	public long getLoad(){
		double dt = Monitor.clock.substract(lastTime).toDouble();
		if (dt < 1.0){
			load = (long)((recievedBytes-recievedBytesPrev) / (dt*intfce.getBandwidth()));
			lastTime = Monitor.clock;
			recievedBytesPrev = recievedBytes;
		}
		return load;
	}
	
}
