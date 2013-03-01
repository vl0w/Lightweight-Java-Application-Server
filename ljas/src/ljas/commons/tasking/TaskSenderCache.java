package ljas.commons.tasking;

import java.util.HashMap;
import java.util.Map;

import ljas.commons.session.Session;

public class TaskSenderCache {
	private Map<Task, Session> indexMap;

	public TaskSenderCache() {
		indexMap = new HashMap<>();
	}

	public Session removeSenderSession(Task task) {
		return indexMap.remove(task);
	}

	public void put(Task task, Session session) {
		indexMap.put(task, session);
	}
}
