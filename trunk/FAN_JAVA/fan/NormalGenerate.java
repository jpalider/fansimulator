package fan;

/**
 * This is the class to implement generation of packets with 
 * normal (gauss) distribution
 * 
 */
public class NormalGenerate extends Generate {
	private Time mean, variance;
	
	public NormalGenerate(Time t, Server s, Time mean, Time variance) {
		super(t, s);
		this.type = GenerateType.normal;
		this.mean = mean;
		this.variance = variance;
	}
	
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time(Monitor.generator.getGaussianNumber(mean.toDouble(), variance.toDouble()) ) );
		Monitor.agenda.schedule(new NormalGenerate(newEventTime, place, mean, variance) );
	}

}
