package ljas.commons.worker;

import ljas.commons.tasking.task.Task;

public class BackgroundWorker extends Worker {
	private Task _task;

	public BackgroundWorker(WorkerController controller, Task task) {
		super(controller, Thread.NORM_PRIORITY, false);
		_task = task;
		setName("BackgroundWorker (" + task.getClass().getSimpleName() + ")");
	}

	@Override
	public void runItOnce() throws Exception {
		_task.perform();
	}
}
