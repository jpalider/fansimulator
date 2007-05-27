package fan;

/**
 * @author  dodek
 */
public class Depart extends Event {
	
	Interface intface;
	public Depart(Time t, Interface intface) {
		super(t);
		this.intface = intface;
	}
	
	public void run() {
		System.out.println("Inside run of Depart");
		super.run();
		intface.send();
	}
	
	
}
