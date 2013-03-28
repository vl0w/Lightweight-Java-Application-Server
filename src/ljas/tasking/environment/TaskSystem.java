package ljas.tasking.environment;

import java.util.Map;

import ljas.application.Application;
import ljas.session.Session;
import ljas.tasking.Task;

public interface TaskSystem {
	void scheduleTask(Task task);

	void scheduleTask(Task task, Session session);

	Map<Task, Session> getSenderCache();

	Application getApplication();

	void shutdown();
}
