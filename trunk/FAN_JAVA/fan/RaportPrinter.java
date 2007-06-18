package fan;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Vector;

public class RaportPrinter {
	
	public static void printSimulationResults(Server s, PrintStream out) {
		DecimalFormat format = new DecimalFormat("#.##");
		out.println("\nThe server has processed TOTAL: " + (s.results.getRejectedPackets() + s.results.getServicedPackets()) + " packets");
		out.println("\nThe number of serviced packets is: " + s.results.getServicedPackets());
		out.println("\nThe number of locally serviced packets is: " + s.results.getLocallyServicedPackets());
		out.println("\nThe number of rejected packets is: " + s.results.getRejectedPackets());
		out.println("\nThe percentage of rejected packets is: " + format.format(s.results.getAvgRejectedPackets()) );
		out.println("\nThe average packet service time is: " + format.format(s.results.getAvgPacketServiceTime()) + " s" );
		out.println("\nThe average queue length is: " + format.format(s.results.getAvgQueueLength()) + " packets");
		out.println("\nThe maximum queue length is: " + s.results.getMaxQueueLength() + " packets");
	}
	
	public static void printResultsForServer(Server s, PrintStream out) {
		printInHashes("This is the configuration of server: " + s.getName(), out);
		printConfiguration(s, out);
		printInHashes("These are the results of simulation for server: " + s.getName(), out);
		printSimulationResults(s, out);
		out.println("\n\n");
	}
	
	public static void printConfiguration(Server s, PrintStream out) {
		out.println("\nThe server has " + s.getInterfacesNumber() + " interface(s):\n");
		Vector<RoutingTable.Route> routing = s.getRoutingTable().getRouting();
		for(int i = 0; i < s.getRoutingTable().getRouting().size(); i++) {
			out.println("Interface " + (i+1) + " pointing to: " +
							routing.elementAt(i).getServerInterface().getServer().getName() +
							", with the probability of: " + routing.elementAt(i).getProbability()
							); 
			out.println("The bandwidth of this interface is: " + 
							routing.elementAt(i).getServerInterface().getBandwidth() + " B/s\n"
							);		
		}
		
	}
	
	public static void printInHashes(String text, PrintStream out) {
		out.println("\n");
		for(int i=0; i< text.length()+4; i++) 
			out.print("#");
		out.println("\n# " + text + " #");
		for(int i=0; i< text.length()+4; i++) 
			out.print("#");
		out.println();
	}
}
