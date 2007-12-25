package fan;
import java.util.Iterator;
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
	 * @deprecated
	 */
	private	int maxTrafficTypes;
	
	/**
	 * The Vector holding the list of output interfaces that the router has
	 */
	private	Vector<Interface> interfaces;
	
	/**
	 * The RoutingTable specifying the routing methods for this Server
	 */
	private	RoutingTable routing;
	
	/**
	 * The FlowList holding the list of protected, registered flows in the system
	 * !!! not in use anymore !!!
	 */
	private	FlowList flowList;
	
	/**
	 * Method for receiving Packet. It rejects or accepts this packet.
	 * If it is accepted then it is added to the queue (interface is full,busy) 
	 * or automatically creates the Depart event (after the time depending
	 * on interface speed) and sets interface as busy
	 * @param p Packet to be received by Server
	 */
	public void recieve(Packet p){
		//Choose the next server and its interface that will be a next destination for this packet
		Interface choiceIntface = routing.getServerInterfaceForResult( Monitor.generator.getNumber(1) );
		
		//check if interface is pointing back to this server - if true than it means that the packet
		//is leaving network - e.g. its final destination is this server or is leaving
		//simulated network
	
		if( choiceIntface.getServer() == this ){
			choiceIntface.results.addServicedPacket( 0, p.getFlowIdentifier() );
			choiceIntface.results.addQueueLength(0);
			p = null;
			return;
		}
		
		
		//check if MBAC accepts packet - if it is rejected it means that congestion has occurred
		if ( choiceIntface.getMBAC().congestionOccured( p ) == true ) {
			Debug.print(Debug.SPEC,"Congestion");
			if( !( (PFQQueueBytes)choiceIntface.getQueue() ).getFlowList().contains( p.getFlowIdentifier() ) )
			{
				//System.out.println("The flow: " + p.getFlowIdentifier().toInt() + " is not in the list" );
				Debug.print(Debug.SPEC, Monitor.clock + " Not in the list, congestion occured, flowID: " + p.getFlowIdentifier() );
				choiceIntface.results.addRejectedPacket( p.getFlowIdentifier() );
				p = null;
				return;
			} else {
				FlowIdentifier mostBackloggedFlow = ( (PFQQueueBytes)choiceIntface.getQueue() ).flowList.getMostBackloggedFlow();
				if( mostBackloggedFlow != null ) { 
					//System.out.println( "The size of the queue before is: " + ( (PFQQueueBytes)choiceIntface.getQueue() ).getSize() );
					if( ( (PFQQueueBytes)choiceIntface.getQueue() ).removeFirstFlowPacket( mostBackloggedFlow ) != null ) {
						Debug.print(Debug.SPEC, Monitor.clock + " In list, removed packet of most backlogged flow, flowID: " + mostBackloggedFlow );
						choiceIntface.results.addRejectedPacket( mostBackloggedFlow );
					}
						
//					System.out.println( "The size of the queue after is: " + ( (PFQQueueBytes)choiceIntface.getQueue() ).getSize() );
				} //else
					//System.out.println( "There is no backlogged flow" );
			}
		}

		choiceIntface.markFirstArrival();
		choiceIntface.updateUpTime();
		
		//Check if interface is free
		if( !choiceIntface.isBusy() ) {
			
			choiceIntface.getQueue().putPacket(p);
			
			//sendTime is equal to: packet length / interface speed
			Time sendTime = new Time( (double) p.getLength() / choiceIntface.getBandwidth() );
			
			//Schedule new Depart event
			Monitor.agenda.schedule( new Depart(Monitor.clock.add(sendTime),choiceIntface) );
			
			//And set interface as busy
			choiceIntface.results.addServicedPacket(0, p.getFlowIdentifier() );
			choiceIntface.results.addQueueLength(0);
		}
		
		//If interface is not free (has any packets waiting in the queue)
		else {
			
			//if the queue is not full
			if( choiceIntface.getQueue().putPacket(p) ) {
				p.setServiceStartTime(Monitor.clock);
				choiceIntface.results.addQueueLength(choiceIntface.getQueue().getSize() );
				
			//if the queue is full
			} else {
				Debug.print(Debug.SPEC, Monitor.clock + " No congestion, the queue is full, flowID: " + p.getFlowIdentifier() );
				choiceIntface.results.addRejectedPacket( p.getFlowIdentifier() );
				p=null;
			}
				
		}
	}
	
	/**
	 * Constructor
	 * @param name A name of a server
	 */
	public Server(String name) {
		this.name = name;
		this.maxTrafficTypes = 2;
		this.routing = new RoutingTable();
		this.interfaces = new Vector<Interface>();
	}
	
	
	/**
	 * Method for adding new interface to server, with specified queue size of the interface
	 * @param destServ Server that is a destination
	 * @param probability Float with probability of packet going that server
	 * @param bandwidth Int with the bandwidth of this interface
	 * @param queueSize size of the queue that interface has (in bytes)
	 * @return
	 */
	public boolean addInterface ( Server destServ, double probability, int bandwidth, int queueSize, int maxFlowListSize, long minFR, double maxPL ) {
		Interface intfc = new Interface( bandwidth, destServ,this, queueSize, maxFlowListSize, minFR, maxPL );
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
	
	/**
	 * This is the method for clearing results already collected for this server
	 */
	public void clearResults() {
		Debug.print(Debug.INFO, "Server.clearResults(): " + name);
		for (Iterator<Interface> iter = interfaces.iterator(); iter.hasNext();) {
			Interface element = (Interface) iter.next();
			element.clearInterface();
		}
	}
	
}
