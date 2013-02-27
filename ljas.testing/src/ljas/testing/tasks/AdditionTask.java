package ljas.testing.tasks;

import ljas.commons.client.Client;
import ljas.commons.tasking.facades.SimpleClientToServerTask;

public class AdditionTask extends SimpleClientToServerTask {

	private static final long serialVersionUID = 5952237921989154683L;
	private double numOne;
	private double numTwo;
	public double sum;

	public AdditionTask(Client client, double numOne, double numTwo) {
		super(client);
		this.numOne = numOne;
		this.numTwo = numTwo;
	}

	@Override
	public void perform() {
		sum = numOne + numTwo;
	}

}
