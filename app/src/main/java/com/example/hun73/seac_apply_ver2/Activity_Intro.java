package com.example.hun73.seac_apply_ver2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

// 응용 1단계 작품 시작!!
/*
    인트로 액티비티.
    앱 실행 후 잠시 머무르고 이동한다.
    이동할 액티비티 = 로그인 & 회원가입 액티비티
*/

public class Activity_Intro extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__intro);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 화면 세로고정

        // 아래 스레드를 이용해 다음 프로세스를 실행한다.
        // 1. try에서 설정한 시간동안 해당 액티비티에 머무른다.
        // 2. 문제가 발생하지 않으면 finally로 넘어가서 로그인 액티비티로 이동하는 인텐트를 실행한다.
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try // 아래 설정한 시간만큼 멈춘다
                {
                    sleep(500); // 3000 = 3초, 500 = 0.5초
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }

                finally
                {
                    // catch에서 문제가 발생하지 않으면 로그인 액티비비티로 이동하는 인텐트를 실행한다.
                    Intent MOVE_LOGIN_ACTIVITY = new Intent(Activity_Intro.this, Activity_Home.class);
                    startActivity(MOVE_LOGIN_ACTIVITY);
                    // 액티비티가 전환되면 인트로 액티비티는 정지된다.
                    // 정지된 액티비티에 대한 처리는 아래 onPause 메소드에서 처리한다.
                }
            }
        };
        thread.start();
    }

    @Override // 해당 액티비티가 정지되면
    protected void onPause()
    {
        super.onPause();

        finish(); // 액티비티를 아예 종료한다.
    }
}