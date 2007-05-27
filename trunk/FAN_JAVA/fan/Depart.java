package fan;

public class Depart extends Event {
	Interface intface;
	public Depart(Time t, Interface intface) {
		super(t);
		this.intface = intface;
	}
}
