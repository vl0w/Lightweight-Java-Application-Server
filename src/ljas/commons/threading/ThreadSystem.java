package ljas.commons.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import ljas.commons.threading.factory.ThreadFactory;

public class ThreadSystem {
	private final int DEFAULT_DELAY = 20;

	private Set<RepetitiveThread> threads;
	private ThreadFactory threadFactory;
	private int maximumTaskWorkers;
	private String executingEnvDescription;

	public ThreadSystem(int maximumTaskWorkers) {
		this("Unknown", maximumTaskWorkers);
	}

	public ThreadSystem(String executingEnvDescription, int maximumTaskWorkers) {
		this.threads = new CopyOnWriteArraySet<>();
		this.threadFactory = new ThreadFactory(this);
		this.maximumTaskWorkers = maximumTaskWorkers;
		this.executingEnvDescription = executingEnvDescription;
	}

	public int getDefaultThreadDelay() {
		return DEFAULT_DELAY;
	}

	public ThreadFactory getThreadFactory() {
		return threadFactory;
	}

	public void registerThread(RepetitiveThread thread) {
		threads.add(thread);
	}

	public void unregisterThread(RepetitiveThread thread) {
		if (!thread.isKilled()) {
			thread.kill();
		}
		threads.remove(thread);
	}

	public Collection<RepetitiveThread> getThreads() {
		return threads;
	}

	public int getMaximumTaskWorkers() {
		return maximumTaskWorkers;
	}

	public void setMaximumTaskWorkers(int maximumTaskWorkers) {
		this.maximumTaskWorkers = maximumTaskWorkers;
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
			thread.kill();
		}

		// boolean hasRunningThreads = true;
		// while (hasRunningThreads) {
		// for (RepetitiveThread thread : getThreads()) {
		// if (!thread.isKilled()) {
		// continue;
		// }
		// }
		// break;
		// }
	}

	public void killAll() {
		for (RepetitiveThread thread : getThreads()) {
			thread.kill();
		}
	}

	public void goAll() {
		for (RepetitiveThread thread : getThreads()) {
			thread.go();
		}
	}

	@Override
	public String toString() {
		return executingEnvDescription;
	}
}
