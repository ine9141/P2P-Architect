package core.handler;

import core.struct.MergeFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileClient implements Runnable {

    final private MergeFile mergeFile;
    final private String peerIP;
    final private String peerPort;
    final private Object lock = new Object();


    public FileClient(MergeFile mergeFile, String peerIP, String peerPort) {
        this.mergeFile = mergeFile;
        this.peerIP = peerIP;
        this.peerPort = peerPort;
    }

    @Override
    public void run() {
        try {
            Socket peerSocket = new Socket(peerIP, Integer.parseInt(peerPort));

            BufferedReader in = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
            PrintWriter out = new PrintWriter(peerSocket.getOutputStream(), true);

            while(mergeFile.isEnd()) {
                boolean flag = true;
                String[] idxInfo = in.readLine().split(":"); // 1:1004
                for (int i = 0; i < 4; i++) {
                    if (Integer.parseInt(idxInfo[i]) < mergeFile.findIdx(i)) {
                        synchronized (lock){
                            out.println("OK");
                            out.println(i + ":" + Integer.parseInt(idxInfo[i]) + ":" + new String(mergeFile.getChunk(i, Integer.parseInt(idxInfo[i])).getFile(), StandardCharsets.UTF_8));
                        }
                        flag = false;
                        break;
                    }
                } if(flag) {
                    synchronized (lock) {
                        out.println("NO");
                        out.println("NO:");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
