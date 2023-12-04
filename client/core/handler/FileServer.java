package core.handler;

import core.struct.Chunk;
import core.struct.MergeFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileServer implements Runnable {
    final private Socket peerSocket;
    final private MergeFile mergeFile;
    final private Object lock = new Object();
    public FileServer(Socket peerSocket, MergeFile mergeFile) {
        this.peerSocket = peerSocket;
        this.mergeFile = mergeFile;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
            PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);

            while(mergeFile.isEnd()) {
                System.out.println("파일 전송 요청 : 현재 청크 : "+mergeFile.findIdx(0) + ":" + mergeFile.findIdx(1) + ":" + mergeFile.findIdx(2) + ":" + mergeFile.findIdx(3));
                out.println(mergeFile.findIdx(0) + ":" + mergeFile.findIdx(1) + ":" + mergeFile.findIdx(2) + ":" + mergeFile.findIdx(3));
                String msg;
                String[] idxInfo;
                synchronized (lock) {
                    msg = in.readLine();
                    idxInfo = in.readLine().split(":");
                }
                System.out.println("파일 전송 요청 응답 메시지 : "+msg);
                if (msg.equals("OK")) {
                    int fileNum = Integer.parseInt(idxInfo[0]);
                    int chunkNum = Integer.parseInt(idxInfo[1]);
                    System.out.println("파일 수신 완료");
                    System.out.println("파일 번호 : " + fileNum + ", " + chunkNum);
                    byte[] chunkByte = idxInfo[0].getBytes(StandardCharsets.UTF_8);
                    mergeFile.addChunk(new Chunk(fileNum, chunkNum, chunkByte));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
