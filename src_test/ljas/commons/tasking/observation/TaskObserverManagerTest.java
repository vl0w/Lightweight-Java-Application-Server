package ljas.commons.tasking.observation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import ljas.commons.tasking.Task;

import org.junit.Test;

public class TaskObserverManagerTest {

	private TaskObserverManager manager = TaskObserverManager.getInstance();

	@Test
	public void testAdd_ObserverAdded() {
		Task task = mock(Task.class);
		TaskObserver observer1 = mock(TaskObserver.class);
		TaskObserver observer2 = mock(TaskObserver.class);

		manager.add(task, observer1);
		manager.add(task, observer2);

		List<TaskObserver> observers = manager.getTaskObservers(task);
		assertEquals(2, observers.size());
		assertTrue(observers.contains(observer1));
		assertTrue(observers.contains(observer2));
	}

	@Test
	public void testRemove_ObserverRemoved() {
		Task task = mock(Task.class);
		TaskObserver observer1 = mock(TaskObserver.class);
		TaskObserver observer2 = mock(TaskObserver.class);

		manager.add(task, observer1);
		manager.add(task, observer2);
		manager.remove(task, observer1);

		List<TaskObserver> observers = manager.getTaskObservers(task);
		assertEquals(1, observers.size());
		assertTrue(observers.contains(observer2));
	}

	@Test
	public void testRemove_NoObserversFound_NothingWillBeRemoved() {
		Task task = mock(Task.class);
		TaskObserver observer1 = mock(TaskObserver.class);
		TaskObserver observer2 = mock(TaskObserver.class);
		TaskObserver observer3 = mock(TaskObserver.class);

		manager.add(task, observer1);
		manager.add(task, observer2);
		manager.remove(task, observer3);

		List<TaskObserver> observers = manager.getTaskObservers(task);
		assertEquals(2, observers.size());
		assertTrue(observers.contains(observer1));
		assertTrue(observers.contains(observer2));
	}

	@Test
	public void testGetTaskObserver_NoObserverFound_MustReturnEmptyList() {
		Task task = mock(Task.class);
		assertTrue(manager.getTaskObservers(task).isEmpty());
	}
}
