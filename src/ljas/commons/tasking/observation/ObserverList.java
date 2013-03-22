package ljas.commons.tasking.observation;

import java.util.ArrayList;
import java.util.List;

import ljas.commons.tasking.Task;

public class ObserverList extends ArrayList<TaskObserver<?>> {

	private static final long serialVersionUID = -1131708384107967191L;

	@SuppressWarnings("unchecked")
	public <V extends Task> List<TaskObserver<V>> castTo(Class<V> taskClass) {
		List<TaskObserver<V>> observers = new ArrayList<>();

		for (TaskObserver<?> observer : this) {
			observers.add((TaskObserver<V>) observer);
		}

		return observers;
	}
}
