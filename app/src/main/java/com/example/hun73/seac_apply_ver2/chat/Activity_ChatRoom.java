package com.example.hun73.seac_apply_ver2.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
//import com.example.hun73.seac_apply_ver2.RecyclerView.ChattingRoomAdapter;
import com.example.hun73.seac_apply_ver2.RecyclerView.Chat_Item_Room_List;
import com.example.hun73.seac_apply_ver2.RecyclerView.chattingMessageContent;
import com.example.hun73.seac_apply_ver2.SessionManager;
import com.example.hun73.seac_apply_ver2.appRTC.CallActivity;
import com.example.hun73.seac_apply_ver2.appRTC.ConnectActivity;
import com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_3;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.hun73.seac_apply_ver2.appRTC.ConnectActivity.roomEditText;

public class Activity_ChatRoom extends AppCompatActivity
{
    private String TAG = "Activity_ChatRoom: ";
    private static String URL_READ = "http://115.68.231.84/read_detail.php"; //iwinv

    // 메시지 전송버튼, 메시지 입력공간
    ImageView select_image, send_button;
    EditText message_content;
    TextView chat_area, setTitle_name;

    // View 연결
//  select_image = findViewById(R.id.select_image); // 이미지 선택버튼

    // 수신용 핸들러
    // 서버에서 전송하는 메시지를 감지한다.
    // 메시지를 받고 리사이클러뷰를 갱신함
    Handler msgHandler;

    // [0] 나에게 메시지를 보낸 사람,
    // [1] 메시지 내용,
    // [2] 보낸 시간을 담는 배열
    String[] msgFilter;

    // 상대방 이름
    String targetNickName, targetID;

    // 상대방 사진, 내 사진
    String ReceiverPhoto, MyPhoto;

    // 방 번호
    String RoomNo;

    // 내 아이디, 내 이름.
    // 액티비티가 onCreate 실행하면
    public static String loginUserId;
    String loginUserNick;

    // 회원정보를 담은 세션 선언.
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId, getName, getPhoto;

    Socket socket;
    SocketClient client;

    RecyclerView message_area;
    ChattingRoomAdapter chattingRoomAdapter;

    List<chattingMessageContent> messageData;
    chattingMessageContent messageContent;

    // 192.168.58.1
    // 58.149.49.102
    // 192.168.192.1
    // 192.168.0.144

    // 3사 192.168.0.247
    // 4사 192.168.0.144
    // starbucks 172.30.18.125

//    String ipad2 = "192.168.0.184";

//    String ipad2 = "192.168.43.50"; // 갤럭시 노트 5 핫스팟
    String ipad2 = "172.30.1.49"; // 8사
//    String ipad2 = "192.168.1.10"; // 1사

//    String ipad2 = "192.168.0.86"; // 4사
    int port = 12346;

    // 메시지 전송 스레드
    // 메시지 입력 후 전송버튼 클릭했을 때, 연결된 소켓으로 메시지를 보냄
    SendThread send;
    ReceiveThread recevie;

    SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");

    // 현재 시간 받아오기
    long mNow;
    Date mDate;
    String time;

    // 메시지 타입
    String Type;

    int Message_views = 1;


    // 읽음표시
    public int ViewsReceive;

    public static Activity activity_chatroom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chat_room);

        activity_chatroom = Activity_ChatRoom.this;

        messageData = new ArrayList<>();

        Intent intent = getIntent();

        targetID = intent.getExtras().getString("user_id");
        targetNickName = intent.getExtras().getString("user_name");
        ReceiverPhoto = intent.getExtras().getString("user_photo");
        RoomNo = intent.getExtras().getString("room_no");

        Log.e(TAG, "onCreate: targetID: " + targetID);
        Log.e(TAG, "onCreate: targetNickName: " + targetNickName);
        Log.e(TAG, "onCreate: ReceiverPhoto: " + ReceiverPhoto);
        Log.e(TAG, "onCreate: : RoomNo: " + RoomNo);

        // 현재시간 받아오기
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        time = mFormat.format(mDate);

        // 슬라이드 루트 네비게이션 바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.ChatToolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); //햄버거 버튼 추가하기, 햄버거 버튼 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // 상대방 이름 세팅
        setTitle_name = findViewById(R.id.chat_user_user_name);
        setTitle_name.setText(targetNickName);
        Log.e(TAG, targetNickName + "님과 대화중");

        Log.e(TAG, "onCreate: 상대방: " + targetNickName + " / targetID: " + targetID + " / ReceiverPhoto: " + ReceiverPhoto);

        // 내 정보 불러오기
        // 소켓 클라이언트 실행
        // 서버에 접속하기
        getUserDetail();

        // 메시지 전송버튼 & 채팅 입력 이벤트
        setSendBtn();
    }

    // 이미지, 영상통화 다이얼로그
    private void OtherTaskDialog()
    {
        AlertDialog.Builder OtherChatTask = new AlertDialog.Builder(this);

//        Log.e(TAG, "onClick: view.setOnClickListener: " + ID_UserIndex );

        // 다이얼로그 세팅
        OtherChatTask.setTitle("작업 선택");
        OtherChatTask.setPositiveButton("사진전송",
                new DialogInterface.OnClickListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // 이미지 크로퍼 실행
                        CropImage.activity() // 크롭하기 위한 이미지를 가져온다.
                                .setGuidelines(CropImageView.Guidelines.ON) // 이미지를 크롭하기 위한 도구 ,Guidelines를
                                .start(Activity_ChatRoom.this); // 실행한다.
                    }
                })
                .setNegativeButton("영상통화",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // 상대방에게 영상통화 요청하기
                                VideoChatRequest();
                            }
                        });

        final AlertDialog edit_dialog = OtherChatTask.create();
        edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
        {
            @Override
            public void onShow(DialogInterface arg0)
            {
                edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        edit_dialog.show(); // 다이얼로그 실행
        // 다이얼로그 끝
    }

    //        ConnectActivity connectActivity = new ConnectActivity();
//        // 내 아이디로 채널 생성함
//        connectActivity.connectToRoom(roomEditText.getText().toString(), false, false, false, 0);

    // todo: 영상통화 요청하기
    // 1. A가 테이블 유저 인덱스로 영상통화 채널을 생성합니다.
    // 2. A는 자신이 생성한 영상통화 채널로 입장 후 B에게 영상통화 요청과 채널명을 전송합니다.
    // 3. 영상통화 요청을 받은 B는 영상통화 수신 화면을 띄웁니다.
    // 4. B가 영상통화 수신 화면에서 '수락' 버튼을 누르면 전송받은 채널명으로 입장합니다.
    // 5. B가 영상통화 수신 하면에서 '거절' 버튼을 누르면 상대방에게 거절 신호를 보냅니다.
    // 6. 거절 신호를 받은 A는 현재 머물러 있는 채널을 종료합니다.
    private void VideoChatRequest()
    {
        // todo a. 내 아이디를 connect 액티비티로 전달한다.

        // todo c. 상대방에게 영상통화 요청 메시지를 전송한다.
        String message = "video_chat_request_code_1";
        Log.e(TAG, "영상통화 요청함");

        // code_1: 영상통화 해요
        // code_2: 영상통화 종료
        // code_3: 영상통화 거절
        Log.e(TAG, "setSendBtn: loginUserId: " + loginUserId + " / loginUserNick: " + loginUserNick);

        messageContent = null;
        //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간, 안 읽은 사람, 참여자 수
        messageContent = new chattingMessageContent(1, loginUserId, loginUserNick, "3", message, time, Message_views);

        // 내 메시지 목록에 추가
        messageData.add(messageContent);
//                    chattingRoomAdapter.notifyDataSetChanged();
        message_area.smoothScrollToPosition(messageData.size());

        // todo: mysql에 메시지 저장하기
        // RoomNo getId MType Mode Message JoinNo Views
        updateMessage(
                RoomNo,
                loginUserId, // 내 아이디로 방 생성
                "3", // 영상통화
                message,
                "2",
                "1",
                time);

        Log.e(TAG, "onClick: updateMessage(): " +
                "\nRoomNo: " + RoomNo + "" +
                "\n / loginUserId: " + loginUserId + "" +
                "\n / message: " + message + "" +
                "\n / time: " + time);

        Log.e(TAG, " ");

        Type = "3";
        // 소켓 서버로 영상통화 신호 보내주기
        send = new SendThread(socket, message);
        Log.e(TAG, " ===== SendThread ===== ");
        Log.e(TAG, " message: " + message);
        Log.e(TAG, " socket: " + socket);
        Log.e(TAG, " ===== ======= ===== ");
        Log.e(TAG, " ");
        send.start();

        // 채팅 입력창 비워주기
        message_content.setText(null);

        // 방 목록 갱신하기
        ChatRoomList_Request();

        // todo b. connect 액티비티에서 내 아이디디로 방을 생성한다.
        Intent intent = new Intent(Activity_ChatRoom.this, ConnectActivity.class);
        intent.putExtra("loginId", loginUserId);
        intent.putExtra("call", "call");
        startActivity(intent);

        // todo d. '메시지목록 리사이클러뷰'에서 메시지 유형을 영상통화 전용 말풍선으로 바꾸자. 리사이클러뷰로 이동!

        return;
    }

    Uri filePath;
    Bitmap bitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK && result != null)
            {
                // todo: 선택한 이미지 불러오기
                // 사진의 경로를 담는다
                filePath = result.getUri();
                try
                {
//                    message = getRealPathFromURI(filePath);
                    String message = String.valueOf(filePath);

                    Log.e(TAG, "onActivityResult: 이미지 전송하기: " + message);

                    int mode = 1;

                    // todo: 내 화면에 이미지 세팅하기
                    messageContent = null;

                    //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간
                    messageContent = new chattingMessageContent(mode, loginUserId, loginUserNick, "2", message, time, Message_views);

                    Log.e(TAG, " ");
                    Log.e(TAG, " ===== 전송 중 ===== ");
//                            Log.e(TAG, "mode: " + 1 + " / MyPhoto: " + MyPhoto + " / senderId: " + senderId);
                    Log.e(TAG, "senderNick: " + loginUserId + " / message: " + message);
                    Log.e(TAG, "time: " + time);
                    Log.e(TAG, " ===== ======= ===== ");
                    Log.e(TAG, " ");

//                    chat_area.append(senderNick + ": " + message + "\n");

                    messageData.add(messageContent);
//                    chattingRoomAdapter.notifyDataSetChanged();
                    message_area.smoothScrollToPosition(messageData.size());

                    // todo: 1. 서버로 이미지를 저장한다.
                    // todo: 방금 저장한 이미지의 주소 불러오기 (이미지 주소 테이블에 저장하기)
                    Log.e(TAG, "onActivityResult: filePath: " + filePath);

                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                    UploadPicture(getId, getStringImage(bitmap));
                    Log.e(TAG, "onActivityResult: getId: " + getId + " / getStringImage(bitmap): " /*+ getStringImage(bitmap)*/);

                    message_content.setText(null);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        // todo: 통화 종료신호 받기
        if (resultCode == RESULT_CANCELED)
        {
            Log.e(TAG, "onActivityResult: 반환값 없음. 통화 종료!");

            // 거절신호 보내기
            // video_chat_request_code_1 요청
            // video_chat_request_code_2 종료
            // video_chat_request_code_3 거절
            String message = "video_chat_request_code_2";

            Type = "3"; // 타입 3 = 영상통화 타입
            // 소켓 서버로 영상통화 신호 보내주기
            send = new SendThread(socket, message);
            Log.e(TAG, " ===== SendThread ===== ");
            Log.e(TAG, " message: " + message);
            Log.e(TAG, " socket: " + socket);
            Log.e(TAG, " ===== ======= ===== ");
            Log.e(TAG, " ");
            send.start();

            // todo: mysql에 메시지 저장하기
            // RoomNo getId MType Mode Message JoinNo Views
            updateMessage(
                    RoomNo,
                    loginUserId,
                    "3", // 영상통화
                    message,
                    "2",
                    "1",
                    time);

            messageContent = null;
            //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간, 안 읽은 사람, 참여자 수
            messageContent = new chattingMessageContent(1, loginUserId, loginUserNick, "3", message, time, Message_views);
//                messageContent = new chattingMessageContent(1, targetID, targetNickName, "3", message, time, Message_views);

            // 내 메시지 목록에 추가
            messageData.add(messageContent);
            message_area.smoothScrollToPosition(messageData.size());

            Log.e(TAG, " ");

            Log.e(TAG, "onClick: updateMessage(): " +
                    "\nRoomNo: " + RoomNo + "" +
                    "\n / loginUserId: " + loginUserId + "" +
                    "\n / message: " + message + "" +
                    "\n / time: " + time);

            Log.e(TAG, " ");

            // 채팅 입력창 비워주기
            message_content.setText(null);

            // 방 목록 갱신하기
            ChatRoomList_Request();
        }

        // todo: 통화 거절신호 받기
        // 통화 거절 신호 응답코드는 300임
        else if (requestCode == 300)
        {
            Log.e(TAG, "onActivityResult: 통화 거절됨");

            String call_refuse = data.getStringExtra("call_refuse");
            Log.e(TAG, "onActivityResult: call_refuse: " + call_refuse );
/*            if (call_refuse.equals("call_start"))
            {
                Intent intent = new Intent(Activity_ChatRoom.this, ConnectActivity.class);
                intent.putExtra("loginId", targetID);
                startActivity(intent);

                Log.e(TAG, "onActivityResult: call_start");
            }

            else */if (call_refuse.equals("call_refuse"))
            {
                // 수신화면 종료
                Activity_Video_Receive activity_video_receive = (Activity_Video_Receive) Activity_Video_Receive.Activity_Video_Receive;
                activity_video_receive.finish();

                // 거절신호 보내기
                // video_chat_request_code_1 요청
                // video_chat_request_code_2 종료
                // video_chat_request_code_3 거절
                String message = "video_chat_request_code_3";
                Log.e(TAG, "영상통화 요청함");

                Type = "3";
                // 소켓 서버로 영상통화 신호 보내주기
                send = new SendThread(socket, message);
                Log.e(TAG, " ===== SendThread ===== ");
                Log.e(TAG, " message: " + message);
                Log.e(TAG, " socket: " + socket);
                Log.e(TAG, " ===== ======= ===== ");
                Log.e(TAG, " ");
                send.start();

                // todo: mysql에 메시지 저장하기
                // RoomNo getId MType Mode Message JoinNo Views
                updateMessage(
                        RoomNo,
                        loginUserId,
                        "3", // 영상통화
                        message,
                        "2",
                        "1",
                        time);

                messageContent = null;
                //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간, 안 읽은 사람, 참여자 수
                messageContent = new chattingMessageContent(1, loginUserId, loginUserNick, "3", message, time, Message_views);
//                messageContent = new chattingMessageContent(1, targetID, targetNickName, "3", message, time, Message_views);

                // 내 메시지 목록에 추가
                messageData.add(messageContent);
                message_area.smoothScrollToPosition(messageData.size());

                Log.e(TAG, " ");

                Log.e(TAG, "onClick: updateMessage(): " +
                        "\nRoomNo: " + RoomNo + "" +
                        "\n / loginUserId: " + loginUserId + "" +
                        "\n / message: " + message + "" +
                        "\n / time: " + time);

                Log.e(TAG, " ");

                // 채팅 입력창 비워주기
                message_content.setText(null);

                // 방 목록 갱신하기
                ChatRoomList_Request();

//              내 메시지 목록에 추가
//              messageData.add(messageContent);
//              chattingRoomAdapter.notifyDataSetChanged();
//              message_area.smoothScrollToPosition(messageData.size());

            // 방 목록 갱신하기
//            ChatRoomList_Request();
            }
//            else
//            {
//                // 영상통화 페이지로 이동
//                Intent intent = new Intent(Activity_ChatRoom.this, ConnectActivity.class);
//                intent.putExtra("loginId", targetID);
//                startActivity(intent);
//
//                Log.e(TAG, "onActivityResult: 영상통화 시작");
//            }
        }
    }

    String URL_UPLOAD = "http://115.68.231.84/Chat_Photo_Message_Record.php";

    // 서버로 사진 전송하기
    private void UploadPicture(final String id, final String photo)
    {
        // 서버로 요청할 메소드와 주소 담기
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) // 요청받은 json 결과를 response 에 담는다.
                    {
                        Log.e(TAG, "onResponse: UploadPicture String response = " + response.toString());

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            String success = jsonObject.getString("success");


                            // 키값이 read인 json에 담긴 value들을 배열에 담는다.
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

//                                    String MIR_No = object.getString("MIR_No").trim();
                                    String MIR_ImagePath = object.getString("MIR_ImagePath").trim();

//                                    Log.e(TAG, "onResponse: MIR_No = " + MIR_No);
                                    Log.e(TAG, "onResponse: MIR_ImagePath = " + MIR_ImagePath);

                                    // todo: 2. 방금 등록한 이미지 주소를 아래 mysql에 저장하기

                                    // todo: mysql에 메시지 저장하기
                                    // RoomNo getId MType Mode Message JoinNo Views
                                    updateMessage(
                                            RoomNo,
                                            loginUserId,
                                            "2", // 사진 모드로 전송
                                            MIR_ImagePath,
                                            "2", // 1대1 방이기때문에 참여 인원은 2명
                                            "1",
                                            time);

                                    Log.e(TAG, "onClick: updateMessage(): " +
                                            "RoomNo: " + RoomNo + "" +
                                            " / loginUserId: " + loginUserId + "" +
                                            " / message: " + MIR_ImagePath + "" +
                                            " / time: " + time);

                                    // todo: 3. 방금 등록한 이미지 주소를 상대방에게 전송하기

                                    Type = "2";

                                    // 소켓 서버로 메세지 보내주기
                                    send = new SendThread(socket, MIR_ImagePath);
                                    Log.e(TAG, " ");
                                    Log.e(TAG, " ===== SendThread ===== ");
                                    Log.e(TAG, " message: " + MIR_ImagePath);
                                    Log.e(TAG, " socket: " + socket);
                                    Log.e(TAG, " ===== ======= ===== ");
                                    Log.e(TAG, " ");
                                    send.start();

                                    // 리사이클러뷰의 마지막 아이템 위치로 보내기
                                    // message_area.scrollToPosition(messageData.size() - 1);

                                    // 채팅 입력창 비워주기
                                    message_content.setText(null);

                                    // 보낼 내용이 없을 땐 파란 불 끔
                                    // send_button.getResources().getDrawable(R.drawable.ic_send_message);
                                    send_button.setImageResource(R.drawable.ic_send_message);
                                    send_button.setEnabled(false); // 전송버튼 비활성화

                                    // 방 목록 갱신하기
                                    ChatRoomList_Request();
                                }
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();

                            Toast.makeText(Activity_ChatRoom.this, "이미지 전송 실패", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e = " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) // 응답이 안오면 실패로 간주.
                    {
                        // 오류 원인 기록
//                        Toast.makeText(Activity_MyPage.this, "다시 시도해주세요" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: UploadPicture VolleyError error = " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                // params에 id와 photo를 저장함.
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("photo", photo);

                return params;
            }
        };

        // stringRequest 에서 설정한 메소드와 서버 주소로 요청을 전송한다.
        // 서버 주소에 있는 php 파일을 실행한다.
        // 실행 결과를 전달받는다.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // 비트맵을 문자열로 변환하는 메소드
    public String getStringImage(Bitmap bitmap)
    {
        // 바이트 배열 사용.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // 세션 생성
//        sessionManager.createProfileImageSession(encodedImage);

        // 인코딩 결과를 반환한다.
        return encodedImage;
    }

    //todo: 텍스트 메시지 전송하기
    public void setSendBtn()
    {
        send_button = (ImageView) findViewById(R.id.send_button); // 메시지 전송버튼
        message_content = findViewById(R.id.message_content); // 메시지 입력공간

        message_content.addTextChangedListener(new TextWatcher()
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
                String message = message_content.getText().toString();

                Log.e(TAG, "setSendBtn: message: " + message);

                if (TextUtils.isEmpty(message) || message.equals(""))
                {
                    // Toast.makeText(Activity_ChatRoom.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();

                    // 보낼 내용이 없을 땐 파란 불 끔
                    // send_button.getResources().getDrawable(R.drawable.ic_send_message);
//                    send_button.setImageResource(R.drawable.ic_send_message);
                    send_button.setImageResource(R.drawable.ic_send_message);
                    send_button.setEnabled(false); // 전송버튼 비활성화

                } else
                {
                    // 보낼 내용이 없을 땐 파란 불 켬
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message_2);
                    send_button.setImageResource(R.drawable.ic_send_message_2);
                    send_button.setEnabled(true); // 전송버튼 활성화

                    send_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 텍스트 메시지: 1
                            // 사진 메시지: 2
                            int mode = 1;

//                            String senderId = loginUserId;
//                            String senderNick = loginUserNick;

                            Log.e(TAG, "setSendBtn: loginUserId: " + loginUserId + " / loginUserNick: " + loginUserNick);

                            messageContent = null;
//                            messageContent = new chattingMessageContent(mode, MyPhoto, senderId, senderNick, message, time);
                            //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간, 안 읽은 사람, 참여자 수
                            messageContent = new chattingMessageContent(mode, loginUserId, loginUserNick, "1", message, time, Message_views);

                            Log.e(TAG, " ");
                            Log.e(TAG, " ===== 전송 중 ===== ");
//                            Log.e(TAG, "mode: " + 1 + " / MyPhoto: " + MyPhoto + " / senderId: " + senderId);
                            Log.e(TAG, "senderNick: " + loginUserId + " / message: " + message);
                            Log.e(TAG, "time: " + time);
                            Log.e(TAG, " ===== ======= ===== ");
                            Log.e(TAG, " ");

//                    chat_area.append(senderNick + ": " + message + "\n");

                            messageData.add(messageContent);
//                    chattingRoomAdapter.notifyDataSetChanged();
                            message_area.smoothScrollToPosition(messageData.size());

                            // todo: mysql에 메시지 저장하기
                            // RoomNo getId MType Mode Message JoinNo Views
                            updateMessage(
                                    RoomNo,
                                    loginUserId,
                                    "1",
                                    message,
                                    "2",
                                    "1",
                                    time);

                            Log.e(TAG, "onClick: updateMessage(): " +
                                    "RoomNo: " + RoomNo + "" +
                                    " / loginUserId: " + loginUserId + "" +
                                    " / message: " + message + "" +
                                    " / time: " + time);

                            // 소켓 서버로 메세지 보내주기

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
                            message_content.setText(null);

                            // 보낼 내용이 없을 땐 파란 불 끔
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message);
                            send_button.setImageResource(R.drawable.ic_send_message);
                            send_button.setEnabled(false); // 전송버튼 비활성화
                            // 키보드 내려주기
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(message_content.getWindowToken(), 0);

//                    Toast.makeText(Activity_ChatRoom.this, "전송", Toast.LENGTH_SHORT).show();

                            // 방 목록 갱신하기
                            ChatRoomList_Request();
                        }
                    });
                }
            }

            // 입력하기 전에
            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }

    // todo: mysql에 메시지 저장하기
    private void updateMessage
    (
            String RoomNo,
            String getId,
            String MType,
            String Message,
            String JoinNo,
            String Views,
            String time
    )
    {
        Log.e(TAG, "updateMessage: 서버로 메시지 전송");

        // 입력한 정보를 php POST로 DB에 전송합니다.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Chat_Message_Record.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
//                      Log.e(TAG, "onResponse: response = " + response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                            String success = jsonObject.getString("success");

                            if (success.equals("1"))
                            {
//                                Toast.makeText(Activity_ChatRoom.this, "전송완료", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: success: " + success);

                                // 방 목록 갱신하기
                                ChatRoomList_Request();
                            } else
                            {
//                                Toast.makeText(Activity_ChatRoom.this, "오류발행", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse: success: " + success);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
//                            Toast.makeText(Activity_ChatRoom.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_ChatRoom.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("RoomNo", RoomNo);
                params.put("getId", getId);
                params.put("MType", MType);
                params.put("Message", Message);
                params.put("JoinNo", JoinNo);
                params.put("Views", Views);
                params.put("time", time);

//                updateMessage(RoomNo, loginUserId, "1", message, "2", "1", time);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Activity_ChatRoom.this);
        requestQueue.add(stringRequest);
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
                        output.writeUTF(RoomNo + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views);

                        Log.e(TAG, "(SendThread) roomNo: " + RoomNo + "/ loginUserId    : " + loginUserId);
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

    // 내 정보 불러오기
    private void getUserDetail()
    {
        // 세션생성
        sessionManager = new SessionManager(this);

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        select_image = findViewById(R.id.select_image);
        select_image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 다이얼로그 실행
                OtherTaskDialog();
            }
        });

        // Volley로 서버 요청을 보내기 위해 데이터 세팅.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>()
                {
                    // JSON 형태로 결과 요청을 받는다.
                    // 요청받은 결과가 response에 저장된다.
                    @Override
                    public void onResponse(String response)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        Log.i(TAG, response.toString());

                        try
                        {
                            // JSON 형식으로 응답받음.
                            JSONObject jsonObject = new JSONObject(response);

                            String success = jsonObject.getString("success");

                            // 키값이 read인 json에 담긴 value들을 배열에 담는다.
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            // success의 값이 아래와 같으면 아래 반복문을 진행한다
                            // {"success":"??"} = ??에 담긴 값이 1일 때 아래 반복문을 진행
                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    loginUserId = getId; // 세션에서 불러온 유저 아이디
                                    loginUserNick = object.getString("name").trim(); // 로그인한 유저의 이름
//                                    MyPhoto = object.getString("image").trim(); // 프로필 이미지 서버경로

                                    Log.e(TAG, "onResponse: loginUserId: " + loginUserId);
                                    Log.e(TAG, "onResponse: loginUserNick: " + loginUserNick);
//                                    Log.e(TAG, "onResponse: MyPhoto: " + MyPhoto);

//                                    if (MyPhoto == null)
//                                    {
//                                        Log.e(TAG, "onResponse: MyPhoto: " + MyPhoto);
//                                    }

                                    // 로그인한 유저 이름
                                    // 방 번호를 어떻게 줘야할까...
//                                    String roomAndUserData = 1 + "@" + loginUserNick;

                                    // 방 번호
                                    String roomAndUserData = RoomNo + "@" + loginUserId;
                                    Log.e(TAG, "onResponse: roomAndUserData: " + roomAndUserData);

//                                    // 방번호와 유저의 이름으로 서버에 접속한다
                                    Log.e(TAG, "onCreate: roomAndUserData: " + roomAndUserData);
                                    client = new SocketClient(roomAndUserData);
                                    client.start();

                                    ///////////////////////////////////////////////////

                                    // 리사이클러뷰 세팅
                                    message_area = findViewById(R.id.message_area); // 리사이클러뷰
                                    message_area.setHasFixedSize(true);
                                    message_area.setLayoutManager(new LinearLayoutManager(Activity_ChatRoom.this));

                                    // 1. 메시지 입력창 클릭 후 키보드가 위로 올라오면
                                    // 2. 말풍선도 맨 아래로 스크롤 한다.
//                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

                                    // todo: 현재 머물러 있는 방에서 주고받았던 메시지 불러오기
                                    getMessageRecord();

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
//                                                // 수신 1
//                                                messageContent = new chattingMessageContent(
//                                                        1               /*모드1 = 수신모드*/,
//                                                        ReceiverPhoto   /*보낸사람 사진*/,
//                                                        msgFilter[0]    /*보낸사람*/,
//                                                        targetNickName  /*보낸 사람의 닉네임*/,
//                                                        msgFilter[1]    /*내용*/,
//                                                        msgFilter[2]    /*시간*/); // chattingMessageContent 데이터 클래스

                                                // 수신 1
                                                messageContent = new chattingMessageContent(
                                                        1               /*모드1 = 수신모드*/,
                                                        msgFilter[0]    /*보낸사람*/,
                                                        targetNickName  /*보낸 사람의 닉네임*/,
                                                        msgFilter[3],   /*메시지 타입. 1 = 메시지, 2 = 사진*/
                                                        msgFilter[1]    /*내용*/,
                                                        msgFilter[2]    /*시간*/,
                                                        Message_views
                                                );

//                                                messageContent = new chattingMessageContent(mode, loginUserId, loginUserNick, "2", message, time);

//                                                chat_area.append(targetNickName + ": " + msgFilter[1] + "\n");

                                                Log.e(TAG, "handleMessage: 보낸사람[0]      : " + msgFilter[0]);
                                                Log.e(TAG, "handleMessage: ReceiverPhoto    : " + ReceiverPhoto);
                                                Log.e(TAG, "handleMessage: targetNickName   : " + targetNickName);
                                                Log.e(TAG, "handleMessage: 내용[1]          : " + msgFilter[1]);
                                                Log.e(TAG, "handleMessage: 시간[2]          : " + msgFilter[2]);
                                                Log.e(TAG, "handleMessage: 타입[3]          : " + msgFilter[3]);

                                                messageData.add(messageContent); // 어레이 리스트
                                                chattingRoomAdapter.notifyDataSetChanged();

                                                // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
                                                message_area.smoothScrollToPosition(messageData.size());

                                                Log.e(TAG, "handleMessage: 대화목록 갱신 ");
//                                                Fragment_Home_Menu_3.slide_chat_list_adpater.notifyDataSetChanged();
//                                                Fragment_Home_Menu_3.ChatRoomList_Request();

                                                // todo: 영상통화 감지하기
                                                if (msgFilter[1].equals("video_chat_request_code_1"))
                                                {
                                                    Intent intent = new Intent(Activity_ChatRoom.this, Activity_Video_Receive.class);
                                                    intent.putExtra("targetID", targetID); // 방번호
                                                    intent.putExtra("targetNickName", targetNickName); // 상대 이름
                                                    intent.putExtra("ReceiverPhoto", ReceiverPhoto); // 상대 사진
                                                    startActivityForResult(intent, 300);
                                                    Log.e(TAG, "handleMessage: 영상통화 감지됨");
                                                }

                                                //todo: 영상통화 거절 감지하기
                                                if (msgFilter[1].equals("video_chat_request_code_3"))
                                                {
                                                    Log.e(TAG, "handleMessage: 통화 거절");

                                                    // 영상통화 종료
                                                    CallActivity callActivity = (CallActivity) CallActivity.callActivity;
                                                    callActivity.finish();

                                                    // todo c. 상대방에게 영상통화 요청 메시지를 전송한다.
                                                    String message = "video_chat_request_code_3";

                                                    Log.e(TAG, "setSendBtn: loginUserId: " + loginUserId + " / loginUserNick: " + loginUserNick);

//                                                    messageContent = null;
//                                                    //                                          모드, 수신인     , 발신인       , 메시지 유형, 내용   , 전송시간, 안 읽은 사람, 참여자 수
//                                                    messageContent = new chattingMessageContent(1, loginUserId, loginUserNick, "3", message, time, Message_views);
//
//                                                    // 내 메시지 목록에 추가
//                                                    messageData.add(messageContent);
//                                                    message_area.smoothScrollToPosition(messageData.size());

                                                    Log.e(TAG, " ");

                                                    Log.e(TAG, "onClick: updateMessage(): " +
                                                            "\nRoomNo: " + RoomNo + "" +
                                                            "\n / loginUserId: " + loginUserId + "" +
                                                            "\n / message: " + message + "" +
                                                            "\n / time: " + time);

                                                    Log.e(TAG, " ");

                                                    // 채팅 입력창 비워주기
                                                    message_content.setText(null);

                                                    // 방 목록 갱신하기
                                                    ChatRoomList_Request();
                                                }



//                                                Log.e(TAG, "handleMessage: messageData.add(messageContent)");
//                                                Log.e(TAG, "어레이리스트 조회하기 ");
//                                                for (int i = 0; i < messageData.size(); i++)
//                                                {
//                                                    Log.e(TAG, "handleMessage for(" + i + "): getMode       : " + messageData.get(i).getMode());
////                                                    Log.e(TAG, "handleMessage for(" + i + "): getUserPhoto  : " + messageData.get(i).getUserPhoto());
//                                                    Log.e(TAG, "handleMessage for(" + i + "): getSenderId   : " + messageData.get(i).getSenderId());
//                                                    Log.e(TAG, "handleMessage for(" + i + "): getSenderNick : " + messageData.get(i).getSenderNick());
//                                                    Log.e(TAG, "handleMessage for(" + i + "): getMessage    : " + messageData.get(i).getMessage());
//                                                    Log.e(TAG, "handleMessage for(" + i + "): getTime       : " + messageData.get(i).getTime());
//                                                }
                                            }
                                        }
                                    };

                                    // 방 목록 갱신하기
                                    ChatRoomList_Request();

//                                    // 메시지 전송버튼 & 채팅 입력 이벤트
//                                    setSendBtn();
                                }
                            }
                        } // try에 포함된 로직 중 틀린 코드가 있으면 예외상황으로 간주함.
                        catch (JSONException e) // 에러 알림
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_ChatRoom.this, "에러발생!" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: getUserDetail JSONException e: " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() // 응답 실패할 시 에러 알림
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_ChatRoom.this, "에러발생. 로그확인", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: getUserDetail VolleyError: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // 로그인 한 유저의 '유저 번호'를 해쉬맵에 담는다.
                params.put("id", getId);

                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void ChatRoomList_Request()
    {
        Log.e(TAG, "ChatUserList_Request: 대화가 진행중인 방 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface ChatRoomRequest = retrofit.create(ApiInterface.class);

        Call<List<Chat_Item_Room_List>> listCall = ChatRoomRequest.ChatRoomList(getId);

        listCall.enqueue(new Callback<List<Chat_Item_Room_List>>()
        {
            @Override
            public void onResponse(Call<List<Chat_Item_Room_List>> call, retrofit2.Response<List<Chat_Item_Room_List>> response)
            {
                Fragment_Home_Menu_3.chat_item_room_listList = response.body();
                Log.e(TAG, "onResponse: 대화목록 요청 완료");

                Fragment_Home_Menu_3.slide_chat_list_adpater = new Fragment_Home_Menu_3.Slide_Chat_List_Adpater(getApplicationContext(), Fragment_Home_Menu_3.chat_item_room_listList);
                Fragment_Home_Menu_3.mRecyclerView.setAdapter(Fragment_Home_Menu_3.slide_chat_list_adpater);
            }

            @Override
            public void onFailure(Call<List<Chat_Item_Room_List>> call, Throwable t)
            {
//                Toast.makeText(Context_Chat_List, "대화목록 조회 실패, 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    // todo: 현재 머물러 있는 방에서 주고받았던 메시지 불러오기
    private void getMessageRecord()
    {
        Log.e(TAG, "ChatUserList_Request: 메시지 기록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface MessageRecordRequest = retrofit.create(ApiInterface.class);

        Call<List<chattingMessageContent>> listCall = MessageRecordRequest.Chat_getMessage(RoomNo);

        listCall.enqueue(new Callback<List<chattingMessageContent>>()
        {
            @Override
            public void onResponse(Call<List<chattingMessageContent>> call, retrofit2.Response<List<chattingMessageContent>> response)
            {
                messageData = response.body();

                Log.e(TAG, "onResponse: 메시지 기록 요청 완료");

                // 리사이클러뷰 세팅
                chattingRoomAdapter = new ChattingRoomAdapter(Activity_ChatRoom.this, (ArrayList<chattingMessageContent>) messageData);
                message_area.setAdapter(chattingRoomAdapter);

                // 리사이클러뷰의 아이템 위치를 아래로 내리기
                message_area.scrollToPosition(messageData.size() - 1);

                // 메시지 읽음처리하기
//                MessageViews();
            }

            @Override
            public void onFailure(Call<List<chattingMessageContent>> call, Throwable t)
            {
                Toast.makeText(Activity_ChatRoom.this, "메시지 기록 조회 실패, 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    // 메시지 읽음 처리하기
    private void MessageViews()
    {
        // TODO: 메시지 읽음표시하기
        // 해당 방에서 내가 읽지 않은 메시지를 조회한다.
        // 내가 읽지 않은 메시지가 있으면 읽음표시를 -1 한다.
        // 그 후 읽은 유저에 나를 포함시킨다
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Chat_Message_View.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG + "MessageViews()", "onResponse: response: " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                            String success = jsonObject.getString("success");

                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            String result = null;

                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    result = object.getString("notice").trim();
                                }

                                if (result.equals("You have already read"))
                                {
                                    // 당신은 이미 읽었습니다.
                                    Log.e(TAG, "onResponse: ViewsReceive: " + result);
                                } else if (result.equals("All participants read"))
                                {
                                    // 모든 유저가 읽었습니다.
                                    Log.e(TAG, "onResponse: ViewsReceive: " + result);
                                } else if (result.equals("New Message read completed"))
                                {
                                    ViewsReceive = -1;
                                }
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_ChatRoom.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_ChatRoom.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("getId", getId);
                params.put("RoomNo", RoomNo);
//                                                    WorkDBIndex http://115.68.231.84/Question_addList.php
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Activity_ChatRoom.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
        // 소켓 연결 해제
//        try
//        {
//            socket.close();
//            Log.e(TAG, "onOptionsItemSelected: 소켓 연결 해제함");
//        }
//
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }

        finish();
        super.onBackPressed();
    }

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            { //toolbar의 back키 눌렀을 때 동작

//                Intent intent = new Intent(Activity_ChatRoom.this, Activity_Home.class);
//                startActivity(intent);

                // 소켓 연결 해제
//                try
//                {
//                    socket.close();
//                    Log.e(TAG, "onOptionsItemSelected: 소켓 연결 해제함");
//                }
//
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }

                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//    }

    // 어댑터 클래스
    public class ChattingRoomAdapter extends RecyclerView.Adapter<ChattingRoomAdapter.ChattingRoom_ViewHolder>
    {
        private Context mContext;
        //        private List<chattingMessageContent> chattingMessageContent;
        private String TAG = "ChattingRoomAdapter: ";

        public ChattingRoomAdapter(Context Context, ArrayList<chattingMessageContent> List)
        {
            mContext = Context;
            messageData = List;
        }

        @NonNull
        @Override
        public ChattingRoom_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_room_message, viewGroup, false);
            return new ChattingRoom_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChattingRoom_ViewHolder chattingRoom_viewHolder, int i)
        {
            chattingMessageContent currentItem = messageData.get(i);

            // 메시지 모드 불러오기
            // 모드 = 1 전송모드
            // 모드 = 2 수신모드
            int mode = currentItem.getMode();

            ViewsReceive = currentItem.getMessage_views();

            Log.e(TAG, "onBindViewHolder: ViewsReceive: " + ViewsReceive);

            Log.e(TAG, "onBindViewHolder: mode: " + mode);

            String
                    /*chat_user_photo = currentItem.getUserPhoto()    // 상대방 프로필 이미지
                    ,*/ chat_user_name = currentItem.getSenderNick()    // 상대방 이름
                    , chat_user_id = currentItem.getSenderId()        // 작성자 아이디
                    , time_send = currentItem.getTime()               // 메시지 전송한 시간
                    , time_receive = currentItem.getTime()            // 메시지 수신받은 시간
                    , chat_message_content = currentItem.getMessage() // 메시지 내용
                    , Message_Type = currentItem.getMType()           // 메시지 타입
                    ;

            Log.e(TAG, "onBindViewHolder: chat_user_id  : " + chat_user_id);
            Log.e(TAG, "onBindViewHolder: targetID      : " + targetID);
            Log.e(TAG, "onBindViewHolder: MType         : " + Message_Type);
            Log.e(TAG, "onBindViewHolder: Message       : " + chat_message_content);

            // 작성자 아이디와 상대방 아이디가 일치하면
            // 화면 왼 쪽에 메시지 띄우기
            if (chat_user_id.equals(targetID))
            {
                // 메시지 타입이 '1'일 경우 텍스트 메시지
                if (Message_Type.equals("1"))
                {
                    // 사진, 이름, 수신 시간 활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.VISIBLE);

                    // 0보다 작거나 같으면 읽음표시 숨기기
//                    if (ViewsReceive == 0)
//                    {
//                        chattingRoom_viewHolder.views_receive.setVisibility(View.GONE);
//                    }
//
//                    else
//                    {
//                        chattingRoom_viewHolder.views_receive.setVisibility(View.VISIBLE);
//                    }

                    // 전송시간, 이미지뷰, 영상통화 말풍선 숨기기
                    chattingRoom_viewHolder.time_send.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.GONE);
//                    chattingRoom_viewHolder.views_send.setVisibility(View.GONE);

//            왼 쪽 표시   , 오른 표시
//            ViewsReceive, views_send

                    // 말풍선 색상을 수신모드로 바꾸기.
                    // 1. 말풍선의 위치를 왼 쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.LEFT);
                    chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_2);

                    // 유저 이미지
                    Picasso.get().load(ReceiverPhoto).
                            placeholder(R.drawable.logo_2).
                            resize(30, 30).
                            into(chattingRoom_viewHolder.chat_user_photo);

                    chattingRoom_viewHolder.chat_user_name.setText(chat_user_name);
                    chattingRoom_viewHolder.time_receive.setText(time_receive);
                    chattingRoom_viewHolder.chat_message_content.setText(chat_message_content);
                }

                // 메시지 타입이 '2'일 경우 이미지 메시지
                else if (Message_Type.equals("2"))
                {
                    Log.e(TAG, "onBindViewHolder: 이미지 수신받음");

                    // 사진, 이름, 이미지뷰, 수신 시간, 읽음표시 활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.VISIBLE);
//                    chattingRoom_viewHolder.ViewsReceive.setVisibility(View.VISIBLE);

                    // 0보다 작거나 같으면 읽음표시 숨기기
//                    if (ViewsReceive == 0)
//                    {
//                        chattingRoom_viewHolder.views_receive.setVisibility(View.GONE);
//                    }
//
//                    else
//                    {
//                        chattingRoom_viewHolder.views_receive.setVisibility(View.VISIBLE);
//                    }

                    // 전송시간, 텍스트뷰, 영상통화 말풍선 숨기기
                    chattingRoom_viewHolder.time_send.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.GONE);
//                    chattingRoom_viewHolder.views_send.setVisibility(View.GONE);

                    // 말풍선 색상을 수신모드로 바꾸기.
                    // 1. 말풍선의 위치를 왼 쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.LEFT);
                    chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_2);

                    // 수신받은 이미지
                    Picasso.get().load(chat_message_content).
                            placeholder(R.drawable.logo_2).
                            resize(150, 100).
                            centerCrop().
                            into(chattingRoom_viewHolder.chat_message_image);

                    // 유저 이미지
                    Picasso.get().load(ReceiverPhoto).
                            placeholder(R.drawable.logo_2).
                            resize(30, 30).
                            into(chattingRoom_viewHolder.chat_user_photo);

                    chattingRoom_viewHolder.chat_message_image.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mContext = v.getContext();
                            Intent intent = new Intent(Activity_ChatRoom.this, Activity_ImageView.class);
                            intent.putExtra("image", chat_message_content);
                            mContext.startActivity(intent);
                        }
                    });

                    chattingRoom_viewHolder.chat_user_name.setText(chat_user_name);

                    chattingRoom_viewHolder.time_receive.setText(time_receive);
                }

                // 메시지 타입이 '3'일 경우 영상통화
                else if (Message_Type.equals("3"))
                {
                    Log.e(TAG, "onBindViewHolder: 영상통화 수신받음");

                    // 영상통화 말풍선, 영상통화 내용, 이름, 수신 시간 활성화
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.VISIBLE);

                    // 상대방 사진, 이름, 상대방의 수신 시간, 활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.VISIBLE);
//                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.VISIBLE);
//                    chattingRoom_viewHolder.time_receive.setVisibility(View.VISIBLE);

                    // chat_video_visibility
                    // chat_video_text_view

                    // 전송시간, 텍스트뷰, 이미지뷰 숨기기
                    chattingRoom_viewHolder.time_send.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.GONE);

                    // 말풍선 색상을 수신모드로 바꾸기.
                    // 1. 말풍선의 위치를 왼 쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.LEFT);
//                    chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_2);
                    chattingRoom_viewHolder.chat_video_visibility.setBackgroundResource(R.drawable.item_chat_type_2);

                    // 유저 이미지
                    Picasso.get().load(ReceiverPhoto).
                            placeholder(R.drawable.logo_2).
                            resize(30, 30).
                            into(chattingRoom_viewHolder.chat_user_photo);


                    chattingRoom_viewHolder.chat_user_name.setText(chat_user_name);
                    chattingRoom_viewHolder.time_receive.setText(time_receive);

                    if (chat_message_content.equals("video_chat_request_code_1"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 해요");
                    } else if (chat_message_content.equals("video_chat_request_code_2"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 종료 _ ");
                    } else if (chat_message_content.equals("video_chat_request_code_3"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 거절 !");
//                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 종료 _ ");
                    }
                    // else if code_4 = 응답없음

                    Log.e(TAG, "onBindViewHolder: chat_message_content: " + chat_message_content);

                }

                Log.e(TAG, " ");
                Log.e(TAG, " ===== ===== 리사이클러뷰 발신 ===== ===== mode: " + mode);
                Log.e(TAG, "메시지 수신: " + chat_message_content);
                Log.e(TAG, "받은 시간" + time_receive);
                Log.e(TAG, "이름: " + chat_user_name + " / 아이디: " + chat_user_id + "이미지: " + ReceiverPhoto);
                Log.e(TAG, " ===== ===== ===== ===== ===== ===== ===== =====");
                Log.e(TAG, " ");
            }

            // 작성자 아이디와 상대방 아이디가 일치하지 않으면
            // 화면 오른쪽에 메시지 띄우기
            else if (chat_user_id.equals(loginUserId))
            {
                // 메시지 타입이 '1'일 경우 텍스트 메시지
                if (Message_Type.equals("1"))
                {
                    // 전송시간 활성화
                    chattingRoom_viewHolder.time_send.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.VISIBLE);

                    // 0보다 작거나 같으면 읽음표시 숨기기
//                    if (ViewsReceive == 0)
//                    {
//                        chattingRoom_viewHolder.views_send.setVisibility(View.GONE);
//                    }

//                    else
//                    {
//                        chattingRoom_viewHolder.views_send.setVisibility(View.VISIBLE);
//                    }

                    // 사진, 이름, 수신 시간, 이미지뷰, 영상통화 말풍선 비활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.GONE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.GONE);
//                    chattingRoom_viewHolder.views_receive.setVisibility(View.GONE);

                    // 1. 말풍선의 위치를 오른쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.RIGHT);
                    chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_1);

                    // 내용, 보낸 시간
                    chattingRoom_viewHolder.chat_message_content.setText(chat_message_content);
                    chattingRoom_viewHolder.time_send.setText(time_send);
                }

                // 메시지 타입이 '2'일 경우 이미지 메시지
                else if (Message_Type.equals("2"))
                {
                    // 전송시간, 이미지뷰 활성화
                    chattingRoom_viewHolder.time_send.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.VISIBLE);

                    // 0보다 작거나 같으면 읽음표시 숨기기
//                    if (ViewsReceive == 0)
//                    {
//                        chattingRoom_viewHolder.views_send.setVisibility(View.VISIBLE);
//                    }
//
//                    else
//                    {
//                        chattingRoom_viewHolder.views_send.setVisibility(View.GONE);
//                    }

                    // 사진, 이름, 수신 시간, 텍스트뷰, 영상통화 말풍선 비활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.GONE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.GONE);
//                    chattingRoom_viewHolder.views_receive.setVisibility(View.GONE);

                    // 1. 말풍선의 위치를 오른쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.RIGHT);
                    chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_1);

                    // 내용, 보낸 시간
                    // chattingRoom_viewHolder.chat_message_content.setText(chat_message_content);

                    // 전송한 이미지
                    Picasso.get().load(chat_message_content).
                            centerCrop().
                            placeholder(R.drawable.logo_2).
                            resize(250, 160).
                            into(chattingRoom_viewHolder.chat_message_image);

                    chattingRoom_viewHolder.chat_message_image.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mContext = v.getContext();
                            Intent intent = new Intent(Activity_ChatRoom.this, Activity_ImageView.class);
                            intent.putExtra("image", chat_message_content);
                            mContext.startActivity(intent);
                        }
                    });

                    chattingRoom_viewHolder.time_send.setText(time_send);
                }

                // 메시지 타입이 '2'일 경우 이미지 메시지
                else if (Message_Type.equals("3"))
                {
                    // 영상통화 말풍선 활성화
                    chattingRoom_viewHolder.time_send.setVisibility(View.VISIBLE);
                    chattingRoom_viewHolder.chat_video_visibility.setVisibility(View.VISIBLE);

                    // 사진, 이름, 수신 시간,  비활성화
                    chattingRoom_viewHolder.chat_user_photo.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_user_name.setVisibility(View.GONE);
                    chattingRoom_viewHolder.time_receive.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_image.setVisibility(View.GONE);
                    chattingRoom_viewHolder.chat_message_content.setVisibility(View.GONE);
//                    chattingRoom_viewHolder.views_receive.setVisibility(View.GONE);

                    // 1. 말풍선의 위치를 오른쪽으로 배치한다.
                    // 2. 말풍선 색상을 전송모드로 바꾸기.
                    chattingRoom_viewHolder.message_gravity.setGravity(Gravity.RIGHT);
                    chattingRoom_viewHolder.chat_video_visibility.setBackgroundResource(R.drawable.item_chat_type_1);

                    // 보낸 시간
                    chattingRoom_viewHolder.time_send.setText(time_send);

                    // 내용
                    if (chat_message_content.equals("video_chat_request_code_1"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 해요");
                    } else if (chat_message_content.equals("video_chat_request_code_2"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 종료 _ ");
                    } else if (chat_message_content.equals("video_chat_request_code_3"))
                    {
                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 거절 !");
//                        chattingRoom_viewHolder.chat_video_text_view.setText("영상통화 종료 _ ");
                    }
                    // else if code_4 = 응답없음

                    Log.e(TAG, "onBindViewHolder: chat_message_content: " + chat_message_content);
                }

                Log.e(TAG, " ");
                Log.e(TAG, " ===== ===== 리사이클러뷰 전송 ===== ===== mode: " + mode);
                Log.e(TAG, "메시지 수신: " + chat_message_content);
                Log.e(TAG, "보낸 시간" + time_send);
                Log.e(TAG, " ===== ===== ===== ===== ===== ===== ===== =====");
                Log.e(TAG, " ");
            }
        }

        @Override
        public int getItemCount()
        {
            return messageData.size();
        }

        public class ChattingRoom_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;

            public LinearLayout message_gravity, chat_video_visibility;

            public CircleImageView chat_user_photo;

            public TextView
                    chat_user_name, time_send, time_receive, chat_message_content,
            /*views_receive, views_send,*/ chat_video_text_view;

            public ImageView chat_message_image;

            public ChattingRoom_ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                view = itemView;
//                views_receive = itemView.findViewById(R.id.views_receive);
//                views_send = itemView.findViewById(R.id.views_send);
                message_gravity = itemView.findViewById(R.id.message_gravity);
                chat_user_photo = itemView.findViewById(R.id.chat_user_photo);
                chat_user_name = itemView.findViewById(R.id.chat_user_name);
                time_send = itemView.findViewById(R.id.time_send);
                time_receive = itemView.findViewById(R.id.time_receive);
                chat_message_content = itemView.findViewById(R.id.chat_message_content);
                chat_message_image = itemView.findViewById(R.id.chat_message_image);
                chat_video_visibility = itemView.findViewById(R.id.chat_video_visibility);
                chat_video_text_view = itemView.findViewById(R.id.chat_video_text_view);
            }
        }
    }
}

