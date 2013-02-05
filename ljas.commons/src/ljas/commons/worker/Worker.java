package ljas.commons.worker;

import ljas.commons.state.RuntimeEnvironmentState;

import org.apache.log4j.Logger;

public abstract class Worker extends Thread {
	private WorkerController _controller;
	private volatile Thread _blinker;
	private Thread thisThread;
	private Logger _log;
	private boolean _delete;

	protected void setWorkerController(WorkerController value) {
		_controller = value;
	}

	protected WorkerController getWorkerController() {
		return _controller;
	}

	protected Logger getLogger() {
		return _log;
	}

	public boolean isKilled() {
		if (getWorkerController().getTaskController().getLocal().getState() == RuntimeEnvironmentState.OFFLINE) {
			return true;
		}
		if (_delete) {
			return true;
		}
		return false;
	}

	public Worker(WorkerController controller, int priority, boolean daemon) {
		setWorkerController(controller);
		_log = getWorkerController().getTaskController().getLocal().getLogger();
		setPriority(priority);
		setDaemon(daemon);
		_delete = false;
	}

	@Override
	public void run() {
		while (!isKilled()) {
			while (_blinker == thisThread) {
				try {
					((Worker) thisThread).runItOnce();
					sleep(getWorkerController().getTaskController().WORKER_DELAY);
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

	public abstract void runItOnce() throws Exception;

	@Override
	public void start() {
		thisThread = this;
		_blinker = this;
		super.start();
	}
}
