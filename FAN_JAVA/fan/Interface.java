package fan;

public class Interface {
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
	
	public int getBandwidth() {
		return this.bandwidth;
	}
}
