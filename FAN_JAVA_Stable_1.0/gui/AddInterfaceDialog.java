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
        shell.setSize( 300, 400);
        
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
        
        //FlowList size label/text creation
        Label maxFlowListSizeLabel = new Label(shell, SWT.NONE);
        maxFlowListSizeLabel.setText("Enter Flow List size [ent.]");
        maxFlowListSizeLabel.setSize ( maxFlowListSizeLabel.computeSize (SWT.DEFAULT, SWT.DEFAULT) );
        maxFlowListSizeLabel.setLocation(	queueSizeText.getLocation().x, 
        									queueSizeText.getLocation().y + 
        									queueSizeText.getSize().y + 5);
        final Text maxFlowListSizeText = new Text( shell, SWT.SINGLE|SWT.BORDER );
        maxFlowListSizeText.setSize ( probabilityText.getSize() );
        maxFlowListSizeText.setLocation ( maxFlowListSizeLabel.getLocation().x,
        									maxFlowListSizeLabel.getLocation().y + 
        									maxFlowListSizeLabel.getSize().y + 5);
        maxFlowListSizeText.setText("1000");
        
        //Minimum Fair Rate Label and TextBox creation
        Label minFRLabel = new Label( shell, SWT.None );
        minFRLabel.setText( "Enter Min Fair Rate [B/s]:" );
        minFRLabel.setSize( minFRLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        minFRLabel.setLocation( maxFlowListSizeText.getLocation().x, 
        						maxFlowListSizeText.getLocation().y + 
        						maxFlowListSizeText.getSize().y + 5 );
        final Text minFRText = new Text( shell, SWT.SINGLE|SWT.BORDER );
        minFRText.setSize( maxFlowListSizeText.getSize().x, maxFlowListSizeText.getSize().y );
        minFRText.setLocation( 	minFRLabel.getLocation().x,
        						minFRLabel.getLocation().y +
        						minFRLabel.getSize().y + 5 );
        minFRText.setText( "1" );
        
        //Maximum Priority Load Label and Text creation
        Label maxPLLabel = new Label( shell, SWT.None );
        maxPLLabel.setText( "Enter Max Priority Load [B/s]:" );
        maxPLLabel.setSize( maxPLLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        maxPLLabel.setLocation( minFRText.getLocation().x, 
        						minFRText.getLocation().y + 
        						minFRText.getSize().y + 5 );
        final Text maxPLText = new Text( shell, SWT.SINGLE|SWT.BORDER );
        maxPLText.setSize( maxFlowListSizeText.getSize().x, maxFlowListSizeText.getSize().y );
        maxPLText.setLocation( 	maxPLLabel.getLocation().x,
        						maxPLLabel.getLocation().y +
        						maxPLLabel.getSize().y + 5 );
        maxPLText.setText( "100000" );
        															
        
        
        //Add Interface Button creation
        Button addInterfaceBut = new Button(shell,SWT.NONE);
        addInterfaceBut.setText("Add This Interface");
        addInterfaceBut.setLocation(queueSizeText.getLocation().x,maxPLText.getLocation().y + maxPLText.getSize().y + 10);
        addInterfaceBut.setSize(bandwidthText.getSize().x, bandwidthText.getSize().y * 2);
        addInterfaceBut.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent arg0) {
        		if(serverList.getSelectionCount() == 1) {
        			server.addInterface(
        					serversVector.elementAt ( serverList.getSelectionIndex() ),
        					Double.parseDouble ( probabilityText.getText() ), 
        					Integer.parseInt ( bandwidthText.getText() ),
        					Integer.parseInt ( queueSizeText.getText() ),
        					Integer.parseInt ( maxFlowListSizeText.getText() ), 
        					Integer.parseInt ( minFRText.getText() ),
        					Integer.parseInt ( maxPLText.getText() ) );
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

