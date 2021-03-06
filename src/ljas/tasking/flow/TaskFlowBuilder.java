package ljas.tasking.flow;

import java.util.List;

import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.step.TaskStep;
import ljas.tasking.step.impl.FinishTaskStep;
import ljas.tasking.step.impl.LocalNavigationStep;
import ljas.tasking.step.impl.RemoteNavigationStep;
import ljas.tasking.step.impl.SendBackToSenderStep;

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
		List<TaskStep> steps = flow.getSteps();
		TaskStep lastStep = steps.get(steps.size() - 1);
		if (!(lastStep instanceof FinishTaskStep)) {
			finishTask();
		}
		return flow;
	}
}
