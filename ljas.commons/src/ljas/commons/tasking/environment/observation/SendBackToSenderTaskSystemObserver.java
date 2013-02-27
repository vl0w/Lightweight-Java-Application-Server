package ljas.commons.tasking.environment.observation;

import ljas.commons.exceptions.SessionException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;

import org.apache.log4j.Logger;

@Deprecated
public class SendBackToSenderTaskSystemObserver implements TaskSystemObserver {

	private TaskSystem taskSystem;

	public SendBackToSenderTaskSystemObserver(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	@Override
	public void notifyTaskExecuted(Task task) {
		Session senderSession = taskSystem.getSenderCache()
				.removeSenderSession(task);
		// // TODO
		// Session senderSession = sessionHolder.findSession(senderSessionInfo);

		if (senderSession != null) {
			try {
				senderSession.sendObject(task);
			} catch (SessionException e) {
				getLogger().error(
						"Error while resending executed task to sender", e);
			}
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
