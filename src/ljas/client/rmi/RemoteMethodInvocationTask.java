package ljas.client.rmi;

import java.lang.reflect.Method;

import ljas.client.Client;
import ljas.commons.application.Application;
import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.facades.SimpleClientToServerTask;
import ljas.commons.tasking.step.ExecutingContext;

public class RemoteMethodInvocationTask extends SimpleClientToServerTask {
	private static final long serialVersionUID = -4357127019879896820L;

	private Object[] args;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object returnValue;

	public RemoteMethodInvocationTask(Client client, Method method,
			Object[] args) {
		this(client, method.getName(), method.getParameterTypes(), args);
	}

	public RemoteMethodInvocationTask(Client client, String methodName,
			Class<?>[] parameterTypes, Object[] args) {
		super(client);
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		this.args = args;
	}

	public Object getReturnValue() {
		return returnValue;
	}

	@Override
	public void perform(ExecutingContext context) throws TaskException {
		Application application = context.getApplication();
		try {
			Method method = application.getClass().getMethod(methodName,
					parameterTypes);

			returnValue = method.invoke(application, args);
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}
}
