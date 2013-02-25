package ljas.testing.tasks;

import java.util.concurrent.atomic.AtomicInteger;

import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.status.AbstractTaskState;
import ljas.commons.tasking.status.StateFactory;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.impl.FinishedState;
import ljas.commons.tasking.status.navigator.RemoteNavigator;
import ljas.commons.tasking.status.navigator.TaskNavigator;

public class MultiStateTask extends Task {

	private static final long serialVersionUID = 4111344774062439818L;

	public static AtomicInteger counter;
	static transient Session targetSession;
	static int howManyTimes;

	public MultiStateTask(Session targetSession, int howManyTimes) {
		super(new StateFactoryImpl());
		MultiStateTask.counter = new AtomicInteger();
		MultiStateTask.targetSession = targetSession;
		MultiStateTask.howManyTimes = howManyTimes;
	}

	private static class StateFactoryImpl implements StateFactory {

		private static final long serialVersionUID = -9165830899090476770L;

		@Override
		public TaskState nextStatus(Task task, TaskState currentStatus) {
			if (counter.get() < MultiStateTask.howManyTimes) {
				return new IncrementCounterState(task);
			} else {
				return new FinishedState(task);
			}
		}

	}

	private static class IncrementCounterState extends AbstractTaskState {

		private static final long serialVersionUID = 3894635864810244602L;

		public IncrementCounterState(Task task) {
			super(task);
		}

		@Override
		public void execute() throws TaskException {
			counter.incrementAndGet();
		}

		@Override
		public TaskNavigator getNavigator() {
			return new RemoteNavigator(targetSession);
		}
	}
}
