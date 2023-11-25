package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class ClientList {
    private static HashMap<Integer, Socket> clients = new HashMap<>();
    private static HashMap<Socket, ObjectOutputStream> outputStreams = new HashMap<>();
    private static HashMap<Socket, ObjectInputStream> inputStreams = new HashMap<>();

    public void setClients(int num, Socket socket) throws IOException {
        clients.put(num, socket);
        outputStreams.put(socket, new ObjectOutputStream(socket.getOutputStream()));
        inputStreams.put(socket, new ObjectInputStream(socket.getInputStream()));
    }

    public HashMap<Integer, Socket> getClients() {
        return clients;
    }

    public HashMap<Socket, ObjectInputStream> getInputStreams() {
        return inputStreams;
    }

    public HashMap<Socket, ObjectOutputStream> getOutputStreams() {
        return outputStreams;
    }
}
