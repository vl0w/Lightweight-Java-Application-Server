package ljas.commons.tasking.step.impl;

import ljas.commons.exceptions.SessionException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionStore;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStepResult;
import ljas.commons.tasking.step.NavigationStep;

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
	public void execute() throws TaskException {
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
