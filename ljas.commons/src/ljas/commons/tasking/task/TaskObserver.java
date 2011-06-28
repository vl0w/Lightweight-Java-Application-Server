package ljas.commons.tasking.task;

public interface TaskObserver {
	public void notifyExecuted(Task task);

	public void notifyFail(Task task);

	public void notifySuccess(Task task);
}
