package ljas.tasking.step.impl;

import ljas.exception.SessionException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.tasking.Task;
import ljas.tasking.TaskStepResult;
import ljas.tasking.step.ExecutingContext;
import ljas.tasking.step.NavigationStep;

import org.apache.log4j.Logger;

public class SendBackToSenderStep extends NavigationStep {

	private static final long serialVersionUID = 5575324790031015290L;
	private Task task;

	public SendBackToSenderStep(Task task) {
		this.task = task;
	}

	@Override
	public void execute(ExecutingContext context) throws TaskException {
		Session senderSession = context.getTaskSystem().getSenderCache()
				.remove(task);
		try {
			senderSession.sendObject(task);
		} catch (SessionException e) {
			String errorMessage = "Error while navigating Task back to sender.";
			getLogger().error(errorMessage, e);

			setResult(TaskStepResult.ERROR);
			setException(new TaskException(errorMessage, e));
		}
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
