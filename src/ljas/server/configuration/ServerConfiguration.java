package ljas.server.configuration;

public interface ServerConfiguration {

	int getMaximumClients();

	int getPort();

	String getLog4JFilePath();

}