package fan;

/**
 * This is the class to implement Generator, which generates new packets,
 * with the same, constant interval specified in constructor
 * 
 */
public class ConstantGenerate extends Generate {
	private Time interval;
	
	
	/**
	 * Constructor of generator with constant probability distribution. It specifies the start
	 * and finish time which should be used to start and turn off this generator. Additionally it
	 * specifies the size of the packets to be generated
	 * @param t start time of generator
	 * @param s server where the generator should be connected
	 * @param interval interval to be used when generating next packets
	 * @param finishTime finish time of the generator
	 * @param packetSize the size of the packets to be created by generator 
	 */
	public ConstantGenerate(Time t, Server s, Time interval, Time finishTime, int packetSize) {
		this(t, s, interval, finishTime);
		this.packetSize = packetSize;
	}
	
	/**
	 * Constructor of generator with constant probability distribution. It specifies the start
	 * time which should be used to start generator. It works till the end of the simulation.
	 * Additionally it specifies the size of the packets to be generated
	 * @param t start time of generator
	 * @param s server where the generator should be connected
	 * @param interval interval to be used when generating next packets
	 * @param packetSize the size of the packets to be created by generator 
	 */
	public ConstantGenerate(Time t, Server s, Time interval, int packetSize) {
		this(t, s, interval);
		this.packetSize = packetSize;
	}
	
	/**
	 * Constructor of generator with constant probability distribution. It specifies the start
	 * and finish time which should be used to start and turn off this generator
	 * @param t start time of generator
	 * @param s server where the generator should be connected
	 * @param interval interval to be used when generating next packets
	 * @param finishTime finish time of the generator 
	 */
	public ConstantGenerate(Time t, Server s, Time interval, Time finishTime) {
		this(t, s, interval);
		this.looped = false;
		this.finishTime = finishTime;
	}
	
	/**
	 * Constructor of generator with constant probability distribution. It specifies the start
	 * time which should be used to start generator. It works till the end of the simulation
	 * @param t start time of generator
	 * @param s server where the generator should be connected
	 * @param interval interval to be used when generating next packets
	 */
	public ConstantGenerate(Time t, Server s, Time interval) {
		super(t,s);
		this.type = GenerateType.constant;
		this.interval = interval;
		this.looped = true;
	}
	
	
	/**
	 * Method to be runned when generation event occurs
	 */
	public void run() {
		Packet p = new Packet( 	new FlowIdentifier((int)Monitor.generator.getNumber(5)), 
								Packet.FlowType.STREAM,
								packetSize);
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( interval );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
		}
		Monitor.agenda.schedule(new ConstantGenerate(newEventTime, place, interval) );
	}
	
	/**
	 * Returns interval used between generated packets
	 * @return interval
	 */
	public Time getInterval() {
		return interval;
	}
}
