package ljas.testing.tests;

import java.util.ArrayList;
import java.util.List;

import ljas.commons.client.Client;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskObserverAdapter;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.AdditionTask;
import ljas.testing.tasks.SleepTask;

public class AsyncTaskTest extends ServerTestCase {
	private List<Task> _executedTaskList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_executedTaskList = new ArrayList<Task>();
	}

	public void testSimpleAsyncTask() {
		try {
			Client client = createAndConnectClient();

			double v1 = Math.random(), v2 = Math.random(), expectedValue = v1
					+ v2;

			AdditionTask task = new AdditionTask(v1, v2);
			task.addObserver(new TaskObserverAdapter() {
				@Override
				public void notifyExecuted(Task task) {
					_executedTaskList.add(task);
				}
			});

			client.runTaskAsync(task);

			while (_executedTaskList.size() == 0) {
				Thread.currentThread();
				Thread.sleep(50);
			}

			double result = ((AdditionTask) _executedTaskList.get(0)).result;
			assertEquals(expectedValue, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testMassiveAsyncTest() {
		final int tasks = 10000;
		int expectedTime = 10000;

		try {
			Client client = createAndConnectClient();

			for (int i = 0; i < tasks; i++) {
				Task task = new SleepTask(0);
				task.addObserver(new TaskObserverAdapter() {
					@Override
					public void notifyExecuted(Task task) {
						_executedTaskList.add(task);
					}
				});

				client.runTaskAsync(task);
			}

			while (_executedTaskList.size() < tasks) {
				Thread.currentThread();
				Thread.sleep(50);
				expectedTime -= 50;
			}

			if(expectedTime<0) {
				fail("Test took too long.");
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
