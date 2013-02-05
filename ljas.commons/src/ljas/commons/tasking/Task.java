package ljas.commons.tasking;

import java.io.Serializable;
import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.TaskSender;
import ljas.commons.tasking.observation.TaskObserver;
import ljas.commons.tasking.observation.TaskObserverFactory;
import ljas.commons.tasking.taskqueue.TaskController;

/**
 * 
 * @author Jonas Hansen
 * 
 */
public abstract class Task implements Serializable {
	private static final long serialVersionUID = -8887389539021809582L;

	public static String MSG_SAFETY_CONCERN = "Task not executed due to safety concerns (Wrong Application ID)";

	private long _id;
	private ConnectionInfo _sender;
	private long _applicationId;
	private final int _maximumLifeTimeSeconds;
	private final int _minimumLifeTimeSeconds;
	private TaskSender _local;
	private TaskState _state;
	private final long _creationTimeMS;
	private TaskResult _result;
	private String _resultMessage;

	public TaskSender getLocal() {
		return _local;
	}

	public void setLocal(TaskSender value) {
		_local = value;
	}

	public TaskState getState() {
		return _state;
	}

	public void setState(TaskState value) {
		_state = value;
	}

	public boolean isFinished() {
		return getState() == TaskState.FINISHED;
	}

	public boolean isExpired() {
		long now = System.currentTimeMillis();
		long lifetime = now - _creationTimeMS;
		if (lifetime >= (getMaximumLifetime() * 1000)) {
			return true;
		}
		return false;
	}

	public boolean isMinimumLifeTimeReached() {
		long now = System.currentTimeMillis();
		long lifetime = now - _creationTimeMS;
		if (lifetime >= (getMinimumLifetime() * 1000)) {
			return true;
		}
		return false;
	}

	public int getMaximumLifetime() {
		return _maximumLifeTimeSeconds;
	}

	public int getMinimumLifetime() {
		return _minimumLifeTimeSeconds;
	}

	public void setResult(TaskResult result) {
		_result = result;
	}

	public TaskResult getResult() {
		return _result;
	}

	public boolean hasSuccessed() {
		if (getResult() == TaskResult.SUCCESSFUL) {
			return true;
		}
		return false;
	}

	public boolean hasWarnings() {
		if (getResult() == TaskResult.WARNING) {
			return true;
		}
		return false;
	}

	public boolean hasFailed() {
		if (getResult() == TaskResult.ERROR) {
			return true;
		}
		return false;
	}

	public String getResultMessage() {
		return _resultMessage;
	}

	public void setResultMessage(String value) {
		_resultMessage = value;
	}

	public long getId() {
		return _id;
	}

	public void setId(long taskId) {
		_id = taskId;
	}

	public ConnectionInfo getSenderInfo() {
		return _sender;
	}

	public void setSenderInfo(ConnectionInfo info) {
		if (getSenderInfo() == null) {
			_sender = info;
		}
	}

	public long getApplicationId() {
		return _applicationId;
	}

	public void setApplicationId(long id) {
		if (getApplicationId() == 0) {
			_applicationId = id;
		}
	}

	public Task(int maximumLifetimeSeconds, int minimumLifeTimeSeconds) {
		setId(TaskController.createTaskId());
		setApplicationId(0);
		setSenderInfo(null);
		_state = TaskState.NEW;
		_creationTimeMS = System.currentTimeMillis();
		_result = TaskResult.NONE;
		_resultMessage = new String();
		_maximumLifeTimeSeconds = maximumLifetimeSeconds;
		_minimumLifeTimeSeconds = minimumLifeTimeSeconds;
	}

	public Task() {
		this(120, 10);
	}

	public void perform() {
		try {
			// Perform the task
			performTask();
		} catch (TaskException e) {
			setResult(TaskResult.ERROR);
			setResultMessage(e.getMessage());
		}
	}

	public abstract void performTask() throws TaskException;

	public void postPerform() throws Exception {

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
		int h = (int) _id;
		return h;
	}

	@Override
	public String toString() {
		return "Task [" + getId() + "," + getSenderInfo() + ","
				+ getClass().getSimpleName() + "]";
	}

	public void addObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().putObserver(this, observer);
	}

	public void removeObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().removeObserver(this, observer);
	}

	public void notifyAllExecuted() {
		// Notify observers
		List<TaskObserver> observers = TaskObserverFactory.getInstance()
				.getTaskObservers(this);
		for (TaskObserver observer : observers) {
			if (hasFailed()) {
				observer.notifyFail(this);
			} else {
				observer.notifySuccess(this);
			}
			observer.notifyExecuted(this);
		}
	}
}
