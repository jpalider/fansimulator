package fan;

public interface Queue {
	/**
	 * Returns true if the queue is full and has no free places
	 * @return true if the queue is full, false if there are any free places 
	 */
	public boolean isFull();
	
	/**
	 * Puts packet at the end of this queue
	 * @param p Packet to be put at the end of this queue
	 * @return true if the packet was put at the end of the queue, false
	 * 			if the packet was rejected due to e.g. full queue.
	 */
	public boolean putPacket(Packet p);
	
	/**
	 * Returns first packet from the front of the queue and removes it from this queue.
	 * @return Packet from the front of the queue
	 */
	public Packet removeFirst();
	
	/**
	 * Returns first packet from the front of the queue, without removing it from this queue
	 * @return Packet at the front of the queue
	 */
	public Packet peekFirst();
	
	/**
	 * Returns true is this queue is empty, has no packets inside
	 * @return true is queue has no packets, false if there are any packets inside
	 */
	public boolean isEmpty();
	
	/**
	 * Returns number of elements in this queue
	 * @return int with number of elements
	 */
	public int getSize();
	
	/**
	 * Returns the maximum queue length
	 * @return
	 */
	public int getMaxSize();
}

