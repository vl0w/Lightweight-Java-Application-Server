package ljas.server.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ServerPropertiesTest {

	@Test
	public void testGet_NoValueSet_MustReturnDefaultValue() {
		ServerProperties properties = new ServerProperties();
		assertEquals(Property.PORT.defaultValue, properties.get(Property.PORT));
	}

	@Test
	public void testGet_ValueSet_MustReturnValue() {
		ServerProperties properties = new ServerProperties();
		properties.set(Property.PORT, 600);
		assertEquals(600, properties.get(Property.PORT));
	}

	@Test
	public void testGet_NoValueSetAndNoDefaultValue_MustReturnNull() {
		ServerProperties properties = new ServerProperties();
		assertNull(properties.get(Property.LOG4J_PATH));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSet_IllegalCast_ThrowException() {
		ServerProperties properties = new ServerProperties();
		properties.set(Property.MAXIMUM_CLIENTS, "50");
	}

	@Test
	public void testIsSet_IsSet_MustReturnTrue() {
		ServerProperties properties = new ServerProperties();
		properties.set(Property.PORT, 600);
		assertTrue(properties.isSet(Property.PORT));
	}

	@Test
	public void testIsSet_OnlyDefaultValue_MustReturnTrue() {
		ServerProperties properties = new ServerProperties();
		assertTrue(properties.isSet(Property.PORT));
	}

	@Test
	public void testIsSet_IsNotSetAndNoDefaultValue_MustReturnFalse() {
		ServerProperties properties = new ServerProperties();
		assertFalse(properties.isSet(Property.LOG4J_PATH));
	}

}
