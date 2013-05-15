package ljas.threading;

import ljas.exception.RequestTimedOutException;

/**
 * Blocks the current thread until:
 * <ul>
 * <li>The {@link ThreadBlocker#release(Object)} function has been called</li>
 * <li>The given time has been passed</li>
 * </ul>
 * 
 * @author jonashansen
 * 
 * @param <V>
 */
public class ThreadBlocker<V> {
	private long expirationTimeInMS;
	private boolean isReleased;
	private Exception exception;
	private V returnValue;

	public ThreadBlocker(long expirationTimeInMS) {
		this.expirationTimeInMS = expirationTimeInMS;
		this.isReleased = false;
		this.exception = null;
		this.returnValue = null;
	}

	public void setExpiration(int expirationTimeInMS) {
		this.expirationTimeInMS = expirationTimeInMS;
	}

	/**
	 * Blocks the current thread
	 * 
	 * @return The object passed to the {@link ThreadBlocker#release(Object)}
	 *         function
	 * @throws Throwable
	 *             When the {@link ThreadBlocker#release(Throwable)} function
	 *             has been called
	 * @throws RequestTimedOutException
	 *             When the given time has passed
	 * @see {@link ThreadBlocker#release(Object)}
	 * @see {@link ThreadBlocker#release(Throwable)}
	 */
	public V block() throws Exception, RequestTimedOutException {
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
				exception = e;
			}
		}

		// The blocker has been released
		if (exception != null) {
			throw exception;
		}
		return returnValue;
	}

	/**
	 * Release by returning a value
	 * 
	 * @param returnValue
	 */
	public void release(V returnValue) {
		isReleased = true;
		this.returnValue = returnValue;
	}

	/**
	 * Release by throwing an exception
	 * 
	 * @param t
	 */
	public void release(Exception t) {
		isReleased = true;
		exception = t;
	}
}
