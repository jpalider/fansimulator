package fan;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class TimeResultsCollector extends ResultsCollector {
	
	private String filename;
	private FileWriter fileSP = null;
	private FileWriter fileLP = null;
	private FileWriter fileRP = null;
	private FileWriter fileQL = null;
	
	public TimeResultsCollector(String filename) {
		super();
		this.filename = filename;
		try{
			fileSP = new FileWriter (filename + "SP.txt");
			fileLP = new FileWriter (filename + "LP.txt");
			fileRP = new FileWriter (filename + "RP.txt");
			fileQL = new FileWriter (filename + "QL.txt");
		}
		catch(FileNotFoundException fnfe) {
			System.out.println ( "Could not open the file: " + fnfe.getMessage() );
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void addServicedPacket(double serviceTime) {
		super.addServicedPacket(serviceTime);
		try {
			fileSP.write( Monitor.clock.toDouble() + ":" + getServicedPackets() + ":" + getAvgPacketServiceTime() + "\n" );
			fileSP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void addLocallyServicedPacket() {
		super.addLocallyServicedPacket();
		try {
			fileLP.write( Monitor.clock.toDouble() + ":" + getLocallyServicedPackets() + "\n" );
			fileLP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void addRejectedPacket() {
		super.addRejectedPacket();
		try {
			fileRP.write( Monitor.clock.toDouble() + ":" + getRejectedPackets() + ":" + getAvgRejectedPackets() + "\n" );
			fileRP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	public void addQueueLength(int length) {
		super.addQueueLength(length);
		try {
			fileQL.write( Monitor.clock.toDouble() + ":" + getAvgQueueLength() + "\n" );
			fileQL.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	public void finalize() {
		try {
			fileSP.close();
			fileRP.close();
			fileLP.close();
			fileQL.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
