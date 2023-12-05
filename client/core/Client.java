package core;

import core.data.Chunk;
import core.data.MergeFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Client {
    public static void main(String[] args) {
        try {

            Socket serverSocket = new Socket("localhost", 23921);
            System.out.println("서버 연결 후 클라이언트 데이터 전송 완료");

            // ObjectInputStream을 사용하여 서버로부터 객체 수신
            ObjectInputStream objectInput = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream objectOutput = new ObjectOutputStream(serverSocket.getOutputStream());

            String receivedObject = (String) objectInput.readObject();

            /* 청크로 쪼개고 MergeFile.addChunk에 담는 코드 */
            MergeFile mergeFile = new MergeFile();
            Path filePath = Paths.get(Client.class.getResource("data/A.file").toURI());
            Long fileSize = Files.size(filePath);
            System.out.println("fileSize = " + fileSize);

            byte[] fileData = Files.readAllBytes(filePath);
            int chunkSize = 256000; // 청크 크기 설정
            int numOfChunks =  fileData.length / chunkSize;
            System.out.println("numOfChunks = " + numOfChunks);

            int to, from=0;
            for (int i = 0; i < numOfChunks; i++) {
                to = Math.min(from + chunkSize, fileData.length);
                byte[] chunkData = new byte[to - from];
                System.arraycopy(fileData, from, chunkData, 0, to - from);
                Chunk chunk = new Chunk("A.file", i, chunkData);
                System.out.println("chunk length = " + chunkData.length);
                mergeFile.addChunk(chunk);
                from = to;
            }
            serverSocket.close(); // 통신 종료
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
