package ljas.functional.application;

import ljas.application.ApplicationImplementation;
import ljas.application.LoginParameters;
import ljas.exception.ApplicationException;
import ljas.session.Session;

public class TestApplicationImpl extends ApplicationImplementation implements
		TestApplication {

	public TestApplicationImpl() {
		super(TestApplication.class);
	}

	@Override
	public void onSessionConnect(Session session, LoginParameters parameters)
			throws ApplicationException {

	}

	@Override
	public void onSessionDisconnect(Session session)
			throws ApplicationException {

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

}
