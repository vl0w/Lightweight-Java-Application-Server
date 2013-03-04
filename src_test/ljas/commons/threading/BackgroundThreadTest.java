package ljas.commons.threading;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class BackgroundThreadTest {

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
		ThreadSystem threadSystem = new ThreadSystem(0);
		return new BackgroundThread(threadSystem, runnable);
	}
}
