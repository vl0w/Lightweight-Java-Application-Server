package ljas.commons.tasking.sendable.task;

public interface TaskObserver {
	public void notifyExecuted(Task task);
	public void notifyFail(Task task);
	public void notifySuccess(Task task);
}
