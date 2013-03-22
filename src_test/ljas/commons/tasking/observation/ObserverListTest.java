package ljas.commons.tasking.observation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import ljas.commons.tasking.Task;

import org.junit.Test;

public class ObserverListTest {
	@Test
	public void testCastTo_SimpleCastToTaskClass() {
		ObserverList observers = new ObserverList();
		TaskObserver<?> observer1 = new NullTaskObserver<>();
		TaskObserver<?> observer2 = new NullTaskObserver<>();
		observers.add(observer1);
		observers.add(observer2);

		List<TaskObserver<Task>> castedObservers = observers.castTo(Task.class);
		assertEquals(2, castedObservers.size());
		assertEquals(observer1, castedObservers.get(0));
		assertEquals(observer2, castedObservers.get(1));
	}
}
