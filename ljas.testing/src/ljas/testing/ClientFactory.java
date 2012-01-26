package ljas.testing;

import java.io.IOException;
import ljas.server.main.ServerConfiguration;

public abstract class ClientFactory {
	public static ServerConfiguration getServerConfiguration()
			throws IOException {
		return new ServerConfiguration(
				"./configuration/ServerConfiguration.properties");
	}




}
