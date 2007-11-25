package fan;

/**
 * This is the basic event class - a parent class for   Depart and Generate classes. It presents a simulation event in fan simulator
 */
public class Event implements Comparable<Event>{

	
	/**
	 * Time of when the event should occur
	 */
	public Time time;
	
	
	/**The default constructor for Event
	 * @param t Time when the event should occur
	 * 
	 */
	public Event( Time t ) {
		this.time = t;
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
	
	
	/**
	 * Method to get time when the event should occur
	 * @return
	 */
	public Time getTime() {
		return time;
	}
}

