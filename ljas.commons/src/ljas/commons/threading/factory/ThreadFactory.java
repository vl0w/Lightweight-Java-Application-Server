package ljas.commons.threading.factory;

import java.util.Collections;
import java.util.List;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.threading.ThreadSystem;

public class ThreadFactory {

	private ThreadSystem threadSystem;

	public ThreadFactory(ThreadSystem threadSystem) {
		this.threadSystem = threadSystem;
	}

	public TaskExecutorThread createTaskThread(Task task, TaskSystem taskSystem) {
		TaskMonitor taskMonitor = taskSystem.getTaskMonitor();
		List<TaskExecutorThread> threads = threadSystem
				.getThreads(TaskExecutorThread.class);

		int threadCount = threads.size();
		int maxTaskWorkerCount = threadSystem.getMaximumTaskWorkers();

		// First check: Create initial taskworker if there has none been before.
		if (threadCount == 0) {
			return new TaskExecutorThread(threadSystem, taskSystem);
		}

		if (!taskMonitor.hasStatistics(task)
				&& threadCount < maxTaskWorkerCount) {
			return new TaskExecutorThread(threadSystem, taskSystem);
		}

		// Step 2: Sort ranking list
		Collections.sort(threads, new ThreadRankingComparator(taskMonitor));

		// Step 3: Average execution time of task
		long averageTimeForTask = taskMonitor.getAverageTaskTime(task);

		// Step 4: Decide
		TaskExecutorThread laziestTaskWorker = threads.get(0);
		long laziestWorkersExecutionTime = taskMonitor
				.getEstimatedExecutionTime(laziestTaskWorker);

		if (averageTimeForTask > laziestWorkersExecutionTime
				|| threadCount >= maxTaskWorkerCount) {
			return laziestTaskWorker;
		} else {
			return new TaskExecutorThread(threadSystem, taskSystem);
		}
	}

	public BackgroundThread createBackgroundThread(Runnable runnable) {
		BackgroundThread thread = new BackgroundThread(threadSystem, runnable);
		thread.start();
		return thread;
	}
}
