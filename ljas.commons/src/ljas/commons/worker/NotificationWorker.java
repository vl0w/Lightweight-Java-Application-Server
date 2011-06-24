package ljas.commons.worker;

import java.util.NoSuchElementException;

import ljas.commons.exceptions.TaskNotFoundException;
import ljas.commons.tasking.sendable.notification.Notification;
import ljas.commons.tasking.taskspool.WorkerController;


public class NotificationWorker extends Worker {
	public NotificationWorker(WorkerController controller) {
		super(controller, Thread.NORM_PRIORITY, false);
		setName("NotificationWorker");
	}

	@Override
	public void runItOnce() throws Exception {
		try {
			Notification notification = getController().getNotificationQueue()
					.remove();
			notification.setLocal(getController().getTaskspool().getLocal());
			notification.notificate();
		} catch (NoSuchElementException e) {
			// ignore
		} catch (TaskNotFoundException e) {
			Thread.sleep(getController().getTaskspool().getWorkerDelay());
		} catch (Exception e) {
			getLogger().error(e);
		}
	}
}
