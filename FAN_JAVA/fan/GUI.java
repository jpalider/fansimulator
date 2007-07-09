package fan;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
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
	
	private void addServerTab(final TabFolder tabs) {
		int next = Integer.parseInt(numberOfServers.getText())+1;
		Server server = new Server( "Server nr " + String.valueOf(next) );
		serversVector.add(server);
		addServerTab(tabs, server);
	}
	
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
				AddInterfaceDialog interfaceDialog = new AddInterfaceDialog(shell,SWT.APPLICATION_MODAL);
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
				AddGeneratorDialog generatorDialog = new AddGeneratorDialog(shell,SWT.APPLICATION_MODAL);
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
								generatorTypeItem.setText( generatorsVector.elementAt(i).type.name() );
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
								if( generatorsVector.elementAt(i).type.equals(GenerateType.constant) ) {
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Interval [s]: " + String.valueOf(((ConstantGenerate)generatorsVector.elementAt(i)).getInterval().toDouble()) );
								}
								else if( generatorsVector.elementAt(i).type.equals(GenerateType.normal) ) {
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Mean [s]: " + String.valueOf(((NormalGenerate)generatorsVector.elementAt(i)).getMean().toDouble()) );
									generatorTypeItem = new TreeItem(generatorItem,SWT.NONE);
									generatorTypeItem.setText( "Variance [s]: " + String.valueOf(((NormalGenerate)generatorsVector.elementAt(i)).getVariance().toDouble()) );
								}
								else if( generatorsVector.elementAt(i).type.equals(GenerateType.uniform) ) {
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
			Monitor.clock = new Time(-1);
			for(int i = 0; i < generatorsVector.size(); i++) {
				Monitor.agenda.schedule(generatorsVector.elementAt(i));
			}
			while( !Monitor.agenda.isEmpty() && Monitor.clock.compareTo(new Time(simulationTime)) <= 0 ) {
				fan.Event now = Monitor.agenda.removeFirst();
				Monitor.clock = now.time;
				now.run();
				//System.out.println("The time is now: " + Monitor.clock);
			}
			
			DisplayResultsDialog sumUpDialog = new DisplayResultsDialog(shell,SWT.NONE);
			sumUpDialog.open();
			for (Iterator iter = serversVector.iterator(); iter.hasNext();) {
				Server element = (Server) iter.next();
				element.clearResults();
			}
		}
	}
	
	private void saveConfig() {
		Configurator conf = new Configurator("ServerConfig.xml");
		conf.saveConfiguration(serversVector, generatorsVector);
	}
	
	private void loadConfig() {
		//numberOfServers.setText("0");
		while(!numberOfServers.getText().equals("0")) {
			System.out.println(numberOfServers.getText());
			removeServerTab(tabs);
		}
		serversVector.clear();
		generatorsVector.clear();
			
		Configurator conf = new Configurator("ServerConfig.xml");
		conf.configure(serversVector, generatorsVector);
		for(int i = 0; i < serversVector.size(); i ++) {
			addServerTab(tabs, serversVector.elementAt(i));
		}
		Event newEvent = new Event();
		newEvent.text = "refresh";
		tabs.notifyListeners(100, newEvent);
	}
	
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
		
	}
	
	
	private class AddInterfaceDialog extends Dialog {

		public AddInterfaceDialog(Shell arg0, int arg1) {
			super(arg0, arg1);
		}
		
		public void open(final Server server) {
			Shell parent = getParent();
            final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText("Add Interface To " + getText());
            shell.setSize(250, 300);
            
            //Server list creation
            final Table serverList = new Table(shell,SWT.SINGLE|SWT.BORDER);
            serverList.setLocation(10,40);
            serverList.setSize(100,200);
            serverList.setHeaderVisible(true);
            TableColumn serverColumn = new TableColumn(serverList,SWT.NONE);
            serverColumn.setText("Select Server");
            for(int i =0; i < serversVector.size(); i++) {
            	TableItem serverItem = new TableItem(serverList,SWT.NONE);
            	serverItem.setText(0, serversVector.elementAt(i).getName());
            }
            
            serverColumn.pack();
            
            //Bandwith text box creation
            Label bandwidthLabel = new Label(shell,SWT.NONE);
            bandwidthLabel.setText("Enter bandwidth [B/s]");
            bandwidthLabel.setSize(bandwidthLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            final Text bandwidthText = new Text(shell,SWT.SINGLE|SWT.BORDER);
            bandwidthText.setSize(100, 20);
            bandwidthText.setLocation(serverList.getLocation().x + serverList.getSize().x + 10, serverList.getLocation().y);
            bandwidthLabel.setLocation(bandwidthText.getLocation().x, bandwidthText.getLocation().y - bandwidthLabel.getSize().y - 5);
            
            //Probability text box creation
            Label probabilityLabel = new Label(shell,SWT.NONE);
            probabilityLabel.setText("Enter probability");
            probabilityLabel.setSize(probabilityLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            probabilityLabel.setLocation(bandwidthText.getLocation().x,bandwidthText.getLocation().y + bandwidthText.getSize().y + 5);
            final Text probabilityText = new Text(shell,SWT.SINGLE|SWT.BORDER);
            probabilityText.setSize(bandwidthText.getSize());
            probabilityText.setLocation(probabilityLabel.getLocation().x,probabilityLabel.getLocation().y + probabilityLabel.getSize().y + 5);
            
            //Queue size label/text creation
            Label queueSizeLabel = new Label(shell, SWT.NONE);
            queueSizeLabel.setText("Enter queue size [B]");
            queueSizeLabel.setSize ( queueSizeLabel.computeSize (SWT.DEFAULT, SWT.DEFAULT) );
            queueSizeLabel.setLocation(	probabilityText.getLocation().x, 
            							probabilityText.getLocation().y + probabilityText.getSize().y + 5);
            final Text queueSizeText = new Text( shell, SWT.SINGLE|SWT.BORDER );
            queueSizeText.setSize ( probabilityText.getSize() );
            queueSizeText.setLocation ( queueSizeLabel.getLocation().x,
            							queueSizeLabel.getLocation().y + queueSizeLabel.getSize().y + 5);
            queueSizeText.setText("1000");
            
            //Add Interface Button creation
            Button addInterfaceBut = new Button(shell,SWT.NONE);
            addInterfaceBut.setText("Add This Interface");
            addInterfaceBut.setLocation(queueSizeText.getLocation().x,queueSizeText.getLocation().y + queueSizeText.getSize().y + 5);
            addInterfaceBut.setSize(bandwidthText.getSize().x, bandwidthText.getSize().y * 2);
            addInterfaceBut.addSelectionListener(new SelectionAdapter() {
            	public void widgetSelected(SelectionEvent arg0) {
            		if(serverList.getSelectionCount() == 1) {
            			server.addInterface(
            					serversVector.elementAt ( serverList.getSelectionIndex() ),
            					Double.parseDouble ( probabilityText.getText() ), 
            					Integer.parseInt ( bandwidthText.getText() ),
            					Integer.parseInt ( queueSizeText.getText() ) );
            			shell.close();
            		}
            			
            	}
            });
            
            shell.open();
            Display display = parent.getDisplay();
            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            return;

		}
		
	}

	private class AddGeneratorDialog extends Dialog {

		public AddGeneratorDialog(Shell shell, int style) {
			super(shell, style);
		}
		
		public void open(final Server server) {
			Shell parent = getParent();
            final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText("Add Generator To " + getText());
            shell.setSize(250, 400);
            
            
            
            //Combo with the list of possible generator types
            final Combo generatorTypeCombo = new Combo(shell,SWT.DROP_DOWN);
            for (Generate.GenerateType types : GenerateType.values()) {
				generatorTypeCombo.add(types.name());
			}
            generatorTypeCombo.setSize(100,25);
            generatorTypeCombo.setLocation(10,40);
            
            //Generator combo label
            Label generatorTypeLabel = new Label(shell,SWT.NONE);
            generatorTypeLabel.setText("Generator type:");
            generatorTypeLabel.setSize(generatorTypeLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            generatorTypeLabel.setLocation(generatorTypeCombo.getLocation().x, generatorTypeCombo.getLocation().y - generatorTypeLabel.getSize().y - 5);
            
            //Packet Size Text box
            final Text packetSizeText = new Text(shell, SWT.SINGLE|SWT.BORDER);
            packetSizeText.setSize(generatorTypeCombo.getSize());
            packetSizeText.setLocation(generatorTypeCombo.getLocation().x, generatorTypeCombo.getLocation().y + generatorTypeCombo.getSize().y + 40);
            packetSizeText.setText("1500");
            
            //Packet Size Label
            Label packetSizeLabel = new Label(shell, SWT.NONE);
            packetSizeLabel.setText("Packet size [B]");
            packetSizeLabel.setSize (packetSizeLabel.computeSize(	SWT.DEFAULT,
            														SWT.DEFAULT));
            packetSizeLabel.setLocation(	packetSizeText.getLocation().x, 
            								packetSizeText.getLocation().y - 
            								packetSizeLabel.getSize().y - 5);
            
            
            //Start Time Text box
            final Text startTimeText = new Text(shell, SWT.SINGLE|SWT.BORDER);
            startTimeText.setSize(generatorTypeCombo.getSize().x,generatorTypeCombo.getSize().y);
            startTimeText.setLocation(packetSizeText.getLocation().x, packetSizeText.getLocation().y + packetSizeText.getSize().y + 40);
            
            //Start time label
            Label startTimeLabel = new Label(shell,SWT.NONE);
            startTimeLabel.setText("Start time [s]:");
            startTimeLabel.setSize(startTimeLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            startTimeLabel.setLocation(startTimeText.getLocation().x, startTimeText.getLocation().y - startTimeLabel.getSize().y - 5);
            
            //Finish time label
            Label finishTimeLabel = new Label(shell, SWT.NONE);
            finishTimeLabel.setText("Finish time [s]:");
            finishTimeLabel.setSize(finishTimeLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            finishTimeLabel.setLocation(startTimeText.getLocation().x, startTimeText.getLocation().y + startTimeText.getSize().y + 5);
            
            //Finish time Text box
            final Text finishTimeText = new Text(shell, SWT.SINGLE|SWT.BORDER);
            finishTimeText.setSize(startTimeText.getSize());
            finishTimeText.setLocation(finishTimeLabel.getLocation().x, finishTimeLabel.getLocation().y + finishTimeLabel.getSize().y + 5);
            
            //Finish time note
            Label finishTimeNoteLabel = new Label(shell, SWT.NONE|SWT.WRAP);
            finishTimeNoteLabel.setText("Note:\nWhen finish time is empty, the generator is turned on till the end of the simulation");
            finishTimeNoteLabel.setSize(finishTimeNoteLabel.computeSize(finishTimeText.getSize().x , SWT.DEFAULT));
            finishTimeNoteLabel.setLocation(finishTimeText.getLocation().x, finishTimeText.getLocation().y + finishTimeText.getSize().y + 5);
            
            //Interval text box
            final Text intervalText = new Text(shell,SWT.SINGLE|SWT.BORDER);
            intervalText.setSize(100, generatorTypeCombo.getSize().y);
            intervalText.setLocation(generatorTypeCombo.getLocation().x + generatorTypeCombo.getSize().x + 10, generatorTypeCombo.getLocation().y);
            intervalText.setVisible(false);
            
            //Interval text box label
            final Label intervalLabel = new Label(shell,SWT.NONE);
            intervalLabel.setText("Interval [s]:");
            intervalLabel.setSize(intervalLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            intervalLabel.setLocation(intervalText.getLocation().x, intervalText.getLocation().y - intervalLabel.getSize().y - 5);
            intervalLabel.setVisible(false);
            
            //Variance text box
            final Text varianceText = new Text(shell,SWT.SINGLE|SWT.BORDER);
            varianceText.setSize(intervalText.getSize().x, intervalText.getSize().y);
            varianceText.setLocation(intervalText.getLocation().x, packetSizeText.getLocation().y);
            varianceText.setVisible(false);
            
            //Variance text box label
            final Label varianceLabel = new Label(shell,SWT.NONE);
            varianceLabel.setText("Variance [s]:");
            varianceLabel.setSize(varianceLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            varianceLabel.setLocation(varianceText.getLocation().x, varianceText.getLocation().y - varianceLabel.getSize().y - 5);
            varianceLabel.setVisible(false);
            
            //Add generator button
            Button addGeneratorBut = new Button(shell, SWT.NONE);
            addGeneratorBut.setText("Add this generator");
            addGeneratorBut.setSize(addGeneratorBut.computeSize( SWT.DEFAULT, SWT.DEFAULT) );
            addGeneratorBut.setLocation(generatorTypeCombo.getLocation().x + generatorTypeCombo.getSize().x + 10, startTimeText.getLocation().y);
            
            //GeneratorType Combo selection listener
            generatorTypeCombo.addSelectionListener(new SelectionAdapter(){
            	public void widgetSelected(SelectionEvent arg0) {
            		if( generatorTypeCombo.getText().equals(GenerateType.basic.name()) ) {
            			intervalText.setVisible(false);
            			intervalLabel.setVisible(false);
            			varianceLabel.setVisible(false);
            			varianceText.setVisible(false);
            		}
            		else if( generatorTypeCombo.getText().equals(GenerateType.constant.name()) ) {
            			intervalText.setVisible(true);
            			intervalLabel.setText("Interval [s]:");
            			intervalLabel.setVisible(true);
            			
            			varianceLabel.setVisible(false);
            			varianceText.setVisible(false);
            		}
            		else if( generatorTypeCombo.getText().equals(GenerateType.normal.name()) ) {
            			intervalLabel.setText("Mean [s]:");
            			intervalText.setVisible(true);
            			intervalLabel.setVisible(true);
            			
            			varianceLabel.setVisible(true);
            			varianceText.setVisible(true);
            		}
            		else if( generatorTypeCombo.getText().equals(GenerateType.uniform.name()) ) {
            			intervalText.setVisible(true);
            			intervalLabel.setText("Range [s]:");
            			intervalLabel.setVisible(true);
            			
            			varianceLabel.setVisible(false);
            			varianceText.setVisible(false);
            		}
            	}
            });
            
            //Add generator button listener
            addGeneratorBut.addSelectionListener(new SelectionAdapter() {
            	public void widgetSelected(SelectionEvent arg0) {
            		Time startTime = new Time( Double.valueOf( startTimeText.getText() ) );
            		Time finishTime = null;
            		int packetSize = Integer.parseInt (packetSizeText.getText());
            		
            		if(finishTimeText.getText().length() > 0)
            			finishTime = new Time( Double.valueOf( finishTimeText.getText() ) );

            		if( generatorTypeCombo.getText().equals(GenerateType.basic.name()) ) {
            			if(finishTime != null)
            				generatorsVector.add(new Generate(startTime, server, finishTime, packetSize) );
            			else
            				generatorsVector.add( new Generate(startTime,server, packetSize) );
            		}
            		
            		else if( generatorTypeCombo.getText().equals(GenerateType.constant.name()) ) {
            			Time intervalTime = new Time( Double.valueOf(intervalText.getText()) );
            			if(finishTime != null)
            				generatorsVector.add( new ConstantGenerate(startTime, server, intervalTime, finishTime, packetSize) );
            			else
            				generatorsVector.add( new ConstantGenerate(startTime, server, intervalTime, packetSize) );
            		}
            		
            		else if( generatorTypeCombo.getText().equals(GenerateType.normal.name()) ) {
            			Time meanTime = new Time( Double.valueOf(intervalText.getText()) );
            			Time varianceTime = new Time( Double.valueOf( varianceText.getText() ) );
            			if(finishTime != null)
            				generatorsVector.add( new NormalGenerate(startTime, server, meanTime, varianceTime, finishTime, packetSize) );
            			else
            				generatorsVector.add( new NormalGenerate(startTime, server, meanTime, varianceTime, packetSize) );
            		}
            		
            		else if( generatorTypeCombo.getText().equals(GenerateType.uniform.name()) ) {
            			Time rangeTime = new Time( Double.valueOf(intervalText.getText()) );
            			if(finishTime != null)
            				generatorsVector.add( new UniformGenerate(startTime, server, rangeTime, finishTime, packetSize) );
            			else
            				generatorsVector.add( new UniformGenerate(startTime, server, rangeTime, packetSize) );
            		}
            		
            		shell.close();
            	}
            });
            
            
            shell.open();
            Display display = parent.getDisplay();
            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            return;

		}
		
	}

	private class DisplayResultsDialog extends Dialog {
		private Text outputText;
		
		public DisplayResultsDialog(Shell arg0, int arg1) {
			super(arg0, arg1);
		}
		
		public void open() {
			Shell parent = getParent();
            final Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
            shell.setText("The Simulation Results");
            shell.setSize(700, 550);
            
            //Text box for displaying output
            outputText = new Text(shell,SWT.BORDER|SWT.MULTI|SWT.WRAP|SWT.V_SCROLL);
            outputText.setSize(shell.getSize().x - 10, shell.getSize().y - 20);
            outputText.setLocation(5,5);
            
            System.setOut(new PrintStream( new TextBoxOutputStream() ) );
            for(int i = 0; i < serversVector.size(); i++) {
				RaportPrinter.printResultsForServer(serversVector.elementAt(i));
			}
            
            System.setOut ( new PrintStream ( new FileOutputStream ( FileDescriptor.out )  )  ) ; 
            
            shell.open();
            Display display = parent.getDisplay();
            while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) display.sleep();
            }
            return;

		}
		private class TextBoxOutputStream extends OutputStream {

			public void write(int b) throws IOException {
				outputText.append(String.valueOf((char)b));				
			}	
		}
	}
}
