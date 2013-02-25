package ljas.testing.tasks;

import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.status.AbstractTaskState;
import ljas.commons.tasking.status.StateFactory;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.impl.FinishedState;
import ljas.commons.tasking.status.navigator.RemoteNavigator;
import ljas.commons.tasking.status.navigator.TaskNavigator;

public class SleepTask extends Task {
	private static final long serialVersionUID = -7775085616940551405L;

	private transient Session remoteSession;
	private final long time;

	public SleepTask(Session remoteSession, long time) {
		super(new StatusFactoryImpl());
		this.remoteSession = remoteSession;
		this.time = time;
	}

	private static class StatusFactoryImpl implements StateFactory {

		@Override
		public TaskState nextStatus(Task task, TaskState currentStatus) {
			if (currentStatus == null) {
				return ((SleepTask) task).new SleepStatus(task);
			} else {
				return new FinishedState(task);
			}
		}
	}

	private class SleepStatus extends AbstractTaskState {

		public SleepStatus(Task task) {
			super(task);
		}

		@Override
		public void execute() throws TaskException {
			try {
				Thread.currentThread();
				Thread.sleep(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public TaskNavigator getNavigator() {
			return new RemoteNavigator(remoteSession);
		}

	}

}
