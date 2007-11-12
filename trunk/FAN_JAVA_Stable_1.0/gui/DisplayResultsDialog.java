package gui;

import fan.RaportPrinter;
import fan.Server;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DisplayResultsDialog extends Dialog {
	private Text outputText;
	private Vector<Server> serversVector;
	
	public DisplayResultsDialog(Shell arg0, int arg1, Vector<Server> serversVector) {
		super(arg0, arg1);
		this.serversVector = serversVector;
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