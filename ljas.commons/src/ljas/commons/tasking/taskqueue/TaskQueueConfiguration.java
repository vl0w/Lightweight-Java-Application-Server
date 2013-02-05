package ljas.commons.tasking.taskqueue;

import ljas.commons.network.TaskSender;

public class TaskQueueConfiguration {
	private TaskSender _local;
	private int _maxTaskWorkers;
	private int _socketWorkers;
	
	public TaskQueueConfiguration(TaskSender local, int taskWorkers,
			int socketWorkers) {
		super();
		setLocal(local);
		setMaxTaskWorkers(taskWorkers);
		setSocketWorkers(socketWorkers);
	}

	public TaskSender getLocal() {
		return _local;
	}

	private void setLocal(TaskSender local) {
		_local = local;
	}

	public int getMaxTaskWorkers() {
		return _maxTaskWorkers;
	}

	private void setMaxTaskWorkers(int taskWorkers) {
		_maxTaskWorkers = taskWorkers;
	}

	public int getSocketWorkers() {
		return _socketWorkers;
	}

	private void setSocketWorkers(int socketWorkers) {
		_socketWorkers = socketWorkers;
	}
	
	
}
