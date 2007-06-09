package fan;
import java.util.Vector;

/**
 * This is the server class
 */
public class Server {
	/**
	 * The name of the server
	 */
	private	String name;
	
	/**
	 * The maximum number of traffic types serviced by router
	 */
	private	int maxTrafficTypes;
	
	/**
	 * The Vector holdling the list of output interfaces that the router has
	 */
	private	Vector<Interface> interfaces;
	
	/**
	 * The RoutingTable specifing the routing methods for this Server
	 */
	private	RoutingTable routing;
	
	/**
	 * The FlowList holding the list of protected, registered flows in the system
	 */
	private	FlowList flowList;
	
	/**
	 * The ResultsCollector holding information about simultation times for this Server
	 */
	public ResultsCollector results;
	
	/**
	 * Method for recieving Packet. It rejects or accepts this packet.
	 * If it is accepted then it is added to the queue (interface is full,busy) 
	 * or automatically creates the Depart event (after the time depending
	 * on interface speed) and sets interface as busy
	 * @param p Packet to be recieved by Server
	 */
	public void recieve(Packet p){
		//Choose the next server and its interface that will be a next destination for this packet
		Interface choiceIntface = routing.getServerInterfaceForResult( Monitor.generator.getNumber(1) );
			
		//check if interface is pointing back to this server - if true than it means that the packet
		//is leaving network - e.g. its final destination is this server or is leaving
		//simulated network
		if( choiceIntface.getServer() == this ){
			p = null;
			results.addServicedPacket(0);
			results.addLocallyServicedPacket();
			results.addQueueLength(0);			
		}
		
		//Check if interface is free
		else if( !choiceIntface.isBusy() ) {
			choiceIntface.getQueue().putPacket(p);
			//sendTime is equal to: packet length / interface speed
			Time sendTime = new Time( (double) p.getLength() / choiceIntface.getBandwidth() );
			
			//Schedule new Depart event
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),choiceIntface) );
			
			//And set interface as busy
			choiceIntface.setBusy();
			results.addServicedPacket(0);
			results.addQueueLength(0);
		}
		
//		//If the interface is busy then check if queue for interface has any free places
//		else if( choiceIntface.getQueue().isFull() ) {
//			//reject packet
//		}
		
		else {
			
			if( choiceIntface.getQueue().putPacket(p) ) {
				p.setServiceStartTime(Monitor.clock);
				results.addQueueLength(choiceIntface.getQueue().getSize() );
			} else
				results.addRejectedPacket();
		}
	}
	
//	/**
//	 * Default constructor 
//	 * @param name
//	 * @param rt
//	 */
//	public Server(String name, RoutingTable rt) {
//		this.name = name;
//		this.maxTrafficTypes = 2;
//		this.routing = rt;
//		//do przedyskutowania jak nalezy zrobic konfiguracje interfejsow, co konfigurujemy pierwsze
//		//this.interfaces = rt.getInterfaces();
//	}
	
	public Server(String name) {
		this.name = name;
		this.maxTrafficTypes = 2;
		this.routing = new RoutingTable();
		this.interfaces = new Vector<Interface>();
		this.results = new ResultsCollector();
	}
	
	/**
	 * Method for adding new interface to server
	 * @param destServ Server that is a destination
	 * @param probability Float with probability of packet going that server
	 * @param bandwidth Int with the bandwidth of this interface
	 */
	public boolean addInterface(Server destServ, double probability, int bandwidth) {
		Interface intfc = new Interface( bandwidth, destServ,this );
		if(routing.addRoute(intfc, probability)) {
			interfaces.add(intfc);
			return true;
		}
		else return false;
	}
	
	/**
	 * This is a method for removing interface with a specified number in the interfaces list
	 * @param number int with the number of the interface to be removed
	 * @return true if successful, false if not able to remove
	 */
	public boolean removeInterface(int number) {
		Interface intfaceToRemove= interfaces.elementAt(number);
		if( routing.removeRoute(intfaceToRemove) ) {
			interfaces.removeElementAt(number);
			return true;
		}
		return false;		
	}
	
	/**
	 * Getter for server name
	 * @return String with the name of the server
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the int with the number of interfaces that server has
	 * @return Int with the number of interfaces
	 */
	public int getInterfacesNumber() {
		return this.interfaces.size();
	}
	
	/**
	 * This is the method for retrieving Vector of Interfaces that this server has
	 * @return Vector<Interface> that this server has
	 */
	public Vector<Interface> getInterfaces() {
		return this.interfaces;
	}
	
	/**
	 * This is the method for retrieving Routing Table of this server
	 * @return RoutingTable of this server
	 */
	public RoutingTable getRoutingTable() {
		return routing;
	}
	
	
}
