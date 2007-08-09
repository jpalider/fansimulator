package fan;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * This is the implementation of PFQ Queue
 * 
 */
public class PFQQueue implements Queue {
	
	String type;
	/**
	 * This is the encapsulation class for Packet,
	 * to hold information about timestamp assigned to it
	 * (according to PFQ algorithm)
	 */
	public class PacketTimestamped implements Comparable<PacketTimestamped>{
		public Packet p;
		public long startTag;
		public long finishTag;
		public double clock;
		public int compareTo(PacketTimestamped p){
			if (this.startTag == p.startTag) {
				if ( this.clock == p.clock ) 
					return 0;
				else if ( this.clock > p.clock )
					return 1;
				else
					return -1;
			}
	            
	        else if ( this.startTag > p.startTag)
	            return 1;
	        else
	            return -1;
		}
	}
	
	/**
	 * This is the extended class for Flow, to hold information about current backlog size 
	 * and received bytes.
	 * According to:
	 * xp-hpsr.pdf : "Cross-protect: implicit service differentiation and admission control"  (III.A)
	 */
	protected class FlowPFQ extends Flow{
		FlowPFQ(FlowIdentifier newFlowID){
			super(newFlowID);			
		}
		public long backlog;
		public long bytes;
	}
	
	/**
	 * Holds internal queue of packets sorted by timestamp
	 */
	protected PriorityQueue<PacketTimestamped> packetQueue;
	
	/**
	 * Maximum size of the PFQQueue
	 */
	protected int maxSize;
	
	/**
	 * Virtual Time for PFQ Queue
	 */
	protected long virtualTime;
	
	/**
	 * Active Flow List 
	 */
	protected FlowList flowList;
	
	/**
	 * Counter that "keeps track of the total number of priority bytes for congestion measurements"
	 */
	protected long priorityBytes;
	
	/**
	 * MTU definition
	 */
	private final int MTU = 1500;
	/**
	 * Variables needed to measure priority load
	 */
	protected long bandwidth = 0;
	protected Time t2 = Monitor.clock;	//new Time(0.0);
	protected Time t1 = Monitor.clock;	//new Time(0.0);
	protected long pbt2 = 0;
	protected long pbt1 = 0;
	/**
	 * Variables needed to measure fair rate
	 */
//	protected Time fpt2 = new Time(0.0);
//	protected Time fpt1 = new Time(0.0);
	protected long vt2 = 0;
	protected long vt1;
	
	/**
	 * Constructor for PFQQueue class
	 * @param intface Interface the PFQ is assigned to - needed to perform link measurements
	 *
	 */
	public PFQQueue(int size, int flowListSize, Interface intface) {
		packetQueue = new PriorityQueue<PacketTimestamped>(size);
		maxSize = size;
		virtualTime = 0;
		flowList = new FlowList(flowListSize);
		bandwidth = intface.getBandwidth();
		type = "PFQ";
	}
	
	public int getSize() {
		return packetQueue.size();
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public boolean isEmpty() {
		return packetQueue.isEmpty();
	}

	/**
	 * Counted in packets, if counted in bytes different action should be taken.
	 */
	public boolean isFull() {
		return packetQueue.size() == maxSize;
		//return ( (packetQueue.sizeInBytes+MTU) > maxSizeInBytes)
	}

	public Packet peekFirst() {
		return packetQueue.peek().p;
	}

	/**
	 * TODO: Check if packetQueue is not full<br>
	 * TODO: Check if flowList is not full - this should be provided by MBAC.
	 * According to:
	 * xp-hpsr.pdf : "Cross-protect: implicit service differentiation and admission control"  (III.B)
	 */		
	public boolean putPacket(Packet p) {

		//Create encapsulation for packet p
		PacketTimestamped pTimestamped = new PacketTimestamped();
		pTimestamped.p = p;
		pTimestamped.clock = Monitor.clock.toDouble();
				
		// TODO: "reject packet at head of longest backlog
		//at first check if there are any free places at packet queue		
		if(packetQueue.size() >= maxSize){
			p = null;
			return false;
		}
		
		//Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
			FlowPFQ packetFlow = (FlowPFQ)flowList.getFlow(pTimestamped.p.getFlowIdentifier());

			packetFlow.backlog += pTimestamped.p.getLength();

			if ( packetFlow.bytes >= MTU ){				
				pTimestamped.startTag = packetFlow.getFinishTag();
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
				packetQueue.offer(pTimestamped); // push { packet, flow_time_stamp } to PIFO
			} else {
				pTimestamped.startTag = virtualTime;
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
				packetQueue.offer(pTimestamped);
				priorityBytes += pTimestamped.p.getLength();
				packetFlow.bytes += pTimestamped.p.getLength();		
				
			}
			packetFlow.setFinishTag( packetFlow.getFinishTag() + pTimestamped.p.getLength() );
		}
		//if this is the packet of new flow
		else {

			pTimestamped.startTag = virtualTime;
			pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
			packetQueue.offer(pTimestamped);

			priorityBytes += pTimestamped.p.getLength();

			if ( (flowList.getMaxLength() - flowList.getLength()) > 0 ){
				FlowPFQ packetFlow = new FlowPFQ( pTimestamped.p.getFlowIdentifier() );
				flowList.registerNewFlow(packetFlow);				
				packetFlow.setFinishTag(pTimestamped.finishTag);		
				packetFlow.backlog = pTimestamped.p.getLength();
				packetFlow.bytes = pTimestamped.p.getLength();				
			}
		}
		
		// measurement operations
		t1 = t2;
		t2 = Monitor.clock; 
		// - for priority load
		pbt1 = pbt2;
		pbt2 = priorityBytes;	
		// - for fair rate
		vt1 = vt2;
		vt2 = virtualTime;
		
		return true;
	}

	/**
	 * TODO: Check if queue is not empty
	 */
	public Packet removeFirst() {
		
		if (packetQueue.isEmpty()){
			// clear flow list (or will it timeout all its flows?)
			return null;
		}			
		
		//Remove first packet from queue
		PacketTimestamped packet = packetQueue.remove();
		if ( flowList.contains(packet.p.getFlowIdentifier()) ){
			FlowPFQ flow = (FlowPFQ)flowList.getFlow(packet.p.getFlowIdentifier());
			flow.backlog -= packet.p.getLength();			
		} else {
			System.out.println("Something has gone wrong in PFQQueue.removeFirst()");
		}
		
//		virtualTime = packet.finishTag;
		
//		PacketTimestamped p = packetQueue.peek();
//		if (p != null){
//			virtualTime = p.finishTag;
//		}
		if ( packet.startTag != virtualTime ){
//			FlowPFQ flow = (FlowPFQ)flowList.getFlow(packet.p.getFlowIdentifier());
			virtualTime = packet.startTag;
			//Remove all queues which finishTag is smaller than virtualTime
			flowList.cleanFlows(virtualTime);
		}		
		return packet.p;
	}
	
	public FlowList getFlowList(){
		return flowList;
	}
	
	public long getFairRate(){
		// TODO
		// max{ S*C, dvt(t)}/dt
		if (t2.compareTo(t1) == 0)
			return 0; //virtualTime*8 /;
		return (long)(((vt2 - vt1)*8) / t2.substract(t1).toDouble());
	}
	
	public long getPriorityLoad(){
		if (t2.compareTo(t1) == 0)	// at startup... 
			return (long)(priorityBytes*8)/bandwidth; 
		return (long)(((pbt2-pbt1)*8)/(bandwidth*(t2.substract(t1).toDouble())));
	}

	public String getType(){
		return type;
	}
}
