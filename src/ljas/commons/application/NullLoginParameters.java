package ljas.commons.application;

import ljas.commons.exceptions.ConnectionRefusedException;

public class NullLoginParameters extends LoginParameters {

	private static final long serialVersionUID = -5577684474064059564L;

	public NullLoginParameters(Application application) {
		super(application);
	}

	@Override
	public void check() throws ConnectionRefusedException {
		// TODO Auto-generated method stub

	}

}
