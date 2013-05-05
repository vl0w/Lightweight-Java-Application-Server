package ljas.client.rmi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import ljas.application.Application;
import ljas.client.Client;
import ljas.exception.ApplicationException;
import ljas.tasking.Task;

import org.junit.Test;

public class RemoteMethodInvocationHandlerTest {
	@Test
	public void testInvoke_TaskReturnsValue_MustReturnValue() throws Throwable {
		Client client = mock(Client.class);
		when(client.getApplication()).thenReturn(mock(Application.class));
		when(client.isOnline()).thenReturn(Boolean.TRUE);
		mockTask(client);

		RemoteMethodInvocationHandler handler = new RemoteMethodInvocationHandler(
				client);

		assertEquals("Hello",
				handler.invoke(null, getClass().getMethods()[0], null));
	}

	private void mockTask(Client client) throws ApplicationException {
		RemoteMethodInvocationTask executedTask = mock(RemoteMethodInvocationTask.class);
		when(executedTask.getReturnValue()).thenReturn("Hello");

		when(client.runTaskSync(any(Task.class))).thenReturn(executedTask);

	}

	@Test(expected = ApplicationException.class)
	public void testInvoke_TaskThrowsException_MustThrowException()
			throws Throwable {
		Client client = mock(Client.class);
		when(client.getApplication()).thenReturn(mock(Application.class));
		when(client.isOnline()).thenReturn(Boolean.TRUE);
		when(client.runTaskSync(any(Task.class))).thenThrow(
				new ApplicationException(""));

		RemoteMethodInvocationHandler handler = new RemoteMethodInvocationHandler(
				client);
		handler.invoke(null, getClass().getMethods()[0], null);
	}
}
