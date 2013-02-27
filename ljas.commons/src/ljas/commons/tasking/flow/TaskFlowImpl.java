package ljas.commons.tasking.flow;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ljas.commons.tasking.step.TaskStep;
import ljas.commons.tools.QueueUtils;

public class TaskFlowImpl implements TaskFlow {
	private static final long serialVersionUID = -2170203817126055325L;

	private Queue<TaskStep> steps;
	private TaskStep lastPoppedStep;

	public TaskFlowImpl() {
		this.steps = new LinkedList<>();
	}

	@Override
	public TaskStep nextStep() {
		lastPoppedStep = steps.remove();
		return lastPoppedStep;
	}

	@Override
	public TaskStep currentStep() {
		return lastPoppedStep;
	}

	public void addStep(TaskStep step) {
		steps.add(step);
	}

	List<TaskStep> getSteps() {
		return QueueUtils.toList(steps);
	}

}
