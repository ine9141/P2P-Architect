package core.handler;

import core.struct.Chunk;
import core.struct.MergeFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileReceiver implements Runnable {
    private final Socket peerSocket;
    private final MergeFile mergeFile;
    public FileReceiver(Socket peerSocket, MergeFile mergeFile) {
        this.peerSocket = peerSocket;
        this.mergeFile = mergeFile;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
            PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);

            out.println(mergeFile.findIdx(0)+":"+mergeFile.findIdx(1)+":"+mergeFile.findIdx(2)+":"+mergeFile.findIdx(3));
            String msg = in.readLine();
            if(msg.equals("OK")){
                String[] idxInfo = in.readLine().split(":"); // 1:1004
                int fileNum = Integer.parseInt(idxInfo[0]);
                int chunkNum = Integer.parseInt(idxInfo[1]);
                byte[] chunkByte = idxInfo[0].getBytes(StandardCharsets.UTF_8);
                mergeFile.addChunk(new Chunk(fileNum,chunkNum,chunkByte));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
