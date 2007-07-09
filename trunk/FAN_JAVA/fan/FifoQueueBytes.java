package fan;

import java.util.Iterator;

public class FifoQueueBytes extends FIFOQueue {
	private int totalSize;
	
	public FifoQueueBytes(int size) {
		super(size);
		totalSize = 0;
	}
	
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
	
	private int getFreeBytes() {
		return maxSize - totalSize;
	}
	
	public boolean putPacket(Packet p) {
		if ( getFreeBytes() - p.getLength() >= 0) {
			fifo.offer(p);
			totalSize += p.getLength();
			return true;		
		}
		else
			return false;
	}
	
	public Packet removeFirst() {
		Packet p = super.removeFirst();
		totalSize -= p.getLength();
		return p;		
	}
	
	public int getSize() {
		return totalSize;
	}
}
