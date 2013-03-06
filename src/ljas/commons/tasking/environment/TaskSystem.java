package ljas.commons.tasking.environment;

import java.util.Map;

import ljas.commons.application.ApplicationEnvironment;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.monitoring.TaskMonitor;

public interface TaskSystem {
	void scheduleTask(Task task);

	void scheduleTask(Task task, Session session);

	TaskMonitor getTaskMonitor();

	Map<Task, Session> getSenderCache();

	ApplicationEnvironment getApplicationEnvironment();
}
