package core;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main{
    private static Queue<Socket> peerSockets = new ConcurrentLinkedQueue<>();
    private static String[] peerIPs = new String[4];
    private static String[] peerPorts = new String[4];
    private static int peerNum = 0;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(23921);
            System.out.println("[Server] 서버 시작.");

            new Thread(new ClientConnectionHandler(serverSocket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientConnectionHandler implements Runnable {
        private ServerSocket serverSocket;

        public ClientConnectionHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket peerSocket = serverSocket.accept();

                    String peerIP = peerSocket.getInetAddress().getHostAddress();
                    String peerPort = String.valueOf(peerSocket.getPort());

                    synchronized (lock) {
                        peerIPs[peerNum] = peerIP;
                        peerPorts[peerNum] = peerPort;
                        peerNum++;
                        peerSockets.add(peerSocket);
                    }

                    System.out.println("새로운 Client 탐지 : " + peerIP+":"+peerPort);

                    initNewPeer(peerSocket);
                    notifyPeersAboutNewPeer(peerIP, peerPort);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void notifyPeersAboutNewPeer(String peerIP, String peerPort) {
        try {
            for (Socket peerSocket : peerSockets) {
                PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);
                out.println(peerIP + ":" + peerPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initNewPeer(Socket newPeerSocket) {
        try {
            PrintWriter out = new PrintWriter(newPeerSocket.getOutputStream(), true);
            for(int i = 0; i < peerNum; i++) out.println(peerIPs[i] + ":" + peerPorts[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}