package fan;

import java.text.DecimalFormat;
import java.util.Vector;

public class RaportPrinter {
	
	public static void printSimulationResults(Server s) {
		DecimalFormat format = new DecimalFormat("#.##");
		System.out.println("\nThe server has processed TOTAL: " + (s.results.getRejectedPackets() + s.results.getServicedPackets()) + " packets");
		System.out.println("\nThe number of serviced packets is: " + s.results.getServicedPackets());
		System.out.println("\nThe number of locally serviced packets is: " + s.results.getLocallyServicedPackets());
		System.out.println("\nThe number of rejected packets is: " + s.results.getRejectedPackets());
		System.out.println("\nThe percentage of rejected packets is: " + format.format(s.results.getAvgRejectedPackets()) );
		System.out.println("\nThe average packet service time is: " + format.format(s.results.getAvgPacketServiceTime()) + " s" );
		System.out.println("\nThe average queue length is: " + format.format(s.results.getAvgQueueLength()) + " packets");
		System.out.println("\nThe maximum queue length is: " + s.results.getMaxQueueLength() + " packets");
	}
	
	public static void printResultsForServer(Server s) {
		printInHashes("This is the configuration of server: " + s.getName());
		printConfiguration(s);
		printInHashes("These are the results of simulation for server: " + s.getName());
		printSimulationResults(s);
		System.out.println("\n\n");
	}
	
	public static void printConfiguration(Server s) {
		System.out.println("\nThe server has " + s.getInterfacesNumber() + " interface(s):\n");
		Vector<RoutingTable.Route> routing = s.getRoutingTable().getRouting();
		for(int i = 0; i < s.getRoutingTable().getRouting().size(); i++) {
			System.out.println("Interface " + (i+1) + " pointing to: " +
							routing.elementAt(i).getServerInterface().getPeer().getName() +
							", with the probability of: " + routing.elementAt(i).getProbability()
							); 
			System.out.println("The bandwidth of this interface is: " + 
							routing.elementAt(i).getServerInterface().getBandwidth() + " B/s\n"
							);		
		}
		
	}
	
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
