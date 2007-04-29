

import java.util.Queue;
import java.util.Vector;

public class Server {

	/**
	 * @uml.property  name="name"
	 */
	private String name = "";

	/**
	 * Getter of the property <tt>name</tt>
	 * @return  Returns the name.
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * @param name  The name to set.
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** 
	 * @uml.property name="maxTrafficTypes"
	 */
	private int maxTrafficTypes = 2;

	/** 
	 * Getter of the property <tt>maxTrafficTypes</tt>
	 * @return  Returns the maxTrafficTypes.
	 * @uml.property  name="maxTrafficTypes"
	 */
	public int getMaxTrafficTypes() {
		return maxTrafficTypes;
	}

	/** 
	 * Setter of the property <tt>maxTrafficTypes</tt>
	 * @param maxTrafficTypes  The maxTrafficTypes to set.
	 * @uml.property  name="maxTrafficTypes"
	 */
	public void setMaxTrafficTypes(int maxTrafficTypes) {
		this.maxTrafficTypes = maxTrafficTypes;
	}

	/**
	 * @uml.property  name="queues" multiplicity="(0 -1)" dimension="1"
	 */
	private Queue[] queues;

	/**
	 * Getter of the property <tt>queues</tt>
	 * @return  Returns the queues.
	 * @uml.property  name="queues"
	 */
	public Queue[] getQueues() {
		return queues;
	}

	/**
	 * Setter of the property <tt>queues</tt>
	 * @param queues  The queues to set.
	 * @uml.property  name="queues"
	 */
	public void setQueues(Queue[] queues) {
		this.queues = queues;
	}

	/**
	 * @uml.property  name="routingTable"
	 */
	private Vector routingTable;

	/**
	 * Getter of the property <tt>routingTable</tt>
	 * @return  Returns the routingTable.
	 * @uml.property  name="routingTable"
	 */
	public Vector getRoutingTable() {
		return routingTable;
	}

	/**
	 * Setter of the property <tt>routingTable</tt>
	 * @param routingTable  The routingTable to set.
	 * @uml.property  name="routingTable"
	 */
	public void setRoutingTable(Vector routingTable) {
		this.routingTable = routingTable;
	}

		
		/**
		 */
		public void recieve(){
		
		}

			
			/**
			 */
			public void send(){
			
			}

				
					
					
						
						
						public Server route(){
						
											
															
															}

}
