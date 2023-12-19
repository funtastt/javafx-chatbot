package dslite.chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private ServerSocket serverSocket;
    private List<Client> clients = new ArrayList<>();

    public void start() {
        try {
            serverSocket = new ServerSocket(5555);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

                Client client = new Client(input, output, this);
                clients.add(client);

                new Thread(client).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessage(String message, Client client) {
        for (Client c : clients) {
            if (c.equals(client)) {
                continue;
            }

            try {
                c.getOutput().write(message + "\n");
                c.getOutput().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();
    }

    static class Client implements Runnable {

        private BufferedReader input;
        private BufferedWriter output;
        private ChatServer server;

        public Client(BufferedReader input, BufferedWriter output, ChatServer chatServer) {
            this.input = input;
            this.output = output;
            this.server = chatServer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = input.readLine();
                    server.sendMessage(message, this);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public BufferedWriter getOutput() {
            return output;
        }
    }
}
