package fan;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.print.attribute.standard.Finishings;

/**
 * This is the implementation of PFQ Queue
 * 
 */
public class PFQQueue implements Queue {
	
	/**
	 * Field to hold the type of the queue - in this case "PFQ"
	 */
	public String type;
	
	/**
	 * This is the measure time used for Fair Rate calculation
	 */
	Time lastMeasureTimeFR;
	
	/**
	 * This is the measure time used for Priority Load calculation
	 */
	Time lastMeasureTimePL;
	
	/**
	 * Field to hold value saying if the measurements were already performed
	 */
	boolean measured = false;
	
	/**
	 * This is the encapsulation class for Packet,
	 * to hold information about timestamp assigned to it
	 * (according to PFQ algorithm)
	 */
	public class PacketTimestamped implements Comparable<PacketTimestamped>{
		/**
		 * Packet encapsulated by this PacketTimestamped
		 */
		public Packet p;
		
		/**
		 * Start tag assigned to this packet
		 */
		public long startTag;
		
		/**
		 * Finish tag assigned to this packet
		 */
		public long finishTag;
		
		/**
		 * Time when this packet came to the queue
		 */
		public double clock;
		
		
		/**
		 * Method to compare PacketTimestamped objects
		 * @param p The packet that should be compared with this packet
		 */
		public int compareTo(PacketTimestamped p){
			if( packetQueue.peek() == p )
				return 1;
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
	protected final int MTU = 1500;
	
	/**
	 * Field that holds the bandwidth of the interface that this queue
	 * is assigned to
	 */
	protected long bandwidth = 0;
	
	/**
	 * Variables needed to measure priority load
	 */
	protected Time t2 = Monitor.clock;	//new Time(0.0);
	protected long pbt2 = 0;
	
	/**
	 * Variables needed to measure fair rate
	 */
	protected long vt2 = 0;
	protected Time idleTime;
	protected Time totalIdleTime;
	
	/**
	 * Variable to hold calculated priority load
	 */
	protected double priorityLoad;
	
	
	/**
	 * Variable to hold calculated fair rate
	 */
	protected long fairRate;
	
	
	/**
	 * Constructor for PFQ class
	 * @param 	size The maximum size (in packets) of the queue
	 * @param 	flowListSize The maximum number of flows that this queue 
	 * 			may serve
	 * @param 	intface Interface that this queue is assigned to
	 */
	public PFQQueue(int size, int flowListSize, Interface intface) {
		packetQueue = new PriorityQueue<PacketTimestamped>(size);
		maxSize = size;
		virtualTime = 0;
		flowList = new FlowList(flowListSize);
		bandwidth = intface.getBandwidth();
		type = "PFQ";
		lastMeasureTimeFR = new Time(0);
		lastMeasureTimePL = new Time(0);
		priorityBytes = 0;
		idleTime = new Time(0);
		totalIdleTime = new Time(0);
		priorityLoad = 0;
		fairRate = bandwidth;
	}
	
	
	/**
	 * Method to get current size of the queue (number of packets
	 * waiting to be served)
	 * @return Number of packets waiting in the queue
	 */
	public int getSize() {
		return packetQueue.size();
	}
	
	
	/**
	 * Method to get maximum size of the queue (in packets)
	 * @return Maximum size of the queue in packets
	 */
	public int getMaxSize() {
		return maxSize;
	}

	
	/**
	 * Method to check if the queue is empty
	 * @return 	True if the queue is empty, false if there are any
	 *			packets inside
	 */
	public boolean isEmpty() {
		return packetQueue.isEmpty();
	}

	
	/**
	 * Checks if the queue is full
	 * Counted in packets, if counted in bytes different action should be taken.
	 * @return True if the number of packets equals queue maximum size, false otherwise
	 */
	public boolean isFull() {
		return packetQueue.size() == maxSize;
	}

	
	/**
	 * Method to get first packet waiting in the queue - but not
	 * remove it from the queue
	 * @return First packet waiting in the queue
	 */
	public Packet peekFirst() {
		return packetQueue.peek().p;
	}

	
	/**
	 * Method to put packet in this queue. If the queue is full, the packet is not put inside
	 * the queue and false value is returned. Otherwise the packet is put in the queue and true
	 * value is returned
	 * @param p The packet that should be put inside this queue
	 * @return 	true if packet was put in this queue, false if the queue was full and packet
	 * 			was rejected
	 */		
	public boolean putPacket(Packet p) {
	
//		measurement for idleTime
		if ( packetQueue.size() == 0 ) {
			totalIdleTime = totalIdleTime.add( Monitor.clock.substract( idleTime ) );
		}
			
		
		//Create encapsulation for packet p
		PacketTimestamped pTimestamped = new PacketTimestamped();
		pTimestamped.p = p;
		pTimestamped.clock = Monitor.clock.toDouble();
			
//		at first check if there are any free places at packet queue		
		if(isFull()){
			p = null;
			return false;
		}
		
//		Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
			Flow packetFlow = flowList.getFlow(pTimestamped.p.getFlowIdentifier());

			packetFlow.backlog += pTimestamped.p.getLength();

			if ( packetFlow.bytes >= MTU ){
				
				 if( packetFlow.getFinishTag() < virtualTime ) 
					 pTimestamped.startTag = virtualTime;
				 else
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
			
			packetFlow.setFinishTag( pTimestamped.finishTag );
		}
		//if this is the packet of new flow
		else {

			pTimestamped.startTag = virtualTime;
			pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
			packetQueue.offer(pTimestamped);

			priorityBytes += pTimestamped.p.getLength();

			if ( (flowList.getMaxLength() - flowList.getLength()) > 0 ){
				Flow packetFlow = new Flow( pTimestamped.p.getFlowIdentifier() );
				packetFlow.setFinishTag(pTimestamped.finishTag);		
				packetFlow.backlog = pTimestamped.p.getLength();
				packetFlow.bytes = pTimestamped.p.getLength();
				flowList.registerNewFlow(packetFlow);
			}
		}
		
		//measurement operations every hundred of ms or more
		performMeasurements("putPacket()");
		
		return true;
	}

	
	/**
	 * Method to perfrom the measurements required to calculate Fair Rate and Priority Load
	 * @param source The name of the fragment that executed this method
	 */
	protected void performMeasurements(String source) {
		
		//measurement for idleTime
		if ( isEmpty() ) {
			totalIdleTime = totalIdleTime.add( Monitor.clock.substract( idleTime ) );
			idleTime = new Time( Monitor.clock.toDouble() );
		}
		
		//Measurements for Fair Rate
		if ( Monitor.clock.substract(lastMeasureTimeFR).toDouble() >= 0.5 ){
//			System.out.println(	"Measurements of Fair Rate at " + 
//								Monitor.clock.toString() +
//								", source: " + source );
//			
			// I misunderstood something and have added such if statement - hope that won't
			// make the algorithm worse 
//			if (virtualTime != vt2){
			if ( totalIdleTime.toDouble() * bandwidth >= (virtualTime - vt2) ) {
				//System.out.println( "totalIdleTime is bigger: " + totalIdleTime.toDouble() + ",vt = " + virtualTime + ",vt2 = " + vt2);
				fairRate = (long)( totalIdleTime.toDouble() * (double)bandwidth / ( Monitor.clock.substract( lastMeasureTimeFR ).toDouble() ) );
			}
			else{
//				System.out.println( "totalIdleTime is smaller: " + totalIdleTime.toDouble() + ",vt = " + virtualTime + ",vt2 = " + vt2 );
				fairRate = (long)( (double)( virtualTime - vt2 ) / ( Monitor.clock.substract( lastMeasureTimeFR ).toDouble() ) );
			}
			
//			System.out.println("Fair Rate is: " + fairRate + "\n");
						
			lastMeasureTimeFR = new Time( Monitor.clock );
			vt2 = virtualTime;
			totalIdleTime = new Time( 0 );
		}
		
		//Measurements for Priority Load
		if ( Monitor.clock.substract(lastMeasureTimePL).toDouble() >= 0.005 ){
			//calculate priority load
			priorityLoad = ( (double)(priorityBytes - pbt2) / 
							(double) Math.round( (double)bandwidth * ( Monitor.clock.substract( lastMeasureTimePL ).toDouble() ) ) );
			if( priorityLoad > 1 )
				priorityLoad = 1;
			
			//save values that will be required next time for PL calculation
			lastMeasureTimePL = Monitor.clock; 
			pbt2 = priorityBytes;
		}
	}

	
	/**
	 * Method to get and remove the first packet waiting in this queue
	 */
	public Packet removeFirst() {

		if (packetQueue.isEmpty()){
			// clear flow list (or will it timeout all its flows?)
			return null;
		}			
		
		//Remove first packet from queue
		PacketTimestamped packet = packetQueue.remove();
		if ( flowList.contains(packet.p.getFlowIdentifier()) ){
			Flow flow = (Flow)flowList.getFlow(packet.p.getFlowIdentifier());
			flow.backlog -= packet.p.getLength();			
		} else {
			System.out.println( "Something has gone wrong in PFQQueue.removeFirst()" );
			System.out.println(	"The flow missing from the list is: " + 
								packet.p.getFlowIdentifier().toInt() );
		}
				
		if( packetQueue.peek() != null ) {
			if ( packetQueue.peek().startTag != virtualTime ){
				
				virtualTime = packetQueue.peek().startTag;
//				Remove all queues which finishTag is smaller than virtualTime with timeout
				flowList.cleanFlows( virtualTime - 10000 );
			}
		}
		
		//if PIFO is now empty remove all flows from flowslist
		if( packetQueue.isEmpty() ) {
		//	flowList.cleanAllFlows();
			idleTime = new Time( Monitor.clock );
		}
		
		return packet.p;
	}
	
	
	/**
	 * Method to get the list of flows used in this queue
	 * @return The list of flows in this queue
	 */
	public FlowList getFlowList(){
		return flowList;
	}
	
	
	/**
	 * Method to get fair rate in this queue
	 * @return Fair Rate in this queue
	 */
	public long getFairRate(){
		performMeasurements("getFairRate()");
		return fairRate;
	}
	
	
	/**
	 * Method to get priority load in this queue
	 * @return Priority Load of this queue
	 */
	public double getPriorityLoad(){
		performMeasurements("getPriorityLoad");
		return priorityLoad;
	}

	
	/**
	 * Method to return type of the queue
	 */
	public String getType(){
		return type;
	}
	
	
	/**
	 * Method to print all elements of this queue - however they will be unsorted
	 */
	public void printElements() {
		System.out.println("\nThe Queue [" + getSize() +"] is:");
		System.out.println("The queue has " + packetQueue.size() + " elements.");
		PacketTimestamped[] packetTable = new PacketTimestamped[packetQueue.size()];
		packetQueue.toArray(packetTable);
		for (int i = 0; i < packetTable.length; i++) {
			System.out.print(	"Clock: " + packetTable[i].clock + 
								", sTag: " +packetTable[i].startTag + 
								", fTag: " + packetTable[i].finishTag +
								", size: " + packetTable[i].p.getLength() +
								", FlowID: " + packetTable[i].p.getFlowIdentifier().toInt());
			if( packetTable[i].p.getServiceStartTime() != null ) {
				System.out.println(", serviceStartTime: " + packetTable[i].p.getServiceStartTime() );
			} else
				System.out.println("");
		}
		System.out.println("");
	}
	
	
	/**
	 * Method to return the first packet of the specified flow
	 * @param flowId The flow which packet should be removed
	 * @return First packet of specified flow or null if there is no such packet
	 */
	public PacketTimestamped getFirstFlowPacket(FlowIdentifier flowId) {
		PacketTimestamped tempPacket = null;
		Packet frontPacket = peekFirst();
		for (Iterator iter = packetQueue.iterator(); iter.hasNext();) {
			PacketTimestamped element = (PacketTimestamped) iter.next();
			if ( element.p.getFlowIdentifier().equals( flowId ) && element.p != frontPacket )
				if( tempPacket == null ) {
					tempPacket = element;
				} else if( element.compareTo( tempPacket ) < 0 ) {
					tempPacket = element;
				}
		}
		return tempPacket;
	}
	
	
	/**
	 * Method for removing packet at the head of the specified flow in the queue
	 * @param flowId The flow which first packet should be removed
	 * @return The removed packet or null if there is no such packet
	 */
	public PacketTimestamped removeFirstFlowPacket(FlowIdentifier flowId) {
		PacketTimestamped tempPacket = null;
		if( isEmpty() )
			return null;
		Packet frontPacket = peekFirst();
		for (Iterator iter = packetQueue.iterator(); iter.hasNext();) {
			PacketTimestamped element = (PacketTimestamped) iter.next();
			if ( element.p.getFlowIdentifier().equals( flowId ) && element.p != frontPacket )
				if( tempPacket == null ) {
					tempPacket = element;
				} else if( element.compareTo( tempPacket ) < 0 ) {
					tempPacket = element;
				}
		} 
		if( tempPacket == null )
			return null;
		else {
			Flow flow = (Flow)flowList.getFlow( flowId );
			flow.backlog -= tempPacket.p.getLength();
			if( packetQueue.remove( tempPacket ) )
				System.out.println("REMOVED THE PACKET OF FLOW " + flowId.toInt() );
			return tempPacket;
		}
	}
	
	
	/**
	 * Method to check if there is any packet in the queue with the specified flowID
	 * @param flowID The flowID to be checked
	 * @return Packet of the specified flow, or null if there is no such a packet
	 */
	public PacketTimestamped checkFlowPackets( FlowIdentifier flowID ) {
		for (Iterator iter = packetQueue.iterator(); iter.hasNext();) {
			PacketTimestamped p = (PacketTimestamped) iter.next();
			if( p.p.getFlowIdentifier().equals( flowID ) ) {
				return p;
			}
		}
		return null;
	}
	
}
