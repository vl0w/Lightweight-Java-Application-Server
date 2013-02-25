package ljas.commons.tasking.facades;

import ljas.commons.client.Client;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.status.AbstractTaskState;
import ljas.commons.tasking.status.StateFactory;
import ljas.commons.tasking.status.TaskState;
import ljas.commons.tasking.status.impl.FinishedState;
import ljas.commons.tasking.status.navigator.RemoteNavigator;
import ljas.commons.tasking.status.navigator.TaskNavigator;

/**
 * A facade for the {@link Task} class. Concrete implementations can override
 * the {@link SimpleClientToServerTask#perform()} method which will be executed
 * on the server
 * 
 * @author jonashansen
 * 
 */
public abstract class SimpleClientToServerTask extends Task {
	private static final long serialVersionUID = 2662549275146497224L;
	private transient Session serverSession;

	private SimpleClientToServerTask(Session serverSession) {
		super(new StatusFactoryImpl());
		this.serverSession = serverSession;
	}

	public SimpleClientToServerTask(Client client) {
		this(client.getServerSession());
	}

	/**
	 * This method will be performed on the server
	 */
	public abstract void perform();

	private static class StatusFactoryImpl implements StateFactory {
		private static final long serialVersionUID = 1867428921769747086L;

		@Override
		public TaskState nextStatus(Task task, TaskState currentStatus) {
			if (currentStatus == null) {
				return ((SimpleClientToServerTask) task).new PerformStatus(task);
			} else {
				return new FinishedState(task);
			}
		}
	}

	private class PerformStatus extends AbstractTaskState {
		private static final long serialVersionUID = 4590685021274585618L;

		public PerformStatus(Task task) {
			super(task);
		}

		@Override
		public void execute() throws TaskException {
			perform();
		}

		@Override
		public TaskNavigator getNavigator() {
			return new RemoteNavigator(serverSession);
		}

	}

}
