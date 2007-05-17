package fan;

/**
 * 
 * @author dodek
 *
 */
public class Event implements Comparable<Event>{
	protected Server place;
	public Time time;
	public Event(Time t, Server s) {
		
	}
	public void run(){
		
	}
	public int compareTo(Event e){
		return this.time.compareTo(e.time);
	}
}

