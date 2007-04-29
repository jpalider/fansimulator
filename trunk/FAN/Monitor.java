public class Monitor {
	public static double clock;
	private static EventsList taskList;
	
	//method for scheduling new event into the tasklist of the simulation
	public static void schedule(Event newEvent) {
		taskList.schedule(newEvent);
	}
	
	//method for getting and removing the front(first) event from the tasklist
	public static Event removeFirst() {
		return taskList.removeFirst();
	}
}
