package fan;

/**
 * Class for group of random generators that can be used in simulation
 */
public class Random implements Randomize {
	
	/**
	 * Generator used in this class
	 */
	private java.util.Random generator;
	
	
	/**
	 * Constructor for this class
	 */
	public Random() {
		generator = new java.util.Random();
	}
	
	
	/**
	 * Method that returns double from range 0 to specified limit
	 * @param range The upper limit of returned double
	 * @return Randomly generated number
	 */
	public double getNumber(double range) {
		return generator.nextDouble() * (range);
	}
	
	
	/**
	 * Method to return randomly generated gaussian number
	 * @param mean Mean in Gauss
	 * @param variance Varian in Gauss
	 * @return Randomly generated gaussian number
	 */
	public double getGaussianNumber(double mean, double variance) {
		return (generator.nextGaussian() * Math.sqrt(variance) + mean);
	}
	
}