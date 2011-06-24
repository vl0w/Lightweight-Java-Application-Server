package ljas.commons.application;

import ljas.commons.application.LoginParameters;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.network.SendsTasks;

public class LoginParametersImpl implements LoginParameters {
	private static final long serialVersionUID = -6656736060800905192L;
	private String _applicationVersion;
	private long _applicationId;
	
	public LoginParametersImpl(String applicationVersion, long applicationId){
		_applicationVersion=applicationVersion;
		_applicationId=applicationId;
	}
	
	public LoginParametersImpl(Application application){
		_applicationVersion=application.getVersion();
		_applicationId=application.getApplicationId();
	}

	@Override
	public String getApplicationVersion() {
		return _applicationVersion;
	}
	
	@Override
	public long getApplicationId() {
		return _applicationId;
	}

	@Override
	public void check(SendsTasks server) throws ConnectionRefusedException {
		// nothing
	}


}
