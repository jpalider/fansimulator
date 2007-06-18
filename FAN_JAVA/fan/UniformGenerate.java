package fan;

/**
 * This is the class to implement Generator with uniform time distribution
 * 
 */
public class UniformGenerate extends Generate {
	private Time range;
	public UniformGenerate(Time t, Server s, Time range) {
		super(t, s);
		this.type = GenerateType.uniform;
		this.range = range;
	}
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time( Monitor.generator.getNumber(range.toDouble()) ) );
		Monitor.agenda.schedule(new ConstantGenerate(newEventTime, place, range) );
	}
	
	public Time getRange() {
		return range;
	}
	
}
