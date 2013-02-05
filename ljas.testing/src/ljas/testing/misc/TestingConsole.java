package ljas.testing.misc;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ljas.commons.client.Client;
import ljas.commons.exceptions.ConnectionRefusedException;
import ljas.commons.tasking.task.Task;
import ljas.commons.tasking.task.TaskObserverAdapter;
import ljas.commons.tasking.taskqueue.TaskController;
import ljas.commons.tools.QueueUtils;
import ljas.commons.worker.TaskWorker;
import ljas.testing.ServerManager;
import ljas.testing.ServerTestCase;
import ljas.testing.tasks.SleepTask;

/**
 * Quick and dirty
 * 
 * @author jonashansen
 * 
 */
public class TestingConsole {
	private static final String DEFAULT_CLIENT_INSTANCE = "DefaultTestingClient";
	private static Map<String, Client> _clientMap;
	private static Scanner _scanner;
	private static String _lastCommand;

	static {
		_clientMap = new HashMap<>();
		try {
			_clientMap.put(DEFAULT_CLIENT_INSTANCE,
					ServerTestCase.createClient());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Scanner getScanner() {
		if (_scanner == null) {
			_scanner = new Scanner(System.in);
		}
		return _scanner;
	}

	public static void main(String[] args) throws Exception {
		// Start by default
		ServerManager.getServer().startup();

		String input;
		do {
			input = getScanner().nextLine();

			if (input.isEmpty()) {
				input = _lastCommand;
			}

			_lastCommand = input;
			switch (input) {
			case "start":
			case "on":
				ServerManager.getServer().startup();
				break;
			case "kill":
			case "stop":
			case "off":
				ServerManager.getServer().shutdown();
				break;
			case "connect":
				connectClient();
				break;
			case "disconnect":
				disconnectClient();
				break;
			case "clients":
				printClients();
				break;
			case "sleep":
			case "zzz":
				sendSleep(askUserFor("Amount?", 1000), askUserFor("MS?", 1000),
						false);
				break;
			case "sleepl":
				sendSleep(askUserFor("Amount?", 1), askUserFor("MS?", 100),
						true);
				break;
			case "workload":
			case "wl":
				printWorkload();
				break;
			case "exit":
				System.out.println("Bye");
				break;
			default:
				System.out.println("Unknown command");
				break;
			}

		} while (!input.equals("exit"));
		ServerManager.getServer().shutdown();
	}

	private static void printClients() {
		StringBuilder sb = new StringBuilder();
		for (String clientIdentifier : _clientMap.keySet()) {
			Client client = _clientMap.get(clientIdentifier);
			sb.append(clientIdentifier);
			sb.append(" ");
			if (client.isOnline()) {
				sb.append("connected");
			} else {
				sb.append("disconnected");
			}
			sb.append("\n");
		}
		System.out.println(sb);
	}

	private static void connectClient() throws ConnectException,
			ConnectionRefusedException {
		try {
			Client client = ServerTestCase.createAndConnectClient();
			String clientIdentifier = askUserFor("Client instance name",
					DEFAULT_CLIENT_INSTANCE);
			_clientMap.put(clientIdentifier, client);
			System.out.println("Client " + clientIdentifier
					+ " connected and registered");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static void disconnectClient() {
		String clientIdentifier = askUserFor("Client instance name",
				DEFAULT_CLIENT_INSTANCE, _clientMap.keySet());
		if (_clientMap.containsKey(clientIdentifier)) {
			_clientMap.get(clientIdentifier).disconnect();
			_clientMap.remove(clientIdentifier);
		} else {
			System.out.println("No such client found");
		}
	}

	private static void printWorkload() throws IOException {
		TaskController taskController = ServerManager.getServer()
				.getTaskController();
		List<TaskWorker> workerList = ServerManager.getServer()
				.getWorkerController().getWorkers(TaskWorker.class);
		for (TaskWorker taskWorker : workerList) {
			List<Task> taskList = QueueUtils.toList(taskWorker.getTaskQueue());
			long estimatedExecutionTime = taskController
					.getEstimatedExecutionTime(taskList);
			int taskCount = taskList.size();
			System.out.println(taskWorker + ": " + taskCount + " tasks / "
					+ estimatedExecutionTime + "ms");
		}
	}

	private static String askUserFor(String what, String defaultValue) {
		return askUserFor(what, defaultValue, null);
	}

	private static String askUserFor(String what, String defaultValue,
			Collection<?> availableValues) {
		System.out.println("Please specify: " + what);
		System.out.println("Default value is: " + defaultValue);
		if (availableValues != null) {
			System.out.println("Available values: " + availableValues);
		}
		String input = getScanner().nextLine();

		if (input.isEmpty()) {
			return defaultValue;
		} else {
			if (availableValues != null && !availableValues.contains(input)) {
				return defaultValue;
			}
			return input;
		}
	}

	private static int askUserFor(String what, int defaultValue) {
		System.out.println("Please specify: " + what);
		System.out.println("Default value is: " + defaultValue);
		String input = getScanner().nextLine();
		if (input.isEmpty()) {
			return defaultValue;
		} else {
			return Integer.valueOf(input);
		}
	}

	private static void sendSleep(int amount, int ms, final boolean log)
			throws ConnectException, ConnectionRefusedException {
		Client client = _clientMap.get(DEFAULT_CLIENT_INSTANCE);
		if (!client.isOnline()) {
			ServerTestCase.connectClient(client);
		}
		for (int i = 0; i < amount; i++) {
			Task task = new SleepTask(ms);

			task.addObserver(new TaskObserverAdapter() {
				@Override
				public void notifyExecuted(Task task) {
					if (log) {
						System.out.println("Sleep executed on server.");
					}
				}
			});

			client.runTaskAsync(task);
		}
	}
}
