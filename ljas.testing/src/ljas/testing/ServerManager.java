package ljas.testing;

import java.io.IOException;
import ljas.commons.application.server.ServerApplicationAdapter;
import ljas.server.Server;

public abstract class ServerManager {
	private static Server _server;

	public static Server getServer() throws IOException{
		if(_server==null){
			_server=createServer();
		}
		return _server;
	}

	public static Server createServer() throws IOException{
		return new Server(new ServerApplicationAdapter("ljas.testing", "1.0"),
				"./configuration/ServerConfiguration.properties");
	}

	public static void startupServer() throws Exception{
		Server server = getServer();
		if(!server.isOnline()){
			server.startup();
		}
	}

	public static void shutdownServer() throws IOException{
		Server server = getServer();
		if(server.isOnline()){
			server.shutdown();
		}
	}
}
