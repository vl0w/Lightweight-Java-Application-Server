package ljas.testing.commons.tasks;

import ljas.commons.exceptions.TaskException;

public class DataSleepTask extends SleepTask {
	private static final long serialVersionUID = 5052283149530666373L;
	
	private final int dataSize;
	
	public DataSleepTask(int time, int dataSize) {
		super(time);
		this.dataSize=dataSize;
	}
	
	
	@Override
	public void performTask() throws TaskException {
		@SuppressWarnings("unused")
		byte[] pointlessData = new byte[dataSize*1000000];
		super.performTask();
		pointlessData = null;
	}

}
