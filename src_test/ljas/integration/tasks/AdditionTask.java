package ljas.integration.tasks;

import ljas.client.Client;
import ljas.tasking.facades.SimpleClientToServerTask;
import ljas.tasking.step.ExecutingContext;

public class AdditionTask extends SimpleClientToServerTask {

	private static final long serialVersionUID = 5952237921989154683L;
	private int numOne;
	private int numTwo;
	public int sum;

	public AdditionTask(Client client, int numOne, int numTwo) {
		super(client);
		this.numOne = numOne;
		this.numTwo = numTwo;
	}

	@Override
	public void perform(ExecutingContext context) {
		sum = numOne + numTwo;
	}
}
