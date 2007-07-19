package gui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import fan.Server;

public class AddInterfaceDialog extends Dialog {
	
	private Vector<Server> serversVector;
	
	public AddInterfaceDialog(Shell arg0, int arg1, Vector<Server> serversVector) {
		super(arg0, arg1);
		this.serversVector = serversVector;
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

