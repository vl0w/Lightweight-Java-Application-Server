package ljas.commons.client;

import java.net.ConnectException;
import java.net.Socket;

import ljas.commons.application.LoginParameters;
import ljas.commons.application.client.ClientApplication;
import ljas.commons.application.client.ClientApplicationException;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.network.ConnectionInfo;
import ljas.commons.network.SocketConnection;
import ljas.commons.state.RefusedMessage;
import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.state.WelcomeMessage;
import ljas.commons.tasking.sendable.task.PreparedTaskObserver;
import ljas.commons.tasking.sendable.task.Task;
import ljas.commons.tasking.taskspool.TaskSpool;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


public class ClientImpl implements Client {
	// MEMBERS
	private RuntimeEnvironmentState _clientState;
	private SocketConnection _serverSocket;
	private final ClientApplication _application;
	private TaskSpool _taskSpool;
	private final ClientUI _ui;
	private ConnectionInfo _myConnectionInfo;

	// GETTERS & SETTERS
	public void setState(RuntimeEnvironmentState value) {
		_clientState = value;
	}

	@Override
	public RuntimeEnvironmentState getState() {
		return _clientState;
	}

	private void setServerSocket(SocketConnection value) {
		_serverSocket = value;
	}

	public SocketConnection getServerSocket() {
		return _serverSocket;
	}

	@Override
	public TaskSpool getTaskSpool() {
		return _taskSpool;
	}

	@Override
	public ClientUI getUI() {
		return _ui;
	}

	// CONSTRUCTORS
	public ClientImpl(ClientUI ui, ClientApplication application) {
		setServerSocket(null);
		setState(RuntimeEnvironmentState.OFFLINE);
		_taskSpool = new TaskSpool(2, 1, 1, this, 0);
		_application = application;
		_application.setClient(this);
		_ui = ui;
		_ui.setClient(this);
		DOMConfigurator.configure("./configuration/log4j.xml");

		// Go!
		_ui.handleStart();
	}

	// METHODS
	@Override
	public void connect(String ip, int port, LoginParameters parameters)
			throws ConnectionRefusedException, ConnectException {
		if (isOnline()) {
			disconnect();
		}

		setState(RuntimeEnvironmentState.STARTUP);
		_taskSpool.activate();
		try {
			// Connecting to server
			setServerSocket(new SocketConnection(new Socket(ip, port), this));

			getServerSocket().writeObject(parameters);

			// Awaiting his answer
			Object o = getServerSocket().readObject();

			if (o instanceof WelcomeMessage) {
				setState(RuntimeEnvironmentState.ONLINE);
				WelcomeMessage message = (WelcomeMessage) o;
				_myConnectionInfo = message.getConnectionInfo();
				_taskSpool.getController().getSocketWorker()
						.setConnection(getServerSocket());
				_ui.handleConnected(message);
			} else if (o instanceof RefusedMessage) {
				RefusedMessage message = (RefusedMessage) o;
				_ui.handleConnectionFailed(message);
				throw new ConnectionRefusedException(message);
			} else {
				throw new Exception("Unknown server response");
			}
		} catch (Exception e) {
			_taskSpool.deactivate();
			setState(RuntimeEnvironmentState.OFFLINE);
			if (ConnectionRefusedException.class.getName().equals(
					e.getClass().getName())) {
				throw (ConnectionRefusedException) e;
			} else {
				throw new ConnectException(e.getMessage()) ;
			}
		}
	}

	private void sendTask(Task task) throws ClientApplicationException {
		if (!isOnline()) {
			throw new ClientApplicationException("Client is not online");
		}

		try {
			getTaskSpool().sendTask(task, getLocalConnectionInfo());
		} catch (Exception e) {
			getLogger().error("Error while sending task",e);
		}
	}

	@Override
	public boolean isOnline() {
		if (getState() == RuntimeEnvironmentState.ONLINE) {
			return true;
		}
		return false;
	}

	@Override
	public void disconnect() {
		if (isOnline()) {
			_taskSpool.deactivate();
			_taskSpool = new TaskSpool(2, 1, 1, this, 0);
			_ui.handleDisconnected();
			getServerSocket().close();
			setState(RuntimeEnvironmentState.OFFLINE);
		}
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	@Override
	public SocketConnection getTaskReceiver(ConnectionInfo connectionInfo) {
		return getServerSocket();
	}

	@Override
	public ConnectionInfo getLocalConnectionInfo() {
		return _myConnectionInfo;
	}

	@Override
	public ClientApplication getApplication() {
		return _application;
	}

	@Override
	public void notifyDisconnectedTaskReceiver(ConnectionInfo connectionInfo) {
		if (isOnline()) {
			disconnect();
		}
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
	public Task runTaskSync(Task task) throws ClientApplicationException {
		// Private class to provide the runTaskSync function!
		class TaskWaiter{
			private Task _finishedTask;
			private final Task _waitingTask;
			private ClientApplicationException _exception;
			private boolean _interrupted;
			private final int _expiration;
			
			public Task getFinishedTask(){
				return _finishedTask;
			}
			
			/**
			 * 
			 * @param task The task which the TaskWaiter should wait for
			 * @param autoExpiration Defines after how many seconds the task expires. 0 indicates, that the TaskWaiter will wait forever
			 */
			public TaskWaiter(Task task, int autoExpiration){
				_waitingTask=task;
				_exception=null;
				_expiration=autoExpiration;
			}
			
			public TaskWaiter(Task task){
				this(task,0);
			}
			
			
			public void doWait() throws ClientApplicationException {
				_waitingTask.addObserver(new PreparedTaskObserver(){
					@Override
					public void notifyExecuted(Task task) {
						_finishedTask=task;
						_interrupted=true;
					}
					
					@Override
					public void notifyFail(Task task) {
						_exception = new ClientApplicationException(task.getResultMessage());
					}
				});
				
				sendTask(_waitingTask);
				
				int timeCounter=0;
				while(!_interrupted){
					try {
						Thread.currentThread();
						Thread.sleep(50);
						timeCounter+=50;
						
						if(_expiration>0 && timeCounter>=_expiration){
							_exception=new ClientApplicationException("Task "+_waitingTask+" expired");
							break;
						}
					} catch (InterruptedException e) {
						// nothing
					}
				}
				
				if(_exception!=null){
					throw _exception;
				}
			}
		}
		
		// Use the waiter
		TaskWaiter waiter = new TaskWaiter(task);
		waiter.doWait();
		return waiter.getFinishedTask();
	}
	


	@Override
	public boolean runTaskAsync(Task task) {
		try {
			sendTask(task);
			return true;
		} catch (ClientApplicationException e) {
			return false;
		}
	}
}
