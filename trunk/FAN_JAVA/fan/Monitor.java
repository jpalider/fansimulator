package fan;
import java.util.Vector;

/**
 * @author  dodek
 */
public class Monitor {
	/**
	 * Vector of servers
	 */
	public static Vector<Server> servers = new Vector<Server>();
	
	
	/**
	 * Main clock of the simulation
	 */
	public static Time clock = new Time(-1);
	
	
	/**
	 * Main event list - agenda of the simulation
	 */
	public static EventList agenda = new EventList();
	
	
	/**
	 * Random number generator that 
	 */
	public static Random generator = new Random();
}
