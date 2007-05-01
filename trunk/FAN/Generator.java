public class Generator extends Event{
	
	public void run() {
		//use the method recieve on selected server to run this event
		place.recieve();
	}
	
	public Generator(double receptionTime, Server receptionPlace) {
		super(receptionTime, receptionPlace);
	}
}
