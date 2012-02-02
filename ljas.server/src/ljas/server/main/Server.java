package ljas.server.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CopyOnWriteArrayList;
import ljas.commons.application.LoginParameters;
import ljas.commons.application.server.ServerApplication;
import ljas.commons.application.server.ServerApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.exceptions.TaskReceiverNotFoundException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.SocketConnection;
import ljas.commons.network.TaskSender;
import ljas.commons.state.RefusedMessage;
import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.tasking.taskqueue.TaskQueue;
import ljas.commons.tasking.taskqueue.TaskQueueConfiguration;
import ljas.commons.worker.SocketWorker;
import ljas.server.exceptions.ServerException;
import ljas.server.tasks.background.ClientConnectionListener;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public final class Server implements TaskSender {
	public static final String						PROJECT_NAME		= "LJAS";
	public static final String						PROJECT_HOMEPAGE	= "http://github.com/Ganymed/Lightweight-Java-Application-Server";
	public static final String						SERVER_VERSION		= "1.0.3";

	private CopyOnWriteArrayList<SocketConnection>	_clientConnections;
	private RuntimeEnvironmentState					_serverState;
	private TaskQueue								_taskQueue;
	private ServerSocket							_serverSocket;
	private ServerApplication						_application;
	private ServerConfiguration						_configuration;

	public synchronized CopyOnWriteArrayList<SocketConnection> getConnectedClients() {
		return _clientConnections;
	}

	private void setConnectedClients(CopyOnWriteArrayList<SocketConnection> _connectedClients) {
		_clientConnections = _connectedClients;
	}

	@Override
	public ServerApplication getApplication() {
		return _application;
	}

	private void setApplication(ServerApplication application) {
		_application = application;
	}

	@Override
	public RuntimeEnvironmentState getState() {
		return _serverState;
	}

	public void setState(RuntimeEnvironmentState value) {
		_serverState = value;
	}

	protected void setServerSocket(ServerSocket value) {
		_serverSocket = value;
	}

	public synchronized ServerSocket getServerSocket() {
		return _serverSocket;
	}

	public boolean isOnline() {
		return getState() == RuntimeEnvironmentState.ONLINE;
	}

	private void setTaskQueue(TaskQueue value) {
		_taskQueue = value;
	}

	@Override
	public TaskQueue getTaskQueue() {
		return _taskQueue;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	public ServerConfiguration getConfiguration() {
		return _configuration;
	}

	private void setConfiguration(ServerConfiguration configuration) {
		_configuration = configuration;
	}

	// CONSTRUCTORS
	public Server(ServerApplication application, String serverConfigurationFilePath)
			throws IOException {
		// Set the configuration
		setConfiguration(new ServerConfiguration(serverConfigurationFilePath));

		// Application
		setApplication(application);
		getApplication().setLocal(this);

		// Other
		setTaskQueue(new TaskQueue(new TaskQueueConfiguration(this, getConfiguration()
				.getTaskWorkerCount(), getConfiguration().getMaximumClients(), getConfiguration()
				.getMaximumTaskCount())));
		setState(RuntimeEnvironmentState.OFFLINE);
		setConnectedClients(new CopyOnWriteArrayList<SocketConnection>());

		// Logging
		DOMConfigurator.configure(getConfiguration().getLog4JFilePath());
	}

	public void startup() throws Exception {
		setState(RuntimeEnvironmentState.STARTUP);

		// Print some information
		getLogger().info(
				"Starting " + this + " (v" + SERVER_VERSION + ") with application "
						+ getApplication().getName() + " (" + getApplication().getVersion() + ")");

		getLogger().info("See \"" + PROJECT_HOMEPAGE + "\" for more information");
		getLogger().info(
				"This server is hosted by " + getConfiguration().getHostName() + " ("
						+ getConfiguration().getHostContact() + ")");

		getLogger().debug("Configuration: " + getConfiguration().toString());

		// Internet connection
		getLogger().debug("Getting internet connection, starting socket");

		// Serversocket
		setServerSocket(new ServerSocket(getConfiguration().getPort()));

		// Initialize TaskQueue
		getLogger().debug("Activating TaskQueue");
		getTaskQueue().activate(new ClientConnectionListener(this));

		// Start application
		getLogger().debug("Starting application");
		getApplication().start();

		// Finish Process
		setState(RuntimeEnvironmentState.ONLINE);
		getLogger().info(this + " has been started");
	}

	public void registerClient(SocketConnection clientConnection, LoginParameters parameters)
			throws Exception {

		getConnectedClients().add(clientConnection);
		SocketWorker wrk = getTaskQueue().getController().getSocketWorker();
		wrk.setConnection(clientConnection);
		getApplication().registerUser(clientConnection, parameters);
	}

	public void checkClient(LoginParameters parameters) throws ConnectionRefusedException {

		// Check server state
		if (!isOnline()) {
			throw new ConnectionRefusedException(RefusedMessage.ILLEGAL_STATE);
		}

		// Check server full
		if (getConnectedClients().size() >= getConfiguration().getMaximumClients()) {
			throw new ConnectionRefusedException(RefusedMessage.SERVER_FULL);
		}

		// Check application id
		if (parameters.getApplicationId() != getApplication().getApplicationId()) {
			throw new ConnectionRefusedException(RefusedMessage.WRONG_APPLICATION_ID);
		}

		// Check application version
		if (!parameters.getApplicationVersion().equals(getApplication().getVersion())) {
			throw new ConnectionRefusedException(RefusedMessage.WRONG_APPLICATION_VERSION);
		}

		// Call check on LoginParameters
		parameters.check(this);
	}

	public void shutdown() {
		try {
			getLogger().debug("Closing socket");
			getServerSocket().close();
			getLogger().debug("Deactivating TaskQueue");
			getTaskQueue().deactivate();
			getLogger().info(this+" is offline");
			setState(RuntimeEnvironmentState.OFFLINE);
		} catch (IOException e) {
			getLogger().error(e);
		}
	}

	@Override
	public String toString() {
		return PROJECT_NAME + "-server";
	}

	public synchronized void disconnectClient(SocketConnection con) throws ServerException {
		// Remove from Application
		try {
			getApplication().removeUser(con);
		} catch (ServerApplicationException e) {
			getLogger().error("Error while deleting user from application", e);
		}

		// Remove from client list
		if (!getConnectedClients().remove(con)) {
			throw new ServerException("Client not found on server");
		}

		getLogger().info(
				"Client disconnected (" + con + "), " + (getConnectedClients().size())
						+ " connection(s) overall");

	}

	@Override
	public synchronized SocketConnection getTaskReceiver(ConnectionInfo connectionInfo)
			throws TaskReceiverNotFoundException, IllegalArgumentException {
		if (connectionInfo == null) {
			throw new NullPointerException("Server has multiple receivers");
		}
		for (SocketConnection c : getConnectedClients()) {
			if (c.getConnectionInfo().equals(connectionInfo)) {
				return c;
			}
		}
		throw new TaskReceiverNotFoundException("Could not find client " + connectionInfo
				+ " on server");
	}

	@Override
	public ConnectionInfo getLocalConnectionInfo() {
		return new ConnectionInfo(getServerSocket().getInetAddress().toString(), getConfiguration()
				.getPort());
	}

	@Override
	public void notifyDisconnectedTaskReceiver(ConnectionInfo connectionInfo) {
		try {
			SocketConnection connection = getTaskReceiver(connectionInfo);
			disconnectClient(connection);
		} catch (Exception e) {
			getLogger().error(e);
		}
	}
}
