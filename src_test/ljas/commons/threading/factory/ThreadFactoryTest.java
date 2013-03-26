package ljas.commons.threading.factory;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.ThreadSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadFactoryTest {

	private ThreadSystem threadSystem;

	@Before
	public void setUpThreadSystem() {
		threadSystem = new ThreadSystem();
	}

	@After
	public void killThreads() {
		threadSystem.killAll();
	}

	@Test
	public void testCreateBackgroundThread() {
		ThreadFactory factory = new ThreadFactory(threadSystem);

		Runnable runnable = mock(Runnable.class);
		BackgroundThread thread = factory.createBackgroundThread(runnable);

		assertTrue(thread.isRunning());
	}
}
