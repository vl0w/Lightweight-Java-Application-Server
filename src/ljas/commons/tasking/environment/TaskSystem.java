package ljas.commons.tasking.environment;

import java.util.Map;

import ljas.commons.application.Application;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;

public interface TaskSystem {
	void scheduleTask(Task task);

	void scheduleTask(Task task, Session session);

	Map<Task, Session> getSenderCache();

	Application getApplication();

	void shutdown();
}
