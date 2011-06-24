package ljas.testing.server;

import ljas.commons.application.server.EmptyServerApplication;
import ljas.server.main.Server;


public class ServerStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Server(new EmptyServerApplication("ljas.testing","1.0"),"./configuration/ServerConfiguration.properties").startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
