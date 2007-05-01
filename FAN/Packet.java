
public class Packet {
	/**
	 * int with the selected flow type 
	 */
	public short flowType;
	
	public Packet(short type) {
		flowType = type;
	}
	
	/**
	 * Method for moving packet to the end of the queue of selected nextServer
	 * @param nextServer Server which is the target of the Packet
	 */
	public void moveToServer(Server nextServer) {
		
		//if server is busy - it means that it services any packet
		//then add packet to server's proper queue - with the priority equal to packet flowType
		//and additionally check if currently serviced traffic type is set to the highest needed priority
		if(nextServer.busy) {
			nextServer.queues[flowType].offer(this);
			if(nextServer.currentTrafficType > flowType) {
				nextServer.currentTrafficType = flowType;
			}
		}
		//else, if the server is not busy, make it service the current packet
		//and assign it time of sending this packet either to other server
		else {
			nextServer.busy = true;
			nextServer.servicedPacket = this;
			double time = Monitor.clock + Monitor.generator.nextDouble() * nextServer.recieveRate;
			Monitor.schedule( new Generator(time,nextServer) );
		}
		
	}
}
