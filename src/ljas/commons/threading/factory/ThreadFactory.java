package ljas.commons.threading.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ljas.commons.tasking.environment.TaskSystem;
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
		if (taskExecutors.size() == 0) {
			addTaskExecutorThreads(taskSystem);
		}
		updateTaskExecutorsByMonitoring();
		return taskExecutors.get(0);
	}

	public BackgroundThread createBackgroundThread(Runnable runnable) {
		BackgroundThread thread = new BackgroundThread(threadSystem, runnable);
		thread.start();
		return thread;
	}

	public void updateTaskExecutorsByMonitoring() {
		ThreadRankingComparator comparator = new ThreadRankingComparator(
				threadSystem.getTaskMonitor());
		Collections.sort(taskExecutors, comparator);
	}

	private void addTaskExecutorThreads(TaskSystem taskSystem) {
		int maximumThreads = threadSystem.getMaximumTaskWorkers();
		for (int i = 0; i < maximumThreads; i++) {
			TaskExecutorThread thread = new TaskExecutorThread(threadSystem,
					taskSystem);
			taskExecutors.add(thread);
		}
	}
}
