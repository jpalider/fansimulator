package fan;

/**This is the basic event class - a parent class for  
 * Depart and Generate classes. It presents a simulation
 * event in fan simulator
 */
public class Event implements Comparable<Event>{
//	/**
//	 * The server where the event is scheduled to occur
//	 */
//	protected Server place;
	
	/**
	 * Time of when the event should occur
	 */
	public Time time;
	
	/**The default constructor for Event
	 * @param t Time when the event should occur
	 * @param s Server where the event should occur
	 */
	public Event(Time t/*, Server s*/) {
		this.time = t;
//		this.place = s;
	}
	
	/**
	 * Method specifing the tasks that should be performed
	 * in this event
	 */
	public void run(){
		
	}
	
	/**
	 * Method to compare 2 Events - is it using a time criteria for events comparision
	 */
	public int compareTo(Event e){
		return this.time.compareTo(e.time);
	}
}

