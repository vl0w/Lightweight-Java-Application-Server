package ljas.commons.tasking.flow;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.step.TaskStep;
import ljas.commons.tasking.step.impl.FinishTaskStep;
import ljas.commons.tasking.step.impl.LocalNavigationStep;
import ljas.commons.tasking.step.impl.RemoteNavigationStep;
import ljas.commons.tasking.step.impl.SendBackToSenderStep;

/**
 * The {@link TaskFlowBuilder} is an easy way to design and create a
 * {@link TaskFlow}.
 * 
 * @author jonashansen
 * 
 */
public class TaskFlowBuilder {

	private Task task;
	private TaskFlowImpl flow;

	public TaskFlowBuilder(Task task) {
		this.task = task;
		this.flow = new TaskFlowImpl();
	}

	public TaskFlowBuilder perform(TaskStep step) {
		flow.addStep(step);
		return this;
	}

	public TaskFlowBuilder navigateLocal() {
		flow.addStep(new LocalNavigationStep(task));
		return this;
	}

	public TaskFlowBuilder navigateRemote(Session session) {
		flow.addStep(new RemoteNavigationStep(task, session));
		return this;
	}

	public TaskFlowBuilder sendBack() {
		flow.addStep(new SendBackToSenderStep(task));
		return this;
	}

	public TaskFlowBuilder finishTask() {
		flow.addStep(new FinishTaskStep(task));
		return this;
	}

	public TaskFlow build() {
		if (!flow.getSteps().get(flow.getSteps().size() - 1).getClass()
				.equals(FinishTaskStep.class)) {
			finishTask();
		}
		return flow;
	}
}
