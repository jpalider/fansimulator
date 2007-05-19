package fan;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * This is the class that represents a list of pending events in fan simulator.
 * @author Mumin
 */
public class EventList {
	
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
	 * Schedules an event according to occurence time of that event. In case of
	 * duplicate time entries ?? where is it put first/last ??
	 * @author Mumin
	 */
	public void schedule(Event newEvent) {
		queue.add(newEvent);
	}
}
