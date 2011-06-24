package ljas.commons.application.client;

import ljas.commons.application.client.ClientApplication;

/**
 * Provides a minimalistic standard-implementation
 * @author jonashansen
 *
 */
public class EmptyClientApplication extends ClientApplication {

	public EmptyClientApplication(String name, String version) {
		super(name, version);
	}


	@Override
	public void start() {
		// nothing
		
	}


}
