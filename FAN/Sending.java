
public class Sending extends Event{
	
	public void run() {
		//use the method send of selected server to perform sending of package
		place.send();
	}
	
	public Sending(double sendTime, Server sendPlace) {
		super(sendTime, sendPlace);
	}
}
