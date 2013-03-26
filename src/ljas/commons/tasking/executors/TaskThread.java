package ljas.commons.tasking.executors;

import ljas.commons.tasking.step.ExecutingContext;

public class TaskThread extends Thread {
	private ExecutingContext executingContext;

	public TaskThread(Runnable r) {
		super(r);
	}

	public void setExecutingContext(ExecutingContext executingContext) {
		this.executingContext = executingContext;
	}

	public ExecutingContext getExecutingContext() {
		return executingContext;
	}
}
