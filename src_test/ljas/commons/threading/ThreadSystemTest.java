package ljas.commons.threading;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import ljas.commons.tasking.monitoring.TaskMonitor;

import org.junit.Test;

public class ThreadSystemTest {

	@Test
	public void testRegisterThread() {
		RepetitiveThread thread = mock(RepetitiveThread.class);

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		threadSystem.registerThread(thread);

		assertEquals(1, threadSystem.getThreads().size());
		assertEquals(thread, threadSystem.getThreads().toArray()[0]);
	}

	@Test
	public void testUnregisterThread() {
		RepetitiveThread thread = mock(RepetitiveThread.class);

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		threadSystem.registerThread(thread);
		threadSystem.unregisterThread(thread);

		assertEquals(0, threadSystem.getThreads().size());
	}

	@Test
	public void testPauseAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.pauseAll();

		verify(thread1).pause();
		verify(thread2).pause();
	}

	@Test
	public void testForceKillAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.forceKillAll();

		verify(thread1).forceKill();
		verify(thread2).forceKill();
	}

	@Test
	public void testKillAll() {
		RepetitiveThread thread1 = mock(RepetitiveThread.class);
		RepetitiveThread thread2 = mock(RepetitiveThread.class);

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
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

		ThreadSystem threadSystem = new ThreadSystem(new TaskMonitor(), 0);
		threadSystem.registerThread(thread1);
		threadSystem.registerThread(thread2);
		threadSystem.goAll();

		verify(thread1).go();
		verify(thread2).go();
	}
}
