package application.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ClientUtil {

	public static String communicateWithServer(String command, String request) {
		ExecutorService es = Executors.newCachedThreadPool();
		SocketClientCallable commandWithSocket = new SocketClientCallable("localhost", 9001, command, request);

		Future<String> response = es.submit(commandWithSocket);
		try {
			return response.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
