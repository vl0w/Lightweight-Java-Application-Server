package ljas.commons.threading;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BackgroundThreadTest {

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
	public void testRunCycle_TaskPerformed() throws Exception {
		// Mocking & Stubbing
		Runnable task = mock(Runnable.class);

		// Run
		createBackgroundThread(task).runCycle();

		// Verifications
		verify(task).run();
	}

	private BackgroundThread createBackgroundThread(Runnable runnable) {
		return new BackgroundThread(threadSystem, runnable);
	}
}
