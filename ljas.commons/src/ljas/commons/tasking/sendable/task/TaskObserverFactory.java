package ljas.commons.tasking.sendable.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskObserverFactory {
	// MEMBERS
	private static TaskObserverFactory _instance;
	private Map<TaskHeader, List<TaskObserver>> _observerMap;
	
	// GETTERS & SETTERS
	public static TaskObserverFactory getInstance(){
		if(_instance==null){
			_instance=new TaskObserverFactory();
		}
		
		return _instance;
	}
	// CONSTRUCTOR
	private TaskObserverFactory(){
		_observerMap=new HashMap<TaskHeader, List<TaskObserver>>();
	}
	
	// METHODS
	public void clearObservers(TaskHeader header){
		if(_observerMap.containsKey(header)){
			_observerMap.remove(header);
		}
	}
	
	public void putObserver(TaskHeader header, TaskObserver observer){
		List<TaskObserver> observers = getTaskObservers(header);
		if(observers==null){
			observers=new ArrayList<TaskObserver>();
		}
		observers.add(observer);
		_observerMap.put(header, observers);
	}
	
	public void removeObserver(TaskHeader header, TaskObserver observer){
		if(_observerMap.containsKey(header)){
			List<TaskObserver> observers = _observerMap.get(header);
			
			if(observers.contains(observer)){
				observers.remove(observers.indexOf(observer));
			}
		}
	}
	
	public List<TaskObserver> getTaskObservers(TaskHeader header){
		if(_observerMap.containsKey(header)){
			return _observerMap.get(header);
		}
		
		return null;
	}
}
