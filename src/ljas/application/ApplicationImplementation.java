package ljas.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ljas.application.annotations.AttachToEverySession;
import ljas.exception.ApplicationException;
import ljas.session.Session;
import ljas.tasking.executors.TaskThread;
import ljas.tasking.step.ExecutingContext;

/**
 * Provides helper methods for the application implementation of a server
 * 
 * @author jonashansen
 * 
 */
public class ApplicationImplementation implements Application {

	Map<Session, Map<Class<?>, Object>> sessionObjects;
	private ApplicationInfo info;

	public ApplicationImplementation(
			Class<? extends Application> applicationClass) {
		sessionObjects = new HashMap<>();
		info = new ApplicationInfo(applicationClass);
	}

	@Override
	public void onSessionConnect(Session session, LoginParameters parameters)
			throws ApplicationException {
		if (info.getAttachToEverySessionAnnotation() != null) {
			addSessionObjects(session);
		}
	}

	@Override
	public void onSessionDisconnect(Session session)
			throws ApplicationException {
		sessionObjects.remove(session);
	}

	/**
	 * 
	 * @return The current {@link ExecutingContext} of the active thread. Fetch
	 *         the ExecutingContext to gather information about the surrounding
	 *         environment in an implementation of an application method.<br/>
	 *         Examples:
	 *         <ul>
	 *         <li>Session of client which is currently executing the method</li>
	 *         <li>TaskSystem of the Server</li>
	 *         </ul>
	 */
	protected ExecutingContext getExecutingContext() {
		TaskThread thread = (TaskThread) Thread.currentThread();
		return thread.getExecutingContext();
	}

	protected <V> V getSessionObject(Class<V> clazz)
			throws ApplicationException {
		Session session = getExecutingContext().getSenderSession();
		return getSessionObject(session, clazz);
	}

	@SuppressWarnings("unchecked")
	protected <V> V getSessionObject(Session session, Class<V> clazz)
			throws ApplicationException {
		if (!info.hasSessionObject(clazz)) {
			throw new ApplicationException("The object type " + clazz
					+ " has not been annotated and attached do a session");
		}

		return (V) sessionObjects.get(session).get(clazz);
	}

	protected <V> Collection<V> getAllSessionObjects(Class<V> clazz)
			throws ApplicationException {
		Collection<V> objects = new ArrayList<>();

		for (Session session : sessionObjects.keySet()) {
			V sessionObject = getSessionObject(session, clazz);
			objects.add(sessionObject);
		}

		return objects;
	}

	protected Collection<Session> getConnectedSessions() {
		return sessionObjects.keySet();
	}

	private void addSessionObjects(Session session) throws ApplicationException {
		AttachToEverySession objToAttach = info
				.getAttachToEverySessionAnnotation();

		for (Class<?> clazz : objToAttach.classes()) {
			try {
				Object obj = clazz.getConstructor().newInstance();

				if (!sessionObjects.containsKey(session)) {
					sessionObjects
							.put(session, new HashMap<Class<?>, Object>());
				}

				Map<Class<?>, Object> objects = sessionObjects.get(session);
				objects.put(clazz, obj);
			} catch (Exception e) {
				throw new ApplicationException(e);
			}
		}
	}

	@Override
	public ApplicationInfo getInfo() {
		return info;
	}
}
