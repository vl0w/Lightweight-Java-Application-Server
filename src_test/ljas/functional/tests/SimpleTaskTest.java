package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import ljas.client.Client;
import ljas.commons.tasking.observation.NullTaskObserver;
import ljas.commons.threading.ThreadBlocker;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.AdditionTask;

import org.junit.Test;

public class SimpleTaskTest extends ServerTestCase {

	@Test(timeout = TEST_TIMEOUT)
	public void testSyncTask() throws Exception {
		Client client = createAndConnectClient();

		AdditionTask additionTask = new AdditionTask(client, 5, 10);
		int sum = client.runTaskSync(additionTask).sum;

		assertEquals(15, sum);
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testAsyncTask() throws Throwable {
		final ThreadBlocker<Integer> blocker = new ThreadBlocker<>(5000);

		Client client = createAndConnectClient();

		AdditionTask task = new AdditionTask(client, 5, 10);
		task.addObserver(new NullTaskObserver<AdditionTask>() {
			@Override
			public void notifyExecuted(AdditionTask task) {
				int sum = task.sum;
				blocker.release(new Integer(sum));
			}
		});

		client.runTaskAsync(task);

		int result = blocker.block();

		assertEquals(15, result);
	}
}
