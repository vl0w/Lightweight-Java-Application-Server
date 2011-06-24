package ljas.commons.tasking.sendable.task;

import java.io.Serializable;

import ljas.commons.network.ConnectionInfo;


public class TaskHeader implements Serializable {
	private static final long serialVersionUID = -7306376302583028569L;

	// MEMBERS
	private long _id;
	private ConnectionInfo _sender;
	private long _applicationId;

	// GETTERS & SETTERS
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

	// CONSTRUCTOR
	public TaskHeader(long id) {
		setId(id);
		setApplicationId(0);
		setSenderInfo(null);
	}

	// METHODS
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskHeader) {
			TaskHeader tObj = (TaskHeader) obj;
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
		return "TaskHeader [" + getId() + "," + getSenderInfo() + "]";
	}
}
