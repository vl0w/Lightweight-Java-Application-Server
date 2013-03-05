package ljas.commons.tasking.monitoring;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ljas.commons.tasking.Task;
import ljas.commons.threading.TaskExecutorThread;
import ljas.commons.tools.Average;
import ljas.commons.tools.QueueUtils;

public class TaskMonitor {
	private Map<Class<? extends Task>, Average> executionTimeMap;

	public TaskMonitor() {
		executionTimeMap = new ConcurrentHashMap<Class<? extends Task>, Average>();
	}

	public void monitorTaskTime(Task task, long millis) {
		Class<? extends Task> clazz = task.getClass();

		if (!executionTimeMap.containsKey(clazz)) {
			executionTimeMap.put(clazz, new Average());
		}
		Average average = executionTimeMap.get(clazz);
		average.add(millis);
	}

	public long getAverageTaskTime(Task task) {
		Class<? extends Task> clazz = task.getClass();
		if (executionTimeMap.containsKey(clazz)) {
			Average average = executionTimeMap.get(clazz);
			return average.asLong();
		} else {
			return 0;
		}
	}

	public long getEstimatedExecutionTime(TaskExecutorThread thread) {
		List<Task> tasks = QueueUtils.toList(thread.getTaskQueue());
		return getEstimatedExecutionTime(tasks);
	}

	public long getEstimatedExecutionTime(List<Task> tasks) {
		long estimatedTime = 0;

		// Loop over every task
		for (Task task : tasks) {
			long averageExecutionTime = getAverageTaskTime(task);
			estimatedTime += averageExecutionTime;
		}

		return estimatedTime;
	}
}
