package ljas.testing.tests;

import junit.framework.TestCase;
import ljas.commons.client.Client;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskObserverAdapter;
import ljas.testing.commons.tasks.SleepTask;

public class ServerOverloadTest extends TestCase {
	public void testSimpleOverload() {
		Client client = Constants.createClient();

		try {
			Constants.doConnect(client);

			int taskCount = (int) (Constants.getServerConfiguration()
					.getMaximumTaskCount() * 1.5);
			TaskFloodAdapter observer = new TaskFloodAdapter(taskCount);
			Task task = new SleepTask(1500);
			task.addObserver(observer);

			for (int i = 0; i < taskCount; i++) {
				client.runTaskAsync(task);
			}

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			client.disconnect();
		}
	}

	private class TaskFloodAdapter extends TaskObserverAdapter {
		public int _counter;
		private int _maximumTaskCount;

		public TaskFloodAdapter(int maximumTaskCount) {
			_counter = 0;
			_maximumTaskCount = maximumTaskCount;
		}

		@Override
		public void notifySuccess(Task task) {
			_counter++;
		}

		@Override
		public void notifyFail(Task task) {
			if (_counter <= _maximumTaskCount) {
				fail("Task failed before");
			} else {
				System.out.println("Remote server overloaded");
			}
		}
	}
}
