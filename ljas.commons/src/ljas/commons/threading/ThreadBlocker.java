package ljas.commons.threading;

import ljas.commons.exceptions.RequestTimedOutException;

public class ThreadBlocker<V> {
	private long expirationTimeInMS;
	private boolean isReleased;
	private Throwable throwable;
	private V returnValue;

	public ThreadBlocker(long expirationTimeInMS) {
		this.expirationTimeInMS = expirationTimeInMS;
		isReleased = false;
		throwable = null;
		returnValue = null;
	}

	public void setExpiration(int expirationTimeInMS) {
		this.expirationTimeInMS = expirationTimeInMS;
	}

	public V block() throws Throwable {
		isReleased = false;
		int timeCounter = 0;
		while (!isReleased) {
			try {
				Thread.currentThread();
				Thread.sleep(50);
				timeCounter += 50;

				if (expirationTimeInMS > 0 && timeCounter >= expirationTimeInMS) {
					throw new RequestTimedOutException();
				}
			} catch (InterruptedException e) {
				// nothing
			}
		}

		// The blocker has been released
		if (throwable != null) {
			throw throwable;
		}
		return returnValue;
	}

	public void release(V returnValue) {
		isReleased = true;
		this.returnValue = returnValue;
	}

	/**
	 * Release the blocker by throwing an exception
	 * 
	 * @param t
	 */
	public void release(Throwable t) {
		isReleased = true;
		throwable = t;
	}
}
