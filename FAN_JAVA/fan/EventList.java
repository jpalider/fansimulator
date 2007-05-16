package fan;

import java.util.PriorityQueue;
import java.util.Queue;

public class EventList {
	
	private Queue<Event> queue; 
	
	public EventList() {
		queue = new PriorityQueue<Event>();
	}
	
	public Event removeFirst(){
		return queue.poll();
	}
	
	public void schedule(Event newEvent) {
		queue.add(newEvent);
	}
}
