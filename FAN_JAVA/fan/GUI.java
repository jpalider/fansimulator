package fan;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;

public class GUI extends JFrame {
	
	
	public GUI(){
		super("FanSimulator by M&N");
		 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		 this.setBounds(100, 300, 300, 400);
		 this.setVisible(true);       
	}

}
