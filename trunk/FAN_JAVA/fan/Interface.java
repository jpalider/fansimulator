package fan;

public class Interface{
	private int bandwidth;
	
	private Server peer;
	
	private Server localhost;
	
	private Queue queue;
	
	private boolean busy;
	
	/**
	 * 
	 * @return
	 */
	public boolean isBusy() {
		return this.busy;
	}
	
	/**
	 * 
	 * @return
	 */
	public void setBusy() {
		this.busy = true;
	}
	
	public void setNotBusy() {
		this.busy = false;
	}
	
	/**
	 * Method for getting the Server pointed by this interface
	 * @return Server pointed by this interface
	 */
	public Server getServer() {
		return this.peer;
	}
	
	/**
	 * Method for setting Server pointed by this interface
	 * @param newPeer Server pointed by this interface
	 */
	public void setServer(Server newPeer) {
		this.peer = newPeer;
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
		peer.recieve(queue.removeFirst());
		Packet p = queue.peekFirst();
		Time sendTime = new Time( (double) p.getLength() / bandwidth );
		Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),this) );
	}

	public Interface(int bandwidth, Server peer, Server local) {
		this.bandwidth = bandwidth;
		this.peer = peer;
		this.localhost = local;
	}

	public Interface(int bandwidth) {
		this.bandwidth = bandwidth;
	}

	public Server getLocalhost() {
		return localhost;
	}

	public void setLocalhost(Server locahost) {
		this.localhost = locahost;
	}
	
	
}
