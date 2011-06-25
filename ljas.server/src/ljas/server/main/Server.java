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
import ljas.commons.network.SendsTasks;
import ljas.commons.network.SocketConnection;
import ljas.commons.state.RefusedMessage;
import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.tasking.taskspool.HasTaskSpool;
import ljas.commons.tasking.taskspool.TaskSpool;
import ljas.commons.worker.SocketWorker;
import ljas.server.exceptions.ServerException;
import ljas.server.tasks.background.ClientConnectionListener;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public final class Server implements HasTaskSpool, SendsTasks {

	// CONSTANTS
	public static final String PROJECT_NAME = "LJAS";
	public static final String PROJECT_HOMEPAGE = "http://github.com/Ganymed/Lightweight-Java-Application-Server";
	public static final String SERVER_VERSION = "1.0.2";

	// MEMBERS
	private CopyOnWriteArrayList<SocketConnection> _clientConnections;
	private RuntimeEnvironmentState _serverState;
	private TaskSpool _taskSpool;
	private ServerSocket _serverSocket;
	private final ServerApplication _application;
	private final ServerConfiguration _serverConfiguration;

	// GETTERS & SETTERS
	public synchronized CopyOnWriteArrayList<SocketConnection> getConnectedClients() {
		return _clientConnections;
	}

	private void setConnectedClients(
			CopyOnWriteArrayList<SocketConnection> _connectedClients) {
		this._clientConnections = _connectedClients;
	}

	@Override
	public ServerApplication getApplication() {
		return _application;
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
		if (getState() == RuntimeEnvironmentState.ONLINE) {
			return true;
		} else {
			return false;
		}
	}

	private void setTaskSpool(TaskSpool value) {
		_taskSpool = value;
	}

	@Override
	public TaskSpool getTaskSpool() {
		return _taskSpool;
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	public ServerConfiguration getServerConfiguration() {
		return _serverConfiguration;
	}

	// CONSTRUCTORS
	public Server(ServerApplication application,
			String serverConfigurationFilePath) throws IOException {
		_serverConfiguration = new ServerConfiguration(
				serverConfigurationFilePath);
		setTaskSpool(new TaskSpool(getServerConfiguration()
				.getTaskWorkerCount(), getServerConfiguration()
				.getMaximumClients(), this, getServerConfiguration()
				.getMaximumTaskCount()));
		setState(RuntimeEnvironmentState.OFFLINE);
		setConnectedClients(new CopyOnWriteArrayList<SocketConnection>());
		_application = application;
		_application.setLocal(this);

		DOMConfigurator.configure(getServerConfiguration().getLog4JFilePath());
	}

	// METHODS
	public void startup() throws Exception {
		setState(RuntimeEnvironmentState.STARTUP);

		// Print some information
		getLogger().info(
				"Starting " + this + " (v" + SERVER_VERSION
						+ ") with application " + getApplication().getName()
						+ " (" + getApplication().getVersion() + ")");

		getLogger().info(
				"See \"" + PROJECT_HOMEPAGE + "\" for more information");
		getLogger().info(
				"This server is hosted by "
						+ getServerConfiguration().getHostName() + " ("
						+ getServerConfiguration().getHostContact() + ")");

		getLogger().debug(
				"Configuration: " + getServerConfiguration().toString());

		// Internet connection
		getLogger().info("Getting internet connection, starting socket");

		// Serversocket
		setServerSocket(new ServerSocket(getServerConfiguration().getPort()));

		// Initialize TaskSpool
		getTaskSpool().addBackgroundTask(new ClientConnectionListener());
		getLogger().info("Activating Taskspool");
		getTaskSpool().activate();

		// Start application
		getLogger().info("Starting application");
		getApplication().start();

		// Start jetty (not implemented yet!)
		// _jettyServer.start();

		// Finish Process
		setState(RuntimeEnvironmentState.ONLINE);
		getLogger().info(this + " has been started up");
	}

	public void registerClient(SocketConnection clientConnection,
			LoginParameters parameters) throws Exception {

		getConnectedClients().add(clientConnection);
		SocketWorker wrk = getTaskSpool().getController().getSocketWorker();
		wrk.setConnection(clientConnection);
		getApplication().registerUser(clientConnection, parameters);
	}

	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {

		// Check server state
		if (getState() != RuntimeEnvironmentState.ONLINE) {
			throw new ConnectionRefusedException(RefusedMessage.ILLEGAL_STATE);
		}

		// Check server full
		if (getConnectedClients().size() >= getServerConfiguration()
				.getMaximumClients()) {
			throw new ConnectionRefusedException(RefusedMessage.SERVER_FULL);
		}

		// Check application id
		if (parameters.getApplicationId() != getApplication()
				.getApplicationId()) {
			throw new ConnectionRefusedException(
					RefusedMessage.WRONG_APPLICATION_ID);
		}

		// Check application version
		if (!parameters.getApplicationVersion().equals(
				getApplication().getVersion())) {
			throw new ConnectionRefusedException(
					RefusedMessage.WRONG_APPLICATION_VERSION);
		}

		// Call check on LoginParameters
		parameters.check(this);
	}

	public void shutdown() {
		try {
			getServerSocket().close();
			setState(RuntimeEnvironmentState.OFFLINE);
		} catch (IOException e) {
			getLogger().error(e);
		}
	}

	@Override
	public String toString() {
		return PROJECT_NAME + "-server";
	}

	public synchronized void disconnectClient(SocketConnection con)
			throws ServerException {
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
				"Client disconnected (" + con + "), "
						+ (getConnectedClients().size())
						+ " connection(s) overall");

	}

	@Override
	public synchronized SocketConnection getTaskReceiver(
			ConnectionInfo connectionInfo)
			throws TaskReceiverNotFoundException, IllegalArgumentException {
		if (connectionInfo == null) {
			throw new NullPointerException("Server has multiple receivers");
		}
		for (SocketConnection c : getConnectedClients()) {
			if (c.getConnectionInfo().equals(connectionInfo)) {
				return c;
			}
		}
		throw new TaskReceiverNotFoundException("Could not find client "
				+ connectionInfo + " on server");
	}

	@Override
	public ConnectionInfo getLocalConnectionInfo() {
		return new ConnectionInfo(
				getServerSocket().getInetAddress().toString(),
				getServerConfiguration().getPort());
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
