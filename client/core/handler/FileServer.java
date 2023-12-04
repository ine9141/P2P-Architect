package core.handler;

import core.struct.Chunk;
import core.struct.MergeFile;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class FileServer implements Runnable {
    final private Socket peerSocket;
    final private MergeFile mergeFile;
    final private int myFileNum;
    final private Object lock = new Object();
    public FileServer(Socket peerSocket, MergeFile mergeFile,int myFileNum) {
        this.peerSocket = peerSocket;
        this.mergeFile = mergeFile;
        this.myFileNum = myFileNum;
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream objectOutput = new ObjectOutputStream(peerSocket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(peerSocket.getInputStream());

            while (mergeFile.isEnd()) {

                System.out.println("파일 전송 요청 : 현재 청크 : " + mergeFile.findIdx(0) + ":" + mergeFile.findIdx(1) + ":" + mergeFile.findIdx(2) + ":" + mergeFile.findIdx(3));
                objectOutput.writeObject(mergeFile.findIdx(0) + ":" + mergeFile.findIdx(1) + ":" + mergeFile.findIdx(2) + ":" + mergeFile.findIdx(3));

                String msg;
                int fileNum;
                int chunkNum;
                byte[] chunk;

                synchronized (lock) {
                    msg = (String) objectInput.readObject();
                    fileNum = (int) objectInput.readObject();
                    chunkNum = (int) objectInput.readObject();
                    chunk = (byte[]) objectInput.readObject();
                }
                if (msg.equals("OK")) {
                    System.out.println("파일 수신 완료");
                    System.out.println("파일 번호 : " + fileNum + ", " + chunkNum);
                    mergeFile.addChunk(new Chunk(fileNum, chunkNum, chunk),myFileNum);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
