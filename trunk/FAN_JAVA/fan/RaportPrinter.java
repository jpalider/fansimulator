package fan;

import java.text.DecimalFormat;
import java.util.Vector;
/**
 * This class is responsible for saving measured data and save it correctly
 */
public class RaportPrinter {
	/**
	 * Prints summary results at the end of simluation 
	 * @param s Server for which results are to be printed
	 */
	public static void printSimulationResults(Server s) {
		DecimalFormat format = new DecimalFormat("#.##");
//		Debug.print("\nThe server has processed TOTAL: " + (s.results.getRejectedPackets() + s.results.getServicedPackets()) + " packets");
//		Debug.print("\nThe number of serviced packets is: " + s.results.getServicedPackets());
//		Debug.print("\nThe number of locally serviced packets is: " + s.results.getLocallyServicedPackets());
//		Debug.print("\nThe number of rejected packets is: " + s.results.getRejectedPackets());
//		Debug.print("\nThe percentage of rejected packets is: " + format.format(s.results.getAvgRejectedPackets()) );
//		Debug.print("\nThe average packet service time is: " + format.format(s.results.getAvgPacketServiceTime()) + " s" );
//		Debug.print("\nThe average queue length is: " + format.format(s.results.getAvgQueueLength()) + " packets");
//		Debug.print("\nThe maximum queue length is: " + s.results.getMaxQueueLength() + " packets");
	
	// Calculations	
		int serverPacketsTotal = 0;
		int serverPacketsServiced = 0;
		int serverPacketsServicedLocally = 0;
		int serverPacketsRejected = 0;
		double serverPacketsRejectedPercentage = 0;
		
		for (int i = 0; i < s.getInterfaces().size(); i++) {
			ResultsCollector results = s.getInterfaces().elementAt(i).results;
			serverPacketsTotal				+= results.getRejectedPackets() + results.getServicedPackets();
			serverPacketsServiced			+= results.getServicedPackets();
			serverPacketsServicedLocally	+= results.getLocallyServicedPackets();
			serverPacketsRejected			+= results.getRejectedPackets();			
		}
		if(serverPacketsTotal > 0)
			serverPacketsRejectedPercentage = ((double)serverPacketsRejected / (double)serverPacketsTotal) * 100.d;
		else if(serverPacketsRejected > 0) {
			serverPacketsRejectedPercentage = 100;
		}
	 // Data that might be interesting from the server's point of view 
		System.out.println("\nThe server has processed TOTAL: " + (serverPacketsTotal) + " packets");
		System.out.println("The number of packets serviced: ");
		System.out.println("\tforwarded: " + (serverPacketsServiced - serverPacketsServicedLocally));
		System.out.println("\trejected: " + serverPacketsRejected + "\t (" + format.format(serverPacketsRejectedPercentage) + "%)");		
		System.out.println("\tlocally: " + serverPacketsServicedLocally);
		//System.out.println("\nThe percentage of rejected packets is: " + format.format(serverPacketsRejectedPercentage) );
	// Data that might be interesting from the point of view of each interface	
		System.out.println("\nThe average packet service time for interface pointing to: ");
		for (int i = 0; i < s.getInterfaces().size(); i++) {
			System.out.println("\t" + s.getInterfaces().elementAt(i).getServer().getName() + ": " + format.format(s.getInterfaces().elementAt(i).results.getAvgPacketServiceTime()) + " s" );
		}		

		System.out.println("\nThe average queue length for interface pointing to: ");
		for (int i = 0; i < s.getInterfaces().size(); i++) {
			System.out.println("\t" + s.getInterfaces().elementAt(i).getServer().getName() + ": " + format.format(s.getInterfaces().elementAt(i).results.getAvgQueueLength()) + " packets");
		}
		
		System.out.println("\nThe maximum queue length for interface pointing to: ");
		for (int i = 0; i < s.getInterfaces().size(); i++) {		
			System.out.println("\t" + s.getInterfaces().elementAt(i).getServer().getName() + ": " + s.getInterfaces().elementAt(i).results.getMaxQueueLength() + " packets");
		}
		
		System.out.println("\nTheNumber of rejected packet for interface pointing to: ");
		for (int i = 0; i < s.getInterfaces().size(); i++) {		
			System.out.println("\t" + s.getInterfaces().elementAt(i).getServer().getName() + ": " + format.format(s.getInterfaces().elementAt(i).results.getRejectedPackets()) + " packets");
		}

		System.out.println("\nChannel utilization of interface pointing to: ");
	
		for (int i = 0; i < s.getInterfaces().size(); i++) {
			ResultsCollector r = s.getInterfaces().elementAt(i).results;
			double upTime = s.getInterfaces().elementAt(i).getUpTime().toDouble();
			double utilization = (r.getAvgPacketLength()*r.getCheckedPacketsLength())/s.getInterfaces().elementAt(i).getBandwidth()/upTime;
			System.out.println("\t" + s.getInterfaces().elementAt(i).getServer().getName());
			System.out.println("\t\tUpTime = " + format.format(upTime) );		
//			System.out.println("\t\tAPL= " + r.getAvgPacketLength() + " " + r.getCheckedPacketsLength());		
//			System.out.println("\t\tSP = " + r.getServicedPackets() );		
//			System.out.println("\t\tLSP = " + r.getLocallyServicedPackets() );		
			System.out.println("\t\tUtilization: " + format.format(utilization*100) + " %");
		}
	}
	/**
	 * Prints server configuration and simulation results
	 * @param s Server for which results are to be printed
	 */
	public static void printResultsForServer(Server s) {
		printInHashes("This is the configuration of server: " + s.getName());
		printConfiguration(s);
		printInHashes("These are the results of simulation for server: " + s.getName());
		printSimulationResults(s);
		System.out.println("\n\n");
	}
	/**
	 * Prints server configuration
	 * @param s Server for which configuration is to be printed
	 */
	public static void printConfiguration(Server s) {
		System.out.println("\nThe server has " + s.getInterfacesNumber() + " interface(s):\n");
		Vector<RoutingTable.Route> routing = s.getRoutingTable().getRouting();
		for(int i = 0; i < s.getRoutingTable().getRouting().size(); i++) {
			System.out.println("Interface " + (i+1) + " pointing to: " +
							routing.elementAt(i).getServerInterface().getServer().getName() +
							", with the probability of: " + routing.elementAt(i).getProbability()
							); 
			System.out.println("The bandwidth of this interface is: " + 
							routing.elementAt(i).getServerInterface().getBandwidth() + " B/s\n"
							);		
		}
		
	}
	/**
	 * Presents data in special format
	 * @param text Text that needs special formatting
	 */
	public static void printInHashes(String text) {
		System.out.println("\n");
		for(int i=0; i< text.length()+4; i++) 
			System.out.print("#");
		System.out.println("\n# " + text + " #");
		for(int i=0; i< text.length()+4; i++) 
			System.out.print("#");
		System.out.println();
	}
}
