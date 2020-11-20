package com.david.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient implements Runnable {

    public static final String SERVER_ADDRESS = "127.0.0.1";
    private final Scanner scanner;
    private ClientSocket clientSocket;

    public ChatClient() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o cliente: " + e.getMessage());
        }
    }

    private void start() throws IOException {
        try {
            clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));

            System.out.println(
                    "Cliente conectado ao servidor em " + SERVER_ADDRESS
                    + ":" + ChatServer.PORT);
            new Thread(this).start();
            messageLoop();
        } finally {
            clientSocket.close();
        }

    }

    private void messageLoop() throws IOException {
        String msg;
        do {
            System.out.print("Digite uma mensagem ou sair para finalizar: ");
            msg = scanner.nextLine();
            clientSocket.sendMsg(msg);
        } while (!(msg).equalsIgnoreCase("sair"));
        clientSocket.close();
    }

    @Override
    public void run() {
        String msg;
        while ((msg = clientSocket.getMessage()) != null) {
            System.out.printf("Mensagem recebida do servidor %s\n", msg);
        }
    }

}
