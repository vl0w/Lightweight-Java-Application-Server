package ljas.commons.tasking.taskqueue;

import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.worker.WorkerController;

import org.apache.log4j.Logger;



public interface HasTaskQueue {
	public TaskController getTaskController();
	
	public WorkerController getWorkerController();

	public RuntimeEnvironmentState getState();

	public Logger getLogger();
}
