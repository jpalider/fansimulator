package fan;
import java.util.Vector;

/**
 * This is the root class from the fan simulator, from here slash screen and gui are
 * started. In text-based environment it also serves as simulation runner.
 *
 */
public class Simulator {

	public static void main(String[] args) {
		//Setup server
//		RoutingTable rt = new RoutingTable();
//		Interface i1 = new Interface(10);
//		i1.setServer(null);
//		if( !rt.addRoute(i1, 0.5f) ) {
//			System.out.println("Error in adding route to RoutingTable rt");
//			return;
//		}
		
//		Vector<Generate> generatorVector = new Vector<Generate>();
//		Vector<Server> serverVector = new Vector<Server>();
//		Configurator cfg = new Configurator("config.xml");
//		cfg.configure(serverVector, generatorVector);
//		cfg.saveConfiguration(serverVector,generatorVector);
//		if (true) return;
		
		new gui.SplashScreen().run();

		new gui.GUI();
		
/*		double simulationTime = 200;
		Server server1 = new Server("Server1");
		server1.addInterface(server1, 0.3 , 1250000);
		Server server2 = new Server("Server2");
		server2.addInterface(server2, 1, 1250000);
		server1.addInterface(server2, 0.7 , 1250000);
		Generate firstGeneratator = new Generate(new Time(1),server1);
		Monitor.clock = new Time(-1);
		Monitor.agenda.schedule(new Generate(new Time(0),server1));
		while( !Monitor.agenda.isEmpty() && Monitor.clock.compareTo(new Time(simulationTime)) <= 0 ) {
			Event now = Monitor.agenda.removeFirst();
			Monitor.clock = now.time;
			now.run();
			//System.out.println("The time is now: " + Monitor.clock);
		}
		
		RaportPrinter.printResultsForServer(server1);
		RaportPrinter.printResultsForServer(server2);*/
	}

}
