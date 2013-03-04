package ljas.commons.tasking.environment;

import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.TaskSenderCache;
import ljas.commons.tasking.monitoring.TaskMonitor;

public interface TaskSystem {
	public void scheduleTask(Task task);

	public void scheduleTask(Task task, Session session);

	public TaskMonitor getTaskMonitor();

	public TaskSenderCache getSenderCache();
}
