package ljas.commons.worker;

import ljas.commons.state.RuntimeEnvironmentState;
import ljas.commons.tasking.taskspool.WorkerController;

import org.apache.log4j.Logger;


public abstract class Worker extends Thread {
	// MEMBERS
	// private Task _task;
	private WorkerController _controller;
	private volatile Thread _blinker;
	private Thread thisThread;
	private Logger _log;
	private boolean _delete;

	// GETTERS & SETTERS
	protected void setController(WorkerController value) {
		_controller = value;
	}

	protected WorkerController getController() {
		return _controller;
	}

	protected Logger getLogger() {
		return _log;
	}

	// CONSTRUCTORS
	public Worker(WorkerController controller, int priority, boolean daemon) {
		setController(controller);
		_log = getController().getTaskspool().getLocal().getLogger();
		setPriority(priority);
		setDaemon(daemon);
		_delete = false;
	}

	// METHODE
	@Override
	public void run() {
		while (!isKilled()) {
			while (_blinker == thisThread) {
				try {
					((Worker) thisThread).runItOnce();
					sleep(getController().getTaskspool().getWorkerDelay());
				} catch (Exception e) {
					getLogger().error(e);
				}
			}
		}
	}

	public void pause() {
		_blinker = null;
	}

	public void go() {
		_blinker = thisThread;
	}

	public void kill() {
		pause();
		_delete = true;
	}

	public boolean isKilled() {
		if (getController().getTaskspool().getLocal().getState() == RuntimeEnvironmentState.OFFLINE)
			return true;
		if (_delete)
			return true;
		return false;
	}

	public abstract void runItOnce() throws Exception;

	@Override
	public void start() {
		thisThread = this;
		_blinker = this;
		super.start();
	}
}
