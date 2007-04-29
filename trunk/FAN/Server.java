import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;

public class Server {
	private String name;
	private int maxTrafficTypes;
	private Queue[] queues;
	private Vector routingTable;
	private double recieveRate;
	private double sendRate;
	private Random generator;
	private Packet servicedPacket;
	
	public void recieve() {
		//Select traffic type for the new packet
		short selection = (short)(maxTrafficTypes * generator.nextDouble());
		
		//Create and move packet to the end of the queue of this server 
		new Packet(selection).moveToServer(this);
		
		//Create new packet arrival event with the time created randomly based on recieveRate
		double time = Monitor.clock + generator.nextDouble() * recieveRate;
		
		//Schedule new arrival event
		Monitor.schedule(new Reception(time, this));
	}
	
	public void send() {
		;
	}
	
	public void route() {
		
	}
	/**
	 * Constructor for Server class
	 * @param serverName String with the name of the server
	 * @param rRate double Rate of incoming packets
	 * @param sRate double Rate of sending the packets
	 * @param routeTab Vector of Routing elements specifing routing table
	 */
	public Server(String serverName, double rRate, double sRate, Vector routeTab, Random gen) {
		//assumption for FAN - there are only 2 traffic types
		maxTrafficTypes = 2;
		
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
		this.routingTable = routeTab;
		
		//assign the random numbers generator
		generator = gen;
	}
}
