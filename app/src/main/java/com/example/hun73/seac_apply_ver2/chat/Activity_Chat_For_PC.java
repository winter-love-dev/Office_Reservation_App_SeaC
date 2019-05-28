package com.example.hun73.seac_apply_ver2.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hun73.seac_apply_ver2.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

// 유저 리스트를 관리하고 유저가 나갔을 때 리스트에서 빼고, 접속했을 때 리스트에 넣어주면 된다.
public class Activity_Chat_For_PC extends AppCompatActivity
{
    // 뷰
    private EditText pc_edit_message, enterRoomChattingEditText;
    private TextView pc_txtMessage;
    private ImageView enterRoomChattingSend;

    // 핸들러, 소켓, 스레드, 클라이언트
    private Handler msgHandler;
//    private SocketClient client;
    private ReceiveThread receive;
    private SendThread send;
    private Socket socket;
    //    private LinkedList<SocketClient> threadList;
    private Context context;

    String mac;

    String messageContent;

    String Edit_IP = "192.168.192.1",       // ip 입력
            Edit_Port = "8387"  // 포트 입력
                    ;
    private String[] msgFilter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_for_pc);

        context = this; // 와이파이 상태 점검할 때 액티비티 정보를 백업해둠
        pc_edit_message = findViewById(R.id.pc_edit_message);
        enterRoomChattingSend = findViewById(R.id.pc_send_button);
        enterRoomChattingEditText = findViewById(R.id.pc_txtMessage);
//        pc_txtMessage = findViewById(R.id.pc_txtMessage);
//        pc_send_button = findViewById(R.id.pc_send_button);

        // 서버 접속하기
//        client = new SocketClient(Edit_IP, Edit_Port); // 포트번호, IP번호 입력
//        client.start();


        // 서버로부터 수신한 메세지를 처리하는 곳  ( AsyncTesk를  써도됨 )
//        msgHandler = new Handler()
//        {
//            @Override
//            public void handleMessage(Message msg)
//            {
//                if (msg.what == 1111)
//                {
//                    // 메세지가 왔다면.
//                    Toast.makeText(context, "메세지 : " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("받은 메세지 ", msg.obj.toString());
//
//                    msgFilter = msg.obj.toString().split("@");
//
//                    // 수신 1
//                    messageContent = new chattingMessageContent(1, msgFilter[0], targetNickName, msgFilter[1], msgFilter[2]);
//
//                    messageData.add(messageContent);
//                    ChattingRoomAdapter.notifyDataSetChanged();
//                }
//            }
//        };

        // 메시지 전송 버튼
        enterRoomChattingSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                String message = pc_edit_message.getText().toString();
//
//                if (message != null && !message.equals(""))
//                {
//                    send = new SendThread(socket);
//                    send.start();
//                    pc_edit_message.setText("");
//                }
//                setSendBtn();
            }
        });

    }

//    // 채팅 입력 이벤트
//    public void setSendBtn()
//    {
//        String message = enterRoomChattingEditText.getText().toString();
//
//        if (message == null || TextUtils.isEmpty(message) || message.equals(""))
//        {
//            Toast.makeText(Activity_Chat_For_PC.this, "메세지를 입력해주세요", Toast.LENGTH_SHORT).show();
//        } else
//        {
//            int mode = 2;
//            String senderId = "1";
//            String senderNick = "ㅎㅎ";
//
//            // 현재 시간 받아오기
//            long mNow;
//            Date mDate;
//            mNow = System.currentTimeMillis();
//            mDate = new Date(mNow);
//
//            String time = mFormat.format(mDate);
//
//            messageContent = null;
//            messageContent = new chattingMessageContent(mode, senderId, senderNick, message, time);
//
//            messageData.add(messageContent);
//            ChattingRoomAdapter.notifyDataSetChanged();
//
//            // 메세지 보내주기
//            send = new SendThread(socket, message);
//            send.start();
//
//            // 에디트 텍스트 비워주기
//            enterRoomChattingEditText.setText(null);
//
//            // 키보드 내려주기
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(enterRoomChattingEditText.getWindowToken(), 0);
//
//            Toast.makeText(Activity_Chat_For_PC.this, "전송", Toast.LENGTH_SHORT).show();
//
//        }
//    }

    // 내부클래스   ( 접속용 )
    class SocketClient extends Thread
    {

        DataInputStream in = null;
        DataOutputStream out = null;
        String roomAndUserData; // 방 정보 ( 방번호 /  접속자 아이디 )

        public SocketClient(String roomAndUserData)
        {
            this.roomAndUserData = roomAndUserData;
        }


//        public void run()
//        {
//            try
//            {
//                // 채팅 서버에 접속 ( 연결 )  ( 서버쪽 ip와 포트 )
//                socket = new Socket(ipad2, port);
//
//                // 메세지를 서버에 전달 할 수 있는 통로 ( 만들기 )
//                output = new DataOutputStream(socket.getOutputStream());
//                in = new DataInputStream(socket.getInputStream());
//
//                // 서버에 초기 데이터 전송  ( 방번호와 접속자 아이디가 담겨서 간다 ) -  식별자 역할을 하게 될 거임.
//                output.writeUTF(roomAndUserData);
//
//                // (메세지 수신용 쓰레드 생성 ) 리시브 쓰레드 시작
//                recevie = new ReceiveThread(socket);
//                recevie.start();
//
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
    } //SocketClient의 끝


    // 내부 클래스  ( 메세지 전송용 )
    class SendThread extends Thread
    {
        Socket socket;
        String sendmsg;
        DataOutputStream output;


        public SendThread(Socket socket, String sendmsg)
        {
            this.socket = socket;
            this.sendmsg = sendmsg;
            try
            {
                // 채팅 서버로 메세지를 보내기 위한  스트림 생성.
                output = new DataOutputStream(socket.getOutputStream());

            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        // 서버로 메세지 전송 ( 이클립스 서버단에서 temp 로 전달이 된다.
        public void run()
        {
            try
            {
                if (output != null)
                {
                    if (sendmsg != null)
                    {

                        // 여기서 방번호와 상대방 아이디 까지 해서 보내줘야 할거같다 .
                        // 서버로 메세지 전송하는 부분
//                        output.writeUTF(roomNo + "@" + loginUserId + "@" + targetId + "@" + sendmsg);
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // ( 메세지 수신용 )  - 서버로부터 받아서, 핸들러에서 처리하도록 할 거.
    class ReceiveThread extends Thread
    {

        Socket socket = null;
        DataInputStream input = null;

        public ReceiveThread(Socket socket)
        {
            this.socket = socket;

            try
            {
                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
                input = new DataInputStream(socket.getInputStream());
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

                    if (msg != null)
                    {
                        // 핸들러에게 전달할 메세지 객체
                        Message hdmg = msgHandler.obtainMessage();

                        // 핸들러에게 전달할 메세지의 식별자
                        hdmg.what = 1111;

                        // 메세지의 본문
                        hdmg.obj = msg;

                        // 핸들러에게 메세지 전달 ( 화면 처리 )
                        msgHandler.sendMessage(hdmg);
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

//    private class SocketClient extends Thread
//    {
//        boolean threadAlive; // 스레드의 동작 여부
//        String ip;
//        String port;
//        OutputStream outputStream = null;
//        BufferedReader br = null;
//        DataOutputStream output = null;
//
//        public SocketClient(String ip, String port)
//        {
//            threadAlive = true;
//            this.ip = ip;
//            this.port = port;
//        }
//
//        public void run()
//        {
//
//            try
//            {
//                // 채팅서버에 접속
//                socket = new Socket(ip, Integer.parseInt(port));
//
//                // 서버에 메시지를 전달하기 위한 스트림 생성
//                output = new DataOutputStream(socket.getOutputStream());
//
//                // 메시지 수신용 스레드 생성
//                receive = new ReceiveThread(socket);
//                receive.start();
//
//                // 와이파이 정보 관리자 객체로부터 폰의 mac address를 가져와서 채팅 서버에 전달한다
//                WifiManager mng = (WifiManager) context.getSystemService(WIFI_SERVICE);
//                WifiInfo info = mng.getConnectionInfo();
//                mac = info.getMacAddress();
//                output.writeUTF(mac);
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    } // 소켓 클라이언트 종료
//
//    class ReceiveThread extends Thread
//    {
//        Socket socket = null;
//        DataInputStream input = null;
//
//        public ReceiveThread(Socket socket)
//        {
//            this.socket = socket;
//            try
//            {
//                // 채팅 서버로부터 메시지를 받기 위한 스트림 생성
//                input = new DataInputStream(socket.getInputStream());
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        public void run()
//        {
//            try
//            {
//                while (input != null)
//                {
//                    // 채팅 서버에서 받은 메시지
//                    String msg = input.readUTF();
//                    if (msg != null)
//                    {
//                        // 핸들러에게 전달할 메시지 객체
//                        Message hdmsg = msgHandler.obtainMessage();
//                        hdmsg.what = 1111; // 메시지의 식별자
//                        hdmsg.obj = msg;   // 메시지의 본문
//
//                        // 핸들러에게 메시지 전달 (화면 변경 요청)
//                        msgHandler.sendMessage(hdmsg);
//                    }
//                }
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private class SendThread extends Thread
//    {
//        Socket socket;
//        String sendmsg = pc_edit_message.getText().toString();
//        DataOutputStream output;
//
//        public SendThread(Socket socket)
//        {
//            this.socket = socket;
//
//            try
//            {
//                // 채팅 서버로 메시지를 보내기 위한 스트림 생성
//                output = new DataOutputStream(socket.getOutputStream());
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//
//        public void run()
//        {
//            try
//            {
//                if (output != null)
//                {
//                    if (sendmsg != null)
//                    {
//                        output.writeUTF(mac + ": " + sendmsg);
//                    }
//                }
//
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

//    private int id; // 룸 ID
//    private List userList;
//    private ChatUser roomOwner; // 방장
//    private String roomName; // 방 이름
//
//    public GameRoom(int roomId) { // 아무도 없는 방을 생성할 때
//        this.id = roomId;
//        userList = new ArrayList();
//    }
//
//    public GameRoom(ChatUser user) { // 유저가 방을 만들때
//        userList = new ArrayList();
//        user.enterRoom(this);
//        userList.add(user); // 유저를 추가시킨 후
//        this.roomOwner = user; // 방장을 유저로 만든다.
//    }
//
//    public GameRoom(List users) { // 유저 리스트가 방을 생성할
//        this.userList = users; // 유저리스트 복사
//
//        // 룸 입장
//        for(ChatUser user : users){
//            user.enterRoom(this);
//        }
//
//        this.roomOwner = userList.get(0); // 첫번째 유저를 방장으로 설정
//    }
//
//    public void enterUser(ChatUser user) {
//        user.enterRoom(this);
//        userList.add(user);
//    }
//
//    public void enterUser(List users) {
//        for(ChatUser gameUser : users){
//            gameUser.enterRoom(this);
//        }
//        userList.addAll(users);
//    }
//
//    /**
//     * 해당 유저를 방에서 내보냄
//     * @param user 내보낼 유저
//     */
//    public void exitUser(ChatUser user)
//    {
//        user.exitRoom(this);
//        userList.remove(user); // 해당 유저를 방에서 내보냄
//
//        if (userList.size() < 1)
//        { // 모든 인원이 다 방을 나갔다면
//            ChatRoomManager.removeRoom(this); // 이 방을 제거한다.
//            return;
//        }
//
//        if (userList.size() < 2)
//        { // 방에 남은 인원이 1명 이하라면
//            this.roomOwner = userList.get(0); // 리스트의 첫번째 유저가 방장이 된다.
//            return;
//        }
//    }
//
//    /**
//     * 해당 룸의 유저를 다 퇴장시키고 삭제함
//     */
//    public void close() {
//        for (GameUser user : userList) {
//            user.exitRoom(this);
//        }
//        this.userList.clear();
//        this.userList = null;
//    }
//
//    // 게임 로직
//
//    /**
//     * 해당 byte 배열을 방의 모든 유저에게 전송
//     * @param data 보낼 data
//     */
//    public void broadcast(byte[] data) {
//        for (GameUser user : userList) { // 방에 속한 유저의 수만큼 반복
//            // 각 유저에게 데이터를 전송하는 메서드 호출~
//            // ex) user.SendData(data);
//
////			try {
////				user.sock.getOutputStream().write(data); // 이런식으로 바이트배열을 보낸다.
////			} catch (IOException e) {
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
//        }
//    }
//
//    public void setOwner(ChatUser gameUser) {
//        this.roomOwner = gameUser; // 특정 사용자를 방장으로 변경한다.
//    }
//
//    public void setRoomName(String name) { // 방 이름을 설정
//        this.roomName = name;
//    }
//
//    public ChatUser getUserByNickName(String nickName) { // 닉네임을 통해서 방에 속한 유저를 리턴함
//
//        for (ChatUser user : userList) {
//            if (user.getNickName().equals(nickName)) {
//                return user; // 유저를 찾았다면
//            }
//        }
//        return null; // 찾는 유저가 없다면
//    }
//
//    public ChatUser getUser(ChatUser gameUser) { // GameUser 객체로 get
//
//        int idx = userList.indexOf(gameUser);
//
//        // 유저가 존재한다면(gameUser의 equals로 비교)
//        if(idx > 0){
//            return userList.get(idx);
//        }
//        else{
//            // 유저가 없다면
//            return null;
//        }
//    }
//
//    public String getRoomName() { // 방 이름을 가져옴
//        return roomName;
//    }
//
//    public int getUserSize() { // 유저의 수를 리턴
//        return userList.size();
//    }
//
//    public ChatUser getOwner() { // 방장을 리턴
//        return roomOwner;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public List getUserList() {
//        return userList;
//    }
//
//    public void setUserList(List userList) {
//        this.userList = userList;
//    }
//
//    public ChatUser getRoomOwner() {
//        return roomOwner;
//    }
//
//    public void setRoomOwner(ChatUser roomOwner) {
//        this.roomOwner = roomOwner;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Activity_Chat_For_PC gameRoom = (Activity_Chat_For_PC) o;
//
//        return id == gameRoom.id;
//    }
//
//    @Override
//    public int hashCode() {
//        return id;
//    }

}
