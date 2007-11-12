package gui;

import javax.print.attribute.standard.Finishings;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public class ProgressDialog extends Dialog {
	
	private ProgressBar progressBar;
	private Label progressLabel;
	private int maximum;
	private boolean closed;
	private short previousProgress;
	private Button finishButton;
	private Shell shell;
	
	public ProgressDialog( Shell shell, int style, double maximum ) {
		super( shell, style );
		this.maximum = (int) maximum;
		closed = true;
		previousProgress = 0;
	}
	
	public void open() {
		
		Shell parent = getParent();
        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        shell.setText("Simulation is running...");
        shell.setSize(400, 250);
        
        //The bar showing the progress of the simulation
        progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
        progressBar.setSize( 200, 30 );
        progressBar.setLocation( 100, 80 );
        progressBar.setMinimum( 0 );
        progressBar.setMaximum( maximum );
        
        //Label to name simulation progress bar
        progressLabel = new Label( shell, SWT.None );
        progressLabel.setText( "Simulation Progress: 0%    ");
        progressLabel.setSize( progressLabel.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        progressLabel.setLocation( progressBar.getLocation().x, progressBar.getLocation().y - progressLabel.getSize().y - 5 );
        
        
        //Button to close the simulation
        finishButton = new Button( shell, SWT.NONE );
        finishButton.setText( "Close this window" );
        finishButton.setSize( finishButton.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
        finishButton.setLocation( 	progressBar.getLocation().x + ( progressBar.getSize().x - finishButton.getSize().x ) / 2,
        							progressBar.getLocation().y + progressBar.getSize().y + 10 );
        finishButton.setEnabled( false );
        
        finishButton.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				ProgressDialog.this.closed = false;
			}  	
        });
        
        
        shell.open();
        Display display = parent.getDisplay();
        
        while (!shell.isDisposed() && closed ) {
                if (!display.readAndDispatch()) display.sleep();
        }
        
        shell.close();

        return;
	}
	
	/**
	 * Method to set progress value
	 * @param value The value of the progress - it is relative to maximum value of progress bar
	 * 				set in the constructor of this ProgressDialog
	 */
	public void setProgress( double value ) {
		progressBar.setSelection( (int) value );
		short progress = (short)( value / (double)maximum * (double)100 );
		if( progress - previousProgress >= 1) {
			progressLabel.setText("Simulation Progress: " +  progress + "%");
			progressLabel.update();
			previousProgress = progress;
			if( progress == 100 ) 
				finishButton.setEnabled( true );
			shell.update();
		}
	}

}
