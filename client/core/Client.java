package core;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import core.handler.FileServer;
import core.handler.FileClient;
import core.struct.Chunk;
import core.struct.MergeFile;

import static java.lang.Thread.sleep;

public class Client {
    private static Queue<Socket> peerSockets = new ConcurrentLinkedQueue<>();
    final private static int totalClients = 4;
    final private static String fileName = "D.file";
    final private static int peerPort = 4444;
    public static int peerNum = 0;
    public static int connectionNum = 0;
    private static String[] peerIPs = new String[4];
    private static String[] peerPorts = new String[4];
    final private static int chunkSize = 256000;
    final private static int numOfChunks = 2000;
    private static final Object lock = new Object();
    public static void main(String[] args) {
        try {
            //중앙 서버에 등록
            Socket socket = new Socket("127.0.0.1", 23921);
            ServerSocket serverSocket = new ServerSocket(peerPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(peerPort);
            System.out.println("서버 연결 완료");

            //내 파일을 청크화
            MergeFile mergeFile = new MergeFile();
            int myFileNum = mergeFile.filenameToIdx(fileName);
            Path filePath = Paths.get(Client.class.getResource(fileName).toURI());
            byte[] fileData = Files.readAllBytes(filePath);
            int to, from=0;
            for (int i = 0; i < numOfChunks; i++) {
                to = Math.min(from + chunkSize, fileData.length);
                byte[] chunkData = new byte[to - from];
                System.arraycopy(fileData, from, chunkData, 0, to - from);
                Chunk chunk = new Chunk(myFileNum, i, chunkData);
                mergeFile.addChunk(chunk,myFileNum);
                from = to;
            }

            //서버로부터 클라이언트들의 IP와 Port를 받아오는 클래스
            while (peerNum<totalClients) {

                String[] serverInfo = in.readLine().split(":");
                String _peerIP = serverInfo[0];
                String _peerPort = serverInfo[1];

                boolean insert = true;
                for (int i = 0; i < peerNum; i++)
                    if (peerIPs[i].equals(_peerIP) && peerPorts[i].equals(_peerPort)) insert = false;

                if (insert) {
                    peerIPs[peerNum] = _peerIP;
                    peerPorts[peerNum] = _peerPort;
                    peerNum++;

                    System.out.println("새로운 Peer : " + _peerIP + ":" + _peerPort);
                }
            }

            // 다른 peer가 연결됨
            System.out.println("P2P START");
            new Thread(new ConnectionHandler(serverSocket,mergeFile,myFileNum)).start();

            // 다른 peer로 연결 시도
            for (int i = 0; i < totalClients; i++) {
                if (peerPorts[i].equals(String.valueOf(peerPort))) continue;
                System.out.println("File Client ON");
                new Thread(new FileClient(mergeFile, peerIPs[i], peerPorts[i])).start();
            }

            while(connectionNum != 3){
                sleep(100);
            }
            System.out.println(peerSockets.toString());
            System.out.println("SYSTEM ALL STARTED");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ConnectionHandler implements Runnable {
        private ServerSocket serverSocket;
        private MergeFile mergeFile;
        private int myFileNum;

        public ConnectionHandler(ServerSocket serverSocket, MergeFile mergeFile,int myFileNum) {
            this.serverSocket = serverSocket;
            this.mergeFile = mergeFile;
            this.myFileNum = myFileNum;
        }

        @Override
        public void run() {
            try {
                while (connectionNum != 3) {
                    Socket peerSocket = serverSocket.accept();
                    System.out.println("File Server ON");
                    new Thread(new FileServer(peerSocket, mergeFile, myFileNum)).start();
                    System.out.println(peerSocket.getPort() +"가 연결됨");
                    synchronized (lock) {
                        peerSockets.add(peerSocket);
                        connectionNum++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}