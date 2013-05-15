package ljas.functional.tests;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.UndeclaredThrowableException;

import ljas.client.Client;
import ljas.functional.ServerTestCase;
import ljas.functional.application.TestApplication;

import org.junit.Test;

public class ApplicationTest extends ServerTestCase {
	@Test(timeout = TEST_TIMEOUT)
	public void testApplication() throws Exception {
		Client client = createAndConnectClient();
		TestApplication application = client.getApplication();
		assertEquals(15, application.getSum(10, 5));
	}

	@Test(timeout = TEST_TIMEOUT)
	public void testCache_SameParameters() throws Exception {
		Client client = createAndConnectClient();
		TestApplication application = client.getApplication();
		assertEquals(10, application.getSumCached(4, 6));
		getServer().shutdown();
		assertEquals(10, application.getSumCached(4, 6)); // Should be in cache
	}

	@Test(timeout = TEST_TIMEOUT, expected = UndeclaredThrowableException.class)
	public void testCache_DifferentParameters() throws Exception {
		Client client = createAndConnectClient();
		TestApplication application = client.getApplication();
		assertEquals(10, application.getSumCached(4, 6));
		getServer().shutdown();
		assertEquals(11, application.getSumCached(2, 9)); // Should *not* be in
															// cache
	}
}
