package ljas.commons.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ljas.commons.tasking.monitoring.TaskMonitor;

public class ThreadSystem {
	private final int DEFAULT_DELAY = 20;
	private final int IDLE_TIME_TO_SELF_DESTRUCTION = 10000;

	private Set<RepetitiveThread> threads;
	private ThreadFactory threadFactory;
	private int maximumTaskWorkers;

	public ThreadSystem(TaskMonitor taskMonitor, int maximumTaskWorkers) {
		this.threads = new CopyOnWriteArraySet<>();
		this.threadFactory = new ThreadFactory(this);
		this.maximumTaskWorkers = maximumTaskWorkers;
	}

	public int getDefaultThreadDelay() {
		return DEFAULT_DELAY;
	}

	public long getTimeForTaskExecutorThreadToSelfDestruction() {
		return IDLE_TIME_TO_SELF_DESTRUCTION;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void registerThread(RepetitiveThread thread) {
		threads.add(thread);
	}

	public void unregisterThread(RepetitiveThread thread) {
		threads.remove(thread);
	}

	public Collection<RepetitiveThread> getThreads() {
		return threads;
	}

	public int getMaximumTaskWorkers() {
		return maximumTaskWorkers;
	}

	@SuppressWarnings("unchecked")
	public <V extends RepetitiveThread> List<V> getThreads(Class<V> workerClass) {
		List<V> threads = new ArrayList<>();

		for (RepetitiveThread thread : getThreads()) {
			if (thread.getClass().equals(workerClass)) {
				threads.add((V) thread);
			}
		}

		return threads;
	}

	public void pauseAll() {
		for (RepetitiveThread thread : getThreads()) {
			thread.pause();
		}
	}

	public void forceKillAll() {
		for (RepetitiveThread thread : getThreads()) {
			thread.forceKill();
		}
	}

	public void goAll() {
		for (RepetitiveThread thread : getThreads()) {
			thread.go();
		}
	}
}
