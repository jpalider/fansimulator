

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class ServerManager {

	/**
	 * @uml.property  name="server"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" inverse="serverManager:Server"
	 */
	private Vector server = new java.util.Vector();

	/**
	 * Getter of the property <tt>server</tt>
	 * @return  Returns the server.
	 * @uml.property  name="server"
	 */
	public Vector getServer() {
		return server;
	}

	/**
	 * Returns the element at the specified position in this list.
	 * @param index  index of element to return.
	 * @return  the element at the specified position in this list.
	 * @see java.util.List#get(int)
	 * @uml.property  name="server"
	 */
	public Server getServer(int i) {
		return (Server) server.get(i);
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 * @return  an iterator over the elements in this list in proper sequence.
	 * @see java.util.List#iterator()
	 * @uml.property  name="server"
	 */
	public Iterator serverIterator() {
		return server.iterator();
	}

	/**
	 * Returns <tt>true</tt> if this list contains no elements.
	 * @return  <tt>true</tt> if this list contains no elements.
	 * @see java.util.List#isEmpty()
	 * @uml.property  name="server"
	 */
	public boolean isServerEmpty() {
		return server.isEmpty();
	}

	/**
	 * Returns <tt>true</tt> if this list contains the specified element.
	 * @param element  element whose presence in this list is to be tested.
	 * @return  <tt>true</tt> if this list contains the specified element.
	 * @see java.util.List#contains(Object)
	 * @uml.property  name="server"
	 */
	public boolean containsServer(Server server) {
		return this.server.contains(server);
	}

	/**
	 * Returns <tt>true</tt> if this list contains all of the elements of the specified collection.
	 * @param elements  collection to be checked for containment in this list.
	 * @return  <tt>true</tt> if this list contains all of the elements of the specified collection.
	 * @see java.util.List#containsAll(Collection)
	 * @uml.property  name="server"
	 */
	public boolean containsAllServer(Collection server) {
		return this.server.containsAll(server);
	}

	/**
	 * Returns the number of elements in this list.
	 * @return  the number of elements in this list.
	 * @see java.util.List#size()
	 * @uml.property  name="server"
	 */
	public int serverSize() {
		return server.size();
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence.
	 * @return  an array containing all of the elements in this list in proper sequence.
	 * @see java.util.List#toArray()
	 * @uml.property  name="server"
	 */
	public Server[] serverToArray() {
		return (Server[]) server.toArray(new Server[server.size()]);
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence; the runtime type of the returned array is that of the specified array.
	 * @param a  the array into which the elements of this list are to be stored.
	 * @return  an array containing all of the elements in this list in proper sequence.
	 * @see java.util.List#toArray(Object[])
	 * @uml.property  name="server"
	 */
	publicServer[] serverToArray(Server[] server) {
		return (Server[]) this.server.toArray(server);
	}

	/**
	 * Inserts the specified element at the specified position in this list (optional operation)
	 * @param index  index at which the specified element is to be inserted.
	 * @param element  element to be inserted.
	 * @see java.util.List#add(int,Object)
	 * @uml.property  name="server"
	 */
	public void addServer(int index, Server server) {
		this.server.add(index, server);
	}

	/**
	 * Appends the specified element to the end of this list (optional operation).
	 * @param element  element to be appended to this list.
	 * @return  <tt>true</tt> (as per the general contract of the <tt>Collection.add</tt> method).
	 * @see java.util.List#add(Object)
	 * @uml.property  name="server"
	 */
	public boolean addServer(Server server) {
		return this.server.add(server);
	}

	/**
	 * Removes the element at the specified position in this list (optional operation).
	 * @param index  the index of the element to removed.
	 * @return  the element previously at the specified position.
	 * @see java.util.List#remove(int)
	 * @uml.property  name="server"
	 */
	public Object removeServer(int index) {
		return server.remove(index);
	}

	/**
	 * Removes the first occurrence in this list of the specified element  (optional operation).
	 * @param element  element to be removed from this list, if present.
	 * @return  <tt>true</tt> if this list contained the specified element.
	 * @see java.util.List#remove(Object)
	 * @uml.property  name="server"
	 */
	public boolean removeServer(Server server) {
		return this.server.remove(server);
	}

	/**
	 * Removes all of the elements from this list (optional operation).
	 * @see java.util.List#clear()
	 * @uml.property  name="server"
	 */
	public void clearServer() {
		server.clear();
	}

	/**
	 * Setter of the property <tt>server</tt>
	 * @param server  the server to set.
	 * @uml.property  name="server"
	 */
	public void setServer(Vector server) {
		this.server = server;
	}

}
