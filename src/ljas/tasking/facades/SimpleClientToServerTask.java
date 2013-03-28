package ljas.tasking.facades;

import ljas.client.Client;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.flow.TaskFlow;
import ljas.tasking.flow.TaskFlowBuilder;
import ljas.tasking.step.AbstractTaskStep;
import ljas.tasking.step.ExecutingContext;

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
		this.serverSession = serverSession;
	}

	public SimpleClientToServerTask(Client client) {
		this(client.getServerSession());
	}

	/**
	 * This method will be performed on the server
	 */
	public abstract void perform(ExecutingContext context) throws TaskException;

	private class PerformStep extends AbstractTaskStep {
		private static final long serialVersionUID = 4590685021274585618L;

		@Override
		public void execute(ExecutingContext context) throws TaskException {
			perform(context);
		}

	}

	@Override
	protected final TaskFlow buildTaskFlow() {
		TaskFlowBuilder builder = new TaskFlowBuilder(this);
		builder.navigateRemote(serverSession).perform(new PerformStep())
				.sendBack().finishTask();
		return builder.build();
	}
}
