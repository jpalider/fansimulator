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
import org.w3c.dom.Document;
import org.w3c.dom.*;
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
	    NodeList listOfServers = document.getElementsByTagName("server");
        int totalServers = listOfServers.getLength();
//      System.out.println("Total no of servers : " + totalServers);
//	    document.getChildNodes();
        // for each server defined in config.xml create it in simulator
        for (int i = 0; i < totalServers; i++) {
        	serverVector.add(new Server(listOfServers.item(i).getAttributes()));
        	// for each interface in config.xml add that interface to server;
		}
		return true;
	}
}
