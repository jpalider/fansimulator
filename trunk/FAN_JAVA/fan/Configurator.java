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

        // for each server defined in config.xml create it in application
        // that must be done at the very beginning, mere, before interface configuration
        for (int i = 0; i < totalServers; i++) {
        	NamedNodeMap serverAttributes = xmlListOfServers.item(i).getAttributes();
        	System.out.println( "ServerNames : " + serverAttributes.getNamedItem("n").getTextContent() );
        	serverVector.add( new Server(serverAttributes.getNamedItem("n").getTextContent()) );
		}

        // for each server defined in config.xml setup its interfaces
        for (int i = 0; i < totalServers; i++) {
        	// for each <server> tag
        	Node serverNode = xmlListOfServers.item(i);
        	if(serverNode.getNodeType() == Node.ELEMENT_NODE){
        		Element serverElement = (Element)serverNode;
        		// get its <interfaces> 
        		NodeList interfaceList = serverElement.getElementsByTagName("interface");
        		for (int j = 0; j < interfaceList.getLength(); j++ ){
        			// and for each interface retrieve its parameters
        			Element interfaceElement = (Element)interfaceList.item(j);
            		// 1. find server that the interface ic connected to
        			String peerName = new String( interfaceElement.getAttributes().getNamedItem("peer").getTextContent() );
            		Server destServ = findServerByName(serverVector, peerName);
            		if (destServ == null){
            			System.out.println( "fan.Configurator: no such destination server for interface " + (j+1) + ".");
            			continue;
            		}
            		// 2. routing probability
            		double probability = Double.parseDouble( interfaceElement.getAttributes().getNamedItem("probability").getTextContent() );
            		// 3. interface bandwidth 
            		int bandwidth = Integer.parseInt( interfaceElement.getAttributes().getNamedItem("bandwidth").getTextContent() );
            		// finally create such interface            		
            		serverVector.get(i).addInterface(destServ, probability, bandwidth);     
        		}
        	}
        }
        
        return true;
	}
	
	public Server findServerByName(Vector<Server> serverVector, String peerName){
		for (int i = 0; i < serverVector.size(); i++) {
			if ( serverVector.get(i).getName().compareTo(peerName) == 0 ){
				return serverVector.get(i);
			}
		}
//		System.out.println( "fan.Configurator.findServerByName : search failed!");	
		return null;
	}
}
