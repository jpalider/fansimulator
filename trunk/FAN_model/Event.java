

public class Event {

	/**
	 * @uml.property  name="time"
	 */
	private long time;

	/**
	 * Getter of the property <tt>time</tt>
	 * @return  Returns the time.
	 * @uml.property  name="time"
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Setter of the property <tt>time</tt>
	 * @param time  The time to set.
	 * @uml.property  name="time"
	 */
	public void setTime(long time) {
		this.time = time;
	}

		
		/**
		 */
		public abstract void run();
		

}
