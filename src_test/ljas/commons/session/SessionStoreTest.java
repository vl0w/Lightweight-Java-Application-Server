package ljas.commons.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class SessionStoreTest {
	@Test
	public void testPut_HashCodeAddedAndReturned() {
		Session session = mock(Session.class);
		assertEquals(session.hashCode(), SessionStore.put(session));
	}

	@Test
	public void testRemove_SessionByHashCodeFound_MustReturnSession() {
		Session session = mock(Session.class);
		SessionStore.put(session);
		assertEquals(session, SessionStore.findSession(session.hashCode()));
	}

	@Test
	public void testRemove_SessionByHashCodeFound_MustReturnNull() {
		assertNull(SessionStore.findSession(123));
	}
}
