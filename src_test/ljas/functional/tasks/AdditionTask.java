package ljas.functional.tasks;

import ljas.client.Client;
import ljas.commons.tasking.facades.SimpleClientToServerTask;
import ljas.commons.tasking.step.ExecutingContext;

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
	public void perform(ExecutingContext context) {
		sum = numOne + numTwo;
	}
}
