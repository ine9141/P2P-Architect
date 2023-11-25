package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket serverSocket = new Socket("localhost", 23921);
            System.out.println("서버 연결 후 클라이언트 데이터 전송 완료");

            // ObjectInputStream을 사용하여 서버로부터 객체 수신
            ObjectInputStream objectInput = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream objectOutput = new ObjectOutputStream(serverSocket.getOutputStream());

            String receivedObject = (String) objectInput.readObject();
            System.out.println("receivedObject = " + receivedObject);
            serverSocket.close(); // 통신 종료
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
