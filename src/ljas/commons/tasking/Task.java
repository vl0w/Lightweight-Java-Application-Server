package ljas.commons.tasking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ljas.commons.tasking.flow.TaskFlow;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverManager;
import ljas.commons.tasking.step.TaskStep;

public abstract class Task implements Serializable {
	private static int TASK_INSTANCE_COUNTER = 0;
	private static final long serialVersionUID = -8887389539021809582L;

	private long id;
	private TaskFlow taskFlow;
	private List<TaskStep> stepHistory;

	public Task() {
		id = ++TASK_INSTANCE_COUNTER;
		this.stepHistory = new ArrayList<>();
	}

	public List<TaskStep> getStepHistory() {
		return stepHistory;
	}

	public long getId() {
		return id;
	}

	public TaskFlow getTaskFlow() {
		if (taskFlow == null) {
			taskFlow = buildTaskFlow();
		}
		return taskFlow;
	}

	public void addObserver(TaskObserver observer) {
		TaskObserverManager.getInstance().add(this, observer);
	}

	public void removeObserver(TaskObserver observer) {
		TaskObserverManager.getInstance().remove(this, observer);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task tObj = (Task) obj;
			if (getId() == tObj.getId())
				return true;
			return false;
		}
		return false;
	}

	@Override
	public int hashCode() {
		int h = (int) id;
		return h;
	}

	@Override
	public String toString() {
		return "Task [" + getClass().getSimpleName() + "]";
	}

	protected abstract TaskFlow buildTaskFlow();
}
