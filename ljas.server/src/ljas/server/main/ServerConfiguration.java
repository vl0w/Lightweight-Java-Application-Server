package ljas.server.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerConfiguration {
	// MEMBERS
	private final Properties _properties;
	
	// GETTERS & SETTERS
	public int getMaximumClients(){
		return Integer.valueOf(_properties.getProperty("MaximumClientConnections"));
	}
	
	public int getPort(){
		return Integer.valueOf(_properties.getProperty("Port"));
	}
	
	public int getJettyPort(){
		return Integer.valueOf(_properties.getProperty("JettyPort"));
	}
	
	public String getHostName(){
		return _properties.getProperty("HostName");
	}
	
	public String getHostContact(){
		return _properties.getProperty("HostContact");
	}
	
	public String getMessasgeOfTheDay(){
		return _properties.getProperty("MessageOfTheDay");
	}
	
	public int getTaskWorkerCount(){
		return Integer.valueOf(_properties.getProperty("TaskWorkers"));
	}
	
	public int getMaximumTaskCount(){
		return Integer.valueOf(_properties.getProperty("MaximumTasks"));
	}
	
	public String getLog4JFilePath(){
		return _properties.getProperty("Log4j");
	}
	
	// CONSTRUCTOR
	public ServerConfiguration(String propertyFilePath) throws IOException{
		_properties = new Properties();
		load(propertyFilePath);
	}
	
	// METHODS
	private void load(String propertyFilePath) throws IOException{
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propertyFilePath));
		_properties.load(stream);
		stream.close();
	}
	
	@Override
	public String toString(){
		return _properties.toString();
	}
	
}
