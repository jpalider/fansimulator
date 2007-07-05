package fan;

/**
 * This is the class to implement Generator with uniform time distribution
 * 
 */
public class UniformGenerate extends Generate {
	/**
	 * Range of the probability distrubution to be used by this generator
	 */
	private Time range;
	
	/**
	 * Constructor for the UniformGenerate class which creates generator with the
	 * specified distribution range. It is started and finished at specified time 
	 * limits
	 * @param t start time of this generator
	 * @param s server to which this generator should be connected
	 * @param range time range of the uniform distribution to be used in this generator
	 * @param fTime finish time when this generator should be turned off
	 */
	public UniformGenerate(Time t, Server s, Time range, Time fTime) {
		this(t,s,range);
		this.looped = false;
		this.finishTime = fTime;
	}
	
	/**
	 * Constructor for the UniformGenerate class which creates generator with specified 
	 * time distribution range. It is started at selected Time limit and works till the end of this simulation
	 * @param t start time of this generator
	 * @param s server where this generator should be connected
	 * @param range time range of the uniform distribution to be used by this generator
	 */
	public UniformGenerate(Time t, Server s, Time range) {
		super(t, s);
		this.type = GenerateType.uniform;
		this.range = range;
		this.looped = true;
	}
	
	/**
	 * Tasks to be runned when this generation event occurs
	 */
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time( Monitor.generator.getNumber(range.toDouble()) ) );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
		}
		Monitor.agenda.schedule(new ConstantGenerate(newEventTime, place, range) );
	}
	
	/**
	 * Returns range of the Time probability distribution used by this generator
	 * @return range of distribution
	 */
	public Time getRange() {
		return range;
	}
	
}
