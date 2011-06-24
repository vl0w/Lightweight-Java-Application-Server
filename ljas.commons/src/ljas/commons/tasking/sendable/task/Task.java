package ljas.commons.tasking.sendable.task;

import java.io.Serializable;
import java.util.List;

import ljas.commons.exceptions.TaskException;
import ljas.commons.network.SendsTasks;
import ljas.commons.tasking.taskspool.TaskSpool;


/**
 * 
 * @author hnj
 * 
 */
public abstract class Task implements Serializable {
	private static final long serialVersionUID = -8887389539021809582L;
	
	// CONSTANTS
	public static String MSG_SYSTEM_OVERLOAD="System overloaded. Try again later";
	public static String MSG_SAFETY_CONCERN="Task not executed due to safety concerns (Wrong Application ID)";

	// MEMBERS
	private final int _maximumLifeTimeSeconds;
	private final int _minimumLifeTimeSeconds;
	private TaskHeader _header;
	private SendsTasks _local;
	private TaskState _state;
	private final long _creationTimeMS;
	private TaskResult _result;
	private String _resultMessage;

	// GETTERS & SETTERS

	public SendsTasks getLocal() {
		return _local;
	}

	public void setLocal(SendsTasks value) {
		_local = value;
	}

	public TaskHeader getHeader() {
		return _header;
	}

	public void setHeader(TaskHeader value) {
		if (getHeader() == null) {
			_header = value;
		}
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

//	public boolean hasData() {
//		if (getClass().getFields().length > 0) {
//			return true;
//		}
//		return false;
//	}

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

//	/**
//	 * Specifies whether the task can be deleted. <b>Returns true when one of
//	 * these conditions apply:</b> <li>Task is finished and has <u>no</u> data</li>
//	 * <li>Task is finished, has data and minimum lifetime has reached</li> <li>
//	 * Task is expired (maximum lifetime reached)</li>
//	 * 
//	 * @return True, when the task can be deleted, false otherwise
//	 */
//	public boolean canDelete() {
//		if (hasData()) {
//			if (isFinished() && isMinimumLifeTimeReached())
//				return true;
//		} else {
//			if (isFinished())
//				return true;
//		}
//
//		if (isExpired())
//			return true;
//
//		return false;
//	}

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

	// CONSTRUCTOR
	public Task(int maximumLifetimeSeconds, int minimumLifeTimeSeconds) {
		_header = new TaskHeader(TaskSpool.createTaskId());
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

	// METHODS
	@Override
	public boolean equals(Object obj) {
		return getHeader().equals(obj);
	}

	@Override
	public int hashCode() {
		return getHeader().hashCode();
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
	public String toString() {
		return "[" + getClass().getSimpleName() + "," + getHeader().getId()
				+ "," + getState() + "]";
	}

	public void addObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().putObserver(getHeader(), observer);
	}

	public void removeObserver(TaskObserver observer) {
		TaskObserverFactory.getInstance().removeObserver(getHeader(), observer);
	}

	public void notifyAllExecuted() {
		// Notify observers
		List<TaskObserver> observers = TaskObserverFactory.getInstance()
				.getTaskObservers(getHeader());
		for (TaskObserver observer : observers) {
			if(hasFailed()){
				observer.notifyFail(this);
			} else{
				observer.notifySuccess(this);
			}
			observer.notifyExecuted(this);
		}
		

		// Delete from Factory
		TaskObserverFactory.getInstance().clearObservers(getHeader());
	}
}
