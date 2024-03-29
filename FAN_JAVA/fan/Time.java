package fan;

/**
 * This is the class that represents Time in fan simulator
 * 
 */
public class Time implements Comparable<Time>{
	/**
	 * In this implementation time is stored in double
	 */
	private double t;
	/**
	 * Basic constructor for this class
	 */
	public Time(double time) {
		this.t = time;
	}
	
	/**
	 * Copy constructor for Time
	 * @param oldTime Time to be copied into this new object
	 */
	public Time( Time oldTime ) {
		this.t = oldTime.t;
	}
	
	/**This method returns new Time object with time value equal to 
	 * the sum of this and tt times. It doesn't change time value of this
	 * object.
	 * @param tt Time object to be added to the time value of this 
	 * @return Time with the value equal to: this + tt
	 */
	public Time add(Time tt) {
		return new Time(tt.t + this.t);
	}
	
	/**This method returns new Time object with time value equal to 
	 * this minus tt. It doesn't change time value of this
	 * object.
	 * @param tt Time object to be substracted from the time value of this 
	 * @return Time with the value equal to: this - tt
	 */
	public Time substract(Time tt) {
		return new Time(this.t - tt.t);
	} 
	
	/** Method to implement Comparable interface
	 *  @see Comparable interface
	 *  @return 1 when this object is bigger than tt, 0 when equal, and -1 when tt is bigger than this
	 */
	public int compareTo(Time tt) {
		if(tt.t - this.t < 0)
			return 1;
		else if(tt.t - this.t == 0)
			return 0;
		return -1;
	}
	
	public String toString(){
		return String.valueOf(t);
	}
	
	public double toDouble(){
		return this.t;
	}
}
