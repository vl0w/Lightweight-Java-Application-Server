package ljas.server.configuration;

public interface ServerConfiguration {

	public abstract int getMaximumClients();

	public abstract int getPort();

	public abstract String getHostName();

	public abstract String getHostContact();

	public abstract int getMaxTaskWorkerCount();

	public abstract String getLog4JFilePath();

}