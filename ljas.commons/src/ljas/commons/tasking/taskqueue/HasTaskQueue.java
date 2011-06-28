package ljas.commons.tasking.taskqueue;

import ljas.commons.state.RuntimeEnvironmentState;

import org.apache.log4j.Logger;



public interface HasTaskQueue {
	public TaskQueue getTaskQueue();

	public RuntimeEnvironmentState getState();

	public Logger getLogger();
}
