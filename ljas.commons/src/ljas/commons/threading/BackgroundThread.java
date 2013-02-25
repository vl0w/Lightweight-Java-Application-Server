package ljas.commons.threading;

public class BackgroundThread extends RepetitiveThread {

	private Runnable runnable;

	public BackgroundThread(ThreadSystem threadSystem, Runnable runnable) {
		super(threadSystem);
		this.runnable = runnable;
		setName("BackgroundWorker (" + runnable.getClass().getSimpleName()
				+ ")");
	}

	@Override
	public void runCycle() throws Exception {
		runnable.run();
	}

}
