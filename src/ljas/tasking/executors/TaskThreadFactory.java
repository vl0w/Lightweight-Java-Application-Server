package ljas.tasking.executors;

import java.util.concurrent.ThreadFactory;

public class TaskThreadFactory implements ThreadFactory {

	@Override
	public Thread newThread(Runnable r) {
		return new TaskThread(r);
	}

}
