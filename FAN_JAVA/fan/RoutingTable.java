package fan;

import java.util.Collections;
import java.util.Vector;


public class RoutingTable {
	
	/**
	 * Class for holding information about the possible routes.
	 * 
	 */
	private class Route implements Comparable{
		
		public Interface serverInterface;
		
		/** Probability specifing the possibility of the packet 
		 * going into this destination 
		 */
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
		
		public Route(Interface newServerInterface, float probab) {
			serverInterface = newServerInterface;
			probability = probab;
		}
		
	}
	
	/**
	 * Vector of Route objects holding information about
	 * possible routes to other hosts
	 */
	Vector<Route> routing;
	
	/**
	 * Default constructot for class RoutingTable
	 */
	public RoutingTable() {
		routing = new Vector<Route>();		
	}
	
	/**
	 * Method for adding the route to the list of possible routes
	 * @param newServer Server where the route goes
	 * @param probability float specifing the probability that the Packet should go to the newServer
	 * @return true if probability is specified correctly, false when the probability is too big and
	 * the sum of all probabilites is more than 1
	 */
	public boolean addRoute(Interface newServerInterface,float probability) {
		if(getProbabilitySum() + probability <= 1) {
			routing.add( new Route(newServerInterface,probability) );
			return true;
		}
		else return false;
	}
	
	/**
	 * Method for getting the server for specified random result
	 * @param result float from 0 to 1
	 * @return Server for this random value
	 */
	public Interface getServerInterfaceForResult(float result) {
		Collections.sort(routing);
		Collections.reverse(routing);
		float sum = 0;
		for(int i=0; i < routing.size(); i++) {
			sum += routing.elementAt(i).probability;
			if(sum >= result)
				return routing.elementAt(i).serverInterface;
		}
		return null;		
	}
	
	/**
	 * Method for getting the probabilities sum for all existing interfaces included in
	 * this class object
	 * @return float with the sum of probabilites
	 */
	public float getProbabilitySum() {
		float sum = 0;
		for(int i=0; i< routing.size(); i++) {
			sum += routing.elementAt(i).probability;
		}
		return sum;
	}
}
