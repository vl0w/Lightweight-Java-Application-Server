package ljas.tasking.environment;

import ljas.session.Session;
import ljas.session.observer.SessionDataObserver;
import ljas.tasking.Task;

import org.apache.log4j.Logger;

public class TaskSystemSessionDataObserver implements SessionDataObserver {
	private TaskSystem taskSystem;

	public TaskSystemSessionDataObserver(TaskSystem taskSystem) {
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

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
