package ljas.functional.application;

import ljas.application.ApplicationImplementation;
import ljas.application.annotations.Cached;
import ljas.exception.ApplicationException;

public class TestApplicationImpl extends ApplicationImplementation implements
		TestApplication {

	public TestApplicationImpl() {
		super(TestApplication.class);
	}

	@Override
	public void sleep(long time) {
		try {
			Thread.currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getSum(int numOne, int numTwo) {
		return numOne + numTwo;
	}

	@Override
	@Cached
	public int getSumCached(int numOne, int numTwo) {
		return getSum(numOne, numTwo);
	}

	@Override
	public void init() throws ApplicationException {
		// Nothing
	}

}
