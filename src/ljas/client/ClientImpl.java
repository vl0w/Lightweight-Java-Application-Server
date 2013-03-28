package ljas.client;

import java.lang.reflect.Proxy;
import java.util.List;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.client.rmi.RemoteMethodInvocationHandler;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.exception.DisconnectException;
import ljas.exception.RequestTimedOutException;
import ljas.exception.SessionException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.session.SessionFactory;
import ljas.state.SystemAvailabilityState;
import ljas.state.login.LoginAcceptedMessage;
import ljas.state.login.LoginMessage;
import ljas.state.login.LoginRefusedMessage;
import ljas.tasking.Task;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.environment.TaskSystemImpl;
import ljas.tasking.environment.TaskSystemSessionObserver;
import ljas.tasking.observation.NullTaskObserver;
import ljas.threading.ThreadBlocker;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ClientImpl implements Client {
	private Session session;
	private TaskSystem taskSystem;

	private SystemAvailabilityState state;
	private Application application;

	public ClientImpl(Class<? extends Application> applicationClass) {
		// Application
		this.application = (Application) Proxy.newProxyInstance(getClass()
				.getClassLoader(), new Class[] { applicationClass },
				new RemoteMethodInvocationHandler(this));

		this.state = SystemAvailabilityState.OFFLINE;
		this.taskSystem = new TaskSystemImpl(application);

		DOMConfigurator.configure("./log4j.xml");
	}

	@Override
	public void connect(String ip, int port) throws ConnectionRefusedException,
			SessionException {
		if (isOnline()) {
			try {
				disconnect();
			} catch (DisconnectException e) {
				getLogger().error("Unable to disconnect client.", e);
			}
		}
		state = SystemAvailabilityState.STARTUP;

		try {
			session = SessionFactory.prepareSession(this);

			LoginParameters loginParameters = new LoginParameters(
					application.getClass());
			ClientLoginHandler loginHandler = new ClientLoginHandler(ip, port,
					session, loginParameters);

			LoginMessage loginMessage = loginHandler.block();

			if (loginMessage instanceof LoginAcceptedMessage) {
				state = SystemAvailabilityState.ONLINE;
				TaskSystemSessionObserver observer = new TaskSystemSessionObserver(
						taskSystem);
				session.setObserver(observer);

			} else if (loginMessage instanceof LoginRefusedMessage) {
				state = SystemAvailabilityState.OFFLINE;
				LoginRefusedMessage message = (LoginRefusedMessage) loginMessage;
				throw new ConnectionRefusedException(message);
			}

		} catch (Throwable t) {
			state = SystemAvailabilityState.OFFLINE;
			if (ConnectionRefusedException.class.getName().equals(
					t.getClass().getName())) {
				throw (ConnectionRefusedException) t;
			} else {
				throw new SessionException(t);
			}
		}
	}

	private void scheduleTask(Task task) throws ApplicationException {
		if (!isOnline()) {
			throw new ApplicationException("Client is not online");
		}

		taskSystem.scheduleTask(task);
	}

	@Override
	public boolean isOnline() {
		return state == SystemAvailabilityState.ONLINE && session != null
				&& session.isConnected();
	}

	@Override
	public void disconnect() throws DisconnectException {
		if (isOnline()) {
			session.disconnect();
			state = SystemAvailabilityState.OFFLINE;
		}
	}

	public Logger getLogger() {
		return Logger.getLogger(Client.class);
	}

	/**
	 * Runs a task, waits for it and throws an exception when it has failed
	 * 
	 * @param task
	 *            The task to execute
	 * @return The executed task
	 * @throws ClientApplicationException
	 *             When the task failed or could not be sended
	 */
	@Override
	public <T extends Task> T runTaskSync(T task) throws ApplicationException {
		final ThreadBlocker<T> threadBlocker = new ThreadBlocker<>(
				Client.REQUEST_TIMEOUT_MS);

		task.addObserver(new NullTaskObserver<T>() {

			@Override
			public void notifyExecuted(T task) {
				threadBlocker.release(task);
			}

			@Override
			public void notifyExecutedWithErrors(T task,
					List<TaskException> exceptions) {
				ApplicationException exception = new ApplicationException(
						exceptions);
				threadBlocker.release(exception);
			}
		});

		scheduleTask(task);

		try {
			return threadBlocker.block();
		} catch (RequestTimedOutException e) {
			throw new ApplicationException("Request timed out");
		} catch (ApplicationException e) {
			throw e;
		} catch (Throwable t) {
			throw new ApplicationException(
					"Unknown exception while executing request", t);
		}
	}

	@Override
	public void runTaskAsync(Task task) {
		try {
			scheduleTask(task);
		} catch (ApplicationException e) {
			getLogger().error(e);
		}
	}

	@Override
	public Session getServerSession() {
		return session;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <A extends Application> A getApplication() {
		return (A) application;

	}
}
