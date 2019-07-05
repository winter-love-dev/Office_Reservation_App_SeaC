/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * © 2015 – 2018 Wowza Media Systems, LLC. All rights reserved.
 */

package com.example.hun73.seac_apply_ver2.Wowza;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.Wowza.ui.AutoFocusListener;
import com.example.hun73.seac_apply_ver2.Wowza.ui.MultiStateButton;
import com.example.hun73.seac_apply_ver2.Wowza.ui.TimerView;
import com.wowza.gocoder.sdk.api.devices.WOWZCamera;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyId;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyName;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyPhoto;

public class CameraActivity extends CameraActivityBase
{
    private final static String TAG = CameraActivity.class.getSimpleName();

    // UI controls
    protected MultiStateButton mBtnSwitchCamera = null;
    protected MultiStateButton mBtnTorch = null;
    protected TimerView mTimerView = null;

    // Gestures are used to toggle the focus modes
    // 제스처는 포커스 모드를 토글하는 데 사용됩니다.
    protected GestureDetectorCompat mAutoFocusDetector = null;

    // todo: 채팅 UI
    private ImageView wowza_camera_caht_button, wowza_camera_caht_send_button;
    private LinearLayout wowza_camera_caht_area;
    private EditText wowza_camera_caht_message_content;


    RecyclerView message_recyclerview;
    Wowza_Chat_Adapter wowza_chat_adapter;

    List<Wowza_Chat_Item> wowza_chat_List;
    Wowza_Chat_Item wowza_chat_item;

    private int chatAreaOnOffSwitch = 0;

    // todo: TCP 채팅 준비
    // 수신용 핸들러
    // 서버에서 전송하는 메시지를 감지한다.
    // 메시지를 받고 리사이클러뷰를 갱신함
    static Handler msgHandler;

    // [0] 나에게 메시지를 보낸 사람,
    // [1] 메시지 내용,
    // [2] 보낸 시간을 담는 배열
    String[] msgFilter;

    // 상대방 이름
    String targetNickName, targetID;

    // 상대방 사진, 내 사진
    String ReceiverPhoto;

    // 방 번호
    String RoomNo;

    // 내 아이디, 내 이름.
    // 액티비티가 onCreate 실행하면
    public static String loginUserId;
    String loginUserNick;

    static Socket socket;
    SocketClient client;

    // 접속할 서버 와이파이 설정
//    static String ipad2 = "192.168.0.16"; // 8사
//    public static String wiwza_ipad2 = "192.168.0.101"; // 5사
    public static String wiwza_ipad2 = "192.168.0.62"; // 3사
//    public static String wiwza_ipad2 = "192.168.1.14";    // 1사
//    public static String wiwza_ipad2 = "192.168.0.25";    // 4사

    static int port = 12346;

    // 메시지 전송 스레드
    // 메시지 입력 후 전송버튼 클릭했을 때, 연결된 소켓으로 메시지를 보냄
    SendThread send;
    static ReceiveThread recevie;

    // 메시지 타입
    String Type;

    // 현재 시간 받아오기
    long mNow;
    SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");
    Date mDate;
    String time;

    int Message_views = 1;

    // todo: TCP 채팅준비 끝

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mRequiredPermissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
        };

        // Initialize the UI controls
        // UI 컨트롤 초기화
        mBtnTorch = (MultiStateButton) findViewById(R.id.ic_torch);
        mBtnSwitchCamera = (MultiStateButton) findViewById(R.id.ic_switch_camera);
        mTimerView = (TimerView) findViewById(R.id.txtTimer);

        wowza_camera_caht_button = findViewById(R.id.wowza_camera_caht_button);
        wowza_camera_caht_area = findViewById(R.id.wowza_camera_caht_area);

        // todo: 채팅 리사이클러뷰 세팅
        wowza_chat_List = new ArrayList<>();
        message_recyclerview = findViewById(R.id.wowza_camera_caht_recycler_view);

        message_recyclerview.setHasFixedSize(true);
        message_recyclerview.setLayoutManager(new LinearLayoutManager(CameraActivity.this));

        wowza_chat_adapter = new Wowza_Chat_Adapter(CameraActivity.this, (ArrayList<Wowza_Chat_Item>) wowza_chat_List);
        message_recyclerview.setAdapter(wowza_chat_adapter);

        // todo: 현재시간 받아오기
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        time = mFormat.format(mDate);

        // todo: 채팅서버 입장하는 유저의 정보
//        Intent intent = getIntent();
//        targetID = intent.getExtras().getString("user_id");
//        targetNickName = intent.getExtras().getString("user_name");
//        ReceiverPhoto = intent.getExtras().getString("user_photo");
//        RoomNo = intent.getExtras().getString("room_no");
        targetID = MyId;
        targetNickName = MyName;
        ReceiverPhoto = MyPhoto;
        RoomNo = MyId;

        // todo: 방 입장하기 (방 번호, 내 아이디)
//        String roomAndUserData = Wowza_Host_Rommno + "@" + targetID;
//        Log.e(TAG, "onResponse: roomAndUserData: " + roomAndUserData);
//
//        // 방번호와 유저의 이름으로 서버에 접속한다
//        Log.e(TAG, "onCreate: roomAndUserData: " + roomAndUserData);
//        client = new SocketClient(roomAndUserData);
//        client.start();

        Log.e(TAG, "onCreate: targetID: " + targetID);
        Log.e(TAG, "onCreate: targetNickName: " + targetNickName);
        Log.e(TAG, "onCreate: ReceiverPhoto: " + ReceiverPhoto);
        Log.e(TAG, "onCreate: : RoomNo: " + RoomNo);

        // todo: 채팅 영역 활성, 비활성 버튼
        wowza_camera_caht_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (chatAreaOnOffSwitch == 0)
                {
//                    chatAreaOnOffSwitch = 1

                    wowza_camera_caht_area.setVisibility(View.VISIBLE);
//                    wowza_camera_caht_area.setVisibility(View.GONE);
                    chatAreaOnOffSwitch = 1;
                } else if (chatAreaOnOffSwitch == 1)
                {
                    wowza_camera_caht_area.setVisibility(View.GONE);
//                    wowza_camera_caht_area.setVisibility(View.VISIBLE);
                    chatAreaOnOffSwitch = 0;
                }
            }
        });

        // todo: 메시지 수신 핸들러
        RecieveMessage();

        // todo: 메시지 전송 스레드
        SendMessage();
    }

    // todo: 메시지 수신 핸들러
    private void RecieveMessage()
    {

        msgHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 1111)
                {
                    Log.e(TAG, " = getUserDetail(): ReceiveThread에서 전달받음 = ");

                    // 메세지가 왔다면.
//                                                Toast.makeText(Activity_ChatRoom.this, "메세지 : " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "받은 메시지: " + msg.obj.toString());

//                                                msgFilter = msg.obj.toString().split("@");
                    msgFilter = msg.obj.toString().split("│");

                    // 수신
                    wowza_chat_item = new Wowza_Chat_Item(
                            1,
                            "1",
                            msgFilter[5],
                            "1",
                            msgFilter[1],
                            msgFilter[2],
                            msgFilter[6],
                            1
                    );

                    Log.e(TAG, "handleMessage: 보낸사람[5]      : " + msgFilter[5]);
                    Log.e(TAG, "handleMessage: 내용[1]      : " + msgFilter[1]);
                    Log.e(TAG, "handleMessage: 시간[2]      : " + msgFilter[2]);
                    Log.e(TAG, "handleMessage: 사진[6]      : " + msgFilter[6]);

                    wowza_chat_List.add(wowza_chat_item); // 어레이 리스트
                    wowza_chat_adapter.notifyDataSetChanged();

                    // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
                    message_recyclerview.smoothScrollToPosition(wowza_chat_List.size());
                }
            }
        };
    }


    // todo: 메시지 전송 스레드
    private void SendMessage()
    {
        wowza_camera_caht_send_button = findViewById(R.id.wowza_camera_caht_send_button);
        wowza_camera_caht_message_content = findViewById(R.id.wowza_camera_caht_message_content);

        wowza_camera_caht_message_content.addTextChangedListener(new TextWatcher()
        {

            // 변화 감지
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            // 입력 후
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String message = wowza_camera_caht_message_content.getText().toString();

                Log.e(TAG, "setSendBtn: SendMessage(): message: " + message);
                if (TextUtils.isEmpty(message) || message.equals(""))
                {
                    // Toast.makeText(Activity_ChatRoom.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();

                    // 보낼 내용이 없을 땐 파란 불 끔
                    // send_button.getResources().getDrawable(R.drawable.ic_send_message);
//                    send_button.setImageResource(R.drawable.ic_send_message);
                    wowza_camera_caht_send_button.setImageResource(R.drawable.ic_wowza_chat_send_button);
                    wowza_camera_caht_send_button.setEnabled(false); // 전송버튼 비활성화

                } else
                {
                    // 보낼 내용이 없을 땐 파란 불 켬
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message_2);
                    wowza_camera_caht_send_button.setImageResource(R.drawable.ic_send_message_2);
                    wowza_camera_caht_send_button.setEnabled(true); // 전송버튼 활성화

                    wowza_camera_caht_send_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 텍스트 메시지: 1
                            // 사진 메시지: 2
                            int mode = 1;

                            Log.e(TAG, "setSendBtn: loginUserId: " + loginUserId + " / loginUserNick: " + loginUserNick);

                            wowza_chat_item = null;
                            wowza_chat_item = new Wowza_Chat_Item(1, targetID, targetNickName, "1", message, time, ReceiverPhoto, 1);

                            Log.e(TAG, " ");
                            Log.e(TAG, " ===== 전송 중 ===== ");
//                            Log.e(TAG, "mode: " + 1 + " / MyPhoto: " + MyPhoto + " / senderId: " + senderId);
                            Log.e(TAG, "senderNick: " + loginUserId + " / message: " + message);
                            Log.e(TAG, "time: " + time);
                            Log.e(TAG, " ===== ======= ===== ");
                            Log.e(TAG, " ");

//                    chat_area.append(senderNick + ": " + message + "\n");

                            wowza_chat_List.add(wowza_chat_item);
//                    chattingRoomAdapter.notifyDataSetChanged();
                            message_recyclerview.smoothScrollToPosition(wowza_chat_List.size());

                            Type = "1";

                            send = new SendThread(socket, message);
                            Log.e(TAG, " ");
                            Log.e(TAG, " ===== SendThread ===== ");
                            Log.e(TAG, " message: " + message);
                            Log.e(TAG, " socket: " + socket);
                            Log.e(TAG, " ===== ======= ===== ");
                            Log.e(TAG, " ");
                            send.start();

//                    // 리사이클러뷰의 마지막 아이템 위치로 보내기
//                    message_area.scrollToPosition(messageData.size() - 1);

                            // 채팅 입력창 비워주기
                            wowza_camera_caht_message_content.setText(null);

                            // 보낼 내용이 없을 땐 파란 불 끔
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message);
                            wowza_camera_caht_send_button.setImageResource(R.drawable.ic_wowza_chat_send_button);
                            wowza_camera_caht_send_button.setEnabled(false); // 전송버튼 비활성화
                            // 키보드 내려주기
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(message_content.getWindowToken(), 0);

//                    Toast.makeText(Activity_ChatRoom.this, "전송", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            // 입력하기 전
            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    /**
     * Android Activity lifecycle methods
     * Android 활동 라이프 사이클 메소드
     */
    @Override
    protected void onResume()
    {
        Log.e(TAG, "onResume: 실행됨");
        super.onResume();
        if (this.hasDevicePermissionToAccess() && sGoCoderSDK != null && mWZCameraView != null)
        {
            if (mAutoFocusDetector == null)
                mAutoFocusDetector = new GestureDetectorCompat(this, new AutoFocusListener(this, mWZCameraView));

            WOWZCamera activeCamera = mWZCameraView.getCamera();
            if (activeCamera != null && activeCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                activeCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);
        }
    }

    @Override
    protected void onPause()
    {
        Log.e(TAG, "onPause: 실행됨");
        super.onPause();
    }

    /**
     * Click handler for the switch camera button
     */
    public void onSwitchCamera(View v)
    {
        if (mWZCameraView == null) return;

        mBtnTorch.setState(false);
        mBtnTorch.setEnabled(false);

        // Set the new surface extension prior to camera switch such that
        // setting will take place with the new one.  So if it is currently the front
        // camera, then switch to default setting (not mirrored).  Otherwise show mirrored.
//        if(mWZCameraView.getCamera().getDirection() == WOWZCamera.DIRECTION_FRONT) {
//            mWZCameraView.setSurfaceExtension(mWZCameraView.EXTENSION_DEFAULT);
//        }
//        else{
//            mWZCameraView.setSurfaceExtension(mWZCameraView.EXTENSION_MIRROR);
//        }

        WOWZCamera newCamera = mWZCameraView.switchCamera();
        if (newCamera != null)
        {
            if (newCamera.hasCapability(WOWZCamera.FOCUS_MODE_CONTINUOUS))
                newCamera.setFocusMode(WOWZCamera.FOCUS_MODE_CONTINUOUS);

            boolean hasTorch = newCamera.hasCapability(WOWZCamera.TORCH);
            if (hasTorch)
            {
                mBtnTorch.setState(newCamera.isTorchOn());
                mBtnTorch.setEnabled(true);
            }
        }
    }

    /**
     * Click handler for the torch/flashlight button
     */
    public void onToggleTorch(View v)
    {
        if (mWZCameraView == null) return;
        WOWZCamera activeCamera = mWZCameraView.getCamera();
        activeCamera.setTorchOn(mBtnTorch.toggleState());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mAutoFocusDetector != null)
            mAutoFocusDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    /**
     * Update the state of the UI controls
     */
    @Override
    protected boolean syncUIControlState()
    {
        boolean disableControls = super.syncUIControlState();

        if (disableControls)
        {
            mBtnSwitchCamera.setEnabled(false);
            mBtnTorch.setEnabled(false);
        } else
        {
            boolean isDisplayingVideo = (this.hasDevicePermissionToAccess(Manifest.permission.CAMERA) && getBroadcastConfig().isVideoEnabled() && mWZCameraView.getCameras().length > 0);
            boolean isStreaming = getBroadcast().getStatus().isRunning();

            if (isDisplayingVideo)
            {
                WOWZCamera activeCamera = mWZCameraView.getCamera();

                boolean hasTorch = (activeCamera != null && activeCamera.hasCapability(WOWZCamera.TORCH));
                mBtnTorch.setEnabled(hasTorch);
                if (hasTorch)
                {
                    mBtnTorch.setState(activeCamera.isTorchOn());
                }

                mBtnSwitchCamera.setEnabled(mWZCameraView.getCameras().length > 0);
            } else
            {
                mBtnSwitchCamera.setEnabled(false);
                mBtnTorch.setEnabled(false);
            }

            if (isStreaming && !mTimerView.isRunning())
            {
                mTimerView.startTimer();
            } else if (getBroadcast().getStatus().isIdle() && mTimerView.isRunning())
            {
                mTimerView.stopTimer();
            } else if (!isStreaming)
            {
                mTimerView.setVisibility(View.GONE);
            }
        }

        return disableControls;
    }


    // todo: 채팅을 위한 내부 스레드 클래스
    // 내부클래스 ( 접속용 )
    static class SocketClient extends Thread
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
                socket = new Socket(wiwza_ipad2/*서버 ip*/, port/*포트*/);

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

    // 내부 클래스  ( 메세지 전송용 )
    class SendThread extends Thread
    {
        Socket socket; // ip와 포트?
        String sendmsg;
        DataOutputStream output;

        public SendThread(Socket socket, String sendmsg)
        {
            Log.e(TAG, "SendThread: 도달했음");

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
            Log.e(TAG, "run: SendThread: 실행됨");
            try
            {
                if (output != null)
                {
                    if (sendmsg != null)
                    {
//                        int roomNo = 1;
                        // 여기서 방번호와 상대방 아이디 까지 해서 보내줘야 할거같다 .
                        // 서버로 메세지 전송하는 부분

                        // 방 번호 @ 유저 인덱스 @ 유저 이름 @ 메시지
//                        output.writeUTF(roomNo + "@" + targetID + "@" + targetNickName + "@" + sendmsg);

//                         방 번호 @ 내 아이디 @ 상대 아이디 @ 메시지
//                        output.writeUTF(RoomNo + "@" + loginUserId + "@" + targetID + "@" + sendmsg + "@" + Type);
//                        output.writeUTF(RoomNo + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views);
                        output.writeUTF(Wowza_Host_Rommno + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views + "│" + targetNickName + "│" + ReceiverPhoto);

                        Log.e(TAG, "(SendThread) roomNo: " + Wowza_Host_Rommno + "/ loginUserId    : " + loginUserId);
                        Log.e(TAG, "(SendThread) targetID: " + targetID + "/ targetNickName: " + targetNickName);
                        Log.e(TAG, "(SendThread) sendmsg: " + sendmsg);
                        Log.e(TAG, "(SendThread) Type: " + Type + "_ 1: 텍스트 / 2: 사진");

                        sendmsg = null;

                        // 리사이클러뷰의 아이템 위치를 아래로 내리기
//                        message_area.scrollToPosition(messageData.size() - 1);
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // ( 메세지 수신용 ) - 서버로부터 받아서, 핸들러에서 처리하도록 할 거.
    static class ReceiveThread extends Thread
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

                        // 방 목록 갱신하기
//                        ChatRoomList_Request();

                        Log.e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ");

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

}
