package ljas.commons.tools;


public class Average {
	private int _counter;
	private double _summedValue;
	
	public Average() {
		_counter = 0;
		_summedValue = 0;
	}
	
	public void add(double value) {
		_summedValue+=value;
		_counter++;
	}
	
	public int asInt() {
		return (int)getAverage();
	}
	
	public long asLong() {
		return (long)getAverage();
	}
	
	private double getAverage() {
		return _summedValue / _counter;
	}
}
