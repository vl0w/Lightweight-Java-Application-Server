package ljas.testing.tests;

import ljas.commons.client.Client;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.observation.NullTaskObserver;
import ljas.commons.threading.ThreadBlocker;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.AdditionTask;

import org.junit.Test;

public class SimpleTaskTest extends ServerTestCase {

	@Test
	public void testSyncTask() {
		try {
			Client client = createAndConnectClient();

			double v1 = Math.random();
			double v2 = Math.random();
			double expectedValue = v1 + v2;
			AdditionTask additionTask = new AdditionTask(client, v1, v2);
			double sum = ((AdditionTask) client.runTaskSync(additionTask)).sum;

			assertEquals(expectedValue, sum);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testAsyncTask() throws Throwable {
		final ThreadBlocker<Double> blocker = new ThreadBlocker<>(10000);

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

		assertEquals(expectedValue, result);
	}
}
