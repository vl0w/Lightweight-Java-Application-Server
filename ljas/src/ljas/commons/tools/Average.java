package ljas.commons.tools;


public class Average {
	private int counter;
	private double summedValue;
	
	public Average() {
		counter = 0;
		summedValue = 0;
	}
	
	public void add(double value) {
		summedValue+=value;
		counter++;
	}
	
	public int asInt() {
		return (int)getAverage();
	}
	
	public long asLong() {
		return (long)getAverage();
	}
	
	private double getAverage() {
		return summedValue / counter;
	}
}
