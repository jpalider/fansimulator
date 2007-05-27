package fan;

public interface Queue {
	public boolean isFull();
	
	public boolean putPacket(Packet p);
	
	public Packet removeFirst();
	
	public Packet peekFirst();
}

