package ljas.threading;

public abstract class RepeatingRunnable implements Runnable {

	@Override
	public final void run() {
		while (!Thread.currentThread().isInterrupted()) {
			runCycle();
		}
	}

	protected abstract void runCycle();

}
