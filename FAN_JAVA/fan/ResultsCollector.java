package fan;

import java.text.DecimalFormat;
/**
 * This is the class collecting all neccesary measurements.
 */
public class ResultsCollector {
	/**
	 * Keeps track of average queue length
	 */
	private	double avgQueueLength;
	/**
	 * Keeps track of average packet service time
	 */
	private double avgPacketServiceTime;
	/**
	 * Keeps track of total number of rejected packets
	 */
	private double avgRejectedPackets;
	/**
	 * Keeps track of average packet length
	 */
	private double avgPacketLength;
	/**
	 * Keeps track of maximum queue length during simulaton
	 */
	private int maxQueueLength;
	/**
	 * Keeps track of number of forwarded packets
	 */
	private int servicedPackets;
	/**
	 * Keeps track of dropped packets
	 */
	private int rejectedPackets;
	/**
	 * Keeps track of number of packets that were not forwarded, their destination
	 * was network directly connected to router this collector serves 
	 */
	private int locallyServicedPackets;
	/**
	 * Number of performed queue length measurements
	 */
	private int queueLengthResults;
	/**
	 * Number of performed packet length measurements
	 */
	private int checkedPacketsLength;
	
	/**
	 * Constructor - initializes all measurements
	 *
	 */
	public ResultsCollector(){
		this.avgQueueLength = 0;
		this.avgPacketServiceTime = 0;
		this.avgRejectedPackets = 0;
		this.maxQueueLength = 0;
		this.rejectedPackets = 0;
		this.servicedPackets = 0;
		this.queueLengthResults = 0;
		this.locallyServicedPackets = 0;
		this.avgPacketLength = 0;
		this.checkedPacketsLength = 0;
	}
	/**
	 * Registers an event of packet forwarding
	 * @param serviceTime Time the service was performed
	 */
	public void addServicedPacket(double serviceTime) {
		avgPacketServiceTime = (avgPacketServiceTime * (double)servicedPackets + serviceTime) / (double)(servicedPackets + 1);
		servicedPackets++;
	}
	/**
	 * Registers an event of achieving destination of packet - network directly connected  to router this collector serves 
	 */
	public void addLocallyServicedPacket() {
		locallyServicedPackets++;
	}
	/**
	 * Registers that a packet has been dropped
	 */
	public void addRejectedPacket() {
		rejectedPackets++;
	}
	/**
	 * Updates average queue length
	 * @param length length of a queue at a certain time
	 */
	public void addQueueLength(int length) {
		avgQueueLength = (avgQueueLength * (double)queueLengthResults + (double)length) / (double)(queueLengthResults + 1);
		queueLengthResults++;
		if(length > maxQueueLength) {
			maxQueueLength = length;
		}
	}
	/**
	 * Updates average packet length
	 * @param p Packet 
	 */
	public void addAvgpacketLength(Packet p){
		avgPacketLength = (avgPacketLength*(double)checkedPacketsLength + (double)p.getLength()) / (double)(checkedPacketsLength + 1);
		checkedPacketsLength++;
	}
	/**
	 * Informs about ratio of rejected packet to serviced packets  
	 * @return Percentage ratio of rejected packet to serviced packets
	 */
	public double getAvgRejectedPackets() {
		if(servicedPackets > 0)
			return ((double)rejectedPackets / (double)servicedPackets) * 100d;
		else if(rejectedPackets > 0) {
			return 100;
		}
		else return 0; 
	}
	/**
	 * Getter for average packet service time
	 * @return Average packet service time
	 */
	public double getAvgPacketServiceTime() {
		return avgPacketServiceTime;
	}
	/**
	 * Getter for average queue length
	 * @return Average queue length
	 */
	public double getAvgQueueLength() {
		return avgQueueLength;
	}
	/**
	 * Getter for maximum queue length
	 * @return Maximum queue length
	 */		
	public int getMaxQueueLength() {
		return maxQueueLength;
	}
	/**
	 * Getter for number of rejected packets
	 * @return Total numer of rejected packets
	 */
	public int getRejectedPackets() {
		return rejectedPackets;
	}
	/**
	 * Getter for number of services packets
	 * @return Total numer of serviced packets
	 */
	public int getServicedPackets() {
		return servicedPackets;
	}
	/**
	 * Getter for number of locally serviced packets
	 * @return Total numer of locally serviced packets
	 */
	public int getLocallyServicedPackets() {
		return locallyServicedPackets;
	}
	/**
	 * Getter for average packet length
	 * @return Average packet length
	 */
	public double getAvgPacketLength(){
		return avgPacketLength;
	}
	/**
	 * No idea
	 * 
	 */
	public double getCheckedPacketsLength(){
		return checkedPacketsLength;
	}
}
