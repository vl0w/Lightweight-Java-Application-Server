package ljas.commons.tasking.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import ljas.commons.session.Session;
import ljas.commons.tasking.Task;
import ljas.commons.tasking.step.TaskStep;
import ljas.commons.tasking.step.impl.FinishTaskStep;
import ljas.commons.tasking.step.impl.LocalNavigationStep;
import ljas.commons.tasking.step.impl.RemoteNavigationStep;
import ljas.commons.tasking.step.impl.SendBackToSenderStep;

import org.junit.Before;
import org.junit.Test;

public class TaskFlowBuilderTest {

	private TaskFlowBuilder builder;

	@Before
	public void setUp() {
		Task task = mock(Task.class);
		builder = new TaskFlowBuilder(task);
	}

	@Test
	public void testPerform_StepAdded() {
		TaskStep step = mock(TaskStep.class);
		builder.perform(step);
		assertEquals(step, builder.build().nextStep());
	}

	@Test
	public void testNavigateLocal_StepAdded() {
		builder.navigateLocal();

		TaskStep nextStep = builder.build().nextStep();
		assertEquals(LocalNavigationStep.class, nextStep.getClass());
	}

	@Test
	public void testNavigateRemote_StepAdded() {
		Session session = mock(Session.class);
		builder.navigateRemote(session);

		TaskStep nextStep = builder.build().nextStep();
		assertEquals(RemoteNavigationStep.class, nextStep.getClass());
	}

	@Test
	public void testSendBack_StepAdded() {
		builder.sendBack();

		TaskStep nextStep = builder.build().nextStep();
		assertEquals(SendBackToSenderStep.class, nextStep.getClass());
	}

	@Test
	public void testFinishTask_StepAdded() {
		builder.finishTask();

		TaskStep nextStep = builder.build().nextStep();
		assertEquals(FinishTaskStep.class, nextStep.getClass());
	}

	@Test
	public void testBuild_NoFinishTaskStep_AddAutomatically() {
		TaskStep step = mock(TaskStep.class);
		builder.perform(step);
		TaskFlow flow = builder.build();
		assertEquals(step.getClass(), flow.nextStep().getClass());
		assertEquals(FinishTaskStep.class, flow.nextStep().getClass());
	}
}
