package fan;

import java.util.Iterator;
/**
 * This is the class implementing typical FIFO Queue with that difference that
 * some parameters are measured in bytes instead of packets as units. 
 * @see FifoQueue
 */
public class FifoQueueBytes extends FIFOQueue {
	private int totalSize;
	/**
	 * Holds the information about the type of the queue - in this case Fifo
	 */
	private String type;
	
	/**
	 * Constructor for this class
	 * @param size Maximum size of this queue - [needs reviewing] 
	 * @param intfce The interface which this queue is assigned to
	 */
	public FifoQueueBytes(int size, Interface intfce) {
		super(size,intfce);
		totalSize = 0;
		type = "FIFOBytes";
	}
	/**
	 * Checks if this queue is full.
	 * @return True if it is full, false otherwise
	 */
	public boolean isFull() {
		int totalSize = 0;
		for (Iterator iter = fifo.iterator(); iter.hasNext();) {
			Packet p = (Packet) iter.next();
			totalSize += p.getLength();
		}
		if(totalSize < maxSize)
			return false;
		else 
			return true;
	}
	/**
	 * Returns space left in this queue 
	 * @return Free bytes left
	 */
	public int getFreeBytes() {
		return maxSize - totalSize;
	}
	/**
	 * Method to put packet inside this queue
	 * @param p The Packet that will be inserted in this queue
	 * @return 	True if the packet was inserted properly, false if the queue was full and there
	 * 			was no space for this packet.
	 */
	public boolean putPacket(Packet p) {
		if ( getFreeBytes() - p.getLength() >= 0) {
			fifo.offer(p);
			totalSize += p.getLength();
			recievedBytes += p.getLength();
			return true;		
		}
		else
			return false;
	}
	/**
	 * Remove and return the first packet of this queue
	 * @return First, removed packet of this queue
	 */
	public Packet removeFirst() {
		Packet p = super.removeFirst();
		totalSize -= p.getLength();
		return p;		
	}
	/**
	 * Method to get size of the queue (number of bytes inside this queue)
	 * @return Number of bytes currently kept in this queue
	 */
	public int getSize() {
		return totalSize;
	}
	/**
	 * Method to return the type of the queue,
	 * in this case "FIFO"
	 */
	public String getType(){
		return type;
	}
}
