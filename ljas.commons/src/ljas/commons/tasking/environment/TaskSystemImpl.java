package ljas.commons.tasking.environment;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskSenderCache;
import ljas.commons.tasking.environment.observation.NullTaskSystemObserver;
import ljas.commons.tasking.environment.observation.TaskSystemObserver;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

import org.apache.log4j.Logger;

public class TaskSystemImpl implements TaskSystem {
	public final int WORKER_DELAY = 5;
	private TaskMonitor taskMonitor;
	private ThreadSystem threadSystem;
	private TaskSenderCache taskSenderCache;
	private TaskSystemObserver observer;

	public TaskSystemImpl(ThreadSystem threadSystem, TaskMonitor taskMonitor) {
		this.threadSystem = threadSystem;
		this.taskMonitor = taskMonitor;
		this.taskSenderCache = new TaskSenderCache();
		this.observer = new NullTaskSystemObserver();
	}

	private Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	@Override
	public TaskMonitor getTaskMonitor() {
		return taskMonitor;
	}

	/**
	 * Adds the task to the queue. The task has to have the state DO_PERFORM or
	 * DO_CHECK!
	 * 
	 * @param task
	 *            The task to add to the queue
	 */
	@Override
	public void scheduleTask(Task task) {
		getLogger().debug("Scheduled task '" + task + "'");

		TaskExecutorThread thread;
		do {
			thread = threadSystem.getThreadFactory().createTaskThread(task,
					this);
		} while (!thread.scheduleTask(task));
	}

	@Override
	public void scheduleTask(Task task, Session senderSession) {
		getSenderCache().put(task, senderSession);
		scheduleTask(task);
	}

	@Override
	public TaskSenderCache getSenderCache() {
		return taskSenderCache;
	}

	@Override
	public void addBackgroundTask(Runnable runnable) {
		threadSystem.getThreadFactory().createBackgroundThread(runnable);
	}

	public boolean checkTask(Task task, Session session) throws Exception {
		// TODO
		return true;
	}

	@Override
	public TaskSystemObserver getTaskSystemObserver() {
		return observer;
	}

	@Override
	public void setTaskSystemObserver(TaskSystemObserver observer) {
		this.observer = observer;
	}
}
