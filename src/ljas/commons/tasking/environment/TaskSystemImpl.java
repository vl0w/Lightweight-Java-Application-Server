package ljas.commons.tasking.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ljas.commons.application.Application;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskRunnable;
import ljas.commons.tasking.executors.TaskThreadFactory;

import org.apache.log4j.Logger;

public class TaskSystemImpl implements TaskSystem {
	private Application application;
	private Map<Task, Session> taskSenderCache;
	private ExecutorService executorService;

	public TaskSystemImpl(Application application) {
		this.application = application;
		this.taskSenderCache = new HashMap<>();
		this.executorService = Executors
				.newCachedThreadPool(new TaskThreadFactory());
	}

	public TaskSystemImpl(Application application,
			ExecutorService executorService) {
		this.application = application;
		this.taskSenderCache = new HashMap<>();
		this.executorService = executorService;
	}

	@Override
	public Map<Task, Session> getSenderCache() {
		return taskSenderCache;
	}

	@Override
	public Application getApplication() {
		return application;
	}

	@Override
	public void scheduleTask(Task task) {
		Logger.getLogger(getClass()).debug("Scheduled task '" + task + "'");
		TaskRunnable taskRunnable = new TaskRunnable(task, this);
		executorService.submit(taskRunnable);
	}

	@Override
	public void scheduleTask(Task task, Session session) {
		getSenderCache().put(task, session);
		scheduleTask(task);
	}

	@Override
	public void shutdown() {
		executorService.shutdown();
		try {
			executorService.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.getLogger(getClass()).warn(e);
		}
	}
}
