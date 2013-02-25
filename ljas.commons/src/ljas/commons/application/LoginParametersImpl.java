package ljas.commons.application;

import ljas.commons.exceptions.ConnectionRefusedException;

public class LoginParametersImpl implements LoginParameters {
	private static final long serialVersionUID = -6656736060800905192L;
	private String applicationVersion;
	private long applicationId;

	public LoginParametersImpl(String applicationVersion, long applicationId) {
		this.applicationVersion = applicationVersion;
		this.applicationId = applicationId;
	}

	public LoginParametersImpl(Application application) {
		applicationVersion = application.getVersion();
		applicationId = application.getApplicationId();
	}

	@Override
	public String getApplicationVersion() {
		return applicationVersion;
	}

	@Override
	public long getApplicationId() {
		return applicationId;
	}

	@Override
	public void check() throws ConnectionRefusedException {
		// nothing
	}

}
