package ljas.client.rmi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ljas.client.Client;
import ljas.commons.exceptions.ApplicationException;
import ljas.commons.tasking.Task;

import org.junit.Test;

public class RemoteMethodInvocationHandlerTest {
	@Test
	public void testInvoke_TaskReturnsValue_MustReturnValue() throws Throwable {
		RemoteMethodInvocationTask executedTask = mock(RemoteMethodInvocationTask.class);
		when(executedTask.getReturnValue()).thenReturn("Hello");

		Client client = mock(Client.class);
		when(client.runTaskSync(any(Task.class))).thenReturn(executedTask);

		RemoteMethodInvocationHandler handler = new RemoteMethodInvocationHandler(
				client);

		assertEquals("Hello",
				handler.invoke(null, getClass().getMethods()[0], null));
	}

	@Test(expected = ApplicationException.class)
	public void testInvoke_TaskThrowsException_MustThrowException()
			throws Throwable {
		RemoteMethodInvocationTask executedTask = mock(RemoteMethodInvocationTask.class);
		when(executedTask.getReturnValue()).thenReturn("Hello");

		Client client = mock(Client.class);
		when(client.runTaskSync(any(Task.class))).thenThrow(
				new ApplicationException(""));

		RemoteMethodInvocationHandler handler = new RemoteMethodInvocationHandler(
				client);
		handler.invoke(null, getClass().getMethods()[0], null);
	}
}
