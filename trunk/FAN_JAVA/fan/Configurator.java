package fan;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

import java.io.File;
import org.w3c.dom.*;

import java.util.Iterator;
import java.util.Vector;

public class Configurator {
	Document document;  
	
	public Configurator(String configFile){
	
	    DocumentBuilderFactory factory =
	        DocumentBuilderFactory.newInstance();
	    //factory.setValidating(true);   
	    //factory.setNamespaceAware(true);
	    try {
	       DocumentBuilder builder = factory.newDocumentBuilder();
	       document = builder.parse( new File(configFile) );
	
	    } catch (SAXException sxe) {
	       // Error generated during parsing)
	       Exception  x = sxe;
	       if (sxe.getException() != null)
	           x = sxe.getException();
	       x.printStackTrace();
	
	    } catch (ParserConfigurationException pce) {
	        // Parser with specified options can't be built
	        pce.printStackTrace();
	
	    } catch (IOException ioe) {
	       // I/O error
	       ioe.printStackTrace();
	    }
	} 
	
	public boolean configure( Vector<Server> serverVector ){

		NodeList xmlListOfServers = document.getElementsByTagName("server");
        int totalServers = xmlListOfServers.getLength();

        // for each server defined in config.xml create it in simulator
        for (int i = 0; i < totalServers; i++) {
        	NamedNodeMap serverAttributes = xmlListOfServers.item(i).getAttributes();
        	System.out.println( "ServerNames : " + serverAttributes.getNamedItem("n").getTextContent() );
        	serverVector.add( new Server(serverAttributes.getNamedItem("n").getTextContent()) );
		}
        
        // for each server defined in config.xml setup its interface
        for (int i = 0; i < totalServers; i++) {
        	// for each interface in config.xml add that interface to server;
        	NodeList interfaces = xmlListOfServers.item(i).getChildNodes();
        	for (int j = 0; j < interfaces.getLength(); j++){
        		NamedNodeMap interfaceAttributes = interfaces.item(j).getAttributes();        		
        		String peerName = interfaceAttributes.getNamedItem("peer").getTextContent();
        		Server destServ = findServerByName(serverVector, peerName);
        		if (destServ == null)
        			System.out.println( "Dest server : no such server ");	
        		double probability = Double.parseDouble( interfaceAttributes.getNamedItem("probability").getTextContent() );
        		int bandwidth = Integer.parseInt( interfaceAttributes.getNamedItem("bandwidth").getTextContent() );
        		serverVector.get(i).addInterface(destServ, probability, bandwidth);
        	}
        }
        
        return true;
	}
	
	public Server findServerByName(Vector<Server> serverVector, String peerName){
		for (int i = 0; i < serverVector.size(); i++) {
			if ( serverVector.get(i).getName().compareTo(peerName) == 0 ){
				System.out.println( "findServerByName : no such server ");	
				return serverVector.get(i);
			}
		}
		return null;
	}
}
