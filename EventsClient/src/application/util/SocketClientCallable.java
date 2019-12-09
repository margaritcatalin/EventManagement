package application.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import java.util.concurrent.Callable;

public class SocketClientCallable implements Callable<String> {

    private String host;
    private int port;
    private Socket socket;
    private String command;
    private String data;

    public SocketClientCallable(String host, int port, String command,
                                String data) {
        this.host = host;
        this.port = port;
        this.command = command;
        this.data = data;
    }

    @Override
    public String call() throws IOException {
        try {
            this.socket = new Socket(this.host, this.port);
            BufferedWriter bufferedOutputWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedInputReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), "UTF-8"));

            bufferedOutputWriter.write(command + "\n" + data);
            bufferedOutputWriter.newLine();
            bufferedOutputWriter.flush();

            return bufferedInputReader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
