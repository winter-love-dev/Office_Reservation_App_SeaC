package com.example.hun73.seac_apply_ver2.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.SessionManager;
import com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class MyService extends Service
{
    private String TAG = "MyService";

    // todo: Notification 알람
    NotificationManager Notifi_M;
    Notification Notifi;

    // todo: Notification 알람 끝

    // ------------------------------------

    // todo: 세션

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    // todo: 세션 끝

    // ------------------------------------

    // todo: 소켓서버 연결
    // 수신용 핸들러
    // 서버에서 전송하는 메시지를 감지한다.
    // 메시지를 받고 리사이클러뷰를 갱신함
    Handler msgHandler;
        String ipad2 = "192.168.0.144"; // 4사
    int port = 12346;
    Socket socket;

    // ------------------------------------

    // 메시지 전송 스레드
    // 메시지 입력 후 전송버튼 클릭했을 때, 연결된 소켓으로 메시지를 보냄
    ReceiveThread recevie;
    SocketClient client;
    // todo: 소켓서버 연결 끝


    public MyService()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    // 백그라운드에서 실행되는 동작들이 들어가는 곳입니다.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
//        return super.onStartCommand(intent, flags, startId);

        Log.e(TAG, "onStartCommand: 서비스 onStartCommand 실행됨");

        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 세션생성
        sessionManager = new SessionManager(this);

        // 로그인 체크 메소드.
        // isLogin() 메소드의 값이 false면 (true가 아니면)
        // 로그인 액티비티로 이동하기
        sessionManager.checkLogin();

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        // 소켓 연결 시도
        if (getId == null)
        {

        }

        else
        {
            // todo: 소켓 연결
            String roomAndUserData = "null" + "@" + getId;
            client = new SocketClient(roomAndUserData);
            client.start();
            msgHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if (msg.what == 1111)
                    {
                        Log.e(TAG, " = 서비스 onStartCommand에서 Handler 실행 중 ");
//                        Fragment_Home_Menu_3.slide_chat_list_adpater.notifyDataSetChanged();
//                        Toast.makeText(getApplicationContext(), "서비스로 메시지 넘어옴", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            // todo: 소켓 연결 끝
        }


        // todo: 서비스 스스로 종료하기
//        int id = startId;
//        stopSelf(id);


        return START_STICKY;
        /**
         * todo: START_STICKY
         * 표준방식. 서비스가 런타임에 의해 종료되면 항상 재시작되며,
         * 재시작 될 때마다 onStartCommand가 호출된다.
         * 이때 전달되는 intent는 null이다.
         * 지속적인 백그라운드 작업이 필요한 경우나
         * 음악재생 서비스 등에 적합한 방식
         *
         * todo: START_NOT_STICKY
         * 서비스가 런타임에 의해 종료되어도
         * startService를 다시 호출하지 않으면 해당 서비스는 중지된다.
         * 업데이트나 네트워크 폴링과 같이 규칙적인 처리를 다루는 서비스에 적합
         * (중지되어도 다음 예약 시점에 다시 호출됨)
         *
         * todo: START_REDELIVER_INTENT
         * 서비스가 런타임에 의해 종료되면 startService를 다시 호출하거나,
         * 프로세스가 stopSelf를 호출하기 전에 종료된 경우에만 재시작된다.
         * 후자의 경우에는 onStartCommand가 호출되며, 처리가 덜 된 초기 Intent가 전달됨.
         * 서비스가 요청받은 명령을 반드시 처리완료 해야 하는 경우에 적합한 방식
         */
    }

    // 서비스가 최초 생성되었을 때 한 번 실행됩니다.
    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    // 서비스가 종료될 때 실행되는 함수가 들어갑니다.
    @Override
    public void onDestroy()
    {
//        thread.stopForever();
//        thread = null; // 쓰레기값을 만들어서 빠르게 회수하기 위해 null을 넣어줌.
        super.onDestroy();
    }

    // ( 메세지 수신용 ) - 서버로부터 받아서, 핸들러에서 처리하도록 할 거.
    class ReceiveThread extends Thread
    {
        //        Socket socket = null;
        Socket socket;
        DataInputStream input = null;

        public ReceiveThread(Socket socket)
        {
            this.socket = socket;

            try
            {
                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
                input = new DataInputStream(socket.getInputStream());
                Log.e(TAG, "ReceiveThread try input: 연결됨 ");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            try
            {
                while (input != null)
                {
                    // 채팅 서버로 부터 받은 메세지
                    String msg = input.readUTF();
                    Log.e(TAG, "ReceiveThread: run(): String msg = null?: " + msg);

                    if (msg != null)
                    {
                        Log.e(TAG, "ReceiveThread: run() = input.readUTF(): " + msg);

                        // 핸들러에게 전달할 메세지 객체
                        Message hdmg = msgHandler.obtainMessage();

                        Log.e(TAG, "ReceiveThread: hdmg = msgHandler.obtainMessage(): " + hdmg);

                        // 핸들러에게 전달할 메세지의 식별자
                        hdmg.what = 1111;

                        // 메세지의 본문
                        hdmg.obj = msg;

                        // 핸들러에게 메세지 전달 ( 화면 처리 )
                        msgHandler.sendMessage(hdmg);

                        Log.e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ");

                        // 리사이클러뷰의 아이템 위치를 아래로 내리기
//                        message_area.scrollToPosition(messageData.size() - 1);

                        // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
//                        message_area.smoothScrollToPosition(messageData.size());
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "run: Exception e: " + e.toString());
//                Toast.makeText(Activity_ChatRoom.this, "Exception e: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 내부클래스 ( 접속용 )
    class SocketClient extends Thread
    {
        DataInputStream in = null;
        DataOutputStream output = null;
        String roomAndUserData; // 방 정보 ( 방번호 /  접속자 아이디 )
//        ReceiveThread recevie;

        public SocketClient(String roomAndUserData)
        {
            this.roomAndUserData = roomAndUserData;
        }

        public void run()
        {
            try
            {
                // 채팅 서버에 접속 ( 연결 )  ( 서버쪽 ip와 포트 )
                socket = new Socket(ipad2/*서버 ip*/, port/*포트*/);

                Log.e(TAG, "SocketClient run: 접속 시도");

                // 메세지를 서버에 전달 할 수 있는 통로 ( 만들기 )
                output = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                // 서버에 초기 데이터 전송  ( 방번호와 접속자 아이디가 담겨서 간다 ) -  식별자 역할을 하게 될 거임.
                output.writeUTF(roomAndUserData);

                // (메세지 수신용 쓰레드 생성 ) 리시브 쓰레드 시작
                recevie = new ReceiveThread(socket);
                recevie.start();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    } //SocketClient의 끝


}
