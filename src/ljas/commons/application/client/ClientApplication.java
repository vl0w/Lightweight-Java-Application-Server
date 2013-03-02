package ljas.commons.application.client;

import ljas.client.Client;
import ljas.commons.application.Application;

public abstract class ClientApplication extends Application {
	private Client client;

	public Client getClient() {
		return client;
	}

	public void setClient(Client value) {
		client = value;
	}

	public ClientApplication(String name, String version) {
		super(name, version);
	}
}
