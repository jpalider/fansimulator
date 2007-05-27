package fan;
import java.util.Vector;

public class Monitor {
	static Vector<Server> servers = new Vector<Server>();
	static Time clock = new Time(-1);
	static EventList agenda = new EventList();
	static Random generator = new Random();
}
