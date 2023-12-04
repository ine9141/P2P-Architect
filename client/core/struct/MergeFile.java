package core.struct;

import java.io.*;

public class MergeFile {
    final private static int chunkSize = 256000;
    final private static int numOfChunks = 2000;
    private Chunk[][] chunks = new Chunk[4][numOfChunks];
    private static int[] chunkCnt = new int[]{0,0,0,0};
    final private Object lock = new Object();

    public boolean isEnd(){
        for(int i = 0 ; i < 4 ; i++) if (chunkCnt[i] != numOfChunks) return true;
        System.out.println(chunkCnt[0]+" "+chunkCnt[1]+" "+chunkCnt[2]+" "+chunkCnt[3]);
        return false;
    }

    public Chunk getChunk(int fileNum, int chunkNum){
        return chunks[fileNum][chunkNum];
    }

    public int findIdx(int client){
        return chunkCnt[client];
    }

    public void addChunk(Chunk chunk) {
        int idx = chunk.getFileNum();
        chunks[idx][chunk.getChunkNumber()] = chunk; // 청크 저장
        synchronized (lock) {
            chunkCnt[idx]++; // 청크 개수 증가
        }
        if (chunkCnt[idx] == numOfChunks) { // 청크 2000개 차면 파일로 변환
            chunkToFile(idx);
        }
    }

    void chunkToFile(int idx) {
        String filePath = idxToFilename(idx); // 저장할 파일명 (idx에 따라 A,B,C,D로 반환
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            // 각각의 byte 배열을 순회하면서 파일에 기록
            for(int i=0; i<numOfChunks; i++)
                bos.write(chunks[idx][i].getFile());
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
