package fan;

public class Interface{
	private int bandwidth;
	
	private Server peer;
	
	private Queue queue;
	
	/**
	 * 
	 * @return
	 */
	public boolean isBusy() {
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public void setBusy() {
		
	}
	
	public Server getServer() {
		return this.peer;
	}
	
	public Queue getQueue() {
		return this.queue;
	}
	
	/**
	 * Getter for interface bandwidth
	 * @return int bandwidth of the server
	 */
	public int getBandwidth() {
		return this.bandwidth;
	}
	
	/**
	 * Called by Depart event. send() makes a call to recieve(Packet p)
	 * method of the next Server. Besides that it takes the first packet
	 * from the interface queue and sets Depart event for it.
	 * It sets inteface as free if there are no more waiting packets
	 *
	 */
	public void send(){
				
	}
}
