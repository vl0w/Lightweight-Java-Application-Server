package ljas.testing.tasks;

import java.util.concurrent.atomic.AtomicInteger;

import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.flow.TaskFlow;
import ljas.commons.tasking.flow.TaskFlowBuilder;
import ljas.commons.tasking.step.AbstractTaskStep;

public class MultiStepTask extends Task {

	private static final long serialVersionUID = 4111344774062439818L;

	public static AtomicInteger counter;
	static transient Session targetSession;
	static int howManyTimes;

	public MultiStepTask(Session targetSession, int howManyTimes) {
		MultiStepTask.counter = new AtomicInteger();
		MultiStepTask.targetSession = targetSession;
		MultiStepTask.howManyTimes = howManyTimes;
	}

	private static class IncrementCounterStep extends AbstractTaskStep {

		private static final long serialVersionUID = 3894635864810244602L;

		@Override
		public void execute() throws TaskException {
			counter.incrementAndGet();
		}

	}

	@Override
	protected TaskFlow buildTaskFlow() {
		TaskFlowBuilder builder = new TaskFlowBuilder(this);
		for (int i = 0; i < howManyTimes; i++) {
			builder.navigateRemote(targetSession)
					.perform(new IncrementCounterStep()).sendBack();
		}
		return builder.build();
	}
}
