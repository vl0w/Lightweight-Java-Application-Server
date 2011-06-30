package ljas.testing.server;

import ljas.commons.application.server.ServerApplicationAdapter;
import ljas.server.main.Server;

public class ServerStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Server(new ServerApplicationAdapter("ljas.testing", "1.0"),
					"./configuration/ServerConfiguration.properties").startup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
