package ljas.client;

import java.lang.reflect.Proxy;
import java.util.List;

import ljas.client.rmi.RemoteMethodInvocationHandler;
import ljas.commons.application.Application;
import ljas.commons.application.LoginParameters;
import ljas.commons.exceptions.ApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.DisconnectException;
import ljas.commons.exceptions.RequestTimedOutException;
import ljas.commons.exceptions.SessionException;
import ljas.commons.exceptions.TaskException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionFactory;
import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.state.login.LoginAcceptedMessage;
import ljas.commons.state.login.LoginMessage;
import ljas.commons.state.login.LoginRefusedMessage;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.environment.TaskSystemSessionObserver;
import ljas.commons.tasking.observation.NullTaskObserver;
import ljas.commons.threading.ThreadBlocker;
import ljas.commons.threading.ThreadSystem;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class ClientImpl implements Client {
	private Session session;
	private TaskSystem taskSystem;
	private ThreadSystem threadSystem;

	private RuntimeEnvironmentState state;
	private Application application;

	@Override
	public void setState(RuntimeEnvironmentState state) {
		this.state = state;
	}

	@Override
	public RuntimeEnvironmentState getState() {
		return state;
	}

	@Override
	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	public ClientImpl(Class<? extends Application> applicationClass) {
		this.state = RuntimeEnvironmentState.OFFLINE;
		this.threadSystem = new ThreadSystem("Client", 1);
		this.taskSystem = new TaskSystemImpl(threadSystem, this);

		// Application
		this.application = (Application) Proxy.newProxyInstance(getClass()
				.getClassLoader(), new Class[] { applicationClass },
				new RemoteMethodInvocationHandler(this));

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
		setState(RuntimeEnvironmentState.STARTUP);

		try {
			session = SessionFactory.prepareSession(this, threadSystem);

			LoginParameters loginParameters = new LoginParameters(application);
			ClientLoginHandler loginHandler = new ClientLoginHandler(ip, port,
					session, loginParameters);

			LoginMessage loginMessage = loginHandler.block();

			if (loginMessage instanceof LoginAcceptedMessage) {
				setState(RuntimeEnvironmentState.ONLINE);
				TaskSystemSessionObserver observer = new TaskSystemSessionObserver(
						this);
				session.setObserver(observer);

			} else if (loginMessage instanceof LoginRefusedMessage) {
				setState(RuntimeEnvironmentState.OFFLINE);
				LoginRefusedMessage message = (LoginRefusedMessage) loginMessage;
				throw new ConnectionRefusedException(message);
			}

		} catch (Throwable t) {
			setState(RuntimeEnvironmentState.OFFLINE);
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
		return getState() == RuntimeEnvironmentState.ONLINE && session != null
				&& session.isConnected();
	}

	@Override
	public void disconnect() throws DisconnectException {
		if (isOnline()) {
			session.disconnect();
			threadSystem.killAll();
			setState(RuntimeEnvironmentState.OFFLINE);
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
	public Task runTaskSync(Task task) throws ApplicationException {
		final ThreadBlocker<Task> threadBlocker = new ThreadBlocker<>(
				Client.REQUEST_TIMEOUT_MS);

		task.addObserver(new NullTaskObserver() {

			@Override
			public void notifyExecuted(Task task) {
				threadBlocker.release(task);
			}

			@Override
			public void notifyExecutedWithErrors(Task task,
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
	public Application getApplication() {
		return application;

	}
}
