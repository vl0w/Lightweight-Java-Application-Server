package ljas.application.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CacheTest {

	@Test
	public void testPut() {
		CacheKey key = new CacheKey(getClass().getMethods()[0]);

		Cache.put(key, "Hello");
		Cache.put(key, "Hello World");

		assertEquals("Hello World", Cache.get(key));
	}

	@Test
	public void testContains() {
		CacheKey existingKey = new CacheKey(getClass().getMethods()[0]);
		CacheKey notExistingKey = new CacheKey(getClass().getMethods()[1]);

		Cache.put(existingKey, "Hello World");

		assertTrue(Cache.contains(existingKey));
		assertFalse(Cache.contains(notExistingKey));
	}

	@Test
	public void testFlush() {
		CacheKey key = new CacheKey(getClass().getMethods()[0]);

		Cache.put(key, "Hello World");
		Cache.flush();

		assertFalse(Cache.contains(key));
	}
}
