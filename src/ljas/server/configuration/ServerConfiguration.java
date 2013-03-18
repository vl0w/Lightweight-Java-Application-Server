package ljas.server.configuration;

public interface ServerConfiguration {

	int getMaximumClients();

	int getPort();

	String getHostName();

	int getMaxTaskWorkerCount();

	String getLog4JFilePath();

}