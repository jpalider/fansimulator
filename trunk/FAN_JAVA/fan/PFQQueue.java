package fan;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * This is the implementation of PFQ Queue
 * 
 */
public class PFQQueue implements Queue {
	
	/**
	 * This is the encapsulation class for Packet,
	 * to hold information about timestamp assigned to it
	 * (according to PFQ algorithm)
	 */
	private class PacketTimestamped {
		public Packet p;
		public long startTag;
		public long finishTag;
	}
	
	/**
	 * This is the extended class for Flow, to hold information about current backlog size 
	 * and received bytes.
	 * According to:
	 * xp-hpsr.pdf : "Cross-protect: implicit service differentiation and admission control"  (III.A)
	 */
	private class FlowPFQ extends Flow{
		FlowPFQ(FlowIdentifier newFlowID){
			super(newFlowID);			
		}
		public long backlog;
		public long bytes;
	}
	
	/**
	 * Holds internal queue of packets sorted by timestamp
	 */
	private PriorityQueue<PacketTimestamped> packetQueue;
	
	/**
	 * Maximum size of the PFQQueue
	 */
	private int maxSize;
	
	/**
	 * Virtual Time for PFQ Queue
	 */
	private long virtualTime;
	
	/**
	 * Active Flow List 
	 */
	private FlowList flowList;
	
	/**
	 * Counter that "keeps track of the total number of priority bytes for congestion measurements"
	 */
	private long priorityBytes;
	
	/**
	 * MTU definition
	 */
	private final int MTU = 1500;
	/**
	 * Variables needed to measure priority load
	 */
	private long bandwidth = 0;
	private Time t2 = new Time(0.0);
	private Time t1 = new Time(0.0);
	private long pbt2 = 0;
	private long pbt1 = 0;
	/**
	 * Variables needed to measure fair rate
	 */
//	private Time fpt2 = new Time(0.0);
//	private Time fpt1 = new Time(0.0);
	private long vt2 = 0;
	private long vt1;
	
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
				
		// TODO: "reject packet at head of longest backlog
		//at first check if there are any free places at packet queue		
		if(packetQueue.size() >= maxSize)
			return false;
		
		priorityBytes += p.getLength();
		
		//Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
			FlowPFQ packetFlow = (FlowPFQ)flowList.getFlow(pTimestamped.p.getFlowIdentifier());
			// (1)
			packetFlow.backlog += pTimestamped.p.getLength();
			// (2)
			if ( packetFlow.bytes >= MTU ){				
				pTimestamped.startTag = packetFlow.getFinishTag();
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
				packetQueue.offer(pTimestamped); // push { packet, flow_time_stamp } to PIFO
			} else {
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();		
				packetQueue.offer(pTimestamped);
				priorityBytes += pTimestamped.p.getLength();
				packetFlow.bytes += pTimestamped.p.getLength();		
				
			}
			packetFlow.setFinishTag( packetFlow.getFinishTag() + pTimestamped.p.getLength() );
//			packetFlow.setFinishTag(pTimestamped.finishTag); should be the same as the former one 
		
	
//--old			
//			//packetFlow.backlog += pTimestamped.p.getLength();
//			Flow packetFlow = flowList.getFlow(pTimestamped.p.getFlowIdentifier());
//			pTimestamped.startTag = packetFlow.getFinishTag();
//			pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
//			packetFlow.setFinishTag(pTimestamped.finishTag);
//			packetQueue.offer(pTimestamped);
//----			
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
		}			
		
		//Remove first packet from queue
		PacketTimestamped packet = packetQueue.remove();
		if ( flowList.contains(packet.p.getFlowIdentifier()) ){
			FlowPFQ flow = (FlowPFQ)flowList.getFlow(packet.p.getFlowIdentifier());
			flow.backlog -= packet.p.getLength();
			
		} else {
			System.out.println("Something gone wrong in PFQQueue.removeFirst()");
		}
		
		//Set virtualTime to currently serviced packet
		if (packetQueue.peek().startTag != virtualTime){
			virtualTime = packetQueue.peek().startTag;
			//Remove all queues which finishTag is smaller than virtualTime
			flowList.cleanFlows(virtualTime);
		}		
		return packet.p;
	}
	
	public FlowList getFlowList(){
		return flowList;
	}
	
	public long getFairRate(){
		// max{ S*C, dvt(t)}/dt
		return (long)(((vt2 - vt1)*8) / t2.substract(t1).toDouble());
	}
	
	public long getPriorityLoad(){		
		return (long)(((pbt2-pbt1)*8)/(bandwidth*(t2.substract(t1).toDouble())));
	}

}
