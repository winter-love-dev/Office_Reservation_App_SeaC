package com.example.hun73.seac_apply_ver2;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_hostRegist // 호스트 등록하기? 호스트 등록 요청하기?
        extends AppCompatActivity
{
    private Button button_host_regist; // 호스트 등록버튼
    private ProgressBar host_request_loading; // 로딩바

    // 세션 선언.
    // 회원정보 불러오기
    SessionManager sessionManager;

    // 호스트 신청 요청주소
//    private static final String URL_HOST_REQUEST = "http://34.73.32.3/host_regist.php"; // gcp
    private static String URL_HOST_REQUEST = "http://115.68.231.84/host_regist.php"; //iwinv

    // 유저의 번호를 담을 변수
    private String getId;
    private String TAG = "호스트 신청 액티비티";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_regist);

        // 호스트 등록하기
        button_host_regist = findViewById(R.id.button_host_register_request);
        host_request_loading = findViewById(R.id.host_reg_loading); // 로딩바

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

        Log.e(TAG, "onCreate: getId = " + getId);

        button_host_regist.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 호스트 요청하기 메소드
                HostRegRequest();
            }
        });
    }

    // 호스트 요청하기
    private void HostRegRequest()
    {
        // '호스트 요청하기'버튼 누르면 이 버튼 비활성화.
        button_host_regist.setVisibility(View.GONE);
        // '호스트 요청하기'버튼 비활성화 후 로딩 프로그레스바 활성화하기
        host_request_loading.setVisibility(View.VISIBLE);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("호스트 승인 대기중");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_HOST_REQUEST,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        progressDialog.dismiss();

                        // 응답 메시지 확인하기
                        Log.e(TAG, "onResponse: response = "+response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            //가입완료 후 마이페이지로 이동하기
                            if (success.equals("HostRequestSuccess"))
                            {
                                Intent intent = new Intent(Activity_hostRegist.this, Activity_MyPage.class);
                                startActivity(intent);
                                finish();
                            }
                        } catch (JSONException e)
                        {
                            // 요청 응답 실패.
                            // 다이얼로그 비활성.
                            progressDialog.dismiss();
                            // '호스트 요청하기'버튼 활성화 후 로딩 프로그레스바 닫기
                            button_host_regist.setVisibility(View.VISIBLE);
                            host_request_loading.setVisibility(View.GONE);
                            e.printStackTrace();
                            Toast.makeText(Activity_hostRegist.this, "가입 실패" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        progressDialog.dismiss();
                        Toast.makeText(Activity_hostRegist.this, "가입 실패" + error.toString(), Toast.LENGTH_SHORT).show();
                        // '호스트 요청하기'버튼 활성화 후 로딩 프로그레스바 닫기
                        button_host_regist.setVisibility(View.VISIBLE);
                        host_request_loading.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, Activity_MyPage.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
