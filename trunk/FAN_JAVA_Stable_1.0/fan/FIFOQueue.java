package fan;
import java.util.LinkedList;

public class FIFOQueue implements Queue {
	LinkedList<Packet> fifo;
	int maxSize;
	String type;
	long recievedBytes = 0;
	long recievedBytesPrev = 0;
	Time lastTime = new Time(0);
	long load = 0;
	Interface intfce;
	
	public FIFOQueue(int maxSize, Interface intfce) {
		fifo = new LinkedList<Packet>();
		this.maxSize = maxSize;
		type = "FIFO";
		this.intfce = intfce;
	}
	
	public boolean isFull() {
		return fifo.size() >= maxSize;
	}

	public boolean putPacket(Packet p) {
		if ( this.isFull() )
			return false;
		fifo.offer(p);
		recievedBytes += p.getLength();
		return true;		
	}

	public Packet removeFirst() {
		return fifo.poll();
	}
	
	public Packet peekFirst() {
		return fifo.peek();
	}

	public boolean isEmpty() {
		return fifo.isEmpty();
	}

	public int getSize() {
		return fifo.size();
	}
	
	public void setSize(int packetNumber) {
		this.maxSize = packetNumber;
	}

	public int getMaxSize() {
		return maxSize;
	}
	
	public String getType(){
		return type;
	}
	
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
