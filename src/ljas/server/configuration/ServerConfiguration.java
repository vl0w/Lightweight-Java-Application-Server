package ljas.server.configuration;

public interface ServerConfiguration {

	int getMaximumClients();

	int getPort();

	String getHostName();

	String getLog4JFilePath();

}