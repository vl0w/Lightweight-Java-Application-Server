package ljas.client.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import ljas.application.ApplicationAnalyzer;
import ljas.application.cache.Cache;
import ljas.application.cache.CacheKey;
import ljas.client.Client;
import ljas.exception.ApplicationException;

public class RemoteMethodInvocationHandler implements InvocationHandler {

	private Client client;

	public RemoteMethodInvocationHandler(Client client) {
		this.client = client;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		boolean isCachedMethod = ApplicationAnalyzer.isCached(client
				.getApplication().getClass(), method);

		if (isCachedMethod) {
			return findInCache(method, args);
		} else {
			return executeRemoteRequest(method, args);
		}
	}

	private Object findInCache(Method method, Object[] args)
			throws ApplicationException {
		CacheKey key = new CacheKey(method, args);
		if (!Cache.contains(key)) {
			Object value = executeRemoteRequest(method, args);
			Cache.put(key, value);
		}
		return Cache.get(key);
	}

	private Object executeRemoteRequest(Method method, Object[] args)
			throws ApplicationException {
		if (!client.isOnline()) {
			throw new ApplicationException("Client is not online");
		}

		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, method, args);
		RemoteMethodInvocationTask invokedTask = client.runTaskSync(task);

		return invokedTask.getReturnValue();
	}
}
