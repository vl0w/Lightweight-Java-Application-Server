package ljas.commons.tasking.observation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ljas.commons.tasking.Task;

public class TaskObserverFactory {
	private static TaskObserverFactory _instance;
	private Map<Task, List<TaskObserver>> _observerMap;

	public static TaskObserverFactory getInstance() {
		if (_instance == null) {
			_instance = new TaskObserverFactory();
		}

		return _instance;
	}

	private TaskObserverFactory() {
		_observerMap = new HashMap<Task, List<TaskObserver>>();
	}

	public void clearObservers(Task header) {
		if (_observerMap.containsKey(header)) {
			_observerMap.remove(header);
		}
	}

	public void putObserver(Task header, TaskObserver observer) {
		List<TaskObserver> observers = getTaskObservers(header);
		if (observers == null) {
			observers = new ArrayList<TaskObserver>();
		}
		observers.add(observer);
		_observerMap.put(header, observers);
	}

	public void removeObserver(Task header, TaskObserver observer) {
		if (_observerMap.containsKey(header)) {
			List<TaskObserver> observers = _observerMap.get(header);

			if (observers.contains(observer)) {
				observers.remove(observers.indexOf(observer));
			}
		}
	}

	public List<TaskObserver> getTaskObservers(Task header) {
		if (_observerMap.containsKey(header)) {
			return _observerMap.get(header);
		}

		return null;
	}
}
