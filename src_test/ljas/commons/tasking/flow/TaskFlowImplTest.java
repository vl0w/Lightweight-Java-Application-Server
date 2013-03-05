package ljas.commons.tasking.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import ljas.commons.tasking.step.TaskStep;

import org.junit.Test;

public class TaskFlowImplTest {

	@Test
	public void testAddStep_StepInFlow() {
		TaskStep step = mock(TaskStep.class);
		TaskFlowImpl flow = new TaskFlowImpl();
		flow.addStep(step);
		assertTrue(flow.getSteps().contains(step));
	}

	@Test
	public void testNextStep_NextStepGetsRemoved() {
		TaskStep step1 = mock(TaskStep.class);
		TaskStep step2 = mock(TaskStep.class);

		TaskFlowImpl flow = new TaskFlowImpl();
		flow.addStep(step1);
		flow.addStep(step2);

		assertEquals(2, flow.getSteps().size());
		assertEquals(step1, flow.nextStep());
		assertEquals(1, flow.getSteps().size());
		assertEquals(step2, flow.nextStep());
	}

	@Test
	public void testCurrentStep_EqualsLastRemovedStep() {
		TaskStep step1 = mock(TaskStep.class);

		TaskFlowImpl flow = new TaskFlowImpl();
		flow.addStep(step1);
		TaskStep nextStep = flow.nextStep();

		assertEquals(step1, nextStep);
		assertEquals(step1, flow.currentStep());
	}

}
