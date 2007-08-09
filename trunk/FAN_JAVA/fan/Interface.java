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
	
	private boolean virgin = true;
	private Time firstPacketArrival = new Time(0);
	private Time lastPacketDepart = new Time(0);
//	/**
//	 * The ResultsCollector holding information about simulation times for this Interface
//	 */
	public ResultsCollector results;
	
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
		updateUpTime();
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
	 * Called by Depart event. send() makes a call to receive(Packet p)
	 * method of the next Server. Besides that it takes the first packet
	 * from the interface queue and sets Depart event for it.
	 * It sets interface as free if there are no more waiting packets
	 *
	 */
	public void send(){
		Packet pkt = queue.removeFirst();
		peer.recieve(pkt);
		results.addAvgpacketLength(pkt);
		markFirstArrival();
		updateUpTime();
		if(queue.isEmpty())
			this.setNotBusy();
		else {
			Packet p = queue.peekFirst();
			//Time used to send packet is not added to its service time
			results.addServicedPacket( Monitor.clock.substract(p.getServiceStartTime()).toDouble() );
			Time sendTime = new Time( (double) p.getLength() / (double)bandwidth );
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),this) );
		}
	}

	public Interface(int bandwidth, Server peer, Server local) {
		this (bandwidth, peer, local, 50);
		this.results = new TimeResultsCollector(peer.getName());
	}
	
	public Interface(int bandwidth, Server peer, Server local, int size) {
		this (bandwidth,size);
		this.peer = peer;
		this.localhost = local;
		this.results = new TimeResultsCollector(peer.getName());
	}
	/**
	 * Constructor for Interface class. It creates interface with default queue type,
	 * with selected bandwidth and selected queue size.
	 * @param bandwidth The bandwidth of interface (in bytes per second)
	 * @param size The size of the queue at the output of this interface
	 */
	public Interface(int bandwidth, int size) {
		this.bandwidth = bandwidth;
		this.queue = new FifoQueueBytes(100);	
//		this.queue = new FifoQueueBytes(size);	
		// TODO: check and then test PFQQueue
		//this.queue = new PFQQueue(size, 100, this);
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

	/**
	 * This is the method responsible for clearing interface data. It doesn't change
	 * configuration of interface, the aim of this method is to clear interface after
	 * simulation.
	 */
	public void clearInterface() {
		this.queue = new FIFOQueue(50);
		//this.queue = new PFQQueue(50, 100, this); 
		this.setNotBusy();
	}

	public Time getUpTime(){		
		return lastPacketDepart.substract(firstPacketArrival);
	}

	public void updateUpTime(){
		lastPacketDepart = Monitor.clock;
	}
	
	public void markFirstArrival(){
		// remembers when a packet appeared on this interface for the first time
		if (this.virgin){
			this.virgin = false;
			this.firstPacketArrival = Monitor.clock;
			System.out.println("markFirstArrival -> " + peer.getName() + " " + Monitor.clock.toDouble());
		}
	}
}
