package ljas.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import ljas.commons.application.LoginParameters;
import ljas.commons.application.server.ServerApplication;
import ljas.commons.application.server.ServerApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.session.Session;
import ljas.commons.session.SessionHolder;
import ljas.commons.state.HasState;
import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.state.login.LoginRefusedMessage;
import ljas.commons.tasking.environment.HasTaskSystem;
import ljas.commons.tasking.environment.TaskSystem;
import ljas.commons.tasking.environment.TaskSystemImpl;
import ljas.commons.tasking.environment.TaskSystemSessionObserver;
import ljas.commons.threading.ThreadSystem;
import ljas.server.configuration.ServerConfiguration;
import ljas.server.login.ClientConnectionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public final class Server extends TaskSystemSessionObserver implements
		HasTaskSystem, SessionHolder, HasState {
	public static final String PROJECT_NAME = "LJAS";
	public static final String PROJECT_HOMEPAGE = "http://github.com/vl0w/Lightweight-Java-Application-Server";
	public static final String SERVER_VERSION = "1.1.0-SNAPSHOT";

	private List<Session> sessions;
	private RuntimeEnvironmentState serverState;
	private ServerSocket serverSocket;
	private ServerApplication application;
	private ServerConfiguration configuration;
	private TaskSystem taskSystem;
	private ThreadSystem threadSystem;

	@Override
	public RuntimeEnvironmentState getState() {
		return serverState;
	}

	@Override
	public void setState(RuntimeEnvironmentState value) {
		serverState = value;
	}

	protected void setServerSocket(ServerSocket value) {
		serverSocket = value;
	}

	public synchronized ServerSocket getServerSocket() {
		return serverSocket;
	}

	public boolean isOnline() {
		return getState() == RuntimeEnvironmentState.ONLINE;
	}

	public Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	public ServerApplication getApplication() {
		return application;
	}

	public ServerConfiguration getConfiguration() {
		return configuration;
	}

	public ThreadSystem getThreadSystem() {
		return threadSystem;
	}

	// CONSTRUCTORS
	public Server(ServerApplication application,
			ServerConfiguration configuration) throws IOException {
		// Set the configuration
		this.configuration = configuration;

		// Application
		this.application = application;

		// ThreadSystem
		threadSystem = new ThreadSystem(Server.class.getSimpleName(),
				configuration.getMaxTaskWorkerCount());

		// Tasksystem
		taskSystem = new TaskSystemImpl(threadSystem);
		setTaskSystem(taskSystem);

		// Other stuff
		// TODO
		setState(RuntimeEnvironmentState.OFFLINE);
		sessions = new ArrayList<>();

		// Logging
		DOMConfigurator.configure(getConfiguration().getLog4JFilePath());
	}

	public void startup() throws Exception {
		if (isOnline()) {
			shutdown();
		}

		setState(RuntimeEnvironmentState.STARTUP);

		// Print some information
		getLogger().info(
				"Starting " + this + " (v" + SERVER_VERSION
						+ ") with application " + getApplication().getName()
						+ " (" + getApplication().getVersion() + ")");

		getLogger().info(
				"See \"" + PROJECT_HOMEPAGE + "\" for more information");
		getLogger().info(
				"This server is hosted by " + getConfiguration().getHostName()
						+ " (" + getConfiguration().getHostContact() + ")");

		getLogger().debug("Configuration: " + getConfiguration().toString());

		// Internet connection
		getLogger().debug("Getting internet connection, starting socket");

		// Serversocket
		setServerSocket(new ServerSocket(getConfiguration().getPort()));

		// Tasks
		for (int i = 0; i < 5; i++) {

			ClientConnectionListener connectionListener = new ClientConnectionListener(
					this);
			threadSystem.getThreadFactory().createBackgroundThread(
					connectionListener);
		}

		// Start application
		getLogger().debug("Starting application");
		getApplication().start();

		// Finish Process
		setState(RuntimeEnvironmentState.ONLINE);
		getLogger().info(this + " has been started");
	}

	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {

		// Check server state
		if (!isOnline()) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.ILLEGAL_STATE);
		}

		// Check server full
		if (getSessions().size() >= getConfiguration().getMaximumClients()) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.SERVER_FULL);
		}

		// Check application id
		if (parameters.getApplicationId() != getApplication()
				.getApplicationId()) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.WRONG_APPLICATION_ID);
		}

		// Check application version
		if (!parameters.getApplicationVersion().equals(
				getApplication().getVersion())) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.WRONG_APPLICATION_VERSION);
		}

		// Call check on LoginParameters
		parameters.check();
	}

	public void shutdown() {
		try {
			getLogger().debug("Closing socket");
			getServerSocket().close();

			getLogger().info("Closing client sessions");
			List<Session> sessionsToClose = new ArrayList<>(sessions);
			for (Session session : sessionsToClose) {
				session.disconnect();
			}

			getLogger().debug(
					"Deactivating " + threadSystem.getClass().getSimpleName());
			threadSystem.killAll();

			getLogger().info(this + " is offline");
			setState(RuntimeEnvironmentState.OFFLINE);
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	@Override
	public String toString() {
		return PROJECT_NAME + "-server";
	}

	@Override
	public void addSession(Session session) {
		sessions.add(session);
		session.setObserver(this);
	}

	@Override
	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	@Override
	public List<Session> getSessions() {
		return sessions;
	}

	@Override
	public void notifySessionDisconnected(Session session) {
		// Remove from Application
		try {
			getApplication().removeUser(session);
		} catch (ServerApplicationException e) {
			getLogger().error("Error while deleting user from application", e);
		}

		// Remove from client list
		if (!getSessions().remove(session)) {
			getLogger().error("Unable to delete session from server");
		}

		getLogger().info(
				"Client disconnected (" + session + "), "
						+ (getSessions().size()) + " connection(s) overall");
	}
}
