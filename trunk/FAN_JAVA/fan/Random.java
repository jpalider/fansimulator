package fan;

public class Random implements Randomize {
	private java.util.Random generator;
	
	public Random() {
		generator = new java.util.Random();
	}
	
	/**
	 * Method that returns double from range 0 to specified limit
	 * @param range The upper limit of returned double
	 */
	public double getNumber(double range) {
		return generator.nextDouble() * range;
	}
	
	public double getGaussianNumber(double mean, double variance) {
		return (generator.nextGaussian() * Math.sqrt(variance) + mean);
	}
	
}
