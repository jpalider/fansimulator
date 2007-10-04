package gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class GenerateGraphsDialog extends Dialog {
	private final String SERV_PACKET_STRING = "Serviced Packets";
	private final String LOC_SERV_PACKET_STRING = "Locally Serviced Packets";
	private final String REJ_PACKET_STRING = "Rejected Packets";
	private final String QUE_LENGTH_STRING = "Queue Length";
	
	private final Color BACKGROUND_COLOR = Color.BLACK;
	
	private Vector<ChartFrame> openedFrames;

	public GenerateGraphsDialog(Shell shell, int style) {
		super(shell, style);
		openedFrames = new Vector<ChartFrame>();
	}
	
	public void open() {
		Shell parent = getParent();
        final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Display graphs for simulation results");
        shell.setSize(400, 250);
        
        //Combo box that will display the list of graphs' types
        final Combo graphTypeCombo = new Combo (shell, SWT.DROP_DOWN|SWT.READ_ONLY);
        graphTypeCombo.add(SERV_PACKET_STRING);
        graphTypeCombo.add(LOC_SERV_PACKET_STRING);
        graphTypeCombo.add(REJ_PACKET_STRING);
        graphTypeCombo.add(QUE_LENGTH_STRING);
        graphTypeCombo.setSize ( graphTypeCombo.computeSize( SWT.DEFAULT, SWT.DEFAULT) );
        graphTypeCombo.setLocation( 20, 20);
        
        //Label to combo box with graphs' types
        Label graphTypeLabel = new Label (shell, SWT.NONE);
        graphTypeLabel.setText ("Choose Graph Type");
        graphTypeLabel.setSize ( graphTypeLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT) );
        graphTypeLabel.setLocation ( 	graphTypeCombo.getLocation().x, 
        								graphTypeCombo.getLocation().y - graphTypeLabel.getSize().y - 5 );
        
        
        //Combo box that will display the servers for which the results were corrected
        final Combo serverCombo = new Combo ( shell, SWT.DROP_DOWN | SWT.READ_ONLY );
        serverCombo.setLocation (	graphTypeCombo.getLocation().x,
        							graphTypeCombo.getLocation().y + graphTypeCombo.getSize().y + 30 );
        serverCombo.setSize ( graphTypeCombo.getSize() );
        serverCombo.setVisible ( false );
        
        //Label to combo box with servers' list
        final Label serverLabel = new Label (shell, SWT.NONE);
        serverLabel.setText ( "Choose Results for Interface" );
        serverLabel.setSize ( serverLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        serverLabel.setLocation( 	serverCombo.getLocation().x, 
        							serverCombo.getLocation().y - serverLabel.getSize().y - 5 );
        serverLabel.setVisible ( false );
        
        
        
        //Display graphs button
        final Button displayGraphsBut = new Button ( shell, SWT.NONE );
        displayGraphsBut.setText( "Display Graphs" );
        displayGraphsBut.setSize( displayGraphsBut.computeSize( serverCombo.getSize().x, SWT.DEFAULT) );
        displayGraphsBut.setLocation( 	serverCombo.getLocation().x, 
        								serverCombo.getLocation().y + serverCombo.getSize().y + 5);
        displayGraphsBut.setVisible( false );
        
        
        //Close graphs button
        final Button closeGraphsBut = new Button( shell, SWT.None );
        closeGraphsBut.setText( "Close Visible Graphs" );
        closeGraphsBut.setSize( closeGraphsBut.computeSize( displayGraphsBut.getSize().x , 
        													SWT.DEFAULT ) );
        closeGraphsBut.setLocation( displayGraphsBut.getLocation().x, 
        							displayGraphsBut.getLocation().y + displayGraphsBut.getSize().y + 5 );
        closeGraphsBut.setVisible( false );
        
        
        
        //Display graphs button selection listener
        displayGraphsBut.addSelectionListener( new SelectionAdapter() {
        	public void widgetSelected( SelectionEvent se ) {
        		
        		closeGraphsBut.setVisible( true );
        		String servName = serverCombo.getText();
        		
        		if ( graphTypeCombo.getText().equals( SERV_PACKET_STRING ) )
        			generateSpGraphs( servName );
        		
        		else if ( graphTypeCombo.getText().equals( LOC_SERV_PACKET_STRING ) )
        			generateLpGraphs( servName );
        		
        		else if ( graphTypeCombo.getText().equals( REJ_PACKET_STRING ) ) 
        			generateRpGraphs( servName );
        		
        		else if ( graphTypeCombo.getText().equals( QUE_LENGTH_STRING ) )
        			generateQlGraphs( servName );		
        		
        	}
        });
        
        
        //Close graphs button selection listener
        closeGraphsBut.addSelectionListener( new SelectionAdapter() {
        	public void widgetSelected( SelectionEvent se ) {
        		while( openedFrames.size() > 0 ) {
        			ChartFrame openedFrame = openedFrames.elementAt(0);
        			if ( openedFrame != null ) 
						openedFrame.dispose();
        			openedFrames.remove( openedFrame );
        		}
           	}
        });
        
        
        //ServerCombo selection listener
        serverCombo.addSelectionListener( new SelectionAdapter() {
        	public void widgetSelected( SelectionEvent se ) {
        		
        		if( serverCombo.getText().length() > 0 ) {
        			displayGraphsBut.setVisible( true );
        		}
        	}
        });
        
        
        //Selection listener for graphTypeCombo
        graphTypeCombo.addSelectionListener( new SelectionAdapter () {
        	public void widgetSelected( SelectionEvent se ) {
        		
        		//Get access to local simulator directory
        		File localDir = new File("./");
    			File[] fileList;
    			serverCombo.removeAll();
    			
    			//This is the part responsible for displaying charts of Serviced Packets
        		if ( graphTypeCombo.getText().equals(SERV_PACKET_STRING) ) {
        		            			
        			fileList = localDir.listFiles( new ResultsFileFilter("SP") );
        			
        			if ( fileList.length > 0 ) {
        				for (int i = 0; i < fileList.length; i++) {
        					//Get name of the local server which this file belongs to
							String localname = fileList[i].getName().substring (0,
																				fileList[i].getName().indexOf("_") );
							
							//Get name of the remote server which was adressed by the outgoing packets
							String remotename = fileList[i].getName().substring (	fileList[i].getName().indexOf("_") + 1,
																					fileList[i].getName().indexOf("SP.txt")	); 
							serverCombo.add( localname + "->" + remotename );
        	 			}
        				serverCombo.setVisible( true );
        				serverLabel.setVisible( true );
        			}
        		}
        		else if ( graphTypeCombo.getText().equals(LOC_SERV_PACKET_STRING) ) {
        			
        			fileList = localDir.listFiles( new ResultsFileFilter("LP") );
        			
        			if ( fileList.length > 0 ) {
        				for (int i = 0; i < fileList.length; i++) {
        					//Get name of the server which this file belongs to
        					String localname = fileList[i].getName().substring (0,
									fileList[i].getName().indexOf("_") );

        					//Get name of the remote server which was adressed by the outgoing packets
        					String remotename = fileList[i].getName().substring (	fileList[i].getName().indexOf("_") + 1,
																					fileList[i].getName().indexOf("LP.txt")	); 
							serverCombo.add( localname + "->" + remotename );
        	 			}
        				serverCombo.setVisible( true );
        				serverLabel.setVisible( true );
        			}
        		}
        		else if ( graphTypeCombo.getText().equals(REJ_PACKET_STRING) ) {
        			
        			fileList = localDir.listFiles( new ResultsFileFilter("RP") );
        			
        			if ( fileList.length > 0 ) {
        				for (int i = 0; i < fileList.length; i++) {
        					//Get name of the server which this file belongs to
        					String localname = fileList[i].getName().substring (0,
									fileList[i].getName().indexOf("_") );

        					//Get name of the remote server which was adressed by the outgoing packets
        					String remotename = fileList[i].getName().substring (	fileList[i].getName().indexOf("_") + 1,
																					fileList[i].getName().indexOf("RP.txt")	); 
        					serverCombo.add( localname + "->" + remotename );
        	 			}
        				serverCombo.setVisible( true );
        				serverLabel.setVisible( true );
        			}
        		}
        		else if ( graphTypeCombo.getText().equals(QUE_LENGTH_STRING) ) {
        			
        			fileList = localDir.listFiles( new ResultsFileFilter("QL") );
        			
        			if ( fileList.length > 0 ) {
        				for (int i = 0; i < fileList.length; i++) {
        					//Get name of the server which this file belongs to
        					String localname = fileList[i].getName().substring (0,
									fileList[i].getName().indexOf("_") );

							//Get name of the remote server which was adressed by the outgoing packets
							String remotename = fileList[i].getName().substring (	fileList[i].getName().indexOf("_") + 1,
																					fileList[i].getName().indexOf("QL.txt")	); 
							serverCombo.add( localname + "->" + remotename );
        	 			}
        				serverCombo.setVisible( true );
        				serverLabel.setVisible( true );
        			}
        		}
        		
        		displayGraphsBut.setVisible( false );
        			
        	}
        } );
        
        
        shell.open();
        Display display = parent.getDisplay();
        while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) display.sleep();
        }
        return;
	}

	/**
	 * Class responsible for filtering files so only files with results are displayed
	 */
	class ResultsFileFilter implements FilenameFilter {
		private String type;
		
		/**
		 * Creates specific results file filter for specified results type
		 * @param type	Specifies the type of the results files to be filtered. The following options are
		 * 				allowed:
		 * 				<ul>
		 * 				<li>SP 	- for serviced packets
		 * 				<li>LP - for locally serviced packets
		 * 				<li>RP 	- for rejected packets
		 * 				<li>QL	- for queue length
		 * 				</ul> 
		 */
		public ResultsFileFilter(String type) {
			this.type = type;
		}
		
		public boolean accept(File dir, String name) {
			if ( name.contains(type + ".txt") ) {
				return true;
			} else
				return false;
		}
		
	}
	
	/**
	 * Method for generating graph with Serviced Packets and Avg Packet Service Time
	 * @param name The name of the server which this results belong to
	 */
	public void generateSpGraphs( String name ) {
		
		String filename = name.replaceAll("->", "_") + "SP.txt";
		
		/*
		 * Hashmap to hold the map of flows against the series of their serviced packet data
		 */
		HashMap<Integer, XYSeries> flowSPSeries = new HashMap<Integer, XYSeries>();
		
		/*
		 * Hashmap to hold the map of flows against the series of their average packet service time data
		 */
		HashMap<Integer, XYSeries> flowASTSeries = new HashMap<Integer, XYSeries>();
		
		try {
			BufferedReader fReader = new BufferedReader ( new FileReader( filename ) );
			String buffer;
			//Create series of data for chart
			XYSeries servicedPacketSeries = new XYSeries ("Serviced Packets");
			XYSeries avgServiceTimeSeries = new XYSeries ("Avg Packet Service Time");
			
			//Process the file until the end
		
			while( (buffer = fReader.readLine()) != null ) {
				String[] params = buffer.split(":");
				servicedPacketSeries.add(	Double.parseDouble (params[0]), 
											Double.parseDouble (params[1])
										);
				avgServiceTimeSeries.add( 	Double.parseDouble (params[0]),
											Double.parseDouble (params[2])
										);
				
				XYSeries newFlowSPSeries;
				XYSeries newFlowASTSeries;
				
				if ( flowSPSeries.get( new Integer(params[5]) ) == null ){
										
					//Create series for new flow of serviced packets
					newFlowSPSeries = new XYSeries( "Flow " + params[5] );
					flowSPSeries.put( new Integer( params[5] ), newFlowSPSeries );
					
					//Create series for new flow of average packet service time
					newFlowASTSeries = new XYSeries( "Flow " + params[5] );
					flowASTSeries.put( new Integer( params[5] ), newFlowASTSeries );
					
				}
				else {
					newFlowSPSeries = flowSPSeries.get( new Integer(params[5]) );
					newFlowASTSeries = flowASTSeries.get( new Integer(params[5]) );
				}
				
				newFlowSPSeries.add(	Double.parseDouble (params[0]), 
										Double.parseDouble (params[3]) );
				newFlowASTSeries.add(	Double.parseDouble (params[0]), 
										Double.parseDouble (params[4]) );

	
			}
		
			//Create chart
			XYSeriesCollection servicedPacketCol = new XYSeriesCollection ( servicedPacketSeries );
			XYSeriesCollection avgServTimeCol = new XYSeriesCollection ( avgServiceTimeSeries );
			
			for (Iterator iter = flowSPSeries.keySet().iterator(); iter.hasNext();) {
				Integer element = (Integer) iter.next();
				servicedPacketCol.addSeries ( flowSPSeries.get(element) );
			}
			
			for (Iterator iter = flowASTSeries.keySet().iterator(); iter.hasNext();) {
				Integer element = (Integer) iter.next();
				avgServTimeCol.addSeries ( flowASTSeries.get(element) );
			}			
			
			JFreeChart chartSP = ChartFactory.createXYLineChart(
						"Total Number of Serviced Packets on " + name,  // Title
						"Time",           								// X-Axis label
						"Number of Serviced Packets",           			// Y-Axis label
						servicedPacketCol,								// Dataset
						PlotOrientation.VERTICAL,
						true,				            	    		// Show legend
						false,
						false
	        		);
			
			JFreeChart chartAST = ChartFactory.createXYLineChart(
					"Average Packet Service Time on " + name,  // Title
					"Time",           								// X-Axis label
					"Average Packet Service Time",        			// Y-Axis label
					avgServTimeCol,								// Dataset
					PlotOrientation.VERTICAL,
					true,				            	    		// Show legend
					false,
					false
	    		);
			
			//set background color of charts
			chartSP.getXYPlot().setBackgroundPaint( BACKGROUND_COLOR );
			chartAST.getXYPlot().setBackgroundPaint( BACKGROUND_COLOR );
			
			//Display chart frames
			ChartFrame frame = new ChartFrame("SP " + name, chartSP);
			frame.pack();
			openedFrames.add( frame );
			frame.setLocation( 20, 20 );
			frame.setVisible(true);
			
			frame = new ChartFrame("AST " + name, chartAST);
			frame.pack();
			openedFrames.add( frame );
			frame.setLocation( 40, 40 );
			frame.setVisible(true);
			
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for generating graph with Locally Serviced Packets
	 * @param name The name of the server which this results belong to
	 */
	public void generateLpGraphs( String name) {
		
		String filename = name.replaceAll("->", "_") + "LP.txt";
		try {
			BufferedReader fReader = new BufferedReader ( new FileReader( filename ) );
			String buffer;
			//Create series of data for chart
			XYSeries locServicedPacketSeries = new XYSeries ("Locally Serviced Packets");
								
			//Process the file until the end
			while( (buffer = fReader.readLine()) != null ) {
				String[] params = buffer.split(":");
				locServicedPacketSeries.add(	Double.parseDouble (params[0]), 
												Double.parseDouble (params[1])
											);
			}
			
			//Create chart
			XYSeriesCollection locServicedPacketCol = new XYSeriesCollection ( locServicedPacketSeries );
			
			JFreeChart chart = ChartFactory.createXYLineChart(
						"Total Number of Locally Serviced Packets on " + name,  	// Title
						"Time",           											// X-Axis label
						"Number of Locally Serviced Packets",           						// Y-Axis label
						locServicedPacketCol,										// Dataset
						PlotOrientation.VERTICAL,
						true,				            	    					// Show legend
						false,
						false
	        		);
			
			//Display chart frame
			ChartFrame frame = new ChartFrame("LSP " + name, chart);
			frame.pack();
			openedFrames.add( frame );
			frame.setLocation( 20, 20 );
			frame.setVisible(true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for generating graph with Rejected Packets
	 * @param name The name of the server which this results belong to
	 */
	public void generateRpGraphs( String name) {
		
		String filename = name.replaceAll("->", "_") + "RP.txt";
		
		/*
		 * Hashmap to hold the map of flows against the series of their rejected packet data
		 */
		HashMap<Integer, XYSeries> flowRPSeries = new HashMap<Integer, XYSeries>();
		
		try {
			BufferedReader fReader = new BufferedReader ( new FileReader( filename ) );
			String buffer;
			//Create series of data for chart
			XYSeries rejectedPacketSeries = new XYSeries ("Rejected Packets");
								
			//Process the file until the end
			while( (buffer = fReader.readLine()) != null ) {
				String[] params = buffer.split(":");
				rejectedPacketSeries.add(	Double.parseDouble (params[0]), 
											Double.parseDouble (params[1])
											);
				XYSeries newFlowRPSeries;
								
				if ( flowRPSeries.get( new Integer(params[4]) ) == null ){
										
					//Create series for new flow of rejected packets
					newFlowRPSeries = new XYSeries( "Flow " + params[4] );
					flowRPSeries.put( new Integer( params[4] ), newFlowRPSeries );
				}
				else {
					newFlowRPSeries = flowRPSeries.get( new Integer(params[4]) );
				}
				
				newFlowRPSeries.add(	Double.parseDouble (params[0]), 
										Double.parseDouble (params[3]) );
			}
		
			
			//Create chart
			XYSeriesCollection rejectedPacketCol = new XYSeriesCollection ( rejectedPacketSeries );
			
			for (Iterator iter = flowRPSeries.keySet().iterator(); iter.hasNext();) {
				Integer element = (Integer) iter.next();
				rejectedPacketCol.addSeries ( flowRPSeries.get(element) );
			}
			
			JFreeChart chart = ChartFactory.createXYLineChart(
						"Total Number of Rejected Packets on " + name,  	// Title
						"Time",           									// X-Axis label
						"Number of Rejected Packets",           			// Y-Axis label
						rejectedPacketCol,									// Dataset
						PlotOrientation.VERTICAL,
						true,				            	    			// Show legend
						false,
						false
	        		);
			
			//set background color of charts
			chart.getXYPlot().setBackgroundPaint( BACKGROUND_COLOR );
			
			
			//Display chart frame
			ChartFrame frame = new ChartFrame("RP " + name, chart);
			frame.pack();
			openedFrames.add( frame );
			frame.setLocation( 20, 20);
			frame.setVisible(true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for generating graph with Rejected Packets
	 * @param name The name of the server which this results belong to
	 */
	public void generateQlGraphs( String name) {
		
		String filename = name.replaceAll("->", "_") + "QL.txt";
		try {
			BufferedReader fReader = new BufferedReader ( new FileReader( filename ) );
			String buffer;
			//Create series of data for chart
			XYSeries queueLenSeries = new XYSeries ("Queue Length");
								
			//Process the file until the end
			while( (buffer = fReader.readLine()) != null ) {
				String[] params = buffer.split(":");
				queueLenSeries.add(	Double.parseDouble (params[0]), 
											Double.parseDouble (params[1])
											);
			}
			
			//Create chart
			XYSeriesCollection queueLenCol = new XYSeriesCollection ( queueLenSeries );
			
			JFreeChart chart = ChartFactory.createXYLineChart(
						"Average Queue Length on " + name,  	// Title
						"Time",           						// X-Axis label
						"Average Queue Length",          		// Y-Axis label
						queueLenCol,							// Dataset
						PlotOrientation.VERTICAL,
						true,				            	    // Show legend
						false,
						false
	        		);
			
			//set background color of charts
			chart.getXYPlot().setBackgroundPaint( BACKGROUND_COLOR );
			
			
			//Display chart frame
			ChartFrame frame = new ChartFrame("QL " + name, chart);
			frame.pack();
			openedFrames.add( frame );
			frame.setLocation( 20, 20);
			frame.setVisible(true);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

