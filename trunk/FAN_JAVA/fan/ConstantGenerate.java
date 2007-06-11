package fan;

/**
 * This is the class to implement Generator, which generates new packets,
 * with the same, constant inteval specified in constructor
 * @author dodek
 *
 */
public class ConstantGenerate extends Generate {
	private Time interval;
	public ConstantGenerate(Time t, Server s, Time interval) {
		super(t,s);
		this.type = GenerateType.constant;
		this.interval = interval;
	}
	public void run() {
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( interval );
		Monitor.agenda.schedule(new ConstantGenerate(newEventTime, place, interval) );
	}
}
