package ljas.commons.threading;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import ljas.commons.exceptions.RequestTimedOutException;

import org.junit.Before;
import org.junit.Test;

public class ThreadBlockerTest {

	private ThreadBlocker<String> blocker;

	@Before
	public void initBlocker() {
		blocker = new ThreadBlocker<>(0);
	}

	@Test
	public void testBlock_ReleaseByReturningObject()
			throws RequestTimedOutException, Throwable {
		final String someString = "SomeString";
		blocker = new ThreadBlocker<>(0);
		new Thread(new DelayedBlockerReleaserByObject(someString)).start();
		assertEquals(someString, blocker.block());
	}

	@Test(expected = Exception.class)
	public void testBlock_ReleaseByThrowingManualException()
			throws RequestTimedOutException, Throwable {
		new Thread(new DelayedBlockerReleaserByException(new Exception(
				"SomeException"))).start();
		blocker.block();
	}

	@Test(timeout = 1000, expected = RequestTimedOutException.class)
	public void testBlock_DoNotRelease_RequestTimedOutExceptionOccurs()
			throws RequestTimedOutException, Throwable {
		blocker.setExpiration(500);
		blocker.block();
	}

	private class DelayedBlockerReleaserByObject implements Runnable {

		private String expectedString;

		public DelayedBlockerReleaserByObject(String expectedString) {
			this.expectedString = expectedString;
		}

		@Override
		public void run() {
			try {
				Thread.currentThread();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
			blocker.release(expectedString);
		}
	}

	private class DelayedBlockerReleaserByException implements Runnable {

		private Exception expectedException;

		public DelayedBlockerReleaserByException(Exception expectedException) {
			this.expectedException = expectedException;
		}

		@Override
		public void run() {
			try {
				Thread.currentThread();
				Thread.sleep(500);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
			blocker.release(expectedException);
		}
	}

}
