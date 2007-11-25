package fan;

/**
 * This is the class responsible for generating new packets at input interface of server
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
	 * Integer to hold the size of the packet in Bytes
	 */
	protected int packetSize;
	
	/**
	 * Lower range of generated flow IDs
	 */
	protected int flowLowerRange;
	
	/**
	 * Higher range of generated flow IDs
	 */
	protected int flowHigherRange;
	
	/**
	 * Constructor for the Generator event, specifing start, finish time and additionally packet size.
	 * It also specifies lower and higher range of generated flow IDs
	 * @param t start time when the generator should be turned on
	 * @param s server where the generator should be connected
	 * @param fTime time when the generator should be turned off
	 * @param packetSize the size of the packets created by generator
	 * @param flowLowerRange the lower limit of generated flow IDs
	 * @param flowHigherRange the higher limit of generated flow IDs
	 */
	public Generate(Time t, Server s, Time fTime, int packetSize, int flowLowerRange, int flowHigherRange) {
		this(t, s, fTime);
		this.packetSize = packetSize;
		this.flowHigherRange = flowHigherRange;
		this.flowLowerRange = flowLowerRange;
	}
	
	
	/**
	 * Constructor for the Generator event specifing only start time,
	 * the Generator is turned on till the end of the simulation.
	 * It also specifies lower and higher range of generated flow IDs
	 * @param t start time when the generator should be turned on
	 * @param s server where the generator should be connected
	 * @param packetSize the size of the packets created by generator
	 * @param flowLowerRange the lower limit of generated flow IDs
	 * @param flowHigherRange the higher limit of generated flow IDs
	 */
	public Generate(Time t, Server s, int packetSize, int flowLowerRange, int flowHigherRange) {
		this(t, s);
		this.packetSize = packetSize;
		this.flowHigherRange = flowHigherRange;
		this.flowLowerRange = flowLowerRange;
	}
	
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
	 * @param t start time when the generator should be turned on
	 * @param s server where the generator should be connected
	 */
	public Generate(Time t, Server s) {
		super(t);
		place = s;
		type = GenerateType.basic;
		this.looped = true;
		this.packetSize = 1500;
		this.flowLowerRange = 0;
		this.flowHigherRange = 5;
	}
	
	/**
	 * Method runned when the generation event occurs
	 */
	public void run() {
		
		FlowIdentifier flowID = new FlowIdentifier(
									flowLowerRange + (int) Monitor.generator.getNumber( this.flowHigherRange - this.flowLowerRange ) );
		
		Packet p = new Packet( 	flowID, 
								Packet.FlowType.STREAM,
								packetSize);
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time(0.0008) );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
			Monitor.agenda.schedule( new Generate(newEventTime, place, finishTime, packetSize, flowLowerRange, flowHigherRange) );
		}
		else
			Monitor.agenda.schedule( new Generate(newEventTime, place, packetSize, flowLowerRange, flowHigherRange) );
			
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
	
	/**
	 * Returns the size of generated packets
	 * @return packetSize in bytes
	 */
	public int getPacketSize() {
		return packetSize;
	}
	
	/**
	 * Returns the type of the generator
	 * @return generator type
	 */
	public GenerateType getType() {
		return type;
	}
	
	/**
	 * Returns the lower range of generated Flow IDs
	 * @return Generated Flow ID lower range
	 */
	public int getFlowIdLowerRange() {
		return flowLowerRange;
	}
	
	/**
	 * Returns the higher range of generated Flow IDs
	 * @return Generated Flow ID higher range
	 */
	public int getFlowIdHigherRange() {
		return flowHigherRange;
	}
}
