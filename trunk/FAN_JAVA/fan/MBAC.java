package fan;

/**
 * MBAC is an admission control block. Its tasks include measurement and optionally admission control.
 * In order perform its tasks MBAC has got to trace:
 *  * priority_load
 *  * fair_rate
 *  and to have access to
 *  * AFL ( fan.FlowList )
 * and all that information (namely admission conditions) derives from Queue (i.e. PFQQueue) thus MBAC
 * has to have access to Interface, which provides reference to mentioned Queue.
 */
public class MBAC {
	
	FlowList flowList;
	Queue queue;
	long minFairRate;
	long maxPriorityLoad;
	long bandwidth;
	
	MBAC(Interface intface, long minFairRate, long maxPriorityLoad){
		queue = intface.getQueue();
		if (queue != null){			
			flowList = ((PFQQueue)queue).getFlowList();
		} else {
			System.out.println("Initialization problem in MBAC.MBAC()");
		}
		this.minFairRate = minFairRate;
		this.maxPriorityLoad = maxPriorityLoad;
		this.bandwidth = intface.getBandwidth();
	}

	/**
	 * Measured priority load in bits.
	 * @return fair rate
	 */
	public long getPriorityLoad(){
		return ((PFQQueue)queue).getPriorityBytes();
	}
	/**
	 * Measured fair rate in bits.
	 * @return fair rate
	 */
	public long getFairRate(){
		return ((PFQQueue)queue).getFairRate();
	}
	
	public boolean congestionOccured(){
		return false;
	}

}
