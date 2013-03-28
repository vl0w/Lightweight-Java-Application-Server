package ljas.tasking.step.impl;

import ljas.exception.SessionException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.session.SessionStore;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.NavigationStep;

import org.apache.log4j.Logger;

/**
 * Navigates the task to a {@link Session}
 * 
 * @author jonashansen
 * 
 */
public class RemoteNavigationStep extends NavigationStep {

	private static final long serialVersionUID = 7982980325209677702L;

	private Task task;
	private int sessionHashCode;

	public RemoteNavigationStep(Task task, Session session) {
		this.task = task;
		sessionHashCode = SessionStore.put(session);
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	@Override
	public void execute(ExecutingContext context) throws TaskException {
		try {
			Session session = SessionStore.findSession(sessionHashCode);
			session.sendObject(task);
		} catch (SessionException e) {
			String errorMessage = "Error while navigating task to remote session";
			getLogger().error(errorMessage, e);

			setResult(TaskStepResult.ERROR);
			setException(new TaskException(errorMessage, e));
		}
	}
}
