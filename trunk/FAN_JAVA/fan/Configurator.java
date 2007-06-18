package fan;

import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;  
import javax.xml.parsers.FactoryConfigurationError;  
import javax.xml.parsers.ParserConfigurationException;
 
import org.xml.sax.SAXException;  
import org.xml.sax.SAXParseException;  

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;

//import java.io.File;
import java.io.*;
import org.w3c.dom.*;

import java.util.Iterator;
import java.util.Vector;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

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
	
	public boolean saveConfiguration(Vector<Server> serverVector, Vector<Generate> generatorVector){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          document = builder.newDocument();  // Create from whole cloth
          Element root = (Element) document.createElement("root"); 
	      document.appendChild(root);
	      
		for (int i = 0; i < serverVector.size(); i++) {
			Element s = (Element) document.createElement("server");
			s.setAttribute("n", serverVector.get(i).getName());
			for (int k = 0; k < serverVector.get(i).getInterfacesNumber(); k++){
				Element ifc = (Element) document.createElement("interface");
				ifc.setAttribute("peer", serverVector.get(i).getInterfaces().get(k).getServer().getName());
				ifc.setAttribute("bandwidth", String.valueOf(( serverVector.get(i).getInterfaces().get(k).getBandwidth() ) ));
				ifc.setAttribute("probability", String.valueOf(( serverVector.get(i).getInterfaces().get(k).getProbability() ) ));
				s.appendChild(ifc);
			}
			
			root.appendChild( s );
		}	      
	      
//	      Element e = null;
//	      Node n = null;
	      // Child i.
//	      e = xmldoc.createElementNS(null, "USER");
//	      e.setAttributeNS(null, "ID", id[i]);
//	      e.setAttributeNS(null, "TYPE", type[i]);
//	      n = xmldoc.createTextNode(desc[i]);
//	      e.appendChild(n);
//	      root.appendChild(e);
//	      FileOutputStream fos = new FileOutputStream("test.xml");
			
	      OutputFormat format = new OutputFormat(document);
	      format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);


			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = 
							new XMLSerializer( new FileOutputStream(new File("test.xml")), format);

			serializer.serialize(document);

        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (Exception e){
        	System.out.println("Exception caught!");
        }
		return true;
	}
}
