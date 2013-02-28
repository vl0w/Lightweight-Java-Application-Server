package ljas.commons.tasking.flow;

import java.io.Serializable;

import ljas.commons.tasking.step.TaskStep;

/**
 * A {@link TaskFlow} holds and manages all the {@link TaskStep}s. It tells the
 * logic which {@link TaskStep} to execute next.
 * 
 * @author jonashansen
 * 
 */
public interface TaskFlow extends Serializable {
	TaskStep nextStep();

	TaskStep currentStep();
}
