package ljas.tools;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ljas.tools.MapUtil;

import org.junit.Test;

public class MapUtilTest {

	@Test
	public void testSortByValue() throws Exception {
		Map<String, Integer> randomMap = new HashMap<>();
		randomMap.put("index2", 2);
		randomMap.put("index5", 5);
		randomMap.put("index4", 4);
		randomMap.put("index3", 3);
		randomMap.put("index1", 1);

		Map<String, Integer> sortedMap = MapUtil.sortByValue(randomMap);

		Iterator<String> iterator = sortedMap.keySet().iterator();
		assertEquals("index1", iterator.next());
		assertEquals("index2", iterator.next());
		assertEquals("index3", iterator.next());
		assertEquals("index4", iterator.next());
		assertEquals("index5", iterator.next());
	}
}
