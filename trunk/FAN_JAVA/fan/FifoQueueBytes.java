package fan;

import java.util.Iterator;

public class FifoQueueBytes extends FIFOQueue {
	private int totalSize;
	private String type;
	
	public FifoQueueBytes(int size, Interface intfce) {
		super(size,intfce);
		totalSize = 0;
		type = "FIFOBytes";
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
	
	public int getFreeBytes() {
		return maxSize - totalSize;
	}
	
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
	
	public Packet removeFirst() {
		Packet p = super.removeFirst();
		totalSize -= p.getLength();
		return p;		
	}
	
	public int getSize() {
		return totalSize;
	}
	
	public String getType(){
		return type;
	}
}
