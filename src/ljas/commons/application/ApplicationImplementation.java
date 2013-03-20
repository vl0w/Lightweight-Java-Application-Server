package ljas.commons.application;

import ljas.commons.exceptions.ApplicationException;
import ljas.commons.tasking.step.ExecutingContext;
import ljas.commons.threading.TaskExecutorThread;

public class ApplicationImplementation {

	protected ExecutingContext getExecutingContext()
			throws ApplicationException {
		TaskExecutorThread thread = (TaskExecutorThread) Thread.currentThread();
		return thread.getCurrentExecutingContext();
	}

}
