package com.david.chat;

import java.io.*;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChatServer {

    public static final int PORT = 4000;
    private ServerSocket serverSocket;
    private final List<ClientSocket> clients = new LinkedList<>();

    private void start() throws IOException {

        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciado na porta " + PORT);
        clientConnectionLoop();
    }

    public static void main(String[] args) {

        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
        System.out.println("Servidor Finalizado");
    }

    private void clientConnectionLoop() throws IOException {
        while (true) {
            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            clients.add(clientSocket);

            new Thread(() -> clientMessageLoop(clientSocket)).start();
        }
    }

    private void clientMessageLoop(ClientSocket clientSocket) {
        String msg;
        try {
            while ((msg = clientSocket.getMessage()) != null) {
                if ("sair".equalsIgnoreCase(msg)) {
                    return;
                }
                System.out.printf("Mensagem recebida do cliente %s: %s\n", clientSocket.getRemoteSocketAdress(), msg);
                sendMsgToAll(clientSocket, msg);
            }
        } finally {
            clientSocket.close();
        }
    }

    private void sendMsgToAll(ClientSocket sender, String msg) {
        Iterator<ClientSocket> iterator = clients.iterator();
        while (iterator.hasNext()) {
            ClientSocket clientSocket = iterator.next();
            if (!sender.equals(clientSocket)) {
                if (!clientSocket.sendMsg("client " + sender.getRemoteSocketAdress() + ": " + msg)) {
                    iterator.remove();
                }

            }
        }
    }

    private void stop() throws IOException {
        System.out.println("Finalizando servidor");
        serverSocket.close();
    }
}
