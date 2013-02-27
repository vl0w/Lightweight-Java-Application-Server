package ljas.commons.tasking.flow;

import java.io.Serializable;

import ljas.commons.tasking.step.TaskStep;

public interface TaskFlow extends Serializable {
	TaskStep nextStep();

	TaskStep currentStep();
}
