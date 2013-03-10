package ljas.functional.tests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import ljas.client.Client;
import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.step.AbstractTaskStep;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.functional.ServerTestCase;
import ljas.functional.tasks.MultiStepTask;

import org.junit.Before;
import org.junit.Test;

public class MultiStepTaskTest extends ServerTestCase {

	public static AtomicInteger counter;

	@Before
	public void resetCounter() {
		counter = new AtomicInteger();
	}

	@Test(timeout = 5000)
	public void testClientToServer() throws Exception {
		final int howManyTimes = 10;

		Client client = createAndConnectClient();
		MultiStepTask initialTask = new MultiStepTask(
				client.getServerSession(), howManyTimes);
		client.runTaskSync(initialTask);
		assertEquals(howManyTimes, MultiStepTask.counter.get());
	}

	private static class IncrementCounterStep extends AbstractTaskStep {

		private static final long serialVersionUID = 3894635864810244602L;

		@Override
		public void execute(ExecutingContext context) throws TaskException {
			counter.incrementAndGet();
		}
	}
}
