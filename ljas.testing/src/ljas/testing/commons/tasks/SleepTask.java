package ljas.testing.commons.tasks;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.sendable.task.Task;

public class SleepTask extends Task {
	private static final long serialVersionUID = -7775085616940551405L;
	
	private final int time;
	
	public SleepTask(int time){
		this.time=time;
		
	}

	@Override
	public void performTask() throws TaskException {
		Thread.currentThread();
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			throw new TaskException(e);
		}
	}
}
