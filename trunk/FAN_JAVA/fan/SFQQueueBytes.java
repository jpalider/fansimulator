/**
 * 
 */
package fan;

import fan.PFQQueue.PacketTimestamped;

/**
 * This is the class that represents SFQ queue used for testing purposes in simulator.
 */
public class SFQQueueBytes extends PFQQueue {
	/**
	 * Informs of currently held bytes
	 */	
	private int sizeInBytes;
	/**
	 * Defines the maximum number of bytes this queue can hold
	 */
	private int maxSizeInBytes;
	
	/**
	 * Constructor
	 * @param maxSizeInBytes Defines the maximum number of bytes this queue can hold
	 * @param flowListSize Defines the maximum number of flows a queue can track
	 * @param intface Interface the queue is assigned to 
	 * @see Interface
	 * @see Queue
	 */
	SFQQueueBytes(int maxSizeInBytes, int flowListSize, Interface intface){
		super(999999/*size in packets*/, flowListSize, intface);
		this.maxSizeInBytes = maxSizeInBytes;
		this.sizeInBytes = 0;
		//System.out.println("PFGQueueBytes  FLsize" + flowListSize); 
	}
	/**
	 * Getter for current queue size in bytes
	 * @return Queue size in bytes
	 */
	public int getSizeBytes() {
		return sizeInBytes;
	}
	/**
	 * Getter for maximum queue size in bytes
	 * @return Maximum queue size in bytes
	 */
	public int getMaxSize() {
		return maxSizeInBytes;
	}
	
	/** 
	 * Must be overloaded to display the graphs properly
	 * @see getSizeBytes
	 */ 
	public int getSize() {
		return sizeInBytes;
	}
	
	/**
	 * Checks wheter queue is full or not - counted in bytes.
	 * @return True if there is enough room for a packet with size of MTU  
	 */
	public boolean isFull() {
		if( (sizeInBytes + MTU) > maxSizeInBytes) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Method to put packet inside this queue
	 * @param p The Packet that will be inserted in this queue
	 * @return 	True if the packet was inserted properly, false if the queue was full and there
	 * 			was no space for this packet.
	 */
	public boolean putPacket(Packet p){
		
//		measurement for idleTime
		if ( packetQueue.size() == 0 ) {
			totalIdleTime = totalIdleTime.add( Monitor.clock.substract( idleTime ) );
		}
			
		
		//Create encapsulation for packet p
		PacketTimestamped pTimestamped = new PacketTimestamped();
		pTimestamped.p = p;
		pTimestamped.clock = Monitor.clock.toDouble();
			
		// TODO: "reject packet at head of longest backlog
		//at first check if there are any free places at packet queue		
//		if(packetQueue.size() >= maxSize){
		if(isFull()){
			p = null;
			return false;
		}
		
		//Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
//			System.out.println("I have found the flow: " + pTimestamped.p.getFlowIdentifier().toInt() );
			Flow packetFlow = flowList.getFlow(pTimestamped.p.getFlowIdentifier());

			packetFlow.backlog += pTimestamped.p.getLength();

//			if ( packetFlow.bytes >= MTU ){
//				
				pTimestamped.startTag = packetFlow.getFinishTag();
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
				packetQueue.offer(pTimestamped); // push { packet, flow_time_stamp } to PIFO
//			} else {
				
//				pTimestamped.startTag = virtualTime;
//				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
//				packetQueue.offer(pTimestamped);
				priorityBytes += pTimestamped.p.getLength();
//				packetFlow.bytes += pTimestamped.p.getLength();	
//			}
			
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
//				packetFlow.bytes = pTimestamped.p.getLength();
				flowList.registerNewFlow(packetFlow);
//				System.out.println("The new registered flow is: " + packetFlow.getFlowID().toInt() );
			} else {
				System.out.println("CONGESTION IN THE LIST");
			}
			
		}
		
		sizeInBytes += p.getLength();
		
		//measurement operations every hundred of ms or more
		performMeasurements("putPacket()");
		
		return true;
			
	}
	/**
	 * Remove and return the first packet of this queue
	 * @return First, removed packet of this queue
	 */
	public Packet removeFirst(){
		Packet p = super.removeFirst();
		sizeInBytes -= p.getLength();
		return p;	
	}	
}
