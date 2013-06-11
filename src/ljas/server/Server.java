package ljas.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ljas.application.Application;
import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.exception.ConnectionRefusedException;
import ljas.server.configuration.ServerProperties;
import ljas.server.socket.LoginObserver;
import ljas.server.socket.ServerSocketBinder;
import ljas.server.state.OfflineState;
import ljas.server.state.OnlineState;
import ljas.server.state.ServerState;
import ljas.server.state.StartupState;
import ljas.session.Session;
import ljas.session.SessionFactory;
import ljas.session.SessionHolder;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.environment.TaskSystemImpl;

import org.apache.log4j.Logger;

public class Server implements SessionHolder, LoginObserver {
	public static final String PROJECT_NAME = "LJAS";
	public static final String PROJECT_HOMEPAGE = "http://github.com/vl0w/Lightweight-Java-Application-Server";
	public static final String SERVER_VERSION = "1.2.0-SNAPSHOT";

	private List<Session> sessions;
	private Application application;
	private TaskSystem taskSystem;
	private ServerProperties properties;
	private ServerSocketBinder serverSocketBinder;
	private ServerState currentState;

	public Server(Application application) throws IOException {
		this.application = application;
		this.sessions = new ArrayList<>();
		this.properties = new ServerProperties();
		this.serverSocketBinder = new ServerSocketBinder();
		this.currentState = new OfflineState();
		this.taskSystem = new TaskSystemImpl(application);
	}

	public ServerProperties getProperties() {
		return properties;
	}

	public void setServerSocketBinder(ServerSocketBinder serverSocketBinder) {
		this.serverSocketBinder = serverSocketBinder;
	}

	public ServerSocketBinder getServerSocketBinder() {
		return serverSocketBinder;
	}

	public void setCurrentState(ServerState currentState) {
		this.currentState = currentState;
	}

	public boolean isOnline() {
		return currentState.getClass().equals(OnlineState.class);
	}

	public boolean isOffline() {
		return currentState.getClass().equals(OfflineState.class);
	}

	public boolean isStarting() {
		return currentState.getClass().equals(StartupState.class);
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

	public void setTaskSystem(TaskSystem taskSystem) {
		this.taskSystem = taskSystem;
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
		ServerState oldState = currentState;

		currentState = new StartupState(this);
		try {
			currentState.startup();
		} catch (ApplicationException | IOException e) {
			currentState = oldState;
			throw e;
		}
		currentState = new OnlineState(this);
	}

	public void shutdown() {
		currentState.shutdown();
		currentState = new OfflineState();
	}

	public void checkClient(LoginParameters parameters)
			throws ConnectionRefusedException {
		currentState.checkClient(parameters);
	}

	@Override
	public String toString() {
		return PROJECT_NAME + "-server";
	}

	@Override
	public void onSocketConnect(Socket socket) {
		Session session = SessionFactory.createSocketSession(socket);
		session.setDataObserver(new ServerLoginSessionDataObserver(this));
	}

}
