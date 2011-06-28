package ljas.commons.application.client;

import ljas.commons.application.Application;
import ljas.commons.client.Client;

public abstract class ClientApplication extends Application {
	private Client _client;

	public Client getClient() {
		return _client;
	}

	public void setClient(Client value) {
		_client = value;
	}

	public ClientApplication(String name, String version) {
		super(name, version);
	}
}
