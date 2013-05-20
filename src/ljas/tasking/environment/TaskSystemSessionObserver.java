package ljas.tasking.environment;

import ljas.session.Session;
import ljas.session.SessionObserver;
import ljas.tasking.Task;

import org.apache.log4j.Logger;

public class TaskSystemSessionObserver implements SessionObserver {
	private TaskSystem taskSystem;

	public TaskSystemSessionObserver(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	@Override
	public final void onObjectReceived(Session session, Object obj) {
		if (obj instanceof Task) {
			Task task = (Task) obj;
			taskSystem.scheduleTask(task, session);
		} else {
			getLogger().warn(
					"Session " + session + " sended unknown object " + obj);
		}
	}

	@Override
	public void onSessionDisconnected(Session session) {
		// Nothing as default
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
