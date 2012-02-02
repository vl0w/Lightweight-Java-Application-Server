package ljas.testing.tasks;

import ljas.commons.exceptions.TaskException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.task.Task;

public class HaxxorTask extends Task {
	private static final long serialVersionUID = 7762423839260091067L;

	private final ConnectionInfo _connectionInfo;;

	@Override
	public long getApplicationId() {
		return "com.haxxor.task".hashCode();
	}

	@Override
	public ConnectionInfo getSenderInfo() {
		return _connectionInfo;
	}

	public HaxxorTask(ConnectionInfo connectionInfo) {
		_connectionInfo = connectionInfo;
	}

	@Override
	public void performTask() throws TaskException {
		System.out.println("Haha, i hax ur system");
	}
}
