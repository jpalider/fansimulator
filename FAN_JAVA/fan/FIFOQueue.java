package fan;
import java.util.LinkedList;

public class FIFOQueue implements Queue {
	LinkedList<Packet> fifo;
	int maxSize;
	
	public FIFOQueue(int maxSize) {
		fifo = new LinkedList<Packet>();
		this.maxSize = maxSize;
	}
	
	public boolean isFull() {
		return fifo.size() >= maxSize;
	}

	public boolean putPacket(Packet p) {
		if ( this.isFull() )
			return false;
		fifo.offer(p);
		return true;		
	}

	public Packet removeFirst() {
		return fifo.poll();
	}
	
	public Packet peekFirst() {
		return fifo.peek();
	}

}