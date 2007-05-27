package fan;

/**
 * This is the class responsible for generating new packets at input interface of server
 * @author  dodek
 */
public class Generate extends Event {
	private Server place;
	public Generate(Time t, Server s) {
		super(t);
		place = s;
	}
	
	public void run() {
		System.out.println("Inside run of Generate");
		Packet p = new Packet( new FlowIdentifier((int)Monitor.generator.getNumber(5)), Packet.FlowType.STREAM  );
		place.recieve(p);
		Time newEventTime = Monitor.clock.add( new Time(Monitor.generator.getNumber(1)) );
		Monitor.agenda.schedule(new Generate(newEventTime, place) );
	}
}
