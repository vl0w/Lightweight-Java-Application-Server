package ljas.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfiguration {
	private final Properties properties;

	public int getMaximumClients() {
		return Integer.valueOf(properties
				.getProperty("MaximumClientConnections"));
	}

	public int getPort() {
		return Integer.valueOf(properties.getProperty("Port"));
	}

	public String getHostName() {
		return properties.getProperty("HostName");
	}

	public String getHostContact() {
		return properties.getProperty("HostContact");
	}

	public String getMessasgeOfTheDay() {
		return properties.getProperty("MessageOfTheDay");
	}

	public int getMaxTaskWorkerCount() {
		return Integer.valueOf(properties.getProperty("MaxTaskWorkers"));
	}

	public String getLog4JFilePath() {
		return properties.getProperty("Log4j");
	}

	public ServerConfiguration(String propertyFilePath) throws IOException {
		properties = new Properties();
		load(propertyFilePath);
	}

	private void load(String propertyFilePath) throws IOException {
		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(propertyFilePath));
		properties.load(stream);
		stream.close();
	}

	@Override
	public String toString() {
		return properties.toString();
	}

}
