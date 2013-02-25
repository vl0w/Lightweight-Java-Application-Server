package ljas.commons.threading;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tools.MapUtil;
import ljas.commons.tools.QueueUtils;

public class ThreadFactory {

	private ThreadSystem threadSystem;

	public ThreadFactory(ThreadSystem threadSystem) {
		this.threadSystem = threadSystem;
	}

	public TaskExecutorThread createTaskThread(Task task, TaskSystem taskSystem) {

		List<TaskExecutorThread> threads = threadSystem
				.getThreads(TaskExecutorThread.class);

		int threadCount = threads.size();
		int maxTaskWorkerCount = threadSystem.getMaximumTaskWorkers();

		// First check: Create initial taskworker if there has none been before.
		if (threadCount == 0) {
			return new TaskExecutorThread(threadSystem, taskSystem);
		}

		// Step 1: Make a ranking (workload) of all taskworkers
		Map<TaskExecutorThread, Long> rankingList = new HashMap<>();
		for (TaskExecutorThread thread : threads) {
			List<Task> taskList = QueueUtils.toList(thread.getTaskQueue());
			long estimatedExecutionTime = taskSystem.getTaskMonitor()
					.getEstimatedExecutionTime(taskList);
			rankingList.put(thread, new Long(estimatedExecutionTime));
		}

		// Step 2: Sort ranking list
		rankingList = MapUtil.sortByValue(rankingList);

		// Step 3: Average execution time of task
		long averageTimeForTask = taskSystem.getTaskMonitor()
				.getAverageTaskTime(task);

		// Step 4: Decide
		TaskExecutorThread laziestTaskWorker = rankingList.keySet().iterator()
				.next();
		long laziestWorkersExecutionTime = rankingList.get(laziestTaskWorker);

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
