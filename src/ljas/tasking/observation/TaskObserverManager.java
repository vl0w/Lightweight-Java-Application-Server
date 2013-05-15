package ljas.tasking.observation;

import java.util.HashMap;
import java.util.Map;

import ljas.tasking.Task;

public final class TaskObserverManager {
	private static TaskObserverManager instance;
	private Map<Task, ObserverList> observerMap;

	public static TaskObserverManager getInstance() {
		if (instance == null) {
			instance = new TaskObserverManager();
		}
		return instance;
	}

	public void add(Task task, TaskObserver<?> observer) {
		ObserverList observers = getObservers(task);
		observers.add(observer);
		observerMap.put(task, observers);
	}

	public void remove(Task task, TaskObserver<?> observer) {
		if (observerMap.containsKey(task)) {
			ObserverList observers = observerMap.get(task);

			if (observers.contains(observer)) {
				observers.remove(observers.indexOf(observer));
			}
		}
	}

	public ObserverList getObservers(Task task) {
		if (observerMap.containsKey(task)) {
			return observerMap.get(task);
		}
		return new ObserverList();
	}

	private TaskObserverManager() {
		observerMap = new HashMap<>();
	}
}
