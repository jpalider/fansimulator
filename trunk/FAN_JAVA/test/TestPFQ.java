package test;
import fan.*;

/**
 * Extended version of FPQQueue for testing
 * @author Mumin
 *
 */

class ExtPFQQueue extends PFQQueue {
	ExtPFQQueue(int size, int flowListSize, Interface intface){
		super(size, flowListSize, intface );
	}
	public String toString(){
		String s = new String();
		s += "virtualTime = " + virtualTime + "\n";		
		s += "t2=" + t2 + " t1=" + t1 + "\n";
		s +=  "PFQ:\n FR = " + getFairRate() + "\n PL = " + getPriorityLoad();
		return s + "\n===";
	}
}

public class TestPFQ {
	
	private static int SIZE = 100;
	private static int FLOW_LIST_SIZE = 100;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		Monitor.clock = Monitor.clock.add(new Time(1));
		ExtPFQQueue queue = new ExtPFQQueue(SIZE, FLOW_LIST_SIZE, new Interface(100, 50));
		
		queue.putPacket(new Packet(new FlowIdentifier(1), Packet.FlowType.ELASTIC, 250));
		System.out.println(queue);
		Monitor.clock = Monitor.clock.add(new Time(0.5));
				
		queue.putPacket(new Packet(new FlowIdentifier(1), Packet.FlowType.ELASTIC, 250));
		System.out.println(queue);
		Monitor.clock = Monitor.clock.add(new Time(1));
		
		queue.removeFirst();
				
		queue.putPacket(new Packet(new FlowIdentifier(1), Packet.FlowType.ELASTIC, 250));
		System.out.println(queue);
		Monitor.clock = Monitor.clock.add(new Time(1));
		
		queue.removeFirst();
		
	}

}
