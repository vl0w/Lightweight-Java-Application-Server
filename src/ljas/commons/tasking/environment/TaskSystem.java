package ljas.commons.tasking.environment;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskSenderCache;
import ljas.commons.tasking.monitoring.TaskMonitor;

public interface TaskSystem {
	void scheduleTask(Task task);

	void scheduleTask(Task task, Session session);

	TaskMonitor getTaskMonitor();

	TaskSenderCache getSenderCache();
}
