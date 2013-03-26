package ljas.functional;

import ljas.server.configuration.ServerConfiguration;

public class TestingServerConfiguration implements ServerConfiguration {

	@Override
	public int getMaximumClients() {
		return 5;
	}

	@Override
	public int getPort() {
		return 1666;
	}

	@Override
	public String getHostName() {
		return "LJAS-TESTING";
	}

	@Override
	public int getMaxTaskWorkerCount() {
		return 10;
	}

	@Override
	public String getLog4JFilePath() {
		return "./log4j-tests.xml";
	}

}
