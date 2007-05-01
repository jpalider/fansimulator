import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

public class Server {
	private String name;
	private int maxTrafficTypes;
	public Queue[] queues;
	private RoutingTable rtable;
	public double recieveRate;
	public double sendRate;
	
	/**
	 * Specifies the current traffic serviced by server, the lower value means higher priority
	 */
	public int currentTrafficType;
	
	/**
	 * Currently serviced packet in the server
	 */
	public Packet servicedPacket;
	
	/**
	 * Specifies if the server is currently busy(if it is servicing any packet)
	 */
	boolean busy;
	
	public void recieve() {
		//Select traffic type for the new packet
		short selection = (short)(maxTrafficTypes * Monitor.generator.nextDouble());
		
		//Create and move packet to the end of the queue of this server 
		new Packet(selection).moveToServer(this);
		
		//Create new packet arrival event with the time created randomly based on recieveRate
		double time = Monitor.clock + Monitor.generator.nextDouble() * recieveRate;
		
		//Schedule new arrival event
		Monitor.schedule(new Generator(time, this));
	}
	
	public void send() {
		
	}
	
	public Server route() {
		return rtable.getServerForResult(Monitor.generator.nextFloat());
	}
	
	/**
	 * Constructor for Server class
	 * @param serverName String with the name of the server
	 * @param rRate double Rate of incoming packets
	 * @param sRate double Rate of sending the packets
	 * @param routeTab Vector of Routing elements specifing routing table
	 */
	public Server(String serverName, double rRate, double sRate, RoutingTable routeTab) {
		//assumption for FAN - there are only 2 traffic types (0 - stream, 1 - elastic)
		maxTrafficTypes = 2;
		
		//the default traffic type is 0 - stream, with the highest priority
		currentTrafficType = 0;
		
		//assing the name
		this.name = serverName;
		
		//create proper amount of queues, queue will be implemented by the linked list
		queues = new LinkedList[maxTrafficTypes];
		for(int i=0; i < maxTrafficTypes; i++) {
			queues[i] = new LinkedList();
		}
		
		//assign values for arrival and send rate
		this.recieveRate 	= rRate;
		this.sendRate 		= sRate;
		
		//assign the routing table
		this.rTable = routeTab;
		
	}
}
