package ljas.server.configuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyServerConfiguration implements ServerConfiguration {
	private final Properties properties;

	@Override
	public int getMaximumClients() {
		return Integer.valueOf(properties
				.getProperty("MaximumClientConnections"));
	}

	@Override
	public int getPort() {
		return Integer.valueOf(properties.getProperty("Port"));
	}

	@Override
	public String getHostName() {
		return properties.getProperty("HostName");
	}

	@Override
	public String getHostContact() {
		return properties.getProperty("HostContact");
	}

	@Override
	public String getMessasgeOfTheDay() {
		return properties.getProperty("MessageOfTheDay");
	}

	@Override
	public int getMaxTaskWorkerCount() {
		return Integer.valueOf(properties.getProperty("MaxTaskWorkers"));
	}

	@Override
	public String getLog4JFilePath() {
		return properties.getProperty("Log4j");
	}

	public PropertyServerConfiguration(String propertyFilePath)
			throws IOException {
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
