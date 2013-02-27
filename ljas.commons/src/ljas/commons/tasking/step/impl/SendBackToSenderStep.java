package ljas.commons.tasking.step.impl;

import ljas.commons.exceptions.SessionException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.step.NavigationStep;

import org.apache.log4j.Logger;

public class SendBackToSenderStep extends NavigationStep {

	private static final long serialVersionUID = 5575324790031015290L;
	private Task task;

	public SendBackToSenderStep(Task task) {
		this.task = task;
	}

	@Override
	public void execute() throws TaskException {
		Session senderSession = taskSystem.getSenderCache()
				.removeSenderSession(task);
		try {
			senderSession.sendObject(task);
		} catch (SessionException e) {
			getLogger().error("Error while navigating Task back to sender.", e);
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
