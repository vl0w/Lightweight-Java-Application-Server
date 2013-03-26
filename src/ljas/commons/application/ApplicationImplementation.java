package ljas.commons.application;

import ljas.commons.tasking.executors.TaskThread;
import ljas.commons.tasking.step.ExecutingContext;

/**
 * Provides helper methods for the application implementation of a server
 * 
 * @author jonashansen
 * 
 */
public class ApplicationImplementation {

	/**
	 * 
	 * @return The current {@link ExecutingContext} of the active thread. Fetch
	 *         the ExecutingContext to gather information about the surrounding
	 *         environment in an implementation of an application method.<br/>
	 *         Examples:
	 *         <ul>
	 *         <li>Session of client which is currently executing the method</li>
	 *         <li>TaskSystem of the Server</li>
	 *         </ul>
	 */
	protected ExecutingContext getExecutingContext() {
		TaskThread thread = (TaskThread) Thread.currentThread();
		return thread.getExecutingContext();
	}

}
