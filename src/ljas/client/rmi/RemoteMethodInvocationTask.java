package ljas.client.rmi;

import java.lang.reflect.Method;
import java.util.Arrays;

import ljas.application.Application;
import ljas.client.Client;
import ljas.exception.TaskException;
import ljas.tasking.facades.SimpleClientToServerTask;
import ljas.tasking.step.ExecutingContext;

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
		if (parameterTypes != null) {
			this.parameterTypes = Arrays.copyOf(parameterTypes,
					parameterTypes.length);
		}
		if (args != null) {
			this.args = Arrays.copyOf(args, args.length);
			;
		}
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
