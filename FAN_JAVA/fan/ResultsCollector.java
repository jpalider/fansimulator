package fan;

public class ResultsCollector {
	private	double avgQueueLength;
	private double avgPacketServiceTime;
	private double avgRejectedPackets;
	
	private int maxQueueLength;
	
	private int servicedPackets;
	private int rejectedPackets;
	private int queueLengthResults;
	
	
	public ResultsCollector(){
		this.avgQueueLength = 0;
		this.avgPacketServiceTime = 0;
		this.avgRejectedPackets = 0;
		this.maxQueueLength = 0;
		this.rejectedPackets = 0;
		this.servicedPackets = 0;
		this.queueLengthResults = 0;
	}
	
	public void addServicedPacket(double serviceTime) {
		avgPacketServiceTime = (avgPacketServiceTime * (double)servicedPackets + serviceTime) / (double)(servicedPackets + 1);
		servicedPackets++;
	}
	
	public void addRejectedPacket() {
		rejectedPackets++;
	}
	
	public void addQueueLength(int length) {
		avgQueueLength = (avgQueueLength * (double)queueLengthResults + (double)length) / (double)(queueLengthResults + 1);
		queueLengthResults++;
		if(length > maxQueueLength) {
			maxQueueLength = length;
		}
	}
	
	public double getAvgRejectedPackets() {
		if(servicedPackets > 0)
			return (double)rejectedPackets / (double)servicedPackets;
		else if(rejectedPackets > 0) {
			return 1;
		}
		else return 0; 
	}
	
	public double getAvgPacketServiceTime() {
		return avgPacketServiceTime;
	}
	
	public double getAvgQueueLength() {
		return avgQueueLength;
	}
		
	public int maxQueueLength() {
		return maxQueueLength;
	}

	public int getRejectedPackets() {
		return rejectedPackets;
	}

	public int getServicedPackets() {
		return servicedPackets;
	}
	
	
}
