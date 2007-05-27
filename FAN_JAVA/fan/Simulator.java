package fan;

public class Simulator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Setup server
//		RoutingTable rt = new RoutingTable();
//		Interface i1 = new Interface(10);
//		i1.setServer(null);
//		if( !rt.addRoute(i1, 0.5f) ) {
//			System.out.println("Error in adding route to RoutingTable rt");
//			return;
//		}
		double simulationTime = 100;
		Server server1 = new Server("server1");
		server1.addInterface(server1, 0.3 , 10);
		Server server2 = new Server("Server2");
		server2.addInterface(server2, 1, 10);
		server1.addInterface(server2, 0.7 , 100);
		Generate firstGeneratator = new Generate(new Time(1),server1);
		Monitor.clock = new Time(-1);
		Monitor.agenda.schedule(new Generate(new Time(0),server1));
		while( !Monitor.agenda.isEmpty() && Monitor.clock.compareTo(new Time(simulationTime)) <= 0 ) {
			Event now = Monitor.agenda.removeFirst();
			Monitor.clock = now.time;
			now.run();
			System.out.println("The time is now: " + Monitor.clock);
		}
	}

}
