package fan;

/**
 * This is the class to implement Generator with uniform time distribution
 * 
 */
public class UniformGenerate extends Generate {
	/**
	 * Start Range of the probability distrubution to be used by this generator
	 */
	private Time rangeStart;
	
	/**
	 * End Range of the probability distrubution to be used by this generator
	 */
	private Time rangeEnd;
	
	/**
	 * Constructor for the UniformGenerate class which creates generator with the
	 * specified distribution range. It is started and finished at specified time 
	 * limits. Additionally it specifies the size of generated packets, lower and higher bounds of generated
	 * flow IDs.
	 * @param t start time of this generator
	 * @param s server to which this generator should be connected
	 * @param range time range of the uniform distribution to be used in this generator
	 * @param fTime finish time when this generator should be turned off
	 * @param packetSize the size of the packets created by this generator
	 * @param flowLowerRange the lower limit of generated flow IDs
	 * @param flowHigherRange the higher limit of generated flow IDs
	 */
	public UniformGenerate(Time t, Server s, Time rangeStart, Time rangeEnd, Time fTime, int packetSize, int flowLowerRange, int flowHigherRange) {
		this(t, s, rangeStart, rangeEnd, fTime);
		this.packetSize = packetSize;
		this.flowLowerRange = flowLowerRange;
		this.flowHigherRange = flowHigherRange;
	}
	
	/**
	 * Constructor for the UniformGenerate class which creates generator with specified 
	 * time distribution range. It is started at selected Time limit and works till the 
	 * end of this simulation. Additionally it specifies the size of generated packets, lower and higher bounds of generated
	 * flow IDs.
	 * @param t start time of this generator
	 * @param s server where this generator should be connected
	 * @param range time range of the uniform distribution to be used by this generator
	 * @param packetSize the size of the packets created by this generator
	 * @param flowLowerRange the lower limit of generated flow IDs
	 * @param flowHigherRange the higher limit of generated flow IDs
	 */
	public UniformGenerate(Time t, Server s, Time rangeStart, Time rangeEnd, int packetSize, int flowLowerRange, int flowHigherRange) {
		this(t, s, rangeStart, rangeEnd);
		this.packetSize = packetSize;
		this.flowLowerRange = flowLowerRange;
		this.flowHigherRange = flowHigherRange;
	}
	
	/**
	 * Constructor for the UniformGenerate class which creates generator with the
	 * specified distribution range. It is started and finished at specified time 
	 * limits
	 * @param t start time of this generator
	 * @param s server to which this generator should be connected
	 * @param range time range of the uniform distribution to be used in this generator
	 * @param fTime finish time when this generator should be turned off
	 */
	public UniformGenerate(Time t, Server s, Time rangeStart, Time rangeEnd, Time fTime) {
		this(t, s, rangeStart, rangeEnd);
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
	public UniformGenerate(Time t, Server s, Time rangeStart, Time rangeEnd ) {
		super(t, s);
		this.type = GenerateType.uniform;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
		this.looped = true;
		this.flowLowerRange = 0;
		this.flowHigherRange = 5;
	}
	
	/**
	 * Tasks to be runned when this generation event occurs
	 */
	public void run() {
		
		FlowIdentifier flowID = new FlowIdentifier(
				flowLowerRange + (int) Monitor.generator.getNumber( this.flowHigherRange - this.flowLowerRange ) );

		Packet p = new Packet( 	flowID, 
								Packet.FlowType.STREAM,
								packetSize);
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time( rangeStart.toDouble() + Monitor.generator.getNumber(rangeEnd.toDouble() - rangeStart.toDouble()) ) );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
			Monitor.agenda.schedule(new UniformGenerate(	newEventTime, 
															place, 
															rangeStart,
															rangeEnd,
															finishTime,
															packetSize,
															flowLowerRange,
															flowHigherRange) );
		}
		else 
			Monitor.agenda.schedule(new UniformGenerate(	newEventTime, 
															place, 
															rangeStart,
															rangeEnd,
															packetSize,
															flowLowerRange,
															flowHigherRange) );
	}
	
	
	/**
	 * Returns start range of the Time probability distribution used by this generator
	 * @return range of distribution
	 */
	public Time getStartRange() {
		return rangeStart;
	}
	
	
	/**
	 * Returns end range of the Time probability distribution used by this generator
	 * @return range of distribution
	 */
	public Time getEndRange() {
		return rangeEnd;
	}
}
