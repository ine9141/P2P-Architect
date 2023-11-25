package core.handler;

import core.ClientList;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ClientJoinHandler {
    public static ClientList clientList = new ClientList();

    public static void waitForGetAllSocket() throws IOException {
        ServerSocket serverSocket = new ServerSocket(23921);
        System.out.println("[Server] 서버 시작.");
        for(int i = 0; i < 2 ; i++) { //i < testClientNumber 로 수정 필요
            Socket socket = null;
            while (socket == null) {
                socket = serverSocket.accept();
                System.out.println("socket = " + socket);
                clientList.setClients(i,socket);
            }
        }
    }

    public static void waitClientRequest() throws IOException, ClassNotFoundException {
        Socket sourceClient = clientList.getClients().get(0);
        Socket receiveClient = clientList.getClients().get(1);
        clientList.getOutputStreams().get(sourceClient).writeObject(receiveClient.getInetAddress().toString());
    }

    public static void giveClientInfo(int destinationClientNumber , int targetClientNumber) throws IOException {
        Socket targetClient = clientList.getClients().get(targetClientNumber);
        clientList.getOutputStreams().get(destinationClientNumber).writeObject(targetClient);
    }
}
