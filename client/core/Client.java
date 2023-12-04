package core;


import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import core.handler.FileReceiver;
import core.handler.FileSender;
import core.struct.Chunk;
import core.struct.MergeFile;
import core.handler.PeerHandler;

public class Client {
    private static Queue<Socket> peerSockets = new ConcurrentLinkedQueue<>();
    private static int peerNum = 0;
    final private static String fileName = "D.file";
    final private static int ClientPort = 44444;
    final private static int chunkSize = 256000;
    final private static int numOfChunks = 2000;
    private static final Object lock = new Object();
    public static void main(String[] args) {
        try {
            // 중앙 서버에 등록
            Socket centralServerSocket = new Socket("127.0.0.1", 23921);
            PrintWriter out = new PrintWriter(centralServerSocket.getOutputStream(), true);
            out.println(String.valueOf(ClientPort));
            System.out.println("서버 연결 완료");

            new Thread(new PeerHandler(centralServerSocket)).start();

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
                mergeFile.addChunk(chunk);
                from = to;
            }

            ServerSocket serverSocket = new ServerSocket(ClientPort);

            new Thread(new ConnectionHandler(serverSocket)).start();


            while(peerNum != 4){
                if(peerNum == 4) break;
            }

            System.out.println("P2P START");
            for (Socket peerSocket : peerSockets) {
                System.out.println("File Receiver ON");
                new Thread(new FileReceiver(peerSocket, mergeFile)).start();

            }

            for (int i = 0; i < 4; i++) {
                if (i == myFileNum) continue;
                System.out.println("File Sender ON");
                new Thread(new FileSender(mergeFile, i)).start();
            }

            System.out.println("SYSTEM ALL STARTED");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ConnectionHandler implements Runnable {
        private ServerSocket serverSocket;

        public ConnectionHandler(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Socket peerSocket = serverSocket.accept();
                    synchronized (lock) {
                        peerSockets.add(peerSocket);
                        peerNum++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}