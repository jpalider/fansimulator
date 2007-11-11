package fan;

import java.util.HashMap;
import java.util.Map;

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
		return generator.nextDouble() * (range);
	}
	
	public double getGaussianNumber(double mean, double variance) {
		return (generator.nextGaussian() * Math.sqrt(variance) + mean);
	}
	
}
/*
 
GENERATION CHECK

public class Testing {

	public static void main(String[] args) {
		
		java.util.Random generator = new java.util.Random();
		int k = 0;
		int range = 19+1;
		
		Map<Integer,Integer> map = new HashMap<Integer,Integer>();
		for (int i = 0; i < 1000000; i++) {
			k = (int)((generator.nextDouble()) * range);
			if (map.containsKey(k)) {
				map.put(k,map.get(k)+1);
			} else {
				map.put(k,1);
			}
		}
		int sum=0;
		for (int i = 0; i < map.size(); i++) {
			if (map.get(i)!=null)
				sum += map.get(i);
			System.out.println(i + " = " + (map.get(i)));
		}
		System.out.println("Sum = " + sum);
	}

}
*/