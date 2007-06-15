package fan;

import java.util.Vector;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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
		
		
		final TabItem serverTab = new TabItem(tabs,SWT.BORDER);
		int next = Integer.parseInt(numberOfServers.getText())+1;
		numberOfServers.setText(String.valueOf(next));
		serverTab.setText("Server nr " + String.valueOf(next));
		
		final Server server = new Server( "Server nr " + String.valueOf(next) );
		serversVector.add(server);
		
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
				generatorsVector.add( new Generate(new Time(0),server) );
				TreeItem generatorItem = new TreeItem(generatorTree,SWT.NONE);
				generatorItem.setText("Generator nr " + generatorsVector.size());
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
					int j = 0;
					for(int i =0; i <= generatorTree.indexOf(generatorTree.getSelection()[0]); i++ ) {
						while(generatorsVector.elementAt(j).getServer() != server)
							j++;
					}
					generatorsVector.removeElementAt(j);
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
						serverTree.removeAll();
						Vector<Route> routes = server.getRoutingTable().getRouting();
						for(int i =0; i < routes.size(); i++) {
							TreeItem interfaceItem = new TreeItem(serverTree,SWT.NONE);
							interfaceItem.setText("Interface to: " + routes.elementAt(i).getServerInterface().getServer().getName());
							TreeItem probabilityItem = new TreeItem(interfaceItem,SWT.NONE);
							probabilityItem.setText("Probability: " + routes.elementAt(i).getProbability());
							TreeItem bandwidthItem = new TreeItem(interfaceItem,SWT.NONE);
							bandwidthItem.setText("Bandwidth: " + routes.elementAt(i).getServerInterface().getBandwidth());
						}
						generatorTree.removeAll();
						for(int i = 0; i < generatorsVector.size(); i++) {
							if( generatorsVector.elementAt(i).getServer() == server) {
								TreeItem generatorItem = new TreeItem(generatorTree,SWT.NONE);
								generatorItem.setText("Generator nr " + (i+1));
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
	
	private void runSimulation() {
		if(validateConfiguration(false)) {
			double simulationTime = 200;
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
			for(int i = 0; i < serversVector.size(); i++) {
				RaportPrinter.printResultsForServer(serversVector.elementAt(i));
			}
		}
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
					runSimulation();
				}
				//Close Program
				else if( ((MenuItem)se.widget).getText().equals("Close Program") )
					closeGUI();			
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

	private void addRunSimulationButton(Shell shell) {
		
		Button validateConfigButton = new Button(shell,SWT.NONE);
		validateConfigButton.setText("Validate Configuration");
		validateConfigButton.setSize(200,50);
		validateConfigButton.setLocation(30,400);
		validateConfigButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				validateConfiguration(true);
			}
		});
		
		Button runSimulationButton = new Button(shell,SWT.NONE);
		runSimulationButton.setText("Run Simulation");
		runSimulationButton.setSize(validateConfigButton.getSize());
		runSimulationButton.setLocation(validateConfigButton.getLocation().x + validateConfigButton.getSize().x + 10, validateConfigButton.getLocation().y);
		runSimulationButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				runSimulation();
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
            bandwidthLabel.setText("Enter bandwidth");
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
            
            //Add Interface Button creation
            Button addInterfaceBut = new Button(shell,SWT.NONE);
            addInterfaceBut.setText("Add This Interface");
            addInterfaceBut.setLocation(probabilityText.getLocation().x,probabilityText.getLocation().y + probabilityText.getSize().y + 5);
            addInterfaceBut.setSize(bandwidthText.getSize().x, bandwidthText.getSize().y * 2);
            addInterfaceBut.addSelectionListener(new SelectionAdapter() {
            	public void widgetSelected(SelectionEvent arg0) {
            		if(serverList.getSelectionCount() == 1) {
            			server.addInterface(
            					serversVector.elementAt(serverList.getSelectionIndex()),
            					Double.parseDouble( probabilityText.getText() ), 
            					Integer.parseInt(bandwidthText.getText()) );
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

}
