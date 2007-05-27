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
		Server server1 = new Server("server1");
		server1.addInterface(server1, 1, 10);
		Generate firstGeneratator = new Generate(new Time(1),server1);
				
	}

}
