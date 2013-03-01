package ljas.commons.application.client;

import ljas.commons.application.client.ClientApplication;

/**
 * Provides a minimalistic standard-implementation
 * 
 * @author jonashansen
 * 
 */
public class ClientApplicationAdapter extends ClientApplication {

	public ClientApplicationAdapter(String name, String version) {
		super(name, version);
	}

	@Override
	public void start() {
		// nothing
	}

}
