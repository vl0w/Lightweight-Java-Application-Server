package ljas.testing.tasks;

import ljas.commons.client.Client;
import ljas.commons.tasking.facades.SimpleClientToServerTask;

public class SleepTask extends SimpleClientToServerTask {

	private static final long serialVersionUID = -7775085616940551405L;

	private long time;

	public SleepTask(Client client, long time) {
		super(client);
		this.time = time;
	}

	@Override
	public void perform() {
		try {
			Thread.currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
