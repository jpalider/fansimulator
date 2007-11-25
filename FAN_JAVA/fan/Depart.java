package fan;

/**
 * Class that creates packet departure event
 */
public class Depart extends Event {
	
	/**
	 * Interface where depart event will occur
	 */
	private Interface intface;
	
	
	/**
	 * Constructor for this class
	 * @param t Time when event should occur
	 * @param intface Interface where packet departure should occur
	 */
	public Depart(Time t, Interface intface) {
		super(t);
		this.intface = intface;
	}
	
	
	/**
	 * Method that will be runned during actual packet departure
	 */
	public void run() {
		//Debug.print("Inside run of Depart");
		super.run();
		intface.send();
	}
	
	
}
