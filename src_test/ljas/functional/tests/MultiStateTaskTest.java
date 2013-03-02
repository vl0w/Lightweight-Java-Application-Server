package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import ljas.client.Client;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.MultiStepTask;

import org.junit.Test;

public class MultiStateTaskTest extends ServerTestCase {

	@Test(timeout = 5000)
	public void testClientToServer() throws Exception {
		final int howManyTimes = 10;

		Client client = createAndConnectClient();
		MultiStepTask initialTask = new MultiStepTask(
				client.getServerSession(), howManyTimes);
		client.runTaskSync(initialTask);
		assertEquals(howManyTimes, MultiStepTask.counter.get());
	}

}
