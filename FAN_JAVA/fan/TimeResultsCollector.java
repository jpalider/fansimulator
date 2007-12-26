package fan;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;

public class TimeResultsCollector extends ResultsCollector {
	
	private FileWriter fileSP = null;
	private FileWriter fileLP = null;
	private FileWriter fileRP = null;
	private FileWriter fileQL = null;
	
	private HashMap<Integer, Integer> flowServicedPackets;
	private HashMap<Integer, Integer> flowLocallyServicedPackets;
	private HashMap<Integer, Integer> flowRejectedPackets;
	private HashMap<Integer, Double> flowAvgPacketServiceTime;
	
	/**
	 * Constructor which initializes variables 
	 * @param filename File name results are to be written to.
	 */
	public TimeResultsCollector(String filename) {
		super();
		
		flowServicedPackets = new HashMap<Integer, Integer>();
		flowLocallyServicedPackets = new HashMap<Integer, Integer>();
		flowRejectedPackets = new HashMap<Integer, Integer>();
		flowAvgPacketServiceTime = new HashMap<Integer, Double>();
		try{
			Debug.print(Debug.INFO, "TimeResultsCollector.TimeResultsCollector(): creating FileWriters");
			//fileSP = new FileWriter ( new File(".").getCanonicalPath() + File.separator + filename + "SP.txt" );
			//new File(".").getCanonicalPath();
			fileSP = new FileWriter (filename + "SP.txt");
			fileLP = new FileWriter (filename + "LP.txt");
			fileRP = new FileWriter (filename + "RP.txt");
			fileQL = new FileWriter (filename + "QL.txt");
		}
		catch(FileNotFoundException fnfe) {
			Debug.print(Debug.WARN, "Could not open the file: " + fnfe.getMessage() );
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Method to add serviced packet to results. Additionally it writes data to output file.
	 * @param serviceTime This is the serviceTime of the served packet
	 */
	public void addServicedPacket(double serviceTime) {
		super.addServicedPacket(serviceTime);
		try {
			fileSP.write( Monitor.clock.toDouble() + ":" + getServicedPackets() + ":" + getAvgPacketServiceTime() + "\n" );
			fileSP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Method to add serviced packet to results. Additionally it writes data to output file,
	 * it also specifies the flow number of serviced packet
	 * @param serviceTime This is the serviceTime of the served packet
	 * @param flowNumber This is the flow number of serviced packet
	 */
	public void addServicedPacket(double serviceTime, FlowIdentifier flowID) {
		super.addServicedPacket(serviceTime);
		
		//if the flow is not yet in the hashmap add it and set its value to 0.
		if (flowServicedPackets.get( flowID.toInt() ) == null) {
			flowServicedPackets.put( flowID.toInt() , 0);
			flowAvgPacketServiceTime.put( flowID.toInt(), 0d);
		}
		
		//calculate average packet service time
		double flowAPST = 	( 	flowAvgPacketServiceTime.get( flowID.toInt() ) * 
								(double)flowServicedPackets.get( flowID.toInt() ) + serviceTime 
							) 
							/ 
							(double)( flowServicedPackets.get( flowID.toInt() ) + 1 );
		
		//increase the number of serviced packets and put it in the hashtable
		flowServicedPackets.put(	flowID.toInt(),
									flowServicedPackets.get( flowID.toInt() ) + 1 );
		
		//put correct average packet service time inside hashmap
		flowAvgPacketServiceTime.put(	flowID.toInt(),
										flowAPST);
		//write the data to file
		try {
			fileSP.write( 
					Monitor.clock.toDouble() + ":" + 
					getServicedPackets() + ":" + 
					getAvgPacketServiceTime() + ":" + 
					flowServicedPackets.get( flowID.toInt() ) + ":" + 
					flowAvgPacketServiceTime.get( flowID.toInt() ) + ":" + 
					flowID.toInt() + "\n" 
					);
			fileSP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();			
		}
	}
	
	/**
	 * Method to add locally serviced packet to results and write this results to file.
	 */
	public void addLocallyServicedPacket() {
		super.addLocallyServicedPacket();
		try {
			fileLP.write( Monitor.clock.toDouble() + ":" + getLocallyServicedPackets() + "\n" );
			fileLP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Method to add locally serviced packet to results and write this results to file.
	 * It also specifies the number of the flow that packet belongs to.
	 * @param flowNumber This is the flow number of locally serviced packet.
	 */
	public void addLocallyServicedPacket( FlowIdentifier flowID ) {
		super.addLocallyServicedPacket();
		
		if( flowLocallyServicedPackets.get( flowID.toInt() ) == null ){
			flowLocallyServicedPackets.put( flowID.toInt(), 0 );
		}
		
		flowLocallyServicedPackets.put(	flowID.toInt(),
										flowLocallyServicedPackets.get( flowID.toInt() ) + 1 ); 
		try {
			fileLP.write(	Monitor.clock.toDouble() + ":" + 
							getLocallyServicedPackets() + ":" + 
							flowLocallyServicedPackets.get( flowID.toInt() ) + ":" + 
							flowID.toInt() + "\n" 
						);
			fileLP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Method to add rejected packet to results and write these results to file.
	 */
	public void addRejectedPacket() {
		super.addRejectedPacket();
		try {
			fileRP.write( Monitor.clock.toDouble() + ":" + getRejectedPackets() + ":" + getAvgRejectedPackets() + "\n" );
			fileRP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	
	/**
	 * Method to add rejected packet to results and write these results to file.
	 * It also specifies the flow number of rejected packet.
	 * @param flowNumber This is the flow number of rejected packet.
	 */
	public void addRejectedPacket( FlowIdentifier flowID ) {
		super.addRejectedPacket();
		if ( flowID == null )
			Debug.print(Debug.ERR,"Flow ID is null in rejectedPacket");
		
		if( flowRejectedPackets.get( flowID.toInt() ) == null ) {
			flowRejectedPackets.put( flowID.toInt(), 0 );
		}
		
		flowRejectedPackets.put(	flowID.toInt(),
									flowRejectedPackets.get( flowID.toInt() ) + 1 ); 
		
		try {
			fileRP.write(	Monitor.clock.toDouble() + ":" + 
							getRejectedPackets() + ":" + 
							getAvgRejectedPackets() + ":" + 
							flowRejectedPackets.get( flowID.toInt() ) + ":" + 
							flowID.toInt() + "\n" );
			fileRP.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
	}
	/**
	 * Method updates average queue length to results and write these results to file
	 * @param length This is the current queue length
	 */
	public void addQueueLength(int length) {
		super.addQueueLength(length);
		try {
			
			fileQL.write( Monitor.clock.toDouble() + ":" + length + "\n" );
//			fileQL.write( Monitor.clock.toDouble() + ":" + getAvgQueueLength() + "\n" );
			fileQL.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		//Debug.print(Debug.SPEC,"aQL at " + Monitor.clock.toString() + " " + length);
	}
	
	public void finalize() {
		Debug.print(Debug.INFO,"TimeResultCollector.Finalize()");
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
