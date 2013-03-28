package ljas.client.rmi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import ljas.application.Application;
import ljas.application.ApplicationImplementation;
import ljas.application.LoginParameters;
import ljas.client.Client;
import ljas.exception.ApplicationException;
import ljas.exception.TaskException;
import ljas.session.Session;
import ljas.tasking.environment.TaskSystem;
import ljas.tasking.environment.TaskSystemImpl;
import ljas.tasking.step.ExecutingContext;

import org.junit.Test;

public class RemoteMethodInvocationTaskTest {

	private static final String FIXVALUE = "SomeFixValue";

	@Test
	public void testPerform_NoArgs_MustReturnFixvalue() throws Exception {
		Client client = mock(Client.class);
		TaskSystem taskSystem = createTaskSystem();

		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, "fixValue", null, null);
		ExecutingContext context = new ExecutingContext(taskSystem, task);
		task.perform(context);

		assertEquals(FIXVALUE, task.getReturnValue());
	}

	@Test
	public void testPerform_WithArgs_MustConcatStrings() throws Exception {
		Client client = mock(Client.class);
		TaskSystem taskSystem = createTaskSystem();

		Class<?>[] parameterTypes = new Class<?>[] { String.class, String.class };
		Object[] args = new Object[] { "Hello", "World" };
		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, "concat", parameterTypes, args);
		ExecutingContext context = new ExecutingContext(taskSystem, task);
		task.perform(context);

		assertEquals("HelloWorld", task.getReturnValue());
	}

	@Test(expected = TaskException.class)
	public void testPerform_MethodThrowsException_TaskExceptionExpected()
			throws Exception {
		Client client = mock(Client.class);
		TaskSystem taskSystem = createTaskSystem();

		RemoteMethodInvocationTask task = new RemoteMethodInvocationTask(
				client, "throwException", null, null);
		ExecutingContext context = new ExecutingContext(taskSystem, task);
		task.perform(context);
	}

	private TaskSystem createTaskSystem() {
		return new TaskSystemImpl(new App());
	}

	@SuppressWarnings("unused")
	private class App extends ApplicationImplementation implements Application {

		public App() {
			super(Application.class);
		}

		@Override
		public void onSessionConnect(Session session, LoginParameters parameters)
				throws ApplicationException {
		}

		@Override
		public void onSessionDisconnect(Session session)
				throws ApplicationException {
		}

		public String fixValue() {
			return FIXVALUE;
		}

		public String concat(String s1, String s2) {
			return s1.concat(s2);
		}

		public void throwException() throws ApplicationException {
			throw new ApplicationException("Some Exception");
		}
	}
}
