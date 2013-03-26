package ljas.commons.tasking.environment;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import ljas.commons.application.Application;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskRunnable;

import org.junit.Test;

public class TaskSystemImplTest {

	@Test
	public void testScheduleTask_NoSession_DelegateTaskToThreadSystem() {
		Task task = mock(Task.class);
		Application application = mock(Application.class);
		ExecutorService executorService = mock(ExecutorService.class);

		TaskSystem taskSystem = new TaskSystemImpl(application, executorService);
		taskSystem.scheduleTask(task);

		verify(executorService).submit(any(TaskRunnable.class));
	}

	@Test
	public void testScheduleTask_WithSession_PutSessionToSenderCache() {
		Session session = mock(Session.class);
		Task task = mock(Task.class);
		Application application = mock(Application.class);
		ExecutorService executorService = mock(ExecutorService.class);

		TaskSystem taskSystem = new TaskSystemImpl(application, executorService);
		taskSystem.scheduleTask(task, session);

		assertEquals(session, taskSystem.getSenderCache().remove(task));
		verify(executorService).submit(any(TaskRunnable.class));
	}

	@Test
	public void testShutdown_ShutdownExecutorService()
			throws InterruptedException {
		Application application = mock(Application.class);
		ExecutorService executorService = mock(ExecutorService.class);

		TaskSystem taskSystem = new TaskSystemImpl(application, executorService);
		taskSystem.shutdown();

		verify(executorService).shutdown();
		verify(executorService).awaitTermination(10, TimeUnit.SECONDS);
	}

}
