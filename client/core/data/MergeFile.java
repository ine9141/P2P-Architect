package core.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MergeFile {
    private Chunk[][] chunks = new Chunk[4][2000];
    private int[] chunkCnt = new int[4];
    //private Object[] file = new Object[4];

    public void addChunk(Chunk chunk) {
        int idx = filenameToIdx(chunk.getFineName());
        chunks[idx][chunk.getChunkNumber()] = chunk; // 청크 저장
        chunkCnt[idx]++; // 청크 개수 증가
        if (chunkCnt[idx] == 2000) { // 청크 2000개 차면 파일로 변환
            chunkToFile(idx);
        }
    }

    void chunkToFile(int idx) {
        String filePath = idxToFilename(idx); // 저장할 파일명

        try {

            /* 청크들을 하나의 파일로 변환 */
            FileOutputStream fileOut = new FileOutputStream(filePath, true);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            for(int i=0; i<2000; i++) {
                objectOut.writeObject(chunks[idx][i].getFile());
            }

            /* 하나의 파일 데이터 출력 테스트 */
            FileInputStream fis=new FileInputStream(filePath);
            ObjectInputStream ois=new ObjectInputStream(fis);
            Object obj;
            while((obj=ois.readObject()) != null) {
                System.out.println("obj = " + obj);
            }



        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
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

    int filenameToIdx(String filename) {
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
