import java.util.Collections;
import java.util.Vector;


public class RoutingTable {
	
	private class Route implements Comparable{
		
		public Server server;
		
		public float probability;
		
		/**
		 * Method to compare Route objects by their probability;
		 * @param toCompate Object to be compared with this one
		 */
		public int compareTo(Object toCompare) {
			Route routeToCompare = (Route)toCompare;
			if( routeToCompare.probability == this.probability )
				return 0;
			else if( routeToCompare.probability < this.probability ) {
				return 1;
			}
			else return -1;
		}
		
		public Route(Server newServer, float probab) {
			server = newServer;
			probability = probab;
		}
		
	}
	
	Vector<Route> routing;
	
	public RoutingTable() {
		routing = new Vector<Route>();		
	}
	public boolean addRoute(Server newServer,float probability) {
		if(getProbabilitySum() + probability <= 1) {
			routing.add( new Route(newServer,probability) );
			return true;
		}
		else return false;
	}
	
	public Server getServerForResult(float result) {
		Collections.sort(routing);
		Collections.reverse(routing);
		float sum = 0;
		for(int i=0; i < routing.size(); i++) {
			sum += routing.elementAt(i).probability;
			if(sum >= result)
				return routing.elementAt(i).server;
		}
		return null;		
	}
	
	public float getProbabilitySum() {
		float sum = 0;
		for(int i=0; i< routing.size(); i++) {
			sum += routing.elementAt(i).probability;
		}
		return sum;
	}
}
