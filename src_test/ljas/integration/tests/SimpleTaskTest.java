package ljas.integration.tests;

import static org.junit.Assert.assertEquals;
import ljas.client.Client;
import ljas.integration.ServerTestCase;
import ljas.integration.tasks.AdditionTask;
import ljas.tasking.observation.NullTaskObserver;
import ljas.testing.IntegrationTest;
import ljas.threading.ThreadBlocker;

import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
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
