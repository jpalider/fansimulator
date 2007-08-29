/**
 * 
 */
package fan;

/**
 * @author Mumin
 *
 */
public class PFQQueueBytes extends PFQQueue {
	private int sizeInBytes;
	private int maxSizeInBytes;
	
	PFQQueueBytes(int maxSizeInBytes, int flowListSize, Interface intface){
		super(99999/*size in packets*/, flowListSize, intface);
		this.maxSizeInBytes = maxSizeInBytes;
	}
	
	public int getSizeBytes() {
		return sizeInBytes;
	}
	
	public int getMaxSize() {
		return maxSizeInBytes;
	}
	/**
	 * Counted in bytes.
	 */
	public boolean isFull() {
		//System.out.println("To powinno sie wyswietlic :-)");
		return ( (sizeInBytes+MTU) > maxSizeInBytes);
	}
	
	public boolean putPacket(Packet p){
		if ( super.putPacket(p) ){
			sizeInBytes += p.getLength();
			return true;
		}
		return false;
	}
	
	public Packet removeFirst(){
		Packet p = super.removeFirst();
		//if (p != null)
			sizeInBytes -= p.getLength();
		return p;	
	}	
}
