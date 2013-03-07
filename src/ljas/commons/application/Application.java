package ljas.commons.application;

import org.apache.log4j.Logger;

public abstract class Application {
	private final String version;
	private final String name;

	public String getVersion() {
		return version;
	}

	public String getName() {
		return name;
	}

	public Logger getLogger() {
		return Logger.getLogger(this.getClass());
	}

	public long getApplicationId() {
		return name.hashCode();
	}

	public Application(String name, String version) {
		this.name = name;
		this.version = version;
	}
}
