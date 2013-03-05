package ljas.commons.tasking.observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljas.commons.tasking.Task;

public class TaskObserverManager {
	private static TaskObserverManager instance;
	private Map<Task, List<TaskObserver>> observerMap;

	public static TaskObserverManager getInstance() {
		if (instance == null) {
			instance = new TaskObserverManager();
		}

		return instance;
	}

	private TaskObserverManager() {
		observerMap = new HashMap<Task, List<TaskObserver>>();
	}

	public void add(Task task, TaskObserver observer) {
		List<TaskObserver> observers = getTaskObservers(task);
		if (observers == null) {
			observers = new ArrayList<TaskObserver>();
		}
		observers.add(observer);
		observerMap.put(task, observers);
	}

	public void remove(Task task, TaskObserver observer) {
		if (observerMap.containsKey(task)) {
			List<TaskObserver> observers = observerMap.get(task);

			if (observers.contains(observer)) {
				observers.remove(observers.indexOf(observer));
			}
		}
	}

	public List<TaskObserver> getTaskObservers(Task task) {
		if (observerMap.containsKey(task)) {
			return observerMap.get(task);
		}

		return null;
	}
}
