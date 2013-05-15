package ljas.exception;

import java.util.List;

public class ApplicationException extends TaskException {
	private static final long serialVersionUID = 7169926723008873161L;

	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable t) {
		super(t);
	}

	public ApplicationException(String message, Throwable t) {
		super(message, t);
	}

	public ApplicationException(List<TaskException> taskExceptions) {
		super(buildMessageForMultipleTaskExceptions(taskExceptions));
	}

	private static String buildMessageForMultipleTaskExceptions(
			List<TaskException> taskExceptions) {
		if (taskExceptions.size() == 1) {
			return taskExceptions.get(0).getMessage();
		} else {
			final String lineSeparator = System.getProperty("line.separator");
			StringBuilder sb = new StringBuilder();

			sb.append("The following exceptions occured while executing a task:");
			sb.append(lineSeparator);
			for (TaskException taskException : taskExceptions) {
				sb.append(taskException.getMessage());
				sb.append(lineSeparator);
			}

			return sb.toString();
		}
	}
}
