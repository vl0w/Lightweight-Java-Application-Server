package ljas.commons.tasking.environment;

import java.util.HashMap;
import java.util.Map;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

import org.apache.log4j.Logger;

public class TaskSystemImpl implements TaskSystem {
	private TaskMonitor taskMonitor;
	private ThreadSystem threadSystem;
	private Map<Task, Session> taskSenderCache;

	public TaskSystemImpl(ThreadSystem threadSystem) {
		this(threadSystem, new TaskMonitor());
	}

	public TaskSystemImpl(ThreadSystem threadSystem, TaskMonitor taskMonitor) {
		this.threadSystem = threadSystem;
		this.taskMonitor = taskMonitor;
		this.taskSenderCache = new HashMap<>();
	}

	@Override
	public TaskMonitor getTaskMonitor() {
		return taskMonitor;
	}

	@Override
	public Map<Task, Session> getSenderCache() {
		return taskSenderCache;
	}

	@Override
	public void scheduleTask(Task task) {
		Logger.getLogger(getClass()).debug("Scheduled task '" + task + "'");

		TaskExecutorThread thread;
		do {
			thread = threadSystem.getThreadFactory().createTaskThread(this);
		} while (!thread.scheduleTask(task));
	}

	@Override
	public void scheduleTask(Task task, Session senderSession) {
		getSenderCache().put(task, senderSession);
		scheduleTask(task);
	}
}
