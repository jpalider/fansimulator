public class Reception extends Event{
	
	public void run() {
		//use the method recieve on selected server to run this event
		place.recieve();
	}
	
	public Reception(double receptionTime, Server receptionPlace) {
		super(receptionTime, receptionPlace);
	}
}
