package fan;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This is the class that represents a list of pending events in fan simulator.
 * @author Mumin
 */
public class EventList {
	
	/**
	 * The queue that stores the list of events that are scheduled to occur
	 */
	private Queue<Event> queue; 
	
	
	/**
	 * Default constructor
	 * @author Mumin
	 */
	public EventList() {
		queue = new PriorityQueue<Event>();
	}
	
	
	/**
	 * Returns an Event that is going to happen now.
	 * @return Event
	 * @author Mumin
	 */
	public Event removeFirst(){
		return queue.poll();
	}
	
	
	/**
	 * Schedules an event according to occurence time of that event.
	 * @author Mumin
	 */
	public void schedule(Event newEvent) {
		queue.add(newEvent);
	}
	
	
	/**
	 * Method to check if there are any events waiting to be scheduled
	 * @return 	True if there are no more events in the list of all events,
	 * 			False otherwise.
	 */
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}