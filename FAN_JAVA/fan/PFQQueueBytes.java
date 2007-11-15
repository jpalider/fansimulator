/**
 * 
 */
package fan;

import fan.PFQQueue.PacketTimestamped;

/**
 * @author Mumin
 *
 */
public class PFQQueueBytes extends PFQQueue {
	private int sizeInBytes;
	private int maxSizeInBytes;
	
	public PFQQueueBytes(int maxSizeInBytes, int flowListSize, Interface intface){
		super(999999/*size in packets*/, flowListSize, intface);
		this.maxSizeInBytes = maxSizeInBytes;
		this.sizeInBytes = 0;
		//System.out.println("PFGQueueBytes  FLsize" + flowListSize); 
	}
	
	public int getSizeBytes() {
		return sizeInBytes;
	}
	
	public int getMaxSize() {
		return maxSizeInBytes;
	}
	
	//Must be overloaded to display the graphs properly
	public int getSize() {
		return sizeInBytes;
	}
	
	/**
	 * Counted in bytes.
	 */
	public boolean isFull() {
		if( (sizeInBytes + MTU) > maxSizeInBytes) {
			return true;
		} else
			return false;
	}
	
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
				System.out.println("CONGESTION IN THE LIST");
			}
			
		}
		
		sizeInBytes += p.getLength();
		
		//measurement operations every hundred of ms or more
		performMeasurements("putPacket()");
		
		return true;
			
	}
	
	
	public Packet removeFirst(){
		Packet p = super.removeFirst();
		sizeInBytes -= p.getLength();
		return p;	
	}
	
	
	public PacketTimestamped removeFirstFlowPacket(FlowIdentifier flowId) {
		PacketTimestamped tempPacket = super.removeFirstFlowPacket( flowId );
		if( tempPacket != null ) {
			sizeInBytes -= tempPacket.p.getLength();
			return tempPacket;
		} else {
			System.out.println("Last packet in Queue!!");
			return null;
		}
	}
}
