package ljas.commons.client;

import ljas.commons.state.RefusedMessage;
import ljas.commons.state.WelcomeMessage;
import ljas.commons.tasking.task.Task;

public interface ClientUI {
	public void handleStart();

	public Client getClient();

	public void setClient(Client client);

	public void handleConnected(WelcomeMessage welcomeMessage);

	public void handleConnectionFailed(RefusedMessage refusedMessage);

	public void handleDisconnected();
}
