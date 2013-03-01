package ljas.commons.threading;

import org.apache.log4j.Logger;

public abstract class RepetitiveThread extends Thread {
	private volatile Thread blinker;
	private Thread thisThread;
	private boolean isKilled;
	private ThreadSystem threadSystem;

	public RepetitiveThread(ThreadSystem threadSystem) {
		this.threadSystem = threadSystem;
		this.isKilled = false;
	}

	protected Logger getLogger() {
		return Logger.getLogger(getClass());
	}

	@Override
	public void run() {
		while (!isKilled()) {
			while (blinker == thisThread) {
				try {
					((RepetitiveThread) thisThread).runCycle();
				} catch (Exception e) {
					e.printStackTrace();
					getLogger().error(e);
				}
			}
		}
	}

	public void pause() {
		blinker = null;
	}

	public void go() {
		blinker = thisThread;
	}

	public void kill() {
		pause();
		isKilled = true;
		threadSystem.unregisterThread(this);
	}

	public void forceKill() {
		kill();
		interrupt();
	}

	public boolean isRunning() {
		if (thisThread == null && blinker == null) {
			return false;
		}

		return thisThread == blinker && !isKilled();
	}

	public boolean isKilled() {
		return isKilled;
	}

	@Override
	public void start() {
		thisThread = this;
		blinker = this;
		threadSystem.registerThread(this);
		super.start();
	}

	protected ThreadSystem getThreadSystem() {
		return threadSystem;
	}

	protected abstract void runCycle() throws Exception;
}
