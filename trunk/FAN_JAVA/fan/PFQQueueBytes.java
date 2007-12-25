package fan;

/**
 * Class for PFQ Queue, but this one performs all calculations and
 * measurements in bytes
 */
public class PFQQueueBytes extends PFQQueue {
	
	/**
	 * Current size in bytes of this queue
	 */
	private int sizeInBytes;
	
	/**
	 * Maximum size in bytes of this queue
	 */
	private int maxSizeInBytes;
	
	
	/**
	 * Constructor for this class
	 * @param 	maxSizeInBytes Maximum size of the queue in bytes
	 * @param 	flowListSize Maximum number of flows that this queue
	 * 			can process
	 * @param 	intface the interface this queue is assigned to
	 */
	public PFQQueueBytes(int maxSizeInBytes, int flowListSize, Interface intface){
		super(999999/*size in packets*/, flowListSize, intface);
		this.maxSizeInBytes = maxSizeInBytes;
		this.sizeInBytes = 0;
		//System.out.println("PFGQueueBytes  FLsize" + flowListSize); 
	}
	
	
	/**
	 * Method to get size of this queue in bytes
	 * @return Size of this queue in bytes
	 */
	public int getSizeBytes() {
		return sizeInBytes;
	}
	
	
	/**
	 * Method to get maximum size of this queue in bytes
	 * @return Maximum size of this queue in bytes
	 */
	public int getMaxSize() {
		return maxSizeInBytes;
	}
	
	/**
	 * Method to get size of this queue in bytes
	 * It has to be overloaded in order to perform all the
	 * calculations properly
	 * @return Maximum size of this queue in bytes
	 */
	public int getSize() {
		return getSizeBytes();
	}
	
	
	/**
	 * Checks if the queue is full (it performs all measurements
	 * in bytes)
	 * @return 	True if queue is full and cannot accept new packets,
	 * 			false if it has enough space for new packets
	 */
	public boolean isFull() {
		if( (sizeInBytes + MTU) > maxSizeInBytes) {
			return true;
		} else
			return false;
	}
	
	
	/**
	 * Method to put packet inside this queue
	 * @param p Packet that should go into this queue
	 * @return 	True if there was free place in this queue for this 
	 * 			packet, false if the packet was not put in this queue
	 */
	public boolean putPacket(Packet p){
		
//		measurement for idleTime
		if ( packetQueue.size() == 0 ) {
			//totalIdleTime = totalIdleTime.add( Monitor.clock.substract( idleTime ) );
		}
		
//		measurement operations every hundred of ms or more
		performMeasurements("putPacket()");
			
		
		//Create encapsulation for packet p
		PacketTimestamped pTimestamped = new PacketTimestamped();
		pTimestamped.p = p;
		pTimestamped.clock = Monitor.clock.toDouble();
		
		
//		at first check if there are any free places at packet queue		
		if(isFull()){
			Debug.print(Debug.SPEC,"The FairRate is: " + getFairRate() );
			p = null;
			Debug.print(Debug.SPEC,"PFQQueueBytes.putPacket(): the queue is full");
			return false;
		}
		
//		Check if this packet belongs to the flow registered in flowList
		if( flowList.contains( pTimestamped.p.getFlowIdentifier() ) ) {
//			System.out.println("I have found the flow: " + pTimestamped.p.getFlowIdentifier().toInt() );
			Flow packetFlow = flowList.getFlow(pTimestamped.p.getFlowIdentifier());

			packetFlow.backlog += pTimestamped.p.getLength();

			if ( packetFlow.bytes >= MTU ){
			
				if( packetFlow.getFinishTag() < virtualTime )
					pTimestamped.startTag = virtualTime;
				else
					pTimestamped.startTag = packetFlow.getFinishTag();
				
				pTimestamped.finishTag = pTimestamped.startTag + pTimestamped.p.getLength();
				packetQueue.offer(pTimestamped); // push { packet, flow_time_stamp } to PIFO
			} 
			else {
			
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
			} else {
				Debug.print(Debug.INFO,"PFQQueueBytes.putPacket(): congestion in the list");
				Debug.print(Debug.INFO, "PFQQueueBytes.putPacket(): flowlist length: " + flowList.getLength() + ", flowlist max size: " + flowList.getMaxLength() );
			}
			
		}
		
		sizeInBytes += p.getLength();
		
		return true;
			
	}
	
	
	/**
	 * Remove and return packet at head of this queue
	 * @return Packet that was removed from the front of the queue
	 */
	public Packet removeFirst(){
		Packet p = super.removeFirst();
		sizeInBytes -= p.getLength();
		return p;	
	}
	
	
	/**
	 * Method to remove first packet in the queue that belongs to specified
	 * flow.
	 * @param flowId Selected flow, which first packet should be removed
	 * @return Removed packet 
	 */
	public PacketTimestamped removeFirstFlowPacket(FlowIdentifier flowId) {
		PacketTimestamped tempPacket = super.removeFirstFlowPacket( flowId );
		if( tempPacket != null ) {
			sizeInBytes -= tempPacket.p.getLength();
			return tempPacket;
		} else {
			Debug.print(Debug.INFO,"PFQQueueBytes.removeFirstFlowPacket(): last packet in Queue!!");
			return null;
		}
	}
}
