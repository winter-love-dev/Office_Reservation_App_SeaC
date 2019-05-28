package com.example.hun73.seac_apply_ver2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Activity_hostAddWorkSpace extends AppCompatActivity
{
    private String TAG = "Activity_hostAddWorkSpace";


    public static Activity activity;

    // 해당 변수에 담긴 값들을 다음 액티비티로 넘긴다
    private TextView adress1; // 주소 1

    private EditText
            adress2, // 주소 2
            work_space_name, // 사무실 이름
            work_space_contact, // 연락처
            work_space_table, // 테이블 수
            work_space_price_d, // 가격_하루
            work_space_price_w, // 가격_일 주일
            work_space_price_m // 가격_한 달
                    ;

    String Work_Edit_Introduce;
    String Work_Edit_WorkDBIndex;
    String Work_Edit_getId;

    // 사무실 정보 입력
    private EditText addSpace_input_address;

    // 이미지 선택, 입력완료 버튼
    private Button addSpace_add_confirm, work_space_edit_next_page;

    private ArrayList<Uri> work_Edit_image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_add_work_space);

        activity = Activity_hostAddWorkSpace.this;

        // 뷰 연결
        // 아래 버튼 addSpace_add_confirm을 누르면
        // 연결한 뷰의 값들을 다음 액티비티로 넘긴다.
        adress1 = findViewById(R.id.work_space_address1); // 주소검색 버튼, 주소1
        adress2 = findViewById(R.id.work_space_address2); // 주소 2
        work_space_name = findViewById(R.id.work_space_name); // 사무실 이름
        work_space_contact = findViewById(R.id.work_space_contact); // 연락처
        work_space_table = findViewById(R.id.work_space_table); // 테이블 수
        work_space_price_d = findViewById(R.id.work_space_price_d); // 가격_하루
        work_space_price_w = findViewById(R.id.work_space_price_w); // 가격_일 주일
        work_space_price_m = findViewById(R.id.work_space_price_m);// 가격_한 달
        addSpace_add_confirm = findViewById(R.id.work_space_next_page); // 다음 페이지로 이동.
        work_space_edit_next_page = findViewById(R.id.work_space_edit_next_page); // 수정

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.addSpace_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // 사무실 수정요청 감지하기
        // 수정이 감지되면 수정할 값들을 받아온다
        Intent intent = getIntent();
        String EditRequest = intent.getStringExtra("Work_Edit_Request");
        Log.e(TAG, "onCreate: Work_Edit_Request: " + EditRequest);

        // 비어있지 않다면 아래 로직을 실행
        if (EditRequest == null)
        {
            Toast.makeText(activity, "양식을 작성합니다.", Toast.LENGTH_SHORT).show();
        } else if (EditRequest.equals("아"))
        {
            // 수정할 땐 수정 버튼 활성화
            addSpace_add_confirm.setVisibility(View.GONE);
            work_space_edit_next_page.setVisibility(View.VISIBLE);

            Intent intent1 = getIntent();
            String Work_Edit_Address1 = intent1.getExtras().getString("Work_Edit_Address1");
            String Work_Edit_Address2 = intent1.getExtras().getString("Work_Edit_Address2");
            String Work_Edit_Name = intent1.getExtras().getString("Work_Edit_Name");
            String Work_Edit_Contact = intent1.getExtras().getString("Work_Edit_Contact");
            String Work_Edit_TableMax = intent1.getExtras().getString("Work_Edit_TableMax");
            String Work_Edit_Pd = intent1.getExtras().getString("Work_Edit_Pd");
            String Work_Edit_Pw = intent1.getExtras().getString("Work_Edit_Pw");
            String Work_Edit_Pm = intent1.getExtras().getString("Work_Edit_Pm");

            work_Edit_image = (ArrayList<Uri>) intent1.getSerializableExtra("work_Edit_image");

            // 다음 액티비티로 전달.
            Work_Edit_Introduce = intent1.getExtras().getString("Work_Edit_Introduce");
            Work_Edit_WorkDBIndex = intent1.getExtras().getString("Work_Edit_WorkDBIndex");
            Work_Edit_getId = intent1.getExtras().getString("Work_Edit_getId");

            Log.e(TAG, "Work_Edit Work_Edit_Address1: " + Work_Edit_Address1);
            Log.e(TAG, "Work_Edit Work_Edit_Address2: " + Work_Edit_Address2);
            Log.e(TAG, "Work_Edit Work_Edit_Name: " + Work_Edit_Name);
            Log.e(TAG, "Work_Edit Work_Edit_Contact: " + Work_Edit_Contact);
            Log.e(TAG, "Work_Edit Work_Edit_TableMax: " + Work_Edit_TableMax);
            Log.e(TAG, "Work_Edit Work_Edit_Pd: " + Work_Edit_Pd);
            Log.e(TAG, "Work_Edit Work_Edit_Pw: " + Work_Edit_Pw);
            Log.e(TAG, "Work_Edit Work_Edit_Pm: " + Work_Edit_Pm);
            Log.e(TAG, "Work_Edit Work_Edit_Introduce: " + Work_Edit_Introduce);
            Log.e(TAG, "Work_Edit Work_Edit_WorkDBIndex: " + Work_Edit_WorkDBIndex);
            Log.e(TAG, "Work_Edit Work_Edit_getId: " + Work_Edit_getId);
            Log.e(TAG, "Work_Edit work_Edit_image: " + work_Edit_image);

            adress1.setText(Work_Edit_Address1);
            adress2.setText(Work_Edit_Address2);
            work_space_name.setText(Work_Edit_Name);
            work_space_contact.setText(Work_Edit_Contact);
            work_space_table.setText(Work_Edit_TableMax);
            work_space_price_d.setText(Work_Edit_Pd);
            work_space_price_w.setText(Work_Edit_Pw);
            work_space_price_m.setText(Work_Edit_Pm);

            // 다음 수정 페이지로
            work_space_edit_next_page.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Work_Edit();
                }
            });
        }


        // 다음 주소입력 웹뷰로 이동
        adress1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Activity_DaumWebView 액티비티에서 입력한 주소값을이 액티비티로 가져오세요.
                // 가져온 주소값은 아래 메소드 onActivityResult가 받을겁니다.
                Intent intent = new Intent(Activity_hostAddWorkSpace.this, Activity_DaumWebView.class);
                startActivityForResult(intent, 3000);
            }
        });

        // 입력값 가지고 다음 양식으로 이동하기
        addSpace_add_confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 입력값 받기
                // 입력값 받아서 다음 액티비티로 값을 넘긴다.
                String IntentAddress1 = adress1.getText().toString(); // 주소검색 버튼, 주소1
                String IntentAddress2 = adress2.getText().toString(); // 주소 2
                String IntentWorkSpaceName = work_space_name.getText().toString(); // 사무실 이름
                String IntentWorkSpaceContact = work_space_contact.getText().toString(); // 연락처
                String IntentWorkSpaceTable = work_space_table.getText().toString(); // 테이블 수
                String IntentWorkSpacePd = work_space_price_d.getText().toString(); // 가격_하루
                String IntentWorkSpacePw = work_space_price_w.getText().toString(); // 가격_일 주일
                String IntentWorkSpacePm = work_space_price_m.getText().toString(); // 가격_한 달

                Log.e(TAG, "IntentAddress1: " + IntentAddress1);
                Log.e(TAG, "IntentAddress2: " + IntentAddress2);
                Log.e(TAG, "IntentWorkSpaceName: " + IntentWorkSpaceName);
                Log.e(TAG, "IntentWorkSpaceContact: " + IntentWorkSpaceContact);
                Log.e(TAG, "IntentWorkSpaceTable: " + IntentWorkSpaceTable);
                Log.e(TAG, "IntentWorkSpacePd: " + IntentWorkSpacePd);
                Log.e(TAG, "IntentWorkSpacePw: " + IntentWorkSpacePw);
                Log.e(TAG, "IntentWorkSpacePm: " + IntentWorkSpacePm);

//                // 빈 칸 있으면 경고하기
//                if (TextUtils.isEmpty(IntentAddress1))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    adress1.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentAddress2))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    adress2.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpaceName))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_name.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpaceContact))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_contact.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpaceTable))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_table.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpacePd))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_price_d.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpacePw))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_price_w.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (TextUtils.isEmpty(IntentWorkSpacePm))
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    addSpace_add_confirm.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    work_space_price_m.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }

                // 빈 칸 모두 채우면 넘어가기
//                if
//                    (
//                        !TextUtils.isEmpty(IntentAddress1) &&
//                        !TextUtils.isEmpty(IntentAddress2) &&
//                        !TextUtils.isEmpty(IntentWorkSpaceName) &&
//                        !TextUtils.isEmpty(IntentWorkSpaceContact) &&
//                        !TextUtils.isEmpty(IntentWorkSpaceTable) &&
//                        !TextUtils.isEmpty(IntentWorkSpacePd) &&
//                        !TextUtils.isEmpty(IntentWorkSpacePw) &&
//                        !TextUtils.isEmpty(IntentWorkSpacePm)
//                    )
//                {
                Intent intent = new Intent(Activity_hostAddWorkSpace.this, Activity_hostAddWorkSpace_2.class);
                intent.putExtra("IntentAddress1", IntentAddress1);
                intent.putExtra("IntentAddress2", IntentAddress2);
                intent.putExtra("IntentWorkSpaceName", IntentWorkSpaceName);
                intent.putExtra("IntentWorkSpaceContact", IntentWorkSpaceContact);
                intent.putExtra("IntentWorkSpaceTable", IntentWorkSpaceTable);
                intent.putExtra("IntentWorkSpacePd", IntentWorkSpacePd);
                intent.putExtra("IntentWorkSpacePw", IntentWorkSpacePw);
                intent.putExtra("IntentWorkSpacePm", IntentWorkSpacePm);
                startActivity(intent);
//                }
            }
        });
    }

    // 수정요청 감지되면 값 받기
    private void Work_Edit()
    {
        // 다음 액티비티로 보낼 준비
        String IntentAddress1 = adress1.getText().toString(); // 주소검색 버튼, 주소1
        String IntentAddress2 = adress2.getText().toString(); // 주소 2
        String IntentWorkSpaceName = work_space_name.getText().toString(); // 사무실 이름
        String IntentWorkSpaceContact = work_space_contact.getText().toString(); // 연락처
        String IntentWorkSpaceTable = work_space_table.getText().toString(); // 테이블 수
        String IntentWorkSpacePd = work_space_price_d.getText().toString(); // 가격_하루
        String IntentWorkSpacePw = work_space_price_w.getText().toString(); // 가격_일 주일
        String IntentWorkSpacePm = work_space_price_m.getText().toString(); // 가격_한 달


        Intent intent1 = new Intent(this, Activity_hostAddWorkSpace_2.class);

//        addSpace_add_confirm.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {

        // 수정요청 메소드
        String request = "아아";
        intent1.putExtra("Work_Edit_Set", request);
        Log.e(TAG, "onClick: Work_Edit_Set 아아: " + request);
        intent1.putExtra("IntentAddress1", IntentAddress1);
        intent1.putExtra("IntentAddress2", IntentAddress2);
        intent1.putExtra("IntentWorkSpaceName", IntentWorkSpaceName);
        intent1.putExtra("IntentWorkSpaceContact", IntentWorkSpaceContact);
        intent1.putExtra("IntentWorkSpaceTable", IntentWorkSpaceTable);
        intent1.putExtra("IntentWorkSpacePd", IntentWorkSpacePd);
        intent1.putExtra("IntentWorkSpacePw", IntentWorkSpacePw);
        intent1.putExtra("IntentWorkSpacePm", IntentWorkSpacePm);
        intent1.putExtra("Work_Edit_Introduce", Work_Edit_Introduce);
        intent1.putExtra("Work_Edit_WorkDBIndex", Work_Edit_WorkDBIndex);
        intent1.putExtra("Work_Edit_getId", Work_Edit_getId);
        intent1.putExtra("work_Edit_image", work_Edit_image);

        Log.e(TAG, "Work_Edit: Work_Edit_Introduce: " + Work_Edit_Introduce);
        Log.e(TAG, "Work_Edit: Work_Edit_WorkDBIndex: " + Work_Edit_WorkDBIndex);
        Log.e(TAG, "Work_Edit: Work_Edit_getId: " + Work_Edit_getId);
        startActivity(intent1);
//            }
//        });
    }

    // 다음 웹뷰에서 처리된 결과를 받는 메소드
    // 처리된 결과 코드 (resultCode) 가 RESULT_OK 이면 requestCode 를 판별해 결과 처리를 진행한다.
    // 다음웹뷰 에서 처리 결과가 담겨온 데이터를 TextView 에 보여준다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case 3000:
                    // Activity_DaumWebView 액티비티에서 입력한 주소값을 아래 텍스트뷰에 표시합니다.
                    adress1.setText(data.getStringExtra("address"));
                    break;
            }
        }
    }

    // 뒤로가기 버튼 누르면 홈 액티비티로 이동한다.
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            { //toolbar의 back키 눌렀을 때 동작
                Intent intent = new Intent(this, Activity_Home.class);
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
