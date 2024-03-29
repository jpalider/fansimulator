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
	private Document document;
	
	/**
	 * String to hold the filename where the configuration is saved
	 */
	private String filename;
	private String description = new String("");
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	
	
	public Configurator(String configFile){
		this.filename = configFile;  
	} 
	
	public boolean configure( Vector<Server> serverVector, Vector<Generate> generatorVector){
		DocumentBuilderFactory factory =
	        DocumentBuilderFactory.newInstance();
	    //factory.setValidating(true);   
	    //factory.setNamespaceAware(true);
	    try {
	       DocumentBuilder builder = factory.newDocumentBuilder();
	       document = builder.parse( new File(filename) );
	
	    } catch (SAXException sxe) {
	       // Error generated during parsing)
	       Exception  x = sxe;
	       if (sxe.getException() != null)
	           x = sxe.getException();
	       x.printStackTrace();
	
	    } catch (ParserConfigurationException pce) {
	        // Parser with specified options can't be built
	        pce.printStackTrace();
	        
	    } catch (FileNotFoundException fnfe) {
	    	return false;
	    } catch (IOException ioe) {
	       // I/O error
	       ioe.printStackTrace();
	    }

	    NodeList xmlDescription = document.getElementsByTagName("description");
	    if (xmlDescription.item(0) != null){	    		    		    	
	    	this.description = xmlDescription.item(0).getTextContent();	    	
	    }
	    
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
            		
            		// 4. queue size
            		int queueSize = Integer.parseInt( 
            							interfaceElement.getAttributes().getNamedItem("queueSize").getTextContent() );
            		
            		// 5. flow list max size
            		int maxFlowListSize = Integer.parseInt( 
            				interfaceElement.getAttributes().getNamedItem("maxFlowListSize").getTextContent() );
                    
            		// 6. MBAC's minimum fair rate
            		long minFR = Integer.parseInt( 
            				interfaceElement.getAttributes().getNamedItem("minFR").getTextContent() );
            		
            		// 7. MBAC's maximum priority load
            		long maxPL = Integer.parseInt(
            				interfaceElement.getAttributes().getNamedItem("maxPL").getTextContent() );
            		
            		// finally create such interface            		
            		serverVector.get(i).addInterface(destServ, probability, bandwidth, queueSize, maxFlowListSize, minFR, maxPL );     
        		}
        		// get its <generators> 
        		NodeList generatorList = serverElement.getElementsByTagName("generator");
        		for (int j = 0; j < generatorList.getLength(); j++ ){
        			Element generatorElement = (Element)generatorList.item(j);
        			String generatorType = new String( generatorElement.getAttributes().getNamedItem("type").getTextContent() );
        			
        			//Load packetSize
        			Element packetSize = (Element) (
        									generatorElement.getElementsByTagName("packetSize").item(0)	);
        			int packetS = Integer.parseInt (packetSize.getTextContent());
        			
        			//Load start time
        			Element startTime = (Element)( 
        							generatorElement.getElementsByTagName("startTime").item(0) );
        			Time start = new Time( 
        								Double.valueOf (startTime.getTextContent()) 
        								);
        			
        			//Load looped
        			Element looped = (Element)(
        							generatorElement.getElementsByTagName("looped").item(0) );
        			boolean loop = Boolean.valueOf (looped.getTextContent());
        			
        			//Load flowLowerRange
        			Element flowLower = (Element)(
        								generatorElement.getElementsByTagName("flowLowerRange").item(0) );
        			int flowLowerRange = Integer.parseInt( flowLower.getTextContent() );
        			
//        			//Load flowHigherRange
        			Element flowHigher = (Element)(
        								generatorElement.getElementsByTagName("flowHigherRange").item(0) );
        			int flowHigherRange = Integer.parseInt( flowHigher.getTextContent() );
        			
        			if (!loop) {
        				Element finishTime = (Element)( 
    							generatorElement.getElementsByTagName("finishTime").item(0) );
            			Time finish = new Time( 
            								Double.valueOf (finishTime.getTextContent()) 
            								);
            			
            			if (generatorType.compareTo("basic") == 0){			
	        				generatorVector.add(new Generate(	start, 
	        													serverVector.get(i), 
	        													finish, 
	        													packetS,
	        													flowLowerRange,
	        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("normal") == 0){			
				        	NodeList meanList = generatorElement.getElementsByTagName("mean");	   	
	        				NodeList varianceList = generatorElement.getElementsByTagName("variance");
	        				Element meanElement = (Element)meanList.item(0);
	        				Element varianceElement = (Element)varianceList.item(0);
	        				double mean = Double.parseDouble( meanElement.getTextContent() );
	        				double variance = Double.parseDouble( varianceElement.getTextContent() );
	        				generatorVector.add(new NormalGenerate(	start, 
	        														serverVector.get(i), 
	        														new Time(mean), 
	        														new Time(variance), 
	        														finish, 
	        														packetS,
		        													flowLowerRange,
		        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("constant") == 0){					
	        				NodeList intervalList = generatorElement.getElementsByTagName("interval");  
	        				Element intervalElement = (Element)intervalList.item(0);  
	        				double interval = Double.parseDouble( intervalElement.getTextContent() );
	        				generatorVector.add(new ConstantGenerate(	start, 
	        															serverVector.get(i), 
	        															new Time(interval), 
	        															finish, 
	        															packetS,
	    	        													flowLowerRange,
	    	        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("uniform") == 0){
	        				NodeList rangeList = generatorElement.getElementsByTagName("range");  
	        				Element rangeElement = (Element)rangeList.item(0);  
	        				double range = Double.parseDouble( rangeElement.getTextContent() );
	        				generatorVector.add(new UniformGenerate(	start, 
	        															serverVector.get(i), 
	        															new Time(range), 
	        															finish, 
	        															packetS,
	    	        													flowLowerRange,
	    	        													flowHigherRange));
	        				
	        			} else{
	            			System.out.println( "fan.Configurator: wrong generator type!");
	        			}
        			}
        			
        			else {
	        			
	        			if (generatorType.compareTo("basic") == 0){			
	        				generatorVector.add(new Generate(	start, 
	        													serverVector.get(i), 
	        													packetS,
	        													flowLowerRange,
	        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("normal") == 0){			
				        	NodeList meanList = generatorElement.getElementsByTagName("mean");	   	
	        				NodeList varianceList = generatorElement.getElementsByTagName("variance");
	        				Element meanElement = (Element)meanList.item(0);
	        				Element varianceElement = (Element)varianceList.item(0);
	        				double mean = Double.parseDouble( meanElement.getTextContent() );
	        				double variance = Double.parseDouble( varianceElement.getTextContent() );
	        				generatorVector.add(new NormalGenerate(	start, 
	        														serverVector.get(i), 
	        														new Time(mean), 
	        														new Time(variance), 
	        														packetS,
		        													flowLowerRange,
		        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("constant") == 0){					
	        				NodeList intervalList = generatorElement.getElementsByTagName("interval");  
	        				Element intervalElement = (Element)intervalList.item(0);  
	        				double interval = Double.parseDouble( intervalElement.getTextContent() );
	        				generatorVector.add(new ConstantGenerate(	start, 
	        															serverVector.get(i), 
	        															new Time(interval), 
	        															packetS,
	    	        													flowLowerRange,
	    	        													flowHigherRange));
	        				
	        			}else if (generatorType.compareTo("uniform") == 0){
	        				NodeList rangeList = generatorElement.getElementsByTagName("range");  
	        				Element rangeElement = (Element)rangeList.item(0);  
	        				double range = Double.parseDouble( rangeElement.getTextContent() );
	        				generatorVector.add(new UniformGenerate(	start, 
	        															serverVector.get(i), 
	        															new Time(range), 
	        															packetS,
	    	        													flowLowerRange,
	    	        													flowHigherRange));
	        				
	        			} else{
	            			System.out.println( "fan.Configurator: wrong generator type!");
	        			}
        			}
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
	
//	public boolean saveConfiguration(Vector<Server> serverVector, Vector<Generate> generatorVector){
//		return saveConfiguration("test.xml", serverVector, generatorVector);		
//	}
	
	public boolean saveConfiguration(Vector<Server> serverVector, Vector<Generate> generatorVector){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
          DocumentBuilder builder = factory.newDocumentBuilder();
          document = builder.newDocument();  // Create from whole cloth
          Element root = (Element) document.createElement("root"); 
	      document.appendChild(root);
	      
	    Element descriprionElement = (Element) document.createElement( "description" );
	    descriprionElement.setTextContent( description );	    
	    root.appendChild(descriprionElement);
	      
		for (int i = 0; i < serverVector.size(); i++) {
			Element s = (Element) document.createElement("server");
			s.setAttribute("n", serverVector.get(i).getName());
			// append interfaces tags
			for (int k = 0; k < serverVector.get(i).getInterfacesNumber(); k++){
				Element ifc = (Element) document.createElement("interface");
				
				//save peer of this interface
				ifc.setAttribute ("peer", serverVector.get(i).getInterfaces().get(k).getServer().getName());
				
				//save bandwidth of this interface
				ifc.setAttribute ("bandwidth", String.valueOf(( serverVector.get(i).getInterfaces().get(k).getBandwidth() ) ));
				
				//save queueSize of this interface
				ifc.setAttribute (	"queueSize", 
									String.valueOf( serverVector.get(i).getInterfaces().get(k).getQueue().getMaxSize() ) );
				
				//save maxFlowList size of this interface
				Queue queue = serverVector.get(i).getInterfaces().get(k).getQueue();
				if ( queue.getType().equals("PFQ")){
					ifc.setAttribute (	"maxFlowListSize", String.valueOf( ((PFQQueueBytes)queue).getFlowList().getMaxLength()) );
				} else {
					ifc.setAttribute (	"maxFlowListSize", "999999" );
				}
				
				//save MBAC's minimum Fair Rate
				ifc.setAttribute ( "minFR", String.valueOf( 
									serverVector.get(i).getInterfaces().get(k).getMBAC().getMinFairRate() ) 
									); 
				
				//save MBAC's maximum Priority Load
				ifc.setAttribute ( "maxPL", String.valueOf( 
						serverVector.get(i).getInterfaces().get(k).getMBAC().getMaxPriorityLoad() ) 
						); 

				// for all routes on particular server...
				for (int m = 0; m < serverVector.get(i).getRoutingTable().getRouting().size() ; m++){
					if ( serverVector.get(i).getInterfaces().get(k) == serverVector.get(i).getRoutingTable().getRouting().get(m).getServerInterface() ){
						ifc.setAttribute("probability", String.valueOf( serverVector.get(i).getRoutingTable().getRouting().get(m).getProbability() ));
					}
				}				
				s.appendChild(ifc);
			}
			// append generator tags 
			for (int k = 0; k < generatorVector.size(); k++){
				Generate.GenerateType generatorType = generatorVector.get(k).type;
				if (generatorVector.get(k).getServer() == serverVector.get(i)){
					Element g = (Element) document.createElement("generator");
					

					//add information about packetSize of generator
					Element packetSize = (Element) document.createElement("packetSize");
					packetSize.setTextContent( 	String.valueOf(
												generatorVector.get(k).getPacketSize() 
												) );
					g.appendChild(packetSize);
					
					//add information about start time of generator
					Element start = (Element) document.createElement("startTime");
					start.setTextContent( 	String.valueOf(
											generatorVector.get(k).getTime().toDouble()
											) );
					g.appendChild(start);
					
					//add information about flowLowerRange of generator
					Element flowLowerRange = (Element) document.createElement( "flowLowerRange" );
					flowLowerRange.setTextContent( 	String.valueOf(
														generatorVector.get( k ).flowLowerRange ) );
					g.appendChild( flowLowerRange );
					
					//add information about flowHigherRange of generator
					Element flowHigherRange = (Element) document.createElement( "flowHigherRange" );
					flowHigherRange.setTextContent( 	String.valueOf(
														generatorVector.get( k ).flowHigherRange ) );
					g.appendChild( flowHigherRange );
					
					
					//add information about the fact if generator is looped, or has some specified
					//finish time before end of simulation
					Element looped = (Element) document.createElement("looped");
					looped.setTextContent(	String.valueOf(
											generatorVector.get(k).isLooped()
											) );
					g.appendChild(looped);
					
					//add information about finish time of generator
					if( !generatorVector.get(k).isLooped() ) {
						Element finish = (Element) document.createElement("finishTime");
						finish.setTextContent(	String.valueOf(
												generatorVector.get(k).getFinishTime().toDouble()
												) );
						g.appendChild(finish);
					}
					
					if (generatorType == Generate.GenerateType.basic){
						g.setAttribute("type", "basic");
        			}else if (generatorType == Generate.GenerateType.normal){
        				g.setAttribute("type", "normal");
        				Element m = (Element) document.createElement("mean");
        				Element v = (Element) document.createElement("variance");
        				NormalGenerate ng = (NormalGenerate) generatorVector.get(k);
        				m.setTextContent(ng.getMean().toString());
        				v.setTextContent(ng.getVariance().toString());
        				g.appendChild(m);
        				g.appendChild(v);
        			}else if (generatorType == Generate.GenerateType.constant){
        				g.setAttribute("type", "constant");
        				Element iv = (Element) document.createElement("interval");
        				ConstantGenerate cg = (ConstantGenerate) generatorVector.get(k);
        				iv.setTextContent(cg.getInterval().toString());
        				g.appendChild(iv);
        			}else if (generatorType == Generate.GenerateType.uniform){
        				g.setAttribute("type", "uniform");
        				Element u = (Element) document.createElement("range");
        				UniformGenerate ug = (UniformGenerate) generatorVector.get(k);
        				u.setTextContent(ug.getRange().toString()); 				
        				g.appendChild(u);
        			} else{
            			System.out.println( "fan.Configurator: wrong generator type!");
        			}
					
					s.appendChild(g);
				}
			}
			
			root.appendChild( s );
		}	      
	      
	
	      OutputFormat format = new OutputFormat(document);
	      format.setIndenting(true);

			//to generate output to console use this serializer
			//XMLSerializer serializer = new XMLSerializer(System.out, format);


			//to generate a file output use fileoutputstream instead of system.out
			XMLSerializer serializer = 
							new XMLSerializer( new FileOutputStream(new File(filename)), format);

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
