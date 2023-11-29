package core;

import core.handler.ClientJoinHandler;

import java.io.IOException;

import static core.handler.ClientJoinHandler.*;

public class Main{

    public static void main(String[] args) throws IOException, ClassNotFoundException {//서버에서 4개 클라이언트의 소켓 정보를 가지고 있는 저장소
        waitForGetAllSocket();
        waitClientRequest();

        while(true){
            // 파일 병합중에 끊길까봐 while문 돌려놨어
        }
    }
}