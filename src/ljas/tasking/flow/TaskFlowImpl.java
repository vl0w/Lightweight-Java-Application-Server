package ljas.tasking.flow;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ljas.tasking.step.TaskStep;
import ljas.tools.QueueUtils;

public class TaskFlowImpl implements TaskFlow {
	private static final long serialVersionUID = -2170203817126055325L;

	private Queue<TaskStep> steps;
	private TaskStep lastRemovedStep;

	public TaskFlowImpl() {
		this.steps = new LinkedList<>();
	}

	@Override
	public TaskStep nextStep() {
		lastRemovedStep = steps.remove();
		return lastRemovedStep;
	}

	@Override
	public TaskStep currentStep() {
		return lastRemovedStep;
	}

	public void addStep(TaskStep step) {
		steps.add(step);
	}

	List<TaskStep> getSteps() {
		return QueueUtils.toList(steps);
	}

}
