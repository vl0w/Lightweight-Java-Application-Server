package ljas.testing.tests;

import static org.junit.Assert.assertEquals;
import ljas.commons.client.Client;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.MultiStateTask;

import org.junit.Test;

public class MultiStateTaskTest extends ServerTestCase {

	@Test
	public void testClientToServer() throws Exception {
		final int howManyTimes = 10;

		Client client = createAndConnectClient();
		MultiStateTask initialTask = new MultiStateTask(
				client.getServerSession(), howManyTimes);
		client.runTaskSync(initialTask);
		assertEquals(howManyTimes, MultiStateTask.counter.get());
	}

}
