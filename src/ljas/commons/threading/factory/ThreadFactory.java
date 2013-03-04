package ljas.commons.threading.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

public class ThreadFactory {

	private ThreadSystem threadSystem;
	private List<TaskExecutorThread> taskExecutors;

	public ThreadFactory(ThreadSystem threadSystem) {
		this.threadSystem = threadSystem;
		this.taskExecutors = new ArrayList<>();
	}

	public TaskExecutorThread createTaskThread(TaskSystem taskSystem) {
		cleanCacheByKilledThreads();
		addMissingThreadsToCache(taskSystem);
		updateTaskExecutorsByMonitoring(taskSystem.getTaskMonitor());
		return taskExecutors.get(0);
	}

	public BackgroundThread createBackgroundThread(Runnable runnable) {
		BackgroundThread thread = new BackgroundThread(threadSystem, runnable);
		thread.start();
		return thread;
	}

	public void updateTaskExecutorsByMonitoring(TaskMonitor monitor) {
		ThreadRankingComparator comparator = new ThreadRankingComparator(
				monitor);
		Collections.sort(taskExecutors, comparator);
	}

	private void cleanCacheByKilledThreads() {
		List<TaskExecutorThread> threadsToDelete = new ArrayList<>();
		for (TaskExecutorThread thread : taskExecutors) {
			if (thread.isKilled()) {
				threadsToDelete.add(thread);
			}
		}
		taskExecutors.removeAll(threadsToDelete);
	}

	private void addMissingThreadsToCache(TaskSystem taskSystem) {
		int maximumThreads = threadSystem.getMaximumTaskWorkers();
		int currentThreads = taskExecutors.size();
		for (int i = currentThreads; i < maximumThreads; i++) {
			TaskExecutorThread thread = new TaskExecutorThread(threadSystem,
					taskSystem);
			taskExecutors.add(thread);
		}
	}
}
