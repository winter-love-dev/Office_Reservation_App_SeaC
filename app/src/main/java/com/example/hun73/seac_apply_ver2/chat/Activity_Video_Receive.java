package com.example.hun73.seac_apply_ver2.chat;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.appRTC.ConnectActivity;
import com.squareup.picasso.Picasso;

public class Activity_Video_Receive extends AppCompatActivity
{

    // 다른 액티비티에서 제어할 수 있게 변수로 만들어줌
    public static Activity Activity_Video_Receive;

    private String TAG = "Activity_Video_Receive";

    private CircleImageView call_user_photo, call_button, call_end_button;

    private TextView call_user_name;

    // 진동
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_receive);

        Activity_Video_Receive = Activity_Video_Receive.this;

        // 진동 시작
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {2500, 500, 2500, 500}; // 3초 진동, 1초 대기, 3초 진동, 0.05초 대기
        vibrator.vibrate(pattern, 0);

        String targetID, targetNickName, ReceiverPhoto;

        call_user_photo = findViewById(R.id.call_user_photo);
        call_user_name = findViewById(R.id.call_user_name);
        call_button = findViewById(R.id.call_button);
        call_end_button = findViewById(R.id.call_end_button);

        Intent intent = getIntent();
        targetID = intent.getExtras().getString("targetID");
        targetNickName = intent.getExtras().getString("targetNickName");
        ReceiverPhoto = intent.getExtras().getString("ReceiverPhoto");

        Log.e(TAG, "onCreate: targetID: " + targetID );
        Log.e(TAG, "onCreate: targetNickName: " + targetNickName );
        Log.e(TAG, "onCreate: ReceiverPhoto: " + ReceiverPhoto );

        // 상대방 사진
        Picasso.get().load(ReceiverPhoto).
                placeholder(R.drawable.logo_2).
//                resize(30, 30).
                into(call_user_photo);

        // 상대방 이름
        call_user_name.setText(targetNickName);

        // 통화 수락버튼
        call_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 진동 종료
                vibrator.cancel();

                // 영상통화 페이지로 이동
                Intent intent2 = new Intent(Activity_Video_Receive.this, Activity_ChatRoom.class);
                intent2.putExtra("call_refuse", "call_start");
                setResult(RESULT_CANCELED, intent2);

                Intent intent = new Intent(Activity_Video_Receive.this, ConnectActivity.class);
                intent.putExtra("loginId", targetID);
                intent.putExtra("call", "call");
                startActivity(intent);
            }
        });

        // 통화 거절버튼
        call_end_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 진동 종료X
                vibrator.cancel();

                Intent intent1 = new Intent(Activity_Video_Receive.this, Activity_ChatRoom.class);
                intent1.putExtra("call_refuse", "call_refuse");
                setResult(RESULT_OK, intent1);

                Activity_Video_Receive.finish();
            }
        });
    }

//    @Override
//    protected void onPause()
//    {
//        finish();
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop()
//    {
//        finish();
//        super.onStop();
//    }
}
