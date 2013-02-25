package ljas.commons.tasking.observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljas.commons.tasking.Task;

public class TaskObserverFactory {
	private static TaskObserverFactory instance;
	private Map<Task, List<TaskObserver>> observerMap;

	public static TaskObserverFactory getInstance() {
		if (instance == null) {
			instance = new TaskObserverFactory();
		}

		return instance;
	}

	private TaskObserverFactory() {
		observerMap = new HashMap<Task, List<TaskObserver>>();
	}

	public void clearObservers(Task header) {
		if (observerMap.containsKey(header)) {
			observerMap.remove(header);
		}
	}

	public void putObserver(Task header, TaskObserver observer) {
		List<TaskObserver> observers = getTaskObservers(header);
		if (observers == null) {
			observers = new ArrayList<TaskObserver>();
		}
		observers.add(observer);
		observerMap.put(header, observers);
	}

	public void removeObserver(Task header, TaskObserver observer) {
		if (observerMap.containsKey(header)) {
			List<TaskObserver> observers = observerMap.get(header);

			if (observers.contains(observer)) {
				observers.remove(observers.indexOf(observer));
			}
		}
	}

	public List<TaskObserver> getTaskObservers(Task header) {
		if (observerMap.containsKey(header)) {
			return observerMap.get(header);
		}

		return null;
	}
}
