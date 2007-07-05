package fan;

/**
 * This is the class responsible for generating new packets at input interface of server
 * @author  dodek
 */
public class Generate extends Event {
	/**
	 * Server where this generator should be connected
	 */
	protected Server place;
	
	/**
	 * Enum used to hold the types of generator used in this simulation
	 * <ul>
	 * <li>basic - constant probability distribution with the value of 0.008
	 * <li>normal - normal probability distribution
	 * <li>constant - constant probability distribution
	 * <li>uniform - uniform probability distribution
	 * </ul>
	 */
	public static enum GenerateType {basic, normal, constant, uniform};
	
	/**
	 * Variable to hold the type of this specific generator
	 */
	protected GenerateType type;
	
	/**
	 * The finish time of this generator 
	 */
	protected Time finishTime;
	
	/**
	 * Boolean to specify if this generator has finish time, or it loops till the end of the simulation
	 */
	protected boolean looped;
	
	/**
	 * Constructor for the Generator event, specifing start and finish time. 
	 * @param t start time when the generator should be turned on
	 * @param s server where the generator should be connected
	 * @param fTime time when the generator should be turned off
	 */
	public Generate(Time t,Server s, Time fTime) {
		this(t,s);
		this.looped = false;
		this.finishTime = fTime;
	}
	
	/**
	 * Constructor for the Generator event specifing only start time,
	 * the Generator is turned on till the end of the simulation
	 * @param t
	 * @param s
	 */
	public Generate(Time t, Server s) {
		super(t);
		place = s;
		type = GenerateType.basic;
		this.looped = true;
	}
	
	/**
	 * Method runned when the generation event occurs
	 */
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time(0.0008) );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
		}
		Monitor.agenda.schedule(new Generate(newEventTime, place) );
			
	}
	
	/**
	 * Returns the server where the generator is connected
	 * @return server
	 */
	public Server getServer() {
		return place;
	}
	
	/**
	 * Returns the finish time of this event occurence loop
	 * @return finish time
	 */
	public Time getFinishTime() {
		return finishTime;
	}
	
	/**
	 * True if event is to be looped till the end of the simulation
	 * False if it has finish time, when it should be deleted
	 * @return boolean
	 */
	public boolean isLooped() {
		return looped;
	}
}
