package core.struct;

import core.handler.LogHandler;

import java.io.*;

import static core.handler.LogHandler.*;

public class MergeFile {
    final private int numOfChunks = 2000;
    final private Object lock = new Object();
    private Chunk[][] chunks = new Chunk[4][numOfChunks+1];
    private int[] chunkCnt = new int[]{0,0,0,0};

    public boolean isEnd() throws IOException {
        for(int i = 0 ; i < 4 ; i++) if (chunkCnt[i] != numOfChunks) return true;
        makeLog(chunkCnt[0]+" "+chunkCnt[1]+" "+chunkCnt[2]+" "+chunkCnt[3]);
        System.out.println(chunkCnt[0]+" "+chunkCnt[1]+" "+chunkCnt[2]+" "+chunkCnt[3]);
        return false;
    }

    public Chunk getChunk(int fileNum, int chunkNum){
        return chunks[fileNum][chunkNum];
    }

    public int findIdx(int client){
        return chunkCnt[client];
    }

    public void addChunk(Chunk chunk,int myFileNum) {
        int idx = chunk.getFileNum();
        if(chunks[idx][chunk.getChunkNumber()] != null) return;

        synchronized (lock) {
            chunks[idx][chunk.getChunkNumber()] = chunk; // 청크 저장
            chunkCnt[idx]++;
        }

        if (idx!=myFileNum&&chunkCnt[idx] == numOfChunks) { // 청크 2000개 차면 파일로 변환
            chunkToFile(idx);
        }
    }

    void chunkToFile(int idx) {
        String filePath = idxToFilename(idx); // 저장할 파일명 (idx에 따라 A,B,C,D로 반환
        int i=0;
        try (FileOutputStream fos = new FileOutputStream("client/core/file/"+filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            // 각각의 byte 배열을 순회하면서 파일에 기록
            for(; i<numOfChunks; i++)
                bos.write(chunks[idx][i].getFile());
            makeLog("파일 병합 완료");
            System.out.println("파일 병합 완료");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String idxToFilename(int idx) {
        if(idx==0) {
            return "A.file";
        } else if(idx==1) {
            return "B.file";
        } else if(idx==2) {
            return "C.file";
        } else {
            return "D.file";
        }
    }

    public int filenameToIdx(String filename) {
        if(filename.equals("A.file")) {
            return 0;
        } else if(filename.equals("B.file")) {
            return 1;
        } else if(filename.equals("C.file")) {
            return 2;
        } else {
            return 3;
        }
    }

}
