package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
//import org.eclipse.swt.layout.*;

import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;


public class SplashScreen implements Runnable {
	
	final Display display = new Display();
	final Image image = new Image(display, "res/splash.png");	
	final Shell splash = new Shell(SWT.ON_TOP);
	Label label = new Label(splash, SWT.NONE);
	
	public SplashScreen() {
	
		label.setImage(image);		
		splash.setLayout( new FillLayout() );		
		splash.setBounds(label.getBounds());
				
		Rectangle splashRect = image.getBounds();
		Rectangle displayRect = display.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);			
		splash.pack();
	}

	
	public void run() {
		splash.open();
		try { Thread.sleep(4*1000); } catch (Exception e) {}
		display.dispose();		
	}
	
}
