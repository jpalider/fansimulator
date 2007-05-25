package fan;
import java.util.Vector;

/**This is the server class
 * 
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
	 * Method for recieving Packet. It rejects or accepts this packet.
	 * If it is accepted then it is added to the queue (interface is full,busy) 
	 * or automatically creates the Depart event (after the time depending
	 * on interface speed) and sets interface as busy
	 * @param p Packet to be recieved by Server
	 */
	public void recieve(Packet p){
		//Choose the next server and its interface that will be a next destination for this packet
		Interface choiceIntface = routing.getServerInterfaceForResult( (float)Monitor.generator.getNumber(1) );
		
		//check if interface is pointing back to this server - if true than it means that the packet
		//is leaving network - e.g. its final destination is this server or is leaving
		//simulated network
		if( choiceIntface.getServer() == this ){
			p = null;
		}
		
		//Check if interface is empty
		if( !choiceIntface.isBusy() ) {
			//sendTime is equal to: packet length / interface speed
			Time sendTime = new Time( (double) p.getLength() / choiceIntface.getBandwidth() );
			
			//Schedule new Depart event
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),this) );
			
			//And set interface as busy
			choiceIntface.setBusy();
		}
		
		//If the interface is busy then check if queue for interface has any free places
		else if( choiceIntface.getQueue().isFull() ) {
			//reject packet
		}
		
		else {
			choiceIntface.getQueue().putPacket(p);
		}
	}
	
	
}
