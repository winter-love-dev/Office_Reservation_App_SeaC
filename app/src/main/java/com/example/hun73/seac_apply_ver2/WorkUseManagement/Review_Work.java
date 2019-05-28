package com.example.hun73.seac_apply_ver2.WorkUseManagement;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.SessionManager;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Review_Work extends AppCompatActivity
{
    private static final String TAG = "Review_Work";

    private static String URL_REVFIEW_UPLOAD = "http://115.68.231.84/review_upload.php";

    String Place_Name, // 사무실 이름
            image,      // 이미지
            Start_Day,  // 시작일
            End_Day,    // 종료일
            Now_ToTal_Day, // 총 이용일
            Pay,        // 비용
            Index,      // 사무실 인덱스
            reviewWrite, // 입력한 후기 받기
            reserNo     // 예약 테이블 인덱스
                    ;

    ImageView review_image;
    TextView review_work_name, review_start_day, review_end_day, review_total_use_day, review_pay;
    EditText review_write;
    Button work_space_write_done;

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review__work);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.review_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        //
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


        // 정보 넘겨받기
        setInfo();

        // 리뷰 등록하기
        updateReview();
    }

    // 리뷰 등록하기
    // 리뷰 등록 후 해당 사무실 테이블의 상태를 '리뷰작성 완료'로 업데이트 하기
    private void updateReview()
    {
        review_write = findViewById(R.id.review_write);
        work_space_write_done = findViewById(R.id.work_space_write_done);

        work_space_write_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reviewWrite = review_write.getText().toString();
                Log.e(TAG, "updateReview: 입력한 값: " + reviewWrite);

                // 입력한 정보를 php POST로 DB에 전송합니다.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REVFIEW_UPLOAD,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
//                                Log.e(TAG, "onResponse: response = " + response);
                                try
                                {
                                    JSONObject jsonObject = new JSONObject(response);

                                    Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                                    // 가입이 완료되면 서버에서 success를 반환한다.
                                    String success = jsonObject.getString("success");

                                    // 계정이 중복되면 서버에서 3을 반환한다
                                    // 중복결과 알리고 가입 중지
                                    if (success.equals("1"))
                                    {
                                        Toast.makeText(Review_Work.this, "전송완료", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onResponse: success: " + success );

                                        finish(); // 액티비티 종료
                                    }
                                    else
                                    {
                                        Toast.makeText(Review_Work.this, "오류발행", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onResponse: success: " + success );
                                    }
                                }

                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                    Toast.makeText(Review_Work.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                                }

                            }
                        },
                        new Response.ErrorListener()
                        {
                            @Override
                            public void onErrorResponse(VolleyError error)
                            {
                                Toast.makeText(Review_Work.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError
                    {
                        Map<String, String> params = new HashMap<>();
                        params.put("reviewWrite", reviewWrite);
                        params.put("getId", getId);
                        params.put("WorkIndex", Index);
                        params.put("reserNo", reserNo);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(Review_Work.this);
                requestQueue.add(stringRequest);
            }
        });
    }

    // 정보 넘겨받아서 액티비티에 세팅하기
    private void setInfo()
    {
        // 리사이클러뷰에서 값 넘겨받기
        Intent intent = getIntent();

        Place_Name = intent.getExtras().getString("Place_Name");
        image = intent.getExtras().getString("image");
        Start_Day = intent.getExtras().getString("Start_Day");
        End_Day = intent.getExtras().getString("End_Day");
        Now_ToTal_Day = intent.getExtras().getString("ToTal_Day");
        Pay = intent.getExtras().getString("Pay");
        Index = intent.getExtras().getString("Work_No");
        reserNo = intent.getExtras().getString("reserNo");

        Log.e(TAG, "review_Place_Name: " + Place_Name);
        Log.e(TAG, "review_image: " + image);
        Log.e(TAG, "review_Start_Day: " + Start_Day);
        Log.e(TAG, "review_End_Day: " + End_Day);
        Log.e(TAG, "review_ToTal_Day: " + Now_ToTal_Day);
        Log.e(TAG, "review_Pay: " + Pay);
        Log.e(TAG, "review_Index: " + Index);
        Log.e(TAG, "reserNo: " + reserNo);


        review_image = findViewById(R.id.review_image);
        review_work_name = findViewById(R.id.review_work_name);
        review_start_day = findViewById(R.id.review_start_day);
        review_end_day = findViewById(R.id.review_end_day);
        review_total_use_day = findViewById(R.id.review_total_use_day);
        review_pay = findViewById(R.id.review_pay);

        // 서버 URL로 불러온 이미지를 세팅한다.
        Picasso.get().load(image).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(review_image);

        review_work_name.setText(Place_Name);
        review_start_day.setText(Start_Day);
        review_end_day.setText(End_Day);
        review_total_use_day.setText(Now_ToTal_Day + " 일");

        long value = Long.parseLong(Pay);
        DecimalFormat format = new DecimalFormat("###,###");//콤마
        format.format(value);
        String result_int = format.format(value);

        review_pay.setText(/*"￦ " + */result_int + " 원");
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
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
