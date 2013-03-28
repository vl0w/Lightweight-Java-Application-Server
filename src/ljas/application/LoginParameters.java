package ljas.application;

import java.io.Serializable;

public class LoginParameters implements Serializable {
	private static final long serialVersionUID = -4540369418091795186L;

	private Class<? extends Application> applicationClass;

	public LoginParameters(Class<? extends Application> applicationClass) {
		this.applicationClass = applicationClass;
	}

	public Class<? extends Application> getApplicationClass() {
		return applicationClass;
	}
}
