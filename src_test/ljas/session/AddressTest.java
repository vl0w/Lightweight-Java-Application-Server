package ljas.session;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AddressTest {

	@Test
	public void testToString() {
		Address address = new Address("localhost", 9000);
		assertEquals("localhost:9000", address.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFromString_NoDelimiter() {
		Address.parseFromString("locahost9000");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFromString_NoPort() {
		Address.parseFromString("localhost:");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseFromString_NoHost() {
		Address.parseFromString(":9000");
	}

	@Test
	public void testParseFromString() {
		Address address = Address.parseFromString("localhost:9000");
		assertEquals("localhost", address.getHost());
		assertEquals(9000, address.getPort());
	}

}
