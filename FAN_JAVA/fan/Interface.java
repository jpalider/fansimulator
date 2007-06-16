package fan;

/**
 * @author  dodek
 */
public class Interface{
	private int bandwidth;
	
	private Server peer;
	
	private Server localhost;
	
	private Queue queue;
	
	private boolean busy;
	
	/**
	 * @return
	 * @uml.property  name="busy"
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
	
	/**
	 * @return  the queue
	 * @uml.property  name="queue"
	 */
	public Queue getQueue() {
		return this.queue;
	}
	
	/**
	 * Getter for interface bandwidth
	 * @return  int bandwidth of the server
	 * @uml.property  name="bandwidth"
	 */
	public int getBandwidth() {
		return this.bandwidth;
	}
	
	/**
	 * Getter for interface probability
	 * @return  int bandwidth of the server
	 * @uml.property  name="probability"
	 */
	public int getProbability() {
		return 0;
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
		if(queue.isEmpty())
			this.setNotBusy();
		else {
			Packet p = queue.peekFirst();
			
			//Time used to send packet is not added to its service time
			localhost.results.addServicedPacket( Monitor.clock.substract(p.getServiceStartTime()).toDouble() );
			Time sendTime = new Time( (double) p.getLength() / (double)bandwidth );
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),this) );
		}
	}

	public Interface(int bandwidth, Server peer, Server local) {
		this(bandwidth);
		this.peer = peer;
		this.localhost = local;
	}

	public Interface(int bandwidth) {
		this.bandwidth = bandwidth;
		this.queue = new FIFOQueue(50);
		this.setNotBusy();
	}

	/**
	 * @return  the localhost
	 * @uml.property  name="localhost"
	 */
	public Server getLocalhost() {
		return localhost;
	}

	/**
	 * @param localhost  the localhost to set
	 * @uml.property  name="localhost"
	 */
	public void setLocalhost(Server locahost) {
		this.localhost = locahost;
	}

	
	
}
