package core;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

import core.handler.FileServer;
import core.handler.FileClient;
import core.handler.LogHandler;
import core.struct.Chunk;
import core.struct.MergeFile;

import static core.handler.LogHandler.*;

public class Client {
    private static Queue<Socket> peerSockets = new ConcurrentLinkedQueue<>();
    final private static int totalClients = 4;
    private static String fileName;
    private static int peerPort;
    public static int peerNum = 0;
    public static int connectionNum = 0;
    private static String[] peerIPs = new String[4];
    private static String[] peerPorts = new String[4];
    final private static int chunkSize = 256000;
    final private static int numOfChunks = 2000;
    private static final Object lock = new Object();
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("실행중인 클라이언트가 가지고 있는 파일의 이름을 입력하세요 (.file 생략) ");
        fileName = sc.nextLine() + ".file";
        if(fileName.equals("A.file")) {
            peerPort = 11111;
        } else if(fileName.equals("B.file")) {
            peerPort = 22222;
        } else if(fileName.equals("C.file")) {
            peerPort = 33333;
        } else{
            peerPort = 44444;
        }

        try {
            //중앙 서버에 등록
            Socket socket = new Socket("127.0.0.1", 23921);
            ServerSocket serverSocket = new ServerSocket(peerPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(peerPort);
            System.out.println("서버 연결 완료");
            makeLog("서버 연결 완료");

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

                    makeLog("새로운 Peer : " + _peerIP + ":" + _peerPort);
                    System.out.println("새로운 Peer : " + _peerIP + ":" + _peerPort);
                }
            }

            //내 파일을 청크화
            MergeFile mergeFile = new MergeFile();
            int myFileNum = mergeFile.filenameToIdx(fileName);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println(currentDirectory + fileName);
            Path filePath = Paths.get(currentDirectory + "\\" + fileName);
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

            makeLog("P2P START");
            System.out.println("P2P START");

            // 다른 peer가 연결됨
            Thread[] client = new Thread[4];
            for (int i = 0; i < totalClients; i++) {
                if (peerPorts[i].equals(String.valueOf(peerPort))) continue;
                makeLog("File Client ON");
                System.out.println("File Client ON");
                client[i] = new Thread(new FileClient(mergeFile, peerIPs[i], peerPorts[i]));
                client[i].start();
            }

            // 다른 peer로 연결 시도
            Thread connection = new Thread(new ConnectionHandler(serverSocket,mergeFile,myFileNum));
            connection.start();
            connection.join();

            // 파일 전송을 요구
            Thread[] server = new Thread[3];
            int serverIdx = 0;
            for(Socket peerSocket : peerSockets) {
                makeLog("File Server ON");
                System.out.println("File Server ON");
                server[serverIdx] = new Thread(new FileServer(peerSocket, mergeFile, myFileNum));
                server[serverIdx].start();
                serverIdx++;
            }

            System.out.println(peerSockets.toString());
            makeLog("SYSTEM ALL STARTED");
            System.out.println("SYSTEM ALL STARTED");

            for(int i = 0; i < 4; i++){
                if(client[i] == null) continue;
                client[i].join();
            }

            for(int i = 0; i < 3; i++) {
                server[i].join();
            }

            out.println("end");
            System.out.println("ALL SYSTEM DISCONNECTED");

        } catch (IOException e) {
            e.printStackTrace();
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
                    makeLog(peerSocket.getPort() +"가 연결됨");
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