package ljas.functional.application;

import ljas.commons.application.LoginParameters;
import ljas.commons.application.ApplicationImplementation;
import ljas.commons.exceptions.ApplicationException;
import ljas.commons.session.Session;

public class TestApplicationImpl extends ApplicationImplementation implements
		TestApplication {

	@Override
	public void onSessionConnect(Session session, LoginParameters parameters)
			throws ApplicationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSessionDisconnect(Session session)
			throws ApplicationException {
		// TODO Auto-generated method stub

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
