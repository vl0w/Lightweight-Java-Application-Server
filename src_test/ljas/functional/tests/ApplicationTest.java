package ljas.functional.tests;

import static org.junit.Assert.assertEquals;
import ljas.client.Client;
import ljas.functional.ServerTestCase;
import ljas.functional.application.TestApplication;

import org.junit.Test;

public class ApplicationTest extends ServerTestCase {
	@Test
	public void testApplication() throws Exception {
		Client client = createAndConnectClient();
		TestApplication application = client.getApplication();
		assertEquals(15, application.getSum(10, 5));
	}
}
