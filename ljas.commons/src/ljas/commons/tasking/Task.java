package ljas.commons.tasking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverFactory;
import ljas.commons.tasking.status.StateFactory;
import ljas.commons.tasking.status.TaskState;

/**
 * 
 * @author Jonas Hansen
 * 
 */
public abstract class Task implements Serializable {
	private static int TASK_INSTANCE_COUNTER = 0;
	private static final long serialVersionUID = -8887389539021809582L;

	public static String MSG_SAFETY_CONCERN = "Task not executed due to safety concerns (Wrong Application ID)";

	private long id;
	private TaskState currentStatus;
	private StateFactory statusFactory;
	private List<TaskState> statusHistory;
	/**
	 * TODO is this necessary?
	 */
	private String resultMessage;

	public TaskState getCurrentState() {
		return currentStatus;
	}

	public StateFactory getStateFactory() {
		return statusFactory;
	}

	public List<TaskState> getStateHistory() {
		return statusHistory;
	}

	public void setCurrentState(TaskState currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String value) {
		resultMessage = value;
	}

	public long getId() {
		return id;
	}

	public Task(StateFactory statusFactory) {
		id = ++TASK_INSTANCE_COUNTER;
		resultMessage = "";
		this.statusFactory = statusFactory;
		this.statusHistory = new ArrayList<>();
		this.currentStatus = null;
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

	public void addObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().putObserver(this, observer);
	}

	public void removeObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().removeObserver(this, observer);
	}
}
