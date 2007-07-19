package fan;
import java.util.Vector;

/**
 * @author  dodek
 */
public class Monitor {
	public static Vector<Server> servers = new Vector<Server>();
	public static Time clock = new Time(-1);
	public static EventList agenda = new EventList();
	public static Random generator = new Random();
}
