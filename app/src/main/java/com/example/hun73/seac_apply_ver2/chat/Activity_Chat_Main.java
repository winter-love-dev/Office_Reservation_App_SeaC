package com.example.hun73.seac_apply_ver2.chat;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hun73.seac_apply_ver2.Activity_Home;
import com.example.hun73.seac_apply_ver2.R;

import java.net.Socket;

// Socket Accept 이후
// Socket 객체를 GameUser 클래스로 생성을 해준다.
// GameUser user = new GameUser(socket);
// 이후 사용자가 원하는 방에 입장하고자 입장처리를 요청하면
// user.enterRoom(room); 이런식으로 룸에 입장처리를 한다.
// enterRoom의 함수에는
// void enterRoom(GameRoom room)
// {
//  this.gameRoom = room;
// }

public class Activity_Chat_Main extends AppCompatActivity
{
    private TextView connect_pc;

    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);

//        ChatRoomManager roomManager = new ChatRoomManager(); // 클래스 시작 시 한번만 생성해야 한다.
//        ChatUser user = new ChatUser(socket);
//        Activity_Chat_For_PC room = new Activity_Chat_For_PC();
//
//        try {
//            Socket socket = new Socket("localhost", 48612);
//
//            // 입력 스트림
//            // 서버에서 보낸 데이터를 받음
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    socket.getInputStream()));
//
//            // 출력 스트림
//            // 서버에 데이터를 송신
//            OutputStream out = socket.getOutputStream();
//
//            // 서버에 데이터 송신
//            out.write("Hellow Java Tcp Client!!!! \n".getBytes());
//            out.flush();
//            System.out.println("데이터를 송신 하였습니다.");
//
//            String line = in.readLine();
//            System.out.println("서버로 부터의 응답 : "+line);
//
//            // 서버 접속 끊기
//            in.close();
//            out.close();
//            socket.close();
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 채팅방 접속
        connect_pc = findViewById(R.id.connect_pc);
        connect_pc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Activity_Chat_Main.this, Activity_ChatRoom.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Activity_Home.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
