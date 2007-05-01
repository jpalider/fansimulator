import java.util.Random;

public class Monitor {
	public static double clock;
	private static EventsList taskList;
	/**
	 * Random numbers generator
	 */
	public static Random generator;
	
	/**
	 * Method for scheduling new event into the tasklist of the simulation
	 * @param newEvent Event to be scheduled
	 */
	public static void schedule(Event newEvent) {
		taskList.schedule(newEvent);
	}
	
	/**
	 * Method for getting and removing the front(first) event from the tasklist
	 * @return Event from the front of the queue
	 */
	public static Event removeFirst() {
		return taskList.removeFirst();
	}
	
	
}
