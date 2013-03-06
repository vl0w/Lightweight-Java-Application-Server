package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ljas.client.Client;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.observation.NullTaskObserver;
import ljas.commons.threading.ThreadBlocker;
import ljas.functional.ServerManager;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.AdditionTask;

import org.junit.Test;

public class SimpleTaskTest extends ServerTestCase {

	@Test(timeout = 5000)
	public void testSyncTask() throws Exception {
		Client client = createAndConnectClient();

		double v1 = Math.random();
		double v2 = Math.random();
		double expectedValue = v1 + v2;
		AdditionTask additionTask = new AdditionTask(client, v1, v2);
		double sum = ((AdditionTask) client.runTaskSync(additionTask)).sum;

		assertEquals(expectedValue, sum, 0.001);

		// Ensure that the task has been executed on the server
		assertTrue(
				"The task was expected to be executed on the server. As the TaskMonitor of the server does not have any statistics of the task, it might not have been there for some reason.",
				ServerManager.getServer().getTaskSystem().getTaskMonitor()
						.getAverageTaskTime(additionTask) != 0);
	}

	@Test(timeout = 5000)
	public void testAsyncTask() throws Throwable {
		final ThreadBlocker<Double> blocker = new ThreadBlocker<>(5000);

		Client client = createAndConnectClient();

		double v1 = Math.random();
		double v2 = Math.random();
		double expectedValue = v1 + v2;

		AdditionTask task = new AdditionTask(client, v1, v2);
		task.addObserver(new NullTaskObserver() {
			@Override
			public void notifyExecuted(Task task) {
				double sum = ((AdditionTask) task).sum;
				blocker.release(new Double(sum));
			}
		});

		client.runTaskAsync(task);

		double result = blocker.block();

		assertEquals(expectedValue, result, 0.001);
	}
}
