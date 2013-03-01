package ljas.commons.tasking.environment;

import ljas.commons.session.Session;
import ljas.commons.session.SessionObserver;
import ljas.commons.tasking.Task;

import org.apache.log4j.Logger;

/**
 * TODO Avoid polymorphism - Server extends this
 * 
 * @author jonashansen
 * 
 */
public class TaskSystemSessionObserver implements SessionObserver {
	private TaskSystem taskSystem;

	public TaskSystemSessionObserver(HasTaskSystem taskSystemProvider) {
		this(taskSystemProvider.getTaskSystem());
	}

	public TaskSystemSessionObserver(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	// TODO REMOVE this!
	public TaskSystemSessionObserver() {
	}

	// TODO REMOVE this!
	public void setTaskSystem(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
	}

	@Override
	public final void notiyObjectReceived(Session session, Object obj) {
		if (obj instanceof Task) {
			Task task = (Task) obj;
			taskSystem.scheduleTask(task, session);
		} else {
			getLogger().warn(
					"Session " + session + " sended unknown object " + obj);
		}
	}

	@Override
	public void notifySessionDisconnected(Session session) {
		// TODO Auto-generated method stub
	}

	private Logger getLogger() {
		return Logger.getLogger(getClass());
	}

}
