package ljas.testing.tests;

import ljas.commons.application.client.ClientApplicationException;
import ljas.commons.client.Client;
import ljas.commons.tasking.task.Task;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.AdditionTask;
import ljas.testing.tasks.HaxxorTask;


public class SyncTaskTest extends ServerTestCase {
	public void testSyncTask() {
		try {
			Client client = createClient();

			double v1 = Math.random(), v2 = Math.random(), expectedValue = v1 + v2;
			double result = ((AdditionTask) client.runTaskSync(new AdditionTask(v1, v2))).result;

			assertEquals(expectedValue, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testFakedTask() {
		try {
			Client client = createClient();

			client.runTaskSync(new HaxxorTask(client.getLocalConnectionInfo()));

			fail("Task should not be ok");

		} catch (ClientApplicationException e) {
			assertEquals(Task.MSG_SAFETY_CONCERN, e.getMessage());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
