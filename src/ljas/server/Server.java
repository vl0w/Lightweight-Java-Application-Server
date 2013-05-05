package ljas.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ljas.application.Application;
import ljas.application.ApplicationAnalyzer;
import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.server.configuration.Property;
import ljas.server.configuration.ServerProperties;
import ljas.server.login.ClientConnectionListenerRunnable;
import ljas.session.Session;
import ljas.session.SessionHolder;
import ljas.state.SystemAvailabilityState;
import ljas.state.login.LoginRefusedMessage;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.environment.TaskSystemImpl;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public final class Server implements SessionHolder {
	public static final String PROJECT_NAME = "LJAS";
	public static final String PROJECT_HOMEPAGE = "http://github.com/vl0w/Lightweight-Java-Application-Server";
	public static final String SERVER_VERSION = "1.2.0-SNAPSHOT";

	private List<Session> sessions;
	private SystemAvailabilityState state;
	private ServerSocket serverSocket;
	private Application application;
	private TaskSystem taskSystem;
	private ExecutorService clientConnectionListenerService;
	private ServerProperties properties;

	public Server(Application application) throws IOException {
		this.application = application;
		this.state = SystemAvailabilityState.OFFLINE;
		this.sessions = new ArrayList<>();
		this.properties = new ServerProperties();
	}

	public ServerProperties getProperties() {
		return properties;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public boolean isOnline() {
		return state == SystemAvailabilityState.ONLINE;
	}

	public Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	public Application getApplication() {
		return application;
	}

	public TaskSystem getTaskSystem() {
		return taskSystem;
	}

	public SystemAvailabilityState getState() {
		return state;
	}

	public void setState(SystemAvailabilityState state) {
		this.state = state;
	}

	@Override
	public List<Session> getSessions() {
		return sessions;
	}

	@Override
	public void addSession(Session session) {
		sessions.add(session);
	}

	public void startup() throws ApplicationException, IOException {
		if (isOnline()) {
			shutdown();
		}

		state = SystemAvailabilityState.STARTUP;

		// Logging
		if (properties.isSet(Property.LOG4J_PATH)) {
			String path = properties.get(Property.LOG4J_PATH).toString();
			URL url = getClass().getClassLoader().getResource(path);
			DOMConfigurator.configure(url);
		} else {
			BasicConfigurator.configure();
		}

		// Initialize systems
		clientConnectionListenerService = Executors.newFixedThreadPool(5);
		taskSystem = new TaskSystemImpl(application);

		// Check application
		ApplicationAnalyzer.validateApplication(application.getClass());

		logServerInfo();

		getLogger().debug("Initializing application");
		getApplication().init();

		getLogger().debug("Getting internet connection, starting socket");
		int port = Integer.valueOf(properties.get(Property.PORT).toString());
		serverSocket = new ServerSocket(port);

		createConnectionListeners();

		getLogger().info(this + " has been started");
		state = SystemAvailabilityState.ONLINE;
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

			getLogger().debug("Killing threads");
			clientConnectionListenerService.shutdownNow();

			getLogger().debug("Shutdown executors");
			taskSystem.shutdown();

			getLogger().info(this + " is offline");
			state = SystemAvailabilityState.OFFLINE;
		} catch (Exception e) {
			getLogger().error(e);
		}
	}

	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {

		// Check server state
		if (!isOnline()) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.ILLEGAL_STATE);
		}

		// Check server full
		int maximumClients = Integer.valueOf(properties.get(
				Property.MAXIMUM_CLIENTS).toString());
		if (getSessions().size() >= maximumClients) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.SERVER_FULL);
		}

		// Check application
		if (!ApplicationAnalyzer.areApplicationsEqual(
				parameters.getClientApplicationClass(), application.getClass())) {
			throw new ConnectionRefusedException(
					LoginRefusedMessage.INVALID_APPLICATION);
		}
	}

	@Override
	public String toString() {
		return PROJECT_NAME + "-server";
	}

	ExecutorService getClientConnectionListenerService() {
		return clientConnectionListenerService;
	}

	private void createConnectionListeners() {
		for (int i = 0; i < 5; i++) {

			ClientConnectionListenerRunnable connectionListenerRunnable = new ClientConnectionListenerRunnable(
					this);

			clientConnectionListenerService.submit(connectionListenerRunnable);
		}
	}

	private void logServerInfo() {
		String applicationName = ApplicationAnalyzer
				.getApplicationName(application.getClass());
		String applicationVersion = ApplicationAnalyzer
				.getApplicationVersion(application.getClass());

		getLogger().info(
				"Starting " + this + " (v" + SERVER_VERSION
						+ ") with application " + applicationName + " ("
						+ applicationVersion + ")");

		getLogger().info(
				"See \"" + PROJECT_HOMEPAGE + "\" for more information");

		getLogger().debug("Configuration: " + properties);
	}
}
