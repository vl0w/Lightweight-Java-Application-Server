package ljas.testing.tests;

import static org.junit.Assert.assertEquals;
import ljas.commons.client.Client;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.MultiStepTask;

import org.junit.Test;

public class MultiStateTaskTest extends ServerTestCase {

	@Test(timeout = 1000)
	public void testClientToServer() throws Exception {
		final int howManyTimes = 10;

		Client client = createAndConnectClient();
		MultiStepTask initialTask = new MultiStepTask(
				client.getServerSession(), howManyTimes);
		client.runTaskSync(initialTask);
		assertEquals(howManyTimes, MultiStepTask.counter.get());
	}

}
