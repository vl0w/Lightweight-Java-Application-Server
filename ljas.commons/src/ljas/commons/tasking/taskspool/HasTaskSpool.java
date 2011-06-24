package ljas.commons.tasking.taskspool;

import ljas.commons.state.RuntimeEnvironmentState;

import org.apache.log4j.Logger;



public interface HasTaskSpool{
	public TaskSpool getTaskSpool();
	public RuntimeEnvironmentState getState();
	public Logger getLogger();
}
