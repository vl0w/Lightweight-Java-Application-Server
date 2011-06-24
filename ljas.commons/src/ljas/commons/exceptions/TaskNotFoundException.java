package ljas.commons.exceptions;

import ljas.commons.tasking.sendable.task.Task;

public class TaskNotFoundException extends Exception {
	private static final long serialVersionUID = -1536946207070983944L;
	
	public TaskNotFoundException(Task t){
		super("Task with id="+t.getHeader().getId()+" not found");
	}
	public TaskNotFoundException(long id){
		super("Task with id="+id+" not found");
	}
}
