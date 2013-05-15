package ljas.application.cache;

import java.util.HashMap;
import java.util.Map;

public final class Cache {

	private static Cache instance;
	private Map<CacheKey, Object> cache;

	public static void flush() {
		getInstance().cache.clear();
	}

	public static void put(CacheKey key, Object value) {
		getInstance().cache.put(key, value);
	}

	public static Object get(CacheKey key) {
		return getInstance().cache.get(key);
	}

	public static boolean contains(CacheKey key) {
		return getInstance().cache.containsKey(key);
	}

	private Cache() {
		cache = new HashMap<>();
	}

	private static Cache getInstance() {
		if (instance == null) {
			instance = new Cache();
		}
		return instance;
	}

}
