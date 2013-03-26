package ljas.commons.threading.factory;

import ljas.commons.threading.BackgroundThread;
import ljas.commons.threading.ThreadSystem;

public class ThreadFactory {

	private ThreadSystem threadSystem;

	public ThreadFactory(ThreadSystem threadSystem) {
		this.threadSystem = threadSystem;
	}

	public BackgroundThread createBackgroundThread(Runnable runnable) {
		BackgroundThread thread = new BackgroundThread(threadSystem, runnable);
		thread.start();
		return thread;
	}

}
