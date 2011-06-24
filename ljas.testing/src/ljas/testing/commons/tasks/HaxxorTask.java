package ljas.testing.commons.tasks;

import java.io.Serializable;

import ljas.commons.exceptions.TaskException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.sendable.task.TaskHeader;


public class HaxxorTask extends Task {
	private static final long serialVersionUID = 7762423839260091067L;

	private final ConnectionInfo _connectionInfo;;
	
	@Override
	public TaskHeader getHeader() {
		return new FakedTaskHeader(_connectionInfo);
	}
	
	public HaxxorTask(ConnectionInfo connectionInfo){
		_connectionInfo=connectionInfo;
	}
	
	@Override
	public void performTask() throws TaskException {
		System.out.println("Haha, i hax ur system");
	}
	
	private class FakedTaskHeader extends TaskHeader implements Serializable{
		private static final long serialVersionUID = 8557590219677294360L;

		private final ConnectionInfo _connectionInfo;
		
		public FakedTaskHeader(ConnectionInfo connectionInfo) {
			super(50);
			_connectionInfo=connectionInfo;
		}
		
		@Override
		public long getApplicationId() {
			return "com.haxxor.task".hashCode();
		}
		
		@Override
		public ConnectionInfo getSenderInfo() {
			return _connectionInfo;
		}
		
	}

}
