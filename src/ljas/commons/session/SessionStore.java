package ljas.commons.session;

import java.util.HashMap;
import java.util.Map;

import ljas.commons.tasking.Task;
import ljas.commons.tasking.step.impl.RemoteNavigationStep;

/**
 * The {@link SessionStore} is a class to store sessions in a serializable way.
 * It is mainly used in the {@link RemoteNavigationStep}. <br />
 * Imagine the following scenario:
 * <ol>
 * <li>X sends Y a task</li>
 * <li>Y sends the task back to X</li>
 * <li>X wants to send the task again to Y</li>
 * </ol>
 * 
 * In the building process of the {@link Task}, X knows about Y's session. But
 * as soon as the task gets sended over the network, X can only refer to Y's
 * session over this helper class (with the hashcode of Y's session).
 * 
 * @author jonashansen
 * @see RemoteNavigationStep
 */
public class SessionStore {
	private static Map<Integer, Session> store = new HashMap<>();

	public static Session findSession(int sessionHashCode) {
		return store.get(Integer.valueOf(sessionHashCode));
	}

	public static int put(Session session) {
		Integer hashCode = Integer.valueOf(session.hashCode());
		store.put(hashCode, session);
		return hashCode;
	}
}
