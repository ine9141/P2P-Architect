package core.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PeerHandler implements Runnable {
    private static String[] peerIPs = new String[4];
    private static String[] peerPorts = new String[4];
    public static int peerNum = 0;
    private static final Object lock = new Object();
    private Socket centralServerSocket;

    public PeerHandler(Socket socket) {
        this.centralServerSocket = socket;
    }

    public static String getPeerIP(int client){
        return peerIPs[client];
    }

    public static String getPeerPort(int client){
        return peerPorts[client];
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(centralServerSocket.getInputStream()));

            String[] serverInfo;
            String peerIP;
            String peerPort;

            while (true) {

                serverInfo = in.readLine().split(":");
                peerIP = serverInfo[0];
                peerPort = serverInfo[1];

                Boolean insert = true;
                for (int i = 0; i < peerNum; i++)
                    if (peerIPs[i].equals(peerIP) && peerPorts[i].equals(peerPort)) insert = false;

                if (insert) {
                    synchronized (lock) {
                        peerIPs[peerNum] = peerIP;
                        peerPorts[peerNum] = peerPort;
                        peerNum++;
                    }
                    System.out.println("새로운 Peer : " + peerIP + ":" + peerPort);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}