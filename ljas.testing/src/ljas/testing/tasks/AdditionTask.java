package ljas.testing.tasks;

import ljas.commons.exceptions.TaskException;
import ljas.commons.tasking.Task;

public class AdditionTask extends Task {
	private static final long serialVersionUID = -1235036848424689310L;

	private double numOne;
	private double numTwo;
	public double result;

	public AdditionTask(double numOne, double numTwo) {
		this.numOne = numOne;
		this.numTwo = numTwo;
	}

	@Override
	public void performTask() throws TaskException {
		result = numOne + numTwo;
	}

}
