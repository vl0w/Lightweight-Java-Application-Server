package ljas.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class QueueUtils {
	public static <V> List<V> toList(Queue<V> queue){
		List<V> list = new ArrayList<V>();
		
		Iterator<V> iterator = queue.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		
		return list;
	}
}
