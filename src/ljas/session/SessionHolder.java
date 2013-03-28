package ljas.session;

import java.util.List;

/**
 * A class that holds and manages sessions
 * 
 * @author jonashansen
 * 
 */
public interface SessionHolder {
	void addSession(Session session);

	List<Session> getSessions();
}
