package fan;

public class Random implements Randomize {
	private java.util.Random generator;
	
	public Random() {
		generator = new java.util.Random();
	}
	public double getNumber(double range) {
		return generator.nextDouble() * range;
	}
	
}
