package fan;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT;

/**
 * This is the class representing a router interface. There are only outgoing interfaces,
 * packet leaving such interface after some time (sending time) reaches its destination
 * router, merely MBAC which is meant to be some kind of incoming interface.
 * In this simulator there are no
 * modelled links between simulated routers, Interface serving that role is in charge
 * of controlling bandwidth limitations, introducing necessary delays etc. In this
 * simulator it is considered that transmition between two router interfaces is
 * immiediate (no propagation delays, signaling, etc.)   
 * @see MBAC
 * @see Queue
 * @see Server
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
	
	/**
	 * Holds information about maximum flow list size
	 */
	private int maxFlowListSize;
	
	/**
	 * Holds information about maximum queue size
	 */
	private int maxQueueSize;
	
	/**
	 * The ResultsCollector holding information about simulation times for this Interface
	 */
	public TimeResultsCollector results;
	
	
	/**
	 * Checks whether any Packet is being processed (sent) at the moment
	 * @return True if a packet is currently being sent
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
	 * Gets a queue of this Interface
	 * @return Packet queue
	 */
	public Queue getQueue() {
		return this.queue;
	}

	
	/**
	 * Getter for interface bandwidth
	 * @return  int bandwidth of the server
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
			Debug.print(Debug.ERR, "Interface.send(): null packet from queues");
		}
		peer.recieve( pkt );
		results.addAvgpacketLength(pkt);
		updateUpTime();
		if( !queue.isEmpty() ) {
			Packet p = queue.peekFirst();
			//Time used to send packet is not added to its service time
			if( p == null) {
				Debug.print(Debug.ERR, "Interface.send(): the packet was null" );
			} else if ( p.getServiceStartTime() == null ) {
				Debug.print(Debug.ERR, "Interface.send(): the packetServiceStartTime is null" );
				( (PFQQueueBytes)getQueue() ).printElements() ;
				Debug.print(Debug.ERR, "Interface.send(): the previous packet had the following parameters:");
				Debug.print(Debug.ERR, "Interface.send(): flow ID: " + pkt.getFlowIdentifier().toInt() + 
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
		this.maxFlowListSize = flsize;
		this.maxQueueSize = size;
	}

	
	/**
	 *  Returns server that this is interface is part of
	 * @return  the localhost
	 */
	public Server getLocalhost() {
		return localhost;
	}

	
	/**
	 * Sets server that this is interface is part of
	 * @param localhost the localhost to set
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
		Debug.print(Debug.INFO,"Interface.clearInterface()");
		this.queue = new PFQQueueBytes(maxQueueSize, maxFlowListSize, this); 
		admissionControl.setQueue(this.queue);
		if (results != null) { results.finalize(); results = null; }
		this.results = new TimeResultsCollector(localhost.getName() + "_" + peer.getName());
	}

	
	/**
	 * Method to get upTime of this interface
	 * @return
	 */
	public Time getUpTime(){		
		return lastPacketDepart.substract(firstPacketArrival);
	}
	
	
	/**
	 * Method to update upTime of this interface
	 */
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
	
	
	/**
	 * Method to return MBAC assigned to this interface
	 * @return MBAC assigned to this interface
	 */
	public MBAC getMBAC() {
		return admissionControl;
	}
}
