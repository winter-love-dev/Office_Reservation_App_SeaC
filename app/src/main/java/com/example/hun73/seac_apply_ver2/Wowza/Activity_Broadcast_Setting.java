package com.example.hun73.seac_apply_ver2.Wowza;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.Wowza.config.AutoCompletePreference;
import com.wowza.gocoder.sdk.api.configuration.WOWZStreamConfig;

import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyId;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyName;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyPhoto;

public class Activity_Broadcast_Setting extends AppCompatActivity
{
    private String TAG = "Activity_Broadcast_Setting";

    // 배경 영상 뷰
    private VideoView broadcast_setting_video_view;
    private TextView wowza_setting_title_input, wowza_setting_start_broadcast;

    // 방송 제목
    public static String broadcast_title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_setting);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.wowza_setting_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // todo: 배경 영상 세팅
        broadcast_setting_video_view = findViewById(R.id.broadcast_setting_video_view);
        broadcast_setting_video_view.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.broadcast));
        broadcast_setting_video_view.start();
        broadcast_setting_video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                mp.setLooping(true); // 동영상 무한반복. 원치 않을경우 false
            }
        });

        // todo: 방송 목록(이전 페이지)에서 내 정보를 담은 변수 가져오기
//        Log.e(TAG, "onCreate: 방송 송출 준비");
//        Log.e(TAG, "onCreate: MyId: " + MyId);
//        Log.e(TAG, "onCreate: MyName: " + MyName);
//        Log.e(TAG, "onCreate: MyPhoto: " + MyPhoto);

        // View find
        wowza_setting_title_input = findViewById(R.id.wowza_setting_title_input);
        wowza_setting_start_broadcast = findViewById(R.id.wowza_setting_start_broadcast);

        wowza_setting_title_input.setHint(MyName + "님이 방송을 시작했습니다");

        // 방송 송출화면으로 이동
        wowza_setting_start_broadcast.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 입력한 방송 제목 확인하기
                broadcast_title = wowza_setting_title_input.getText().toString();

                //todo: 제목 입력하지 않으면 자동 지정
                if (broadcast_title.length() == 0)
                {
                    broadcast_title = MyName + "님이 방송을 시작했습니다";
                    Log.e(TAG, "onClick: broadcast_title: length() == 0: " + broadcast_title);
                    broadCastStartDialog();
                } else if (wowza_setting_title_input.length() == 0)
                {
                    broadcast_title = MyName + "님이 방송을 시작했습니다";
                    Log.e(TAG, "onClick: broadcast_title: == null: " + broadcast_title);
                    broadCastStartDialog();
                }

                //todo: 입력한 제목 받기
                else
                {
                    Log.e(TAG, "onClick: broadcast_title: " + broadcast_title);

                    broadCastStartDialog();
                }
            }
        });
    }

    // todo: 방송 시작알림 다이얼로그
    private void broadCastStartDialog()
    {

        // 다이얼로그 세팅
        AlertDialog.Builder startBroadcastDialog = new AlertDialog.Builder(Activity_Broadcast_Setting.this);
        startBroadcastDialog.setTitle("제목: \"" + broadcast_title + "\"");
        startBroadcastDialog.setMessage("방송을 시작합니다");
        startBroadcastDialog
                .setPositiveButton("시작하기",
                        new DialogInterface.OnClickListener()
                        {
                            @SuppressLint("LongLogTag")
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // todo: 방송 목록(이전 페이지)에서 내 정보를 담은 변수 가져오기
                                Log.e(TAG, "onCreate: 방송 송출 준비");
                                Log.e(TAG, "onCreate: MyId: " + MyId);
                                Log.e(TAG, "onCreate: MyName: " + MyName);
                                Log.e(TAG, "onCreate: MyPhoto: " + MyPhoto);

                                // todo: 송출화면에서 시작 버튼을 누르면 서버로 방송 정보 등록하기
                                Intent intent = new Intent(Activity_Broadcast_Setting.this, CameraActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                .setNegativeButton("다시 입력하기",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                            }
                        });

        final AlertDialog edit_dialog = startBroadcastDialog.create();

        edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
        {
            @Override
            public void onShow(DialogInterface arg0)
            {
                edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
//                edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        edit_dialog.show(); // 다이얼로그 실행
        // 다이얼로그 끝
    }

    @Override
    public void onBackPressed()
    {
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
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
