package ljas.commons.tasking.taskqueue;

import ljas.commons.network.TaskSender;

public class TaskQueueConfiguration {
	private TaskSender _local;
	private int _maximumTasks;
	private int _taskWorkers;
	private int _socketWorkers;
	
	public TaskQueueConfiguration(TaskSender local, int taskWorkers,
			int socketWorkers) {
		this(local,taskWorkers,socketWorkers,1000);
	}
	
	public TaskQueueConfiguration(TaskSender local, int taskWorkers,
			int socketWorkers, int maximumTasks) {
		super();
		setLocal(local);
		setTaskWorkers(taskWorkers);
		setSocketWorkers(socketWorkers);
		setMaximumTasks(maximumTasks);
	}

	public TaskSender getLocal() {
		return _local;
	}

	private void setLocal(TaskSender local) {
		_local = local;
	}

	public int getMaximumTasks() {
		return _maximumTasks;
	}

	private void setMaximumTasks(int maximumTasks) {
		_maximumTasks = maximumTasks;
	}

	public int getTaskWorkers() {
		return _taskWorkers;
	}

	private void setTaskWorkers(int taskWorkers) {
		_taskWorkers = taskWorkers;
	}

	public int getSocketWorkers() {
		return _socketWorkers;
	}

	private void setSocketWorkers(int socketWorkers) {
		_socketWorkers = socketWorkers;
	}
	
	
}
