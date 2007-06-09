package fan;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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

public class GUI {
	
	private Shell shell;
	private Display display;
	private Label numberOfServers;
	private TabFolder tabs;
	
	public GUI(){
		display = new Display();
		shell = new Shell(display,SWT.DIALOG_TRIM);
		shell.setLayout( new FillLayout());
	    
	    shell.setSize(500, 600);
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
	
	private void addServerTab(TabFolder tabs) {
		
		final TabItem serverTab = new TabItem(tabs,SWT.BORDER);
		int next = Integer.parseInt(numberOfServers.getText())+1;
		numberOfServers.setText(String.valueOf(next));
		serverTab.setText("Server nr " + String.valueOf(next));
		Composite serverTabComp = new Composite(tabs,SWT.BORDER);
		serverTabComp.setLayout(null);
		serverTabComp.setSize(300, 300);
		
		//Label with server name
		Label serverNameLbl = new Label(serverTabComp,SWT.NONE);
		serverNameLbl.setText("Server nr " + String.valueOf(next));
		serverNameLbl.setBounds(10, 20, 200, 20);
		
		//Tree with interfaces
		final Tree serverTree = new Tree(serverTabComp,SWT.SINGLE|SWT.BORDER);
		serverTree.setBounds(10, 50, 200, 200);
		
		//Button to add interface
		Button addInterfaceBut = new Button(serverTabComp,SWT.NONE);
		addInterfaceBut.setText("Add Interface");
		addInterfaceBut.setLocation(serverTree.getLocation().x + serverTree.getSize().x + 10, serverTree.getLocation().y);
		addInterfaceBut.setSize(addInterfaceBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addInterfaceBut.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent arg0) {
				TreeItem interfaceItem = new TreeItem(serverTree,SWT.NONE);
				AddInterfaceDialog interfaceDialog = new AddInterfaceDialog(shell,SWT.APPLICATION_MODAL);
				interfaceDialog.setText(serverTab.getText());
				interfaceDialog.open(interfaceItem);
			}
			
		});
		
		//Button to remove interface
		Button removeInterfaceBut = new Button(serverTabComp,SWT.NONE);
		removeInterfaceBut.setText("Remove Interface");
		removeInterfaceBut.setLocation(serverTree.getLocation().x + serverTree.getSize().x + 10, addInterfaceBut.getLocation().y + addInterfaceBut.getSize().y + 5);
		removeInterfaceBut.setSize(removeInterfaceBut.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		addInterfaceBut.setSize(removeInterfaceBut.getSize().x, addInterfaceBut.getSize().y);
		removeInterfaceBut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				if( serverTree.getSelectionCount() == 1 ) {
					if( serverTree.getSelection()[0].getParentItem() == null)
						serverTree.getSelection()[0].dispose();
					else
						serverTree.getSelection()[0].getParentItem().dispose();
				}
			}
		});
		
		serverTab.setControl(serverTabComp);
	}
	
	private void removeServerTab(TabFolder tabs) {
		if(tabs.getItemCount() > 0) {
			tabs.getItems()[tabs.getItemCount()-1].dispose();
			int next = Integer.parseInt(numberOfServers.getText())-  1;
			numberOfServers.setText(String.valueOf(next));
		}
	}
	
	private void addServerTabFolder(Shell shell) {
		tabs = new TabFolder(shell, SWT.TOP|SWT.NO_REDRAW_RESIZE);
	    tabs.setSize(400, 300);
	    tabs.setLocation(30,80);
	}
	
	private void validateConfiguration() {
		
	}
	
	private void runSimulation() {
		
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
					validateConfiguration();
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
				validateConfiguration();
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
		
		public void open(final TreeItem interfaceItem) {
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
            for(int i =0; i < tabs.getItemCount(); i++) {
            	TableItem serverItem = new TableItem(serverList,SWT.NONE);
            	serverItem.setText(0, tabs.getItems()[i].getText());
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
            			interfaceItem.setText("Interface to: " + serverList.getSelection()[0].getText());
            			TreeItem bandwidthItem = new TreeItem(interfaceItem,SWT.NONE);
            			bandwidthItem.setText("Bandwidth: " + bandwidthText.getText());
            			TreeItem probabilityItem = new TreeItem(interfaceItem,SWT.NONE);
            			probabilityItem.setText("Probability: " + probabilityText.getText());
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
