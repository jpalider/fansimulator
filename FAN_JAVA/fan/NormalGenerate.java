package fan;

/**
 * This is the class to implement generation of packets with 
 * normal (gauss) distribution
 * 
 */
public class NormalGenerate extends Generate {
	private Time mean, variance;
	
	/**
	 * Constructor for Generator of packets with Normal(Gauss) probability distribution,
	 * that starts and finished within specified time limits
	 * @param t start time of this generator
	 * @param s server where this generator should be connected
	 * @param mean Mean of normal probability distribution
	 * @param variance Variance of this probability distribution
	 * @param fTime finish time of this generator
	 */
	public NormalGenerate(Time t, Server s, Time mean, Time variance, Time fTime) {
		this(t,s,mean,variance);
		this.looped = false;
		this.finishTime = fTime;
	}
	
	/**
	 * Constructor for Generator of packets with Normal(Gauss) probability distribution,
	 * that starts at specified time and lasts till the end of whole simulation
	 * @param t start time of this generator
	 * @param s server where this generator should be connected
	 * @param mean Mean of normal probability distribution
	 * @param variance Variance of this probability distribution
	 */
	public NormalGenerate(Time t, Server s, Time mean, Time variance) {
		super(t, s);
		this.type = GenerateType.normal;
		this.mean = mean;
		this.variance = variance;
		this.looped = true;
	}
	
	/**
	 * Method runned when this generation event occurs
	 */
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time(Monitor.generator.getGaussianNumber(mean.toDouble(), variance.toDouble()) ) );
		if(!looped) {
			if (newEventTime.compareTo(finishTime) > 0)
				return;
		}
		Monitor.agenda.schedule(new NormalGenerate(newEventTime, place, mean, variance) );
	}

	/**
	 * Returns mean of this normal probability distribution
	 * @return mean
	 */
	public Time getMean() {
		return mean;
	}
	
	/**
	 * Returns variance of this normal probability distribution
	 * @return variance
	 */
	public Time getVariance() {
		return variance;
	}
}
