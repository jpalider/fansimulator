package gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.TrayIcon;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fan.Configurator;
import fan.ConstantGenerate;
import fan.Generate;
import fan.Monitor;
import fan.NormalGenerate;
import fan.PFQQueueBytes;
import fan.Queue;
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
	
	private class SimulationThread extends Thread {
		private ProgressDialog progressDialog;
		private double simulationTime;
		
		public SimulationThread( ProgressDialog pd, double simulTime ) {
			progressDialog = pd;
			simulationTime = simulTime;
		}
		
		public void run() {
			while( !Monitor.agenda.isEmpty() && Monitor.clock.compareTo(new Time(simulationTime)) <= 0 ) {
				progressDialog.setProgress( Monitor.clock.toDouble() );
				fan.Event now = Monitor.agenda.removeFirst();
				Monitor.clock = now.time;				
				now.run();
			}
			progressDialog.setProgress( simulationTime );
		}
	}
	
	Text descField;
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
	    shell.setImage(new Image(display,"res/titleimage.png"));
	    setTray();	    
	    centerWindow();
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
							Queue queue = routes.elementAt(i).getServerInterface().getQueue();
							if ( queue.getType().equals("PFQ")){
								TreeItem maxFlowListSizeItem = new TreeItem(interfaceItem, SWT.NONE);
								maxFlowListSizeItem.setText(	"FlowList size [ent.]: " + 
										((PFQQueueBytes)queue).getFlowList().getMaxLength() );
							}
							
							TreeItem minFRItem = new TreeItem( interfaceItem, SWT.NONE );
							minFRItem.setText( "Min Fair Rate [B/s]: " + routes.elementAt(i).getServerInterface().getMBAC().getMinFairRate());
							
							TreeItem maxPLItem = new TreeItem( interfaceItem, SWT.None );
							maxPLItem.setText( "Max Priority Load [B/s]: " + routes.elementAt(i).getServerInterface().getMBAC().getMaxPriorityLoad() );
						}
						
						//refresh generator Tree
						generatorTree.removeAll();
						for(int i = 0; i < generatorsVector.size(); i++) {
							if( generatorsVector.elementAt(i).getServer() == server) {
								TreeItem generatorItem = new TreeItem(generatorTree,SWT.NONE);
								generatorItem.setText("Generator nr " + (i+1));
								
								TreeItem generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( generatorsVector.elementAt(i).getType().name() );
								
								generatorTypeItem = new TreeItem(generatorItem, SWT.NONE);
								generatorTypeItem.setText(	"Packet size [B]: " + 
														String.valueOf(
																generatorsVector.elementAt(i).getPacketSize()
																) );
								
								generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( "Lower FlowID Range: " + String.valueOf( generatorsVector.elementAt(i).getFlowIdLowerRange() ) );
								generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( "Higher FlowID Range: " + String.valueOf( generatorsVector.elementAt(i).getFlowIdHigherRange() ) );
								
								generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
								generatorTypeItem.setText( "Start [s]: " + String.valueOf( generatorsVector.elementAt(i).getTime().toDouble() ) );
									
								
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
			
			
			//Remove files with results of previous simulations
			File localDir = new File("./");
			File[] fileList;
			fileList = localDir.listFiles(new FilenameFilter() {

				public boolean accept(File dir, String name) {
					if( name.startsWith("Server nr") && name.contains(".txt") )
						return true;
					else
						return false;
				}
				
			});
			
			System.out.println("filelist size is: " + fileList.length );
			
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				file.delete();
			}

			
			//Reset clock and schedule all generators attached to servers
			Monitor.clock = new Time(-1);
			for(int i = 0; i < generatorsVector.size(); i++) {
				Monitor.agenda.schedule(generatorsVector.elementAt(i));
			}
			
			
			ProgressDialog progressDialog = new ProgressDialog( shell, SWT.NONE, simulationTime );
			display.asyncExec( new SimulationThread( progressDialog, simulationTime ) );
			progressDialog.open();
			
			shell.update();
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
		
		GenerateGraphsDialog test = new GenerateGraphsDialog(shell,SWT.NONE);
		test.open();
		
	}
	
	/**
	 * Save the configuration of servers into file
	 *
	 */
	private void saveConfig() {
		saveConfig("ServerConfig.xml");
	}
	private void saveConfig(String fname) {
		if ((fname==null) || (fname.compareTo("")==0)) {
			System.out.println("Empty config file name!");
			return;
		}
		Configurator conf = new Configurator(fname);
		conf.setDescription(descField.getText());
		conf.saveConfiguration(serversVector, generatorsVector);
	}
	
	/**
	 * Loads the configuration of servers from file
	 *
	 */
	private void loadConfig() {
		loadConfig("ServerConfig.xml");
	}
	
	private void loadConfig(String fname) {
		//numberOfServers.setText("0");
		while(!numberOfServers.getText().equals("0")) {
			System.out.println(numberOfServers.getText());
			removeServerTab(tabs);
		}
		serversVector.clear();
		generatorsVector.clear();
			
		Configurator conf = new Configurator(fname);
		if( conf.configure(serversVector, generatorsVector) ) {
			descField.setText( conf.getDescription() );			
			for(int i = 0; i < serversVector.size(); i ++) {
				addServerTab(tabs, serversVector.elementAt(i));
			}
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
				//Save config file as...
				else if( ((MenuItem)se.widget).getText().equals("Save Configuration as ...") ) {
//					
					FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				    dialog.setFilterNames(new String[] { "Config Files", "All Files (*.*)" });
				    dialog.setFilterExtensions(new String[] { "*.xml", "*.*" });				    
				    dialog.setFileName("ServerConfig.xml");
				    dialog.open();
				    shell.update();
				    saveConfig(dialog.getFileName());
				}
				//Open config file
				else if( ((MenuItem)se.widget).getText().equals("Open Configuration from File") ) {
					loadConfig();
				}
				//Open config file...
				else if( ((MenuItem)se.widget).getText().equals("Select Configuration File...") ) {
				    FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);

			        //fileDialog.setFilterPath("c:");
			        
			        fileDialog.setFilterExtensions(new String[]{"*.xml", "*.*"});
			        fileDialog.setFilterNames(new String[]{ "XML config file", "Any"});
			        
			        String firstFile = fileDialog.open();
			        shell.update();

			        String selectedFile = new String();
			        if(firstFile != null) {
			          selectedFile = fileDialog.getFileNames()[0];
			        }
			        if ((selectedFile.compareTo("") != 0) && (selectedFile!=null)) {
			        	loadConfig(selectedFile);			        	
			        } else {
			        	System.out.println("Not a proger config file!");
			        }
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
		
		MenuItem saveAsConfigItem = new MenuItem(fileMenu,SWT.PUSH);
		saveAsConfigItem.setText("Save Configuration as ...");
		saveAsConfigItem.addSelectionListener(menuSelection);

		MenuItem openConfigItem = new MenuItem(fileMenu,SWT.PUSH);
		openConfigItem.setText("Open Configuration from File");
		openConfigItem.addSelectionListener(menuSelection);
		
		MenuItem selectConfigItem = new MenuItem(fileMenu,SWT.PUSH);
		selectConfigItem.setText("Select Configuration File...");
		selectConfigItem.addSelectionListener(menuSelection);
		
		
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
		
		descField = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		descField.setSize(330, 60);
		descField.setLocation( removeServerBut.getLocation().x + removeServerBut.getSize().x + 30, removeServerBut.getLocation().y );

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
		simulationTimeText.setText("200");
		
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
	
	void setTray() {
	    Tray tray = display.getSystemTray();
	    Image image = new Image(display,"res/titleimage.png");
	    if(tray != null) {
	    	TrayItem trayItem = new TrayItem(tray, SWT.NONE);
	    	trayItem.setImage(image);	    	
	    }	    
	}
	
	void centerWindow(){
		Rectangle splashRect = shell.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		shell.setLocation(x, y);
	}
}
