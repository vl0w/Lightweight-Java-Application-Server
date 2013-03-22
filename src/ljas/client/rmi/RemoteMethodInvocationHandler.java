package ljas.client.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ljas.client.Client;

public class RemoteMethodInvocationHandler implements InvocationHandler {

	private Client client;

	public RemoteMethodInvocationHandler(Client client) {
		this.client = client;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, method, args);
		RemoteMethodInvocationTask invokedTask = client.runTaskSync(task);

		return invokedTask.getReturnValue();
	}
}
