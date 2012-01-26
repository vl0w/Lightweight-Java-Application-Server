package ljas.commons.tools;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Automatically sets its value back to a default after x seconds
 *
 * @author jonashansen
 *
 */
@Deprecated
public class AutoVariable<V> extends Thread {
	private V _defaultValue;
	private V _value;
	private int _milliSeconds;
	private AtomicInteger _counter;
	private int _accessCount;

	public synchronized void setValue(V value) {
		_accessCount = _counter.getAndSet(0) + 1;
		_value = value;
	}

	public synchronized int getAccessCount() {
		return _accessCount;
	}

	public synchronized V getValue() {
		_counter.incrementAndGet();
		return _value;
	}

	public AutoVariable(V defaultValue, int milliSeconds) {
		_defaultValue = defaultValue;
		_value = defaultValue;
		_milliSeconds = milliSeconds;
		_counter = new AtomicInteger(0);
		_accessCount = 0;
		setName("AutoVariable");
		start();
	}

	@Override
	public void run() {
		while (!isInterrupted()) {
			try {
				sleep(_milliSeconds);
				setValue(_defaultValue);
			} catch (InterruptedException e) {
				// nothing
			}
		}
	}
}
