package ljas.commons.threading;

public abstract class RepeatingRunnable implements Runnable {

	@Override
	public final void run() {
		while (!Thread.currentThread().isInterrupted()) {
			runCycle();
		}
	}

	protected abstract void runCycle();

}
