package fan;

/**
 * Class responsible for proper, if any, information printing.
 * 
 * Has 3 levels of severity: INFO, WARN and ERR;
 *
 */
public class Debug {
	static final public int INFO = 0;
	static final public int WARN = 1; 
	static final public int ERR = 2; 
	
	static final private boolean doDebugging = true;	
	static final private int severity = INFO;
	
	/**
	 * If Debug class allows, it displays all types of information
	 * @param s String to be either displayed, saved in file or both
	 */
	static public void  print(String s) {
		if (doDebugging) {
			System.out.println(s);
		}
	}
	/**
	 * If Debug class allows, it displays only some types information
	 * 
	 * @param sev Lowest displayable severity
	 * @param s String to be displayed
	 */
	static public void  print(int sev, String s) {
		if (doDebugging && sev >= severity) {
			System.out.println(s);
		}
	}
}
