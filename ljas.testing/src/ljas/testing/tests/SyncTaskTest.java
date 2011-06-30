package ljas.testing.tests;

import junit.framework.TestCase;
import ljas.commons.application.client.ClientApplicationException;
import ljas.commons.client.Client;
import ljas.commons.tasking.task.Task;
import ljas.testing.commons.tasks.AdditionTask;
import ljas.testing.commons.tasks.HaxxorTask;

public class SyncTaskTest extends TestCase {
	public void testSyncTask() {
		Client client = Constants.createClient();

		try {
			Constants.doConnect(client);

			double v1 = Math.random(), v2 = Math.random(), expectedValue = v1
					+ v2;
			double result = ((AdditionTask) client
					.runTaskSync(new AdditionTask(v1, v2))).result;

			assertEquals(expectedValue, result);

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			client.disconnect();
		}
	}

	public void testFakedTask() {
		Client client = Constants.createClient();

		try {
			Constants.doConnect(client);

			client.runTaskSync(new HaxxorTask(client.getLocalConnectionInfo()));

			fail("Task should not be ok");

		} catch (ClientApplicationException e) {
			assertEquals(Task.MSG_SAFETY_CONCERN, e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			client.disconnect();
		}
	}
}