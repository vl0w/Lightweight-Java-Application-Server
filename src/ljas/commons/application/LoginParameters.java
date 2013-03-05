package ljas.commons.application;

import java.io.Serializable;

import ljas.commons.exceptions.ConnectionRefusedException;

public abstract class LoginParameters implements Serializable {
	private static final long serialVersionUID = -4540369418091795186L;

	private String applicationVersion;
	private long applicationId;

	public LoginParameters(Application application) {
		applicationVersion = application.getVersion();
		applicationId = application.getApplicationId();
	}

	public String getApplicationVersion() {
		return applicationVersion;
	}

	public long getApplicationId() {
		return applicationId;
	}

	public abstract void check() throws ConnectionRefusedException;

}
