package ljas.testing.tests;

import java.io.IOException;
import java.net.ConnectException;

import ljas.commons.application.LoginParametersImpl;
import ljas.commons.application.client.ClientApplicationAdapter;
import ljas.commons.client.Client;
import ljas.commons.client.ClientImpl;
import ljas.commons.client.EmptyUI;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.server.main.ServerConfiguration;

public abstract class Constants {
	public static String IP = "127.0.0.1";
	public static int PORT = 1666;
	public static String APPLICATION_ID = "ljas.testing";
	public static String APPLICATION_VERSION = "1.0";

	public static ServerConfiguration getServerConfiguration()
			throws IOException {
		return new ServerConfiguration(
				"./configuration/ServerConfiguration.properties");
	}

	public static Client createClient(String applId, String applVersion) {
		return new ClientImpl(new EmptyUI(), new ClientApplicationAdapter(applId,
				applVersion));
	}

	public static Client createClient() {
		return createClient(APPLICATION_ID, APPLICATION_VERSION);
	}

	public static void doConnect(Client client) throws ConnectException,
			ConnectionRefusedException {
		client.connect(Constants.IP, Constants.PORT, new LoginParametersImpl(
				client.getApplication()));
	}
}
