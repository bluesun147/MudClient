package org.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

// https://kj84.tistory.com/entry/TCP-프로그래밍-간단한-채팅-클라이언트-서버-프로그래밍

public class newChatClient {

    public static void main(String[] args) {
        Socket sock = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        boolean endflag = false;
        try{
            sock = new Socket("localhost", 10001);
            pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));

            // 키보드로부터 한 줄씩 입력 받기 위한 br 생성
            br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
            // 사용자의 id를 전송한다.
            System.out.print("id를 입력하세요 : ");
            Scanner sc = new Scanner(System.in);
            String id = sc.next();
            pw.println(id);
            pw.flush();
            InputThread it = new InputThread(sock, br);
            it.start();
            String line = null;
            // 키보드로부터 한줄씩 입력받아 서버에 전송
            while((line = keyboard.readLine()) != null){
                pw.println(line);
                pw.flush();
                if(line.equals("/quit")){ // 강제 종료
                    endflag = true;
                    break;
                }
            }
            System.out.println("클라이언트의 접속을 종료합니다.");
        }catch(Exception ex){
            if(!endflag)
                System.out.println(ex);
        }finally{
            try{
                if(pw != null)
                    pw.close();
            }catch(Exception ex){}
            try{
                if(br != null)
                    br.close();
            }catch(Exception ex){}
            try{
                if(sock != null)
                    sock.close();
            }catch(Exception ex){}
        } // finally
    } // main
} // class

class InputThread extends Thread{
    private Socket sock = null;
    private BufferedReader br = null;
    public InputThread(Socket sock, BufferedReader br){
        this.sock = sock;
        this.br = br;
    }
    public void run(){
        try{
            String line = null;
            while((line = br.readLine()) != null){
                System.out.println(line);
            }
        }catch(Exception ex){
        }finally{
            try{
                if(br != null)
                    br.close();
            }catch(Exception ex){}
            try{
                if(sock != null)
                    sock.close();
            }catch(Exception ex){}
        }
    } // InputThread
}