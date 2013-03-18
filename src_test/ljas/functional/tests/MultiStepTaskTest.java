package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ljas.client.Client;
import ljas.commons.application.client.ClientApplicationException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.flow.TaskFlow;
import ljas.commons.tasking.flow.TaskFlowBuilder;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.step.AbstractTaskStep;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.functional.ServerTestCase;

import org.junit.Before;
import org.junit.Test;

public class MultiStepTaskTest extends ServerTestCase implements Serializable {
	private static final long serialVersionUID = -2756653997232376462L;
	public static AtomicInteger counter;

	@Before
	public void resetCounter() {
		counter = new AtomicInteger();
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testClientToServerSeveralTimes() throws Exception {
		final int howManyTimes = 10;

		Client client = createAndConnectClient();

		InstantiableTask task = new InstantiableTask();
		TaskFlowBuilder builder = task.getBuilder();

		for (int i = 0; i < howManyTimes; i++) {
			builder.navigateRemote(client.getServerSession())
					.perform(new IncrementCounterStep()).sendBack();
		}

		client.runTaskSync(task);
		assertEquals(howManyTimes, counter.get());
	}

	@Test(timeout = TEST_TIMEOUT, expected = ClientApplicationException.class)
	public void testOneStepFailsOnSyncTaskExecution() throws Exception {
		Client client = createAndConnectClient();

		InstantiableTask task = new InstantiableTask();
		TaskFlowBuilder builder = task.getBuilder();

		builder.navigateRemote(client.getServerSession())
				.perform(new IncrementCounterStep()).sendBack();
		builder.navigateRemote(client.getServerSession())
				.perform(new IncrementCounterStep()).sendBack();
		builder.navigateRemote(client.getServerSession())
				.perform(new FailStep()).sendBack();

		client.runTaskSync(task);
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testOneStepFailsOnAsyncTaskExecution() throws Exception {
		Client client = createAndConnectClient();

		InstantiableTask task = new InstantiableTask();
		TaskFlowBuilder builder = task.getBuilder();

		builder.navigateRemote(client.getServerSession())
				.perform(new IncrementCounterStep()).sendBack();
		builder.navigateRemote(client.getServerSession())
				.perform(new IncrementCounterStep()).sendBack();
		builder.navigateRemote(client.getServerSession())
				.perform(new FailStep()).sendBack();
		builder.navigateRemote(client.getServerSession())
				.perform(new IncrementCounterStep()).sendBack();

		task.addObserver(new TaskObserver() {

			@Override
			public void notifyExecutedWithWarnings(Task task) {
				fail("Task is expected to be failed");
			}

			@Override
			public void notifyExecutedWithSuccess(Task task) {
				fail("Task is expected to be failed");
			}

			@Override
			public void notifyExecutedWithErrors(Task task,
					List<TaskException> exceptions) {
				assertEquals(3, counter.get());
			}

			@Override
			public void notifyExecuted(Task task) {
				assertEquals(3, counter.get());
			}
		});

		client.runTaskAsync(task);
	}

	private class InstantiableTask extends Task {
		private static final long serialVersionUID = -6833080915697155183L;

		private transient TaskFlowBuilder builder;

		public InstantiableTask() {
			builder = new TaskFlowBuilder(this);
		}

		public TaskFlowBuilder getBuilder() {
			return builder;
		}

		@Override
		protected TaskFlow buildTaskFlow() {
			return builder.build();
		}

	}

	private class IncrementCounterStep extends AbstractTaskStep {
		private static final long serialVersionUID = 3894635864810244602L;

		@Override
		public void execute(ExecutingContext context) throws TaskException {
			counter.incrementAndGet();
		}
	}

	private class FailStep extends AbstractTaskStep {
		private static final long serialVersionUID = 3894635864810244602L;

		@Override
		public void execute(ExecutingContext context) throws TaskException {
			throw new TaskException();
		}
	}

}
