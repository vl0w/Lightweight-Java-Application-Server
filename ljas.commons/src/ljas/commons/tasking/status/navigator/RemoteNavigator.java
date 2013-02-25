package ljas.commons.tasking.status.navigator;

import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskStateResult;

import org.apache.log4j.Logger;

/**
 * Navigates the task to a {@link Session}
 * 
 * @author jonashansen
 * 
 */
public class RemoteNavigator implements TaskNavigator {

	private Session session;

	public RemoteNavigator(Session session) {
		this.session = session;
	}

	@Override
	public void navigate(Task task) {
		try {
			session.sendObject(task);
		} catch (SessionException e) {
			getLogger().error("Error while navigating task to remote session",
					e);
			task.getCurrentState().setResult(TaskStateResult.ERROR);
			task.setResultMessage("Error while navigating task to remote session");
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}
}
