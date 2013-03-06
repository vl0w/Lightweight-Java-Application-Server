package ljas.commons.tasking.facades;

import ljas.client.Client;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.flow.TaskFlow;
import ljas.commons.tasking.flow.TaskFlowBuilder;
import ljas.commons.tasking.step.AbstractTaskStep;
import ljas.commons.tasking.step.ExecutingContext;

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
	public abstract void perform(ExecutingContext context);

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
