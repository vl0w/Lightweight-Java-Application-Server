package ljas.commons.threading.factory;

import java.util.Comparator;

import ljas.commons.tasking.monitoring.TaskMonitor;
import ljas.commons.threading.TaskExecutorThread;

public class ThreadRankingComparator implements Comparator<TaskExecutorThread> {

	private TaskMonitor taskMonitor;

	public ThreadRankingComparator(TaskMonitor taskMonitor) {
		this.taskMonitor = taskMonitor;
	}

	@Override
	public int compare(TaskExecutorThread o1, TaskExecutorThread o2) {
		long estimatedExecutionTimeThread1 = taskMonitor
				.getEstimatedExecutionTime(o1);
		long estimatedExecutionTimeThread2 = taskMonitor
				.getEstimatedExecutionTime(o2);
		return (int) (estimatedExecutionTimeThread1 - estimatedExecutionTimeThread2);
	}

}
