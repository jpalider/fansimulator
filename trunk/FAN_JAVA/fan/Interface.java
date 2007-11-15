package fan;

/**
 * @author  dodek
 */
public class Interface{
	private int bandwidth;
	
	private Server peer;
	
	private Server localhost;
	
	private Queue queue;
	
	private MBAC admissionControl;
	
	private boolean virgin = true;
	private Time firstPacketArrival = new Time(0);
	private Time lastPacketDepart = new Time(0);
//	/**
//	 * The ResultsCollector holding information about simulation times for this Interface
//	 */
	public TimeResultsCollector results;
	
	/**
	 * @return
	 * @uml.property  name="busy"
	 */
	public boolean isBusy() {
		return !queue.isEmpty();
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
		if(pkt == null)
		{
			System.out.println("null packet from queues");
		}
		peer.recieve( pkt );
		results.addAvgpacketLength(pkt);
//		markFirstArrival();
		updateUpTime();
		if( !queue.isEmpty() ) {
			Packet p = queue.peekFirst();
			//Time used to send packet is not added to its service time
			if( p == null) {
				System.out.println( "ERROR: The packet was null" );
			} else if ( p.getServiceStartTime() == null ) {
				System.out.println( "ERROR: The packetServiceStartTime is null" );
				( (PFQQueueBytes)getQueue() ).printElements() ;
				System.out.println("The previous packet had the following parameters:");
				System.out.println(	"Flow ID: " + pkt.getFlowIdentifier().toInt() + 
									", service start time: " + pkt.getServiceStartTime().toDouble() );
			}
			results.addServicedPacket( 	Monitor.clock.substract(p.getServiceStartTime()).toDouble(),
										p.getFlowIdentifier() );
			Time sendTime = new Time( (double) p.getLength() / (double)bandwidth );
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),this) );
		}
	}


	
	/**
	 * Constructor for Interface class. It creates interface with selected
	 * bandwidth, queue size, and flowlist size. It also uses specified servers
	 * as peer (destination) server and local server. Finally the constructor specifies
	 * the parameters that are used by MBAC block in this router - minimum Fair Rate and 
	 * maximum Priority Load.
	 * @param bandwidth the bandwidth of this interface (in bytes)
	 * @param peer The server which is a destination of this interface
	 * @param local The server where this interface is located
	 * @param minFR The minimum Fair Rate for MBAC used in this interface
	 * @param maxPL The maximum Priority Load for MBAC used in this interface
	 */
	public Interface(int bandwidth, Server peer, Server local, int size, int flsize, long minFR, double maxPL) {
		this.bandwidth = bandwidth;
		this.queue = new PFQQueueBytes(size, flsize, this);
		this.peer = peer;
		this.localhost = local;
		this.admissionControl = new MBAC(this, minFR, maxPL );
		this.results = new TimeResultsCollector(localhost.getName() + "_" + peer.getName());
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
		//this.queue = new FifoQueueBytes(150000,this);
		this.queue = new PFQQueueBytes(100000, 100, this); 
		admissionControl.setQueue(this.queue);
		//this.queue = new FifoQueueBytes(100000,this);
	}

	public Time getUpTime(){		
		return lastPacketDepart.substract(firstPacketArrival);
	}
	
	public void updateUpTime(){
		lastPacketDepart = Monitor.clock;
	}
	
	/**
	 * Remembers when a packet appeared on this interface for the first time
	 *
	 */
	public void markFirstArrival(){
		if (this.virgin){
			this.virgin = false;
			this.firstPacketArrival = Monitor.clock;
		}
	}
	
	public MBAC getMBAC() {
		return admissionControl;
	}
}
