package core.handler;

import core.struct.MergeFile;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


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
            peerSocket.setSoTimeout(3000);

            ObjectOutputStream objectOutput = new ObjectOutputStream(peerSocket.getOutputStream());
            ObjectInputStream objectInput = new ObjectInputStream(peerSocket.getInputStream());

            while (true) {
                boolean flag = true;
                String[] idxInfo;
                try {
                    idxInfo = ((String) objectInput.readObject()).split(":");
                } catch (SocketTimeoutException | SocketException e){
                    break;
                }

                for (int i = 0; i < 4; i++) {
                    int need_idx = Integer.parseInt(idxInfo[i]);
                    int have_idx = mergeFile.findIdx(i);

                    if (have_idx > need_idx) {
                        LogHandler.makeLog("current data " + have_idx + " read data " + need_idx + ", " + i);
                        System.out.println("current data " + have_idx + " read data " + need_idx + ", " + i);

                        synchronized (lock) {
                            objectOutput.writeObject("OK");
                            objectOutput.writeObject(i);
                            objectOutput.writeObject(need_idx);
                            objectOutput.writeObject(mergeFile.getChunk(i, need_idx).getFile());
                        }
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    synchronized (lock) {
                        objectOutput.writeObject("NO");
                        objectOutput.writeObject(0);
                        objectOutput.writeObject(0);
                        objectOutput.writeObject("NO".getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
