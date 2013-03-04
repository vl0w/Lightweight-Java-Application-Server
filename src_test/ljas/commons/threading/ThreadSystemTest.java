package ljas.commons.threading;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadSystemTest {

	private ThreadSystem threadSystem;

	@Before
	public void setUpThreadSystem() {
		threadSystem = new ThreadSystem(0);
	}

	@After
	public void tearDownThreadSystem() {
		threadSystem.killAll();
	}

	@Test
	public void testRegisterThread() {
		RepetitiveThread thread = mock(RepetitiveThread.class);

		threadSystem.registerThread(thread);

		assertEquals(1, threadSystem.getThreads().size());
		assertEquals(thread, threadSystem.getThreads().toArray()[0]);
	}

	@Test
	public void testUnregisterThread() {
		RepetitiveThread thread = mock(RepetitiveThread.class);

		threadSystem.registerThread(thread);
		threadSystem.unregisterThread(thread);

		verify(thread).kill();
		assertEquals(0, threadSystem.getThreads().size());
	}

	@Test
	public void testPauseAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.pauseAll();

		verify(thread1).pause();
		verify(thread2).pause();
	}

	@Test
	public void testKillAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.killAll();

		verify(thread1).kill();
		verify(thread2).kill();
	}

	@Test
	public void testGoAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.goAll();

		verify(thread1).go();
		verify(thread2).go();
	}
}
