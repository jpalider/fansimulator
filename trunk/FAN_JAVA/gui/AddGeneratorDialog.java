package gui;

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
import org.eclipse.swt.widgets.Text;

import fan.ConstantGenerate;
import fan.Generate;
import fan.NormalGenerate;
import fan.Server;
import fan.Time;
import fan.UniformGenerate;
import fan.Generate.GenerateType;

public class AddGeneratorDialog extends Dialog {
	
	private Vector<Generate> generatorsVector;
	
	public AddGeneratorDialog(Shell shell, int style, Vector<Generate> generatorsVector) {
		super(shell, style);
		this.generatorsVector = generatorsVector;
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
