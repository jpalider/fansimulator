package fan;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * This is the implementation of PFQ Queue
 * 
 */
public class PFQQueue implements Queue {
	
	/**
	 * This is the encapsulatoin class for Packet,
	 * to hold information about timestamp assigned to it
	 * (according to PFQ algorithm)
	 */
	private class PacketTimestamped {
		public Packet p;
		public long startTag;
		public long finishTag;
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
	 * Constructor for PFQQueue class
	 *
	 */
	public PFQQueue(int size, int flowListSize) {
		packetQueue = new PriorityQueue<PacketTimestamped>(size);
		maxSize = size;
		virtualTime = 0;
		flowList = new FlowList(flowListSize);
	}
	
	public int getSize() {
		return packetQueue.size();
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isFull() {
		// TODO Auto-generated method stub
		return false;
	}

	public Packet peekFirst() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: Check if packetQueue is not full<br>
	 * TODO: Check if flowList is not full
	 */		
	public boolean putPacket(Packet p) {
				
		//Create encapsulation for packet p
		PacketTimestamped pTimestamped = new PacketTimestamped();
		pTimestamped.p = p;
				
		//at first check if there are any free places at packet queue
		if(packetQueue.size() >= maxSize)
			return false;
		
		//Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
			Flow packetFlow = flowList.getFlow(pTimestamped.p.getFlowIdentifier());
			pTimestamped.startTag = packetFlow.getFinishTag();
			pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
			packetFlow.setFinishTag(pTimestamped.finishTag);
			packetQueue.offer(pTimestamped);
		}
		//if this is the packet of new flow
		else {
			Flow packetFlow = new Flow( pTimestamped.p.getFlowIdentifier() );
			flowList.registerNewFlow(packetFlow);
			pTimestamped.startTag = virtualTime;
			pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
			packetFlow.setFinishTag(pTimestamped.finishTag);
			packetQueue.offer(pTimestamped);
		}
		return true;
	}

	/**
	 * TODO: Check if queue is not empty
	 */
	public Packet removeFirst() {
		//Remove first packet from queue
		PacketTimestamped packet = packetQueue.remove();
		
		//Set virtualTime to currently serviced packet
		virtualTime = packetQueue.peek().startTag;
		
		//Remove all queues which finishTag is smaller than virtualTime
		flowList.cleanFlows(virtualTime);
		
		return packet.p;
	}

}
