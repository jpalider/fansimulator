
public abstract class Event {
	public double time;
	public Server place;
	abstract void run();
	
	/**
	 * Constructor for Event class
	 * @param eventTime double specifing the time in which the event is scheduled
	 */
	public Event(double eventTime, Server eventPlace) {
		this.time	= eventTime;
		this.place 	= eventPlace;
	}
}
