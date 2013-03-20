package ljas.client.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ljas.client.Client;

import org.apache.log4j.Logger;

public class RemoteMethodInvocationHandler implements InvocationHandler {

	private Client client;

	public RemoteMethodInvocationHandler(Client client) {
		this.client = client;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		logDebugMessage(method, args);

		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, method, args);
		RemoteMethodInvocationTask invokedTask = (RemoteMethodInvocationTask) client
				.runTaskSync(task);

		return invokedTask.getReturnValue();
	}

	private void logDebugMessage(Method method, Object[] args) {
		Logger logger = Logger.getLogger(getClass());

		logger.debug("RMIH method: " + method);
		StringBuilder sb = new StringBuilder();
		if (args.length == 0) {
			sb = new StringBuilder("No Args");
		} else {
			for (int i = 0; i < args.length; i++) {
				sb.append("arg");
				sb.append(i);
				sb.append(": ");
				sb.append(args[i]);
				sb.append(" / ");
			}
		}
		logger.debug(sb.toString());
	}

}
