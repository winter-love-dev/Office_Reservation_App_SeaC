package com.example.hun73.seac_apply_ver2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.Import.KakaoWebViewClient;
import com.example.hun73.seac_apply_ver2.WorkUseManagement.WorkUseManagement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Reservation_Web_View extends AppCompatActivity
{
    private static final String TAG = "Activity_Reservation_Web_View: ";
    private static final String SEND_PRICE = "http://115.68.231.84/WorkSpace_addReser.php";

    // 카카오 웹뷰
    private WebView reservation_webview;

    //    private final String APP_SCHEME = "iamportkakao://";
    private final String APP_SCHEME = "http://115.68.231.84/Raservation_kakao.php";

    // 예약 페이지에서 보낸 값 받기
    private String
            UserIndex,
            WorkIndex,
            StartTime,
            EndTime,
            DiffDay,
            Pay,
            Image,
            Aviliability;

    // 가격의 콤마제거
    private String ClearCom_Pay;

    private TextView confirm_message, reser_confirm_button;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__reservation__web__view);

        // 결제 완료메시지 연결
        confirm_message = findViewById(R.id.confirm_message);

        // 예약완료버튼
        // 예약현황 액티비티로 이동하기
        reser_confirm_button = findViewById(R.id.reser_confirm_button);
        reser_confirm_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(Activity_Reservation_Web_View.this, "예약현황 액티비티로 이동합니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(
                        Activity_Reservation_Web_View.this,
                        WorkUseManagement.class);

                startActivity(intent);
                finish();
            }
        });

        // 예약 페이지에서 보낸 값 받기
        Intent intent = getIntent();

        WorkIndex = intent.getExtras().getString("WorkIndex");
        UserIndex = intent.getExtras().getString("UserIndex");
        StartTime = intent.getExtras().getString("StartTime");
        EndTime = intent.getExtras().getString("EndTime");
        DiffDay = intent.getExtras().getString("DiffDay");
        Pay = intent.getExtras().getString("Pay");
        Image = intent.getExtras().getString("image");
        Aviliability = intent.getExtras().getString("Aviliability");

        Log.e(TAG, "onCreate: UserIndex: " + UserIndex);
        Log.e(TAG, "onCreate: WorkIndex: " + WorkIndex);
        Log.e(TAG, "onCreate: StartTime: " + StartTime);
        Log.e(TAG, "onCreate: EndTime: " + EndTime);
        Log.e(TAG, "onCreate: DiffDay: " + DiffDay);
        Log.e(TAG, "onCreate: Pay: " + Pay);
        Log.e(TAG, "onCreate: Image: " + Image);
        Log.e(TAG, "onCreate: Aviliability: " + Aviliability);

        // 콤마제거
        ClearCom_Pay = ClearCom(Pay);
        Log.e(TAG, "onCreate: ClearCom(Pay): " + ClearCom_Pay);

//        // 카카오 웹뷰 실행하기
        reservation_webview = (WebView) findViewById(R.id.reservation_webview);
        reservation_webview.setWebViewClient(new KakaoWebViewClient(Activity_Reservation_Web_View.this)
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Log.e(TAG, "onPageStarted");
                Toast.makeText(Activity_Reservation_Web_View.this, "결제 화면 불러오는 중", Toast.LENGTH_SHORT).show();
                super.onPageStarted(view, url, favicon);
            }

            // 웹뷰가 종료되면 서버로 값 전송
            @Override
            public void onPageFinished(WebView view, String url)
            {
                Log.e(TAG, "onPageFinished");

                Toast.makeText(Activity_Reservation_Web_View.this, "카카오페이 로드 완료", Toast.LENGTH_SHORT).show();

                // 서버로 값 전송
                doReservation(
                        WorkIndex,
                        UserIndex,
                        ClearCom_Pay,
                        StartTime,
                        EndTime,
                        DiffDay,
                        Image,
                        Aviliability);

                super.onPageFinished(view, url);
            }
        });
        WebSettings settings = reservation_webview.getSettings();
        settings.setJavaScriptEnabled(true);

        // 카카오페이 실행하기
        reservation_webview.loadUrl("http://115.68.231.84/Raservation_kakao.php?amount=" + ClearCom_Pay); // 카카오페이로 입력값 보내기
    }

    // 숫자의 콤마 제거
    public String ClearCom(String data)
    {
        return data.replaceAll("\\,", "");
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onStop()
    {
        // 액티비티가 멈출 때 결제완료 메시지, 완료버튼 활성화
        confirm_message.setVisibility(View.VISIBLE);
        reser_confirm_button.setVisibility(View.VISIBLE);


        // 전송 성공하면 아래 두 액티비티를 종료한다.

        // 페이지 상세보기 액티비티 종료하기
        // 예약 양식 작성 액티비티 종료하기

        // 상세정보 액티비티 종료
        Activity_WorkSpace_Detail_Info activity_workSpace_detail_info = (Activity_WorkSpace_Detail_Info) Activity_WorkSpace_Detail_Info.detailInfoActivity;
        activity_workSpace_detail_info.finish();

        // 예약 양식 작성 액티비티 종료
        Activity_Work_Reservation activity_work_reservation = (Activity_Work_Reservation) Activity_Work_Reservation.reservationActivity;
        activity_work_reservation.finish();

        Toast.makeText(activity_work_reservation, "이전 액티비티 종료 완료", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onStop: 이전 액티비티 종료 완료" );

        super.onStop();
    }

    @Override
    public void onBackPressed()
    {
        finish();
        // 예약 완료되면 이전 액티비티 종료하기
        super.onBackPressed();
    }

    // 아임포트로 값 전달
    private void doReservation(String workIndex, // 사무실 인덱스
                               String userIndex, // 유저 인덱스
                               String pay,       // 지불 금액
                               String startTime, // 사용 시작일
                               String endTime,   // 사용 종료일
                               String diffDay,
                               String image,
                               String Aviliability)   // 총 사용 일 수
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_PRICE,
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "onResponse: onResponse(String response): " + response);

                            // 전달받은 json에서 success를 받는다.
                            // {"success":"??"}
                            String success = jsonObject.getString("success");

                            if (success.equals("1"))
                            {
//                                Toast.makeText(Activity_Reservation_Web_View.this, "예약완료", Toast.LENGTH_SHORT).show();

                            } else
                            {
                                Toast.makeText(Activity_Reservation_Web_View.this, "예약: 에러발생. 로그 확인", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_Reservation_Web_View.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_Reservation_Web_View.this, error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: JSONException VolleyError error: " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // 포스트로 보낼 값
                params.put("workIndex", workIndex);
                params.put("userIndex", userIndex);
                params.put("pay", pay);
                params.put("startTime", startTime);
                params.put("endTime", endTime);
                params.put("diffDay", diffDay);
                params.put("image", image);
                params.put("Aviliability", Aviliability);
                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
