package ljas.commons.application.client;

public class NullClientApplication extends ClientApplication {

	public NullClientApplication(String name, String version) {
		super(name, version);
	}

	@Override
	public void start() {
		// nothing
	}
}
