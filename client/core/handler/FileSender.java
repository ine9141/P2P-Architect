package core.handler;

import core.struct.Chunk;
import core.struct.MergeFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class FileSender implements Runnable {
    private final int index;
    private final MergeFile mergeFile;
    public FileSender(MergeFile mergeFile, int index) {
        this.index = index;
        this.mergeFile = mergeFile;
    }

    @Override
    public void run() {
        try {
            Socket peerSocket = new Socket(PeerHandler.getPeerIP(index), Integer.parseInt(PeerHandler.getPeerPort(index)));

            BufferedReader in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
            PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);
            while(true) {
                String[] idxInfo = in.readLine().split(":"); // 1:1004
                for (int i = 0; i < 4; i++) {
                    if (Integer.parseInt(idxInfo[i]) < mergeFile.findIdx(i)) {
                        out.println(i + ":" + Integer.parseInt(idxInfo[i]) + ":" + new String(mergeFile.getChunk(i, Integer.parseInt(idxInfo[i])).getFile(), StandardCharsets.UTF_8));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

}
