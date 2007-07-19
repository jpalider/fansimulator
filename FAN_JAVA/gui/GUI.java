package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fan.Configurator;
import fan.ConstantGenerate;
import fan.Generate;
import fan.Monitor;
import fan.NormalGenerate;
import fan.RaportPrinter;
import fan.Server;
import fan.Time;
import fan.UniformGenerate;
import fan.Generate.GenerateType;
import fan.RoutingTable.Route;

public class GUI {
	
	private Shell shell;
	private Display display;
	private Label numberOfServers;
	private TabFolder tabs;
	private Vector<Server> serversVector;
	/**
	 * @uml.property   name="generatorsVector"
	 * @uml.associationEnd   multiplicity="(0 -1)" elementType="fan.ConstaGenerate"
	 */
	private Vector<Generate> generatorsVector;
	
	/**
	 * Constructor for GUI class, creates main window, and runs method responsible for 
	 * adding this window's elements.
	 *
	 */
	public GUI(){
		serversVector = new Vector<Server>();
		generatorsVector = new Vector<Generate>();
		display = new Display();
		shell = new Shell(display,SWT.DIALOG_TRIM);
		shell.setLayout( new FillLayout());
	    shell.setSize(750, 600);
	    shell.setText("FAN simulator");
	    shell.setLayout(null);
	    this.addMenu(shell);
	    this.addServerTabFolder(shell);
	    this.addServerNumberControl(shell);
	    this.addRunSimulationButton(shell);
	    shell.layout();
	    shell.open();
	    while( !shell.isDisposed()) {
			if(!display.readAndDispatch()) 
				display.sleep();
		}
		display.dispose();
	}
	
	/**
	 * Adds server tab and creates new server object referenced by it
	 * @param tabs TabFolder where this tab should be added
	 */
	private void addServerTab(final TabFolder tabs) {
		int next = Integer.parseInt(numberOfServers.getText())+1;
		Server server = new Server( "Server nr " + String.valueOf(next) );
		serversVector.add(server);
		addServerTab(tabs, server);
	}
	
	
	/**
	 * Adds server tab with specifed reference to Server object
	 * @param tabs TabFolder where this tab should be created
	 * @param server Server which this tab should reference
	 */
	private void addServerTab(final TabFolder tabs, final Server server) {
				
		final TabItem serverTab = new TabItem(tabs,SWT.BORDER);
		int next = Integer.parseInt(numberOfServers.getText()) + 1;
		numberOfServers.setText(String.valueOf(next));
		serverTab.setText(server.getName());
				
		Composite serverTabComp = new Composite(tabs,SWT.BORDER);
		serverTabComp.setLayout(null);
		serverTabComp.setSize(300, 300);
		
		//Tree with interfaces
		final Tree serverTree = new Tree(serverTabComp,SWT.SINGLE|SWT.BORDER);
		serverTree.setBounds(10, 50, 200, 200);
		
		//Label with interfaces
		Label interfacesLbl = new Label(serverTabComp,SWT.NONE);
		interfacesLbl.setText("Server's interfaces:");
		interfacesLbl.setSize(interfacesLbl.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		interfacesLbl.setLocation(serverTree.getLocation().x, serverTree.getLocation().y - 20);
		
		//Button to add interface
		Button addInterfaceBut = new Button(serverTabComp,SWT.NONE);
		addInterfaceBut.setText("Add Interface");
		addInterfaceBut.setLocation(serverTree.getLocation().x + serverTree.getSize().x + 5, serverTree.getLocation().y);
		addInterfaceBut.setSize(addInterfaceBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addInterfaceBut.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent arg0) {
				AddInterfaceDialog interfaceDialog = new AddInterfaceDialog(shell, SWT.APPLICATION_MODAL, serversVector);
				interfaceDialog.setText(serverTab.getText());
				interfaceDialog.open(server);
				Event refreshEvent = new Event();
				refreshEvent.text = "refresh";
				serverTab.notifyListeners(100, refreshEvent);
				
			}
			
		});
		
		//Button to remove interface
		Button removeInterfaceBut = new Button(serverTabComp,SWT.NONE);
		removeInterfaceBut.setText("Remove Interface");
		removeInterfaceBut.setLocation(serverTree.getLocation().x + serverTree.getSize().x + 5, addInterfaceBut.getLocation().y + addInterfaceBut.getSize().y + 5);
		removeInterfaceBut.setSize(removeInterfaceBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addInterfaceBut.setSize(removeInterfaceBut.getSize().x, addInterfaceBut.getSize().y);
		removeInterfaceBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if( serverTree.getSelectionCount() == 1 ) {
					if( serverTree.getSelection()[0].getParentItem() == null) {
						server.removeInterface( serverTree.indexOf(serverTree.getSelection()[0]) );
						serverTree.getSelection()[0].dispose();
					}
					else {
						server.removeInterface( serverTree.indexOf(serverTree.getSelection()[0].getParentItem()) );
						serverTree.getSelection()[0].getParentItem().dispose();						
					}
						
				}
			}
		});
		
		//Tree with traffic generators
		final Tree generatorTree = new Tree(serverTabComp,SWT.SINGLE|SWT.BORDER);
		generatorTree.setSize(serverTree.getSize());
		generatorTree.setLocation(removeInterfaceBut.getLocation().x + removeInterfaceBut.getSize().x + 15, serverTree.getLocation().y);
		
		//Generators label
		Label generatorsLabel = new Label(serverTabComp, SWT.NONE);
		generatorsLabel.setText("Server's traffic generators:");
		generatorsLabel.setSize(generatorsLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		generatorsLabel.setLocation(generatorTree.getLocation().x, generatorTree.getLocation().y - 20);
		
		//Add Generator button
		Button addGeneratorBut = new Button(serverTabComp, SWT.NONE);
		addGeneratorBut.setText("Add Generator");
		addGeneratorBut.setLocation(generatorTree.getLocation().x + generatorTree.getSize().x + 5, generatorTree.getLocation().y);
		addGeneratorBut.setSize(addGeneratorBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addGeneratorBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
//				generatorsVector.add( new Generate(new Time(0),server) );
//				TreeItem generatorItem = new TreeItem(generatorTree,SWT.NONE);
//				generatorItem.setText("Generator nr " + generatorsVector.size());
				AddGeneratorDialog generatorDialog = new AddGeneratorDialog(shell, SWT.APPLICATION_MODAL, generatorsVector);
				generatorDialog.setText(serverTab.getText());
				generatorDialog.open(server);
				Event newEvent = new Event();
				newEvent.text = "refresh";
				tabs.notifyListeners(100, newEvent);
			}
		});
		
		//Remove Generator Button
		Button removeGeneratorBut = new Button(serverTabComp, SWT.NONE);
		removeGeneratorBut.setText("Remove Generator");
		removeGeneratorBut.setLocation(addGeneratorBut.getLocation().x, addGeneratorBut.getLocation().y + addGeneratorBut.getSize().y + 5);
		removeGeneratorBut.setSize(removeGeneratorBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addGeneratorBut.setSize(removeGeneratorBut.getSize().x, addGeneratorBut.getSize().y);
		removeGeneratorBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if( generatorTree.getSelectionCount() == 1 ) {
					TreeItem selectedItem;
					//Check if the top level element was selected in the tree. If not use parent tree item as selected
					if(generatorTree.getSelection()[0].getParentItem() != null) {
						selectedItem = generatorTree.getSelection()[0].getParentItem();
					}
					else {
						selectedItem = generatorTree.getSelection()[0];
					}
					
					//finding selected generator inside the generatorsVector
					int i = 0;
					int j = 0;
					do {
						if(generatorsVector.elementAt(i).getServer() == server) {
							if(j >= generatorTree.indexOf(selectedItem) )
								break;
							else
								j++;
						}
						i++;						
					} while( i <= generatorsVector.size());
					
					//removing selected generator and sending refresh so the servers 
					//and generators tree will be refreshed, and without removed 
					//elements
					generatorsVector.removeElementAt(i);
					Event newEvent = new Event();
					newEvent.text = "refresh";
					tabs.notifyListeners(100, newEvent);
				}
			}
		});
		
		//Listener for refresh events
		serverTab.addListener(100, new Listener(){

			public void handleEvent(Event e) {
				if(e.text != null)
					if(e.text.equals("refresh")) {
						//refresh server tree
						serverTree.removeAll();
						Vector<Route> routes = server.getRoutingTable().getRouting();
						for(int i =0; i < routes.size(); i++) {
							TreeItem interfaceItem = new TreeItem(serverTree,SWT.NONE);
							interfaceItem.setText("Interface to: " + routes.elementAt(i).getServerInterface().getServer().getName());
							
							TreeItem probabilityItem = new TreeItem(interfaceItem,SWT.NONE);
							probabilityItem.setText("Probability: " + routes.elementAt(i).getProbability());
							
							TreeItem bandwidthItem = new TreeItem(interfaceItem,SWT.NONE);
							bandwidthItem.setText("Bandwidth [B/s]: " + routes.elementAt(i).getServerInterface().getBandwidth());
							
							TreeItem queueSizeItem = new TreeItem(interfaceItem, SWT.NONE);
							queueSizeItem.setText(	"Queue size [B]: " + 
													routes.elementAt(i).getServerInterface().getQueue().getMaxSize());
							
						}
						//refresh generator Tree
						generatorTree.removeAll();
						for(int i = 0; i < generatorsVector.size(); i++) {
							if( generatorsVector.elementAt(i).getServer() == server) {
								TreeItem generatorItem = new TreeItem(generatorTree,SWT.NONE);
								generatorItem.setText("Generator nr " + (i+1));
								TreeItem generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( generatorsVector.elementAt(i).getType().name() );
								generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( "Start [s]: " + String.valueOf( generatorsVector.elementAt(i).getTime().toDouble() ) );
								generatorTypeItem = new TreeItem(generatorItem, SWT.NONE);
								generatorTypeItem.setText(	"Packet size [B]: " + 
														String.valueOf(
																generatorsVector.elementAt(i).getPacketSize()
																) );
								
								if( !generatorsVector.elementAt(i).isLooped() ) {
									generatorTypeItem = new TreeItem(generatorItem, SWT.NONE);
									generatorTypeItem.setText( "Finish [s]: " + String.valueOf( generatorsVector.elementAt(i).getFinishTime().toDouble() ) );
								}
								if( generatorsVector.elementAt(i).getType().equals(GenerateType.constant) ) {
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Interval [s]: " + String.valueOf(((ConstantGenerate)generatorsVector.elementAt(i)).getInterval().toDouble()) );
								}
								else if( generatorsVector.elementAt(i).getType().equals(GenerateType.normal) ) {
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Mean [s]: " + String.valueOf(((NormalGenerate)generatorsVector.elementAt(i)).getMean().toDouble()) );
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Variance [s]: " + String.valueOf(((NormalGenerate)generatorsVector.elementAt(i)).getVariance().toDouble()) );
								}
								else if( generatorsVector.elementAt(i).getType().equals(GenerateType.uniform) ) {
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Range [s]: " + String.valueOf(((UniformGenerate)generatorsVector.elementAt(i)).getRange().toDouble()) );
								}
									
							}
						}
					}
		
			}
			
		});
		
		//adding composite to server tab
		serverTab.setControl(serverTabComp);
	}
	
	/**
	 * Removes the last tab from TabFolder tabs, and removes server associated with this tab
	 * @param tabs TabFolder where the last tab should be removed
	 */
	private void removeServerTab(TabFolder tabs) {
		if(tabs.getItemCount() > 0) {
			tabs.getItems()[tabs.getItemCount()-1].dispose();
			int next = Integer.parseInt(numberOfServers.getText())-  1;
			numberOfServers.setText(String.valueOf(next));
			for(int i = 0; i < serversVector.size(); i++) {
				for(int j = 0; j < serversVector.elementAt(i).getInterfaces().size(); j++) {
					if(serversVector.elementAt(i).getInterfaces().elementAt(j).getServer() == serversVector.elementAt(next)) {
						serversVector.elementAt(i).removeInterface(j);
					}
				}
			}
			serversVector.removeElementAt(next);
		}
		Event newEvent = new Event();
		newEvent.text = "refresh";
		tabs.notifyListeners(100, newEvent);
		
	}
	
	
	/**
	 * Adds TabFolder to hold server tabs to main window
	 * @param shell The window, where this TabFolder should be added
	 */
	private void addServerTabFolder(Shell shell) {
		tabs = new TabFolder(shell, SWT.TOP|SWT.NO_REDRAW_RESIZE);
	    tabs.setSize(650, 300);
	    tabs.setLocation(30,80);
	    tabs.addListener(100, new Listener(){

			public void handleEvent(Event e) {
				for(int i =0; i < tabs.getItemCount(); i++ ){
					tabs.getItem(i).notifyListeners(100, e);
				}
			}
	    });
	}
	
	/** 
	 * Validates configuration of servers made by user inside GUI
	 * @param showMessage if true the message is displayed when configuration has any errors. If false
	 * 						then no message is displayed, even with wrong configuration.
	 * @return true if configuration is proper, false when configuration is incorrect
	 */	
	private boolean validateConfiguration(boolean showMessage) {
		//Check if the sum of probability of all routes in each servers equals 1
		for(int i = 0; i < serversVector.size(); i++ ) {
			if( serversVector.elementAt(i).getRoutingTable().getProbabilitySum() != 1 ) {
				if(showMessage) {
					MessageBox errorMsgBox = new MessageBox(shell,SWT.ICON_ERROR|SWT.OK);
					errorMsgBox.setText("Error in configuration");
					errorMsgBox.setMessage("It seems that server nr " + (i+1) + " has wrong routing. Please check the configuration.");
					errorMsgBox.open();
				}
				return false;
			}				
		}
		if(showMessage) {
			MessageBox correctMsgBox = new MessageBox(shell, SWT.ICON_INFORMATION|SWT.OK);
			correctMsgBox.setMessage("The configuration is set properly.");
			correctMsgBox.setText("Good configuration");
			correctMsgBox.open();
		}
		return true;
	}
	
	/**
	 * Method that runs simulation
	 * @param simulationTime The time that simulation should be runned
	 */
	private void runSimulation(double simulationTime) {
		if(validateConfiguration(false)) {
			//Clear results of previous simulations
			for (Iterator iter = serversVector.iterator(); iter.hasNext();) {
				Server element = (Server) iter.next();
				element.clearResults();
			}

			//Reset clock and schedule all generators attached to servers
			Monitor.clock = new Time(-1);
			for(int i = 0; i < generatorsVector.size(); i++) {
				Monitor.agenda.schedule(generatorsVector.elementAt(i));
			}
			
			//Run main simulation loop
			while( !Monitor.agenda.isEmpty() && Monitor.clock.compareTo(new Time(simulationTime)) <= 0 ) {
				fan.Event now = Monitor.agenda.removeFirst();
				Monitor.clock = now.time;
				now.run();
				//System.out.println("The time is now: " + Monitor.clock);
			}
			
			//Display the results of simulation
			DisplayResultsDialog sumUpDialog = new DisplayResultsDialog(shell, SWT.NONE, serversVector);
			sumUpDialog.open();
		}
	}
	
	
	/**
	 * Method responsible for generating graphs with results of simulation
	 *
	 */
	private void generateGraphs() {
		
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
		
		File localDir = new File("./");
		File[] fileList;
		int chartIndex = 0;
		
		//This is the part responsible for displaying charts of Serviced Packets
		fileList = localDir.listFiles( new ResultsFileFilter("SP") );
		
		if ( fileList.length > 0 ) {
			
			for (int i = 0; i < fileList.length; i++) {
				try {
					//Get name of the server which this file belongs to
					String name = fileList[i].getName().substring (	0,
																	fileList[i].getName().indexOf("SP.txt") 
																 	);
					BufferedReader fReader = new BufferedReader ( new FileReader(fileList[i]) );
					String buffer;
					//Create series of data for chart
					XYSeries servicedPacketSeries = new XYSeries ("Serviced Packets");
					XYSeries avgServiceTimeSeries = new XYSeries ("Avg Packet Service Time");
					
					//Process the file until the end
					while( (buffer = fReader.readLine()) != null ) {
						String[] params = buffer.split(":");
						System.out.println(params[0]);
						servicedPacketSeries.add(	Double.parseDouble (params[0]), 
													Double.parseDouble (params[1])
												);
						avgServiceTimeSeries.add( 	Double.parseDouble (params[0]),
													Double.parseDouble (params[2])
												);
					}
					
					//Create chart
					XYSeriesCollection servicedPacketCol = new XYSeriesCollection ( servicedPacketSeries );
					XYSeriesCollection avgServTimeCol = new XYSeriesCollection ( avgServiceTimeSeries );
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
					
					//Display chart frames
					ChartFrame frame = new ChartFrame("SP " + name, chartSP);
					frame.pack();
					frame.setLocation(20 + chartIndex * 20, 20 + chartIndex * 20);
					chartIndex++;
					frame.setVisible(true);
					
					frame = new ChartFrame("AST " + name, chartAST);
					frame.pack();
					frame.setLocation(20 + chartIndex * 20, 20 + chartIndex * 20);
					chartIndex++;
					frame.setVisible(true);
					
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
					
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
 			}
		}
		
				
		//This is the part responsible for displaying charts of Locally Serviced Packets
		fileList = localDir.listFiles( new ResultsFileFilter("LP") );
		
		if ( fileList.length > 0 ) {
			
			for (int i = 0; i < fileList.length; i++) {
				try {
					//Get name of the server which this file belongs to
					String name = fileList[i].getName().substring (	0,
																	fileList[i].getName().indexOf("LP.txt") 
																 	);
					BufferedReader fReader = new BufferedReader ( new FileReader(fileList[i]) );
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
					frame.setLocation(20 + chartIndex * 20, 20 + chartIndex * 20);
					chartIndex++;
					frame.setVisible(true);
					
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
					
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
 			}
		}
		
		//This is the part responsible for displaying charts of Rejected Packets
		fileList = localDir.listFiles( new ResultsFileFilter("RP") );
		
		if ( fileList.length > 0 ) {
			
			for (int i = 0; i < fileList.length; i++) {
				try {
					//Get name of the server which this file belongs to
					String name = fileList[i].getName().substring (	0,
																	fileList[i].getName().indexOf("RP.txt") 
																 	);
					BufferedReader fReader = new BufferedReader ( new FileReader(fileList[i]) );
					String buffer;
					//Create series of data for chart
					XYSeries rejectedPacketSeries = new XYSeries ("Rejected Packets");
										
					//Process the file until the end
					while( (buffer = fReader.readLine()) != null ) {
						String[] params = buffer.split(":");
						rejectedPacketSeries.add(	Double.parseDouble (params[0]), 
													Double.parseDouble (params[1])
													);
					}
					
					//Create chart
					XYSeriesCollection rejectedServicedPacketCol = new XYSeriesCollection ( rejectedPacketSeries );
					
					JFreeChart chart = ChartFactory.createXYLineChart(
								"Total Number of Rejected Packets on " + name,  	// Title
								"Time",           									// X-Axis label
								"Number of Rejected Packets",           			// Y-Axis label
								rejectedServicedPacketCol,							// Dataset
								PlotOrientation.VERTICAL,
								true,				            	    			// Show legend
								false,
								false
			        		);
					
					//Display chart frame
					ChartFrame frame = new ChartFrame("RP " + name, chart);
					frame.pack();
					frame.setLocation(20 + chartIndex * 20, 20 + chartIndex * 20);
					chartIndex++;
					frame.setVisible(true);
					
				} catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
					
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
 			}
		}
		
	}
	
	/**
	 * Save the configuration of servers into file
	 *
	 */
	private void saveConfig() {
		Configurator conf = new Configurator("ServerConfig.xml");
		conf.saveConfiguration(serversVector, generatorsVector);
	}
	
	/**
	 * Loads the configuration of servers from file
	 *
	 */
	private void loadConfig() {
		//numberOfServers.setText("0");
		while(!numberOfServers.getText().equals("0")) {
			System.out.println(numberOfServers.getText());
			removeServerTab(tabs);
		}
		serversVector.clear();
		generatorsVector.clear();
			
		Configurator conf = new Configurator("ServerConfig.xml");
		if( conf.configure(serversVector, generatorsVector) )
			for(int i = 0; i < serversVector.size(); i ++) {
				addServerTab(tabs, serversVector.elementAt(i));
			}
		else {
			MessageBox errorMsgBox = new MessageBox(shell, SWT.ICON_ERROR|SWT.OK);
			errorMsgBox.setMessage("Cannot read configuration file");
			errorMsgBox.setText("Error while loading configuration");
			errorMsgBox.open();
		}
			
		Event newEvent = new Event();
		newEvent.text = "refresh";
		tabs.notifyListeners(100, newEvent);
	}
	
	/**
	 * Adds menu to this GUI
	 * @param shell Window where the menu should be added
	 */
	private void addMenu(final Shell shell) {
		class MenuListener extends SelectionAdapter {

			public void widgetSelected(SelectionEvent se) {
				//Add Server 
				if( ((MenuItem)se.widget).getText().equals("Add Server") ) {
					addServerTab(tabs);
				}
				//Remove Server
				else if( ((MenuItem)se.widget).getText().equals("Remove Server") ) {
					removeServerTab(tabs);
				}
				//Validate Configuration
				else if( ((MenuItem)se.widget).getText().equals("Validate Configuration") ) {
					validateConfiguration(true);
				}
				//Run Simulation
				else if( ((MenuItem)se.widget).getText().equals("Run Simulation") ) {
					runSimulation(2000);
				}
				//Close Program
				else if( ((MenuItem)se.widget).getText().equals("Close Program") )
					closeGUI();	
				
				//Save config file
				else if( ((MenuItem)se.widget).getText().equals("Save Configuration in File") ) {
					saveConfig();
				}
				//Open config file
				else if( ((MenuItem)se.widget).getText().equals("Open Configuration from File") ) {
					loadConfig();
				}
			}
			
			private void closeGUI() {
				shell.close();
				display.dispose();
			}
			
		}
		Menu appMenu = new Menu(shell,SWT.BAR);
		appMenu.setLocation(0, 0);
		shell.setMenuBar(appMenu);
		SelectionListener menuSelection;
		menuSelection = new MenuListener();
		
		//FILE Menu
		MenuItem fileMenuHeader = new MenuItem(appMenu,SWT.CASCADE);
		fileMenuHeader.setText("File");
		Menu fileMenu = new Menu(shell,SWT.DROP_DOWN);
		fileMenuHeader.setMenu(fileMenu);
		MenuItem closeProgramItem = new MenuItem(fileMenu,SWT.PUSH);
		closeProgramItem.setText("Close Program");
		closeProgramItem.addSelectionListener(menuSelection);
		
		MenuItem saveConfigItem = new MenuItem(fileMenu,SWT.PUSH);
		saveConfigItem.setText("Save Configuration in File");
		saveConfigItem.addSelectionListener(menuSelection);

		MenuItem openConfigItem = new MenuItem(fileMenu,SWT.PUSH);
		openConfigItem.setText("Open Configuration from File");
		openConfigItem.addSelectionListener(menuSelection);
		
		
		
		//SERVERS Menu
		MenuItem serverMenuHeader = new MenuItem( appMenu, SWT.CASCADE );
		serverMenuHeader.setText("Servers");
		Menu serverMenu = new Menu(shell,SWT.DROP_DOWN);
		serverMenuHeader.setMenu(serverMenu);
		
		MenuItem addServer = new MenuItem(serverMenu, SWT.PUSH);
		addServer.setText("Add Server");
		addServer.addSelectionListener(menuSelection);
		
		MenuItem removeServer = new MenuItem(serverMenu,SWT.PUSH);
		removeServer.setText("Remove Server");
		removeServer.addSelectionListener(menuSelection);
		
		MenuItem validateConfig = new MenuItem(serverMenu,SWT.PUSH);
		validateConfig.setText("Validate Configuration");
		validateConfig.addSelectionListener(menuSelection);
		
		MenuItem runSimulation = new MenuItem(serverMenu,SWT.PUSH);
		runSimulation.setText("Run Simulation");
		runSimulation.addSelectionListener(menuSelection);
	}
	
	/**
	 * Adds labels and buttons to control the number of servers in the main window
	 * @param shell Windows where the buttons and labels should be added
	 */
	private void addServerNumberControl(Shell shell) {
		
		class butListener extends SelectionAdapter {
			public void widgetSelected(SelectionEvent e) {
				if( ((Button)e.widget).getText().equals("+") )
					addServerTab(tabs);
				else if( ((Button)e.widget).getText().equals("-") )
					removeServerTab(tabs);
			}
		}
		
		Font newFont = new Font(display,"System",12,SWT.NONE);
		SelectionListener buttonListener = new butListener();
		Label serverNumberLabel = new Label(shell,SWT.NONE);
		serverNumberLabel.setText("The number of servers is: ");
		serverNumberLabel.setFont(newFont);
		serverNumberLabel.setSize(serverNumberLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		serverNumberLabel.setLocation(30,30);
		serverNumberLabel.setFont(newFont);
		
		numberOfServers = new Label(shell,SWT.NONE);
		numberOfServers.setText("0");
		newFont = new Font(display,"System",12,SWT.BOLD);
		numberOfServers.setFont(newFont);
		numberOfServers.setSize(numberOfServers.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		numberOfServers.setLocation(serverNumberLabel.getLocation().x + serverNumberLabel.getSize().x + 10, serverNumberLabel.getLocation().y);
		
		Button addServerBut = new Button(shell,SWT.NONE);
		addServerBut.setSize(30,30);
		addServerBut.setLocation( numberOfServers.getLocation().x + numberOfServers.getSize().x + 10, numberOfServers.getSize().y );
		addServerBut.setText("+");
		addServerBut.addSelectionListener(buttonListener);
		
		Button removeServerBut = new Button(shell,SWT.NONE);
		removeServerBut.setSize(30,30);
		removeServerBut.setLocation( addServerBut.getLocation().x + addServerBut.getSize().x + 10, addServerBut.getLocation().y );
		removeServerBut.setText("-");
		removeServerBut.addSelectionListener(buttonListener);
	}

	/**
	 * Method that adds Validate Configuration and Run Simulation buttons and 
	 * surrounding components
	 * @param shell The shell where the components should be added
	 */
	private void addRunSimulationButton(Shell shell) {
		
		//Validate configuration button
		Button validateConfigButton = new Button(shell,SWT.NONE);
		validateConfigButton.setText("Validate Configuration");
		validateConfigButton.setSize(200,50);
		validateConfigButton.setLocation(30,400);
		validateConfigButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				validateConfiguration(true);
			}
		});
		
		//Run Simulation button
		Button runSimulationButton = new Button(shell,SWT.NONE);
		runSimulationButton.setText("Run Simulation");
		runSimulationButton.setSize(validateConfigButton.getSize());
		runSimulationButton.setLocation(validateConfigButton.getLocation().x + 
										validateConfigButton.getSize().x + 10, 
										validateConfigButton.getLocation().y);
		
		
		//Simulation Time Label
		Label simulationTimeLabel = new Label(shell, SWT.NONE);
		simulationTimeLabel.setText("Simulations Time [s]:");
		simulationTimeLabel.setFont(new Font(display,"System",12,SWT.NONE) );
		simulationTimeLabel.setSize(simulationTimeLabel.computeSize(
									SWT.DEFAULT, 
									SWT.DEFAULT));
		simulationTimeLabel.setLocation(runSimulationButton.getLocation().x + 
										runSimulationButton.getSize().x + 5,
										runSimulationButton.getLocation().y + 10);
		
		//Simulation Time Text
		final Text simulationTimeText = new Text(shell,SWT.BORDER|SWT.SINGLE);
		simulationTimeText.setLocation(	simulationTimeLabel.getLocation().x +
										simulationTimeLabel.getSize().x + 5,
										simulationTimeLabel.getLocation().y);
		simulationTimeText.setSize( 50, simulationTimeLabel.getSize().y);
		simulationTimeText.setText("2000");
		
		//Run Simulation button listener
		runSimulationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				runSimulation( 	Double.valueOf( 
								simulationTimeText.getText() )
								);
			}
		});
		
		//Generate graphs button
		Button genGraphsButton = new Button(shell,SWT.NONE);
		genGraphsButton.setSize(runSimulationButton.getSize());
		genGraphsButton.setText("Display graphs");
		genGraphsButton.setLocation (	runSimulationButton.getLocation().x, 
										runSimulationButton.getLocation().y + runSimulationButton.getSize().y + 5 );
		
		//Add Generate Graphs Button listener
		genGraphsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected( SelectionEvent arg0) {
				generateGraphs();
			}
		});
	}
}
