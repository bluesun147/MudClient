package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

// https://wakestand.tistory.com/126
// https://balabala.tistory.com/25

// chatClient3 json으로 데이터 보내기

public class chatClient {

    // 소켓 생성한 뒤 서버 접속
    // 성공 시 서버에 메시지 보내고 읽을 스레드 생성한 뒤 start 시킴
    public static void main(String[] args) {
        try {
            Socket socket = null;
            // 소켓 서버에 접속
            socket = new Socket("localhost", 1234);
            System.out.println("서버에 연결되었습니다! ID를 입력해 주세요!!"); // 접속 확인용

            // 서버에서 보낸 메세지 읽는 Thread
            ListeningThread t1 = new ListeningThread(socket);
            // 서버로 메세지 보내는 Thread
            WritingThread t2 = new WritingThread(socket);

            t1.start(); // ListeningThread Start
            t2.start(); // WritingThread Start

        } catch (IOException e) {
            e.printStackTrace(); // 예외처리
        }
    }

    // 서버로 메시지 보내는 스레드
    public static class WritingThread extends Thread { // 서버로 메세지 보내는 Thread
        Socket socket = null;
        Scanner scanner = new Scanner(System.in); // 채팅용 Scanner

        public WritingThread(Socket socket) { // 생성자
            // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
            this.socket = socket;
        }

        public void run() {
            try {
                // OutputStream - 클라이언트에서 Server로 메세지 발송
                // socket의 OutputStream 정보를 OutputStream out에 넣은 뒤
                OutputStream out = socket.getOutputStream();
                // PrintWriter에 위 OutputStream을 담아 사용
                PrintWriter writer = new PrintWriter(out, true);

                // 콘솔에 입력한 값 있을 시 바로 서버로 보내주는 역할
                while (true) { // 무한반복

                    String text = scanner.next();
                    JSONObject jsonText = new JSONObject();

                    // 근데 move인 경우만 이렇게 클라에서 따로 빼서 처리해도 됨? ....
                    if (text.equals("move")) { // move한 뒤 x,y값을 json키로 해서 넘김
                        String x = scanner.next();
                        String y = scanner.next();
                        jsonText.put("text", text);
                        jsonText.put("x", x);
                        jsonText.put("y", y);

                    } else if (text.equals("chat")) {
                        String user = scanner.next();
                        String message = scanner.nextLine();
                        jsonText.put("text", text);
                        jsonText.put("user", user);
                        jsonText.put("message", message);

                    } else { // 일반 명령문
                        jsonText.put("text", text);
                        // System.out.println("jsonText = " + jsonText); // jsonData = {"text":"bluesun"}
                    }
                    writer.println(jsonText); // 입력한 메세지 발송

                    if (text.equals("quit")) {
                        out.close();
                        writer.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace(); // 예외처리
            }
        }
    }

    // 읽는 스레드
    public static class ListeningThread extends Thread { // 서버에서 보낸 메세지 읽는 Thread
        Socket socket = null;

        public ListeningThread(Socket socket) { // 생성자
            this.socket = socket; // 받아온 Socket Parameter를 해당 클래스 Socket에 넣기
        }

        public void run() {
            try {
                // InputStream - Server에서 보낸 메세지를 클라이언트로 가져옴
                // socket의 InputStream 정보를 InputStream in에 넣은 뒤
                InputStream input = socket.getInputStream();
                // BufferedReader에 위 InputStream을 담아 사용
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                // 서버에서 메시지 보냈을 경우 바로 읽음.
                while (true) { // 무한 반복
                    String readValue = reader.readLine();

                    JSONObject jsonData = new JSONObject(readValue);

                    if (jsonData.has("fromServerJsonKey")) { // 일반 greeting, 일반 명령문에 대한 응답은 fromServerJsonKey 키로 옴
                        String execution = jsonData.getString("fromServerJsonKey");
                        System.out.println(execution);
                        // continue;
                    }


                    if (jsonData.has("users") || jsonData.has("monsters")) { // 서버에서 넘어오는 json 데이터에 users 키가 있다면 (users 명령문 입력했을 때)


                        // JSONArray jsonArray = new JSONArray(reader.readLine());
                        String xx = reader.readLine();
                        if (xx.toString().charAt(0) == '[') { // JSONArray 형식 제대로 들어온다면
                            // 근데 이걸 클라이언트에서 하는게 맞음??
                            JSONArray jsonArray = new JSONArray(xx);
                            // JsonArray에서 요소 추출하기 (배열처럼 인덱스 사용)
                            for (int i = 0; i < jsonArray.length(); i++) {
                                System.out.println("<<" + ((JSONObject) jsonArray.get(i)).get("name") + " 위치>>\nx좌표: " + ((JSONObject) jsonArray.get(i)).get("x") + ", " + "y좌표: " + ((JSONObject) jsonArray.get(i)).get("y"));
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}