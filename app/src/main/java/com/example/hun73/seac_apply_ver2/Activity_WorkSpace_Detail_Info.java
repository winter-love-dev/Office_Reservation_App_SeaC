package com.example.hun73.seac_apply_ver2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.chat.Activity_ChatRoom;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_WorkSpace_Detail_Info extends AppCompatActivity
{
    private static final String TAG = "Activity_WorkSpace_Detail_Info";

    private static String WORK_READ = "http://115.68.231.84/read_workInfo_detail.php"; // 사무실 정보 불러오기
    private static String WORK_DELET = "http://115.68.231.84/workSpace_Delete.php"; // 사무실 정보 불러오기

    // view 연결
    private TextView
            work_info_name,
            work_info_price_d,
            work_info_price_w,
            work_info_price_m,
            work_info_host_profile_name,
            work_info_host_profile_call,
            work_info_table,
            work_info_address_1,
            work_info_address_2,
            work_info_introduce,
            work_info_table_max,
            DetailViewPage,
            work_info_reg_button,
            review_guid,
            review_list_button;

    private LinearLayout work_info_host_question;


    // 수정 액티비티로 보낼 이미지 리스ㅌ,
    ArrayList<Uri> imageList;

    // 호스트 이미지
    private CircleImageView work_info_host_profile_image;

    // 뷰페이저
    private ViewPager work_info_image_pager;
    // 뷰페이저 어댑터 클래스는 해당 파일에 포함되어 있습니다.
    private ViewPagerAdapter pagerAdapter;
    // 서버 이미지 경로를 담을 배열
    public String[] ImageArray;

    private LinearLayout card_1, card_2;

    // 서버에서 전송한 값을 담는다.
    private String
            WorkDBIndex, // 홈 화면에서 넘어온 값을 받는다
            WorkName,// 사무실 이름
            WorkImage,
            WorkPriceD, // 가격 1일
            WrokPriceW, // 가격 7일
            WrokPriceM, // 가격 28일
            WorkHostProfileImageUrl, // 호스트 프로필 이미지 경로
            work_hostIndex, // 호스트의 테이블 인덱스
            WorkHostProfileName, // 호스트 이름
            WorkHostProfileCall, // 호스트 연락처
            WorkTable, // 사용 가능한 테이블
            WorkTableCurrent,
            WorkAddress1, // 주소 1
            WorkAddress2, // 주소 2
            WorkIntroduce, // 사무실 소개
            getId, // 세션에서 불러온 유저의 '인덱스' 담기
            work_DB_Index, // 사무실 db 인덱스
            ResertumbImage, // 예약 페이지에 띄울 썸네일
            reviewGuid // 리뷰 개수 안내
                    ;

    private ImageView work_info_question_icon;

    private Menu action;

    // 예약 완료 후 해당 액티비티를 종료하기 위해 이 액티비티를 static으로 선언한다.
    public static Activity detailInfoActivity;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__work_space__detail__info);

        // 예약 완료 후 해당 액티비티를 종료하기 위해 이 액티비티를 static으로 선언한다.
        detailInfoActivity = Activity_WorkSpace_Detail_Info.this;

        work_info_reg_button = findViewById(R.id.work_info_reg_button); // 예약버튼

        DetailViewPage = findViewById(R.id.DetailViewPage);

        // 세션 선언, 세션 생성
        // 회원정보 변경 또는 로그아웃
        SessionManager sessionManager;
        sessionManager = new SessionManager(this);
        // 로그인 체크 메소드.
        // isLogin() 메소드의 값이 false면 (true가 아니면) 로그인 액티비티로 이동한다.
        sessionManager.checkLogin();

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        card_1 = findViewById(R.id.de_card_1);
        card_2 = findViewById(R.id.de_card_2);

        // 홈화면 리사이클러뷰에서 보낸 사무실 인덱스 받기
        Intent intent = getIntent();
        WorkDBIndex = intent.getExtras().getString("Work_No");
        ResertumbImage = intent.getExtras().getString("ReserThumbImage");

        // 인덱스 확인하기
        Log.e(TAG, "onCreate: WorkDBIndex: " + WorkDBIndex);

        getWorkSpaceInfo(); // 사무실 정보 불러오기 메소드
    }

    // 사무실 정보 불러오기
    private void getWorkSpaceInfo()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WORK_READ,
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG, "onResponse: response: " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if (success.equals("1"))
                            {
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    Log.e(TAG, "onResponse: jsonArray.length(): " + jsonArray.length());
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Log.e(TAG, "onResponse: JSONObject: " + object);

                                    WorkImage = object.getString("work_Image");
                                    WorkName = object.getString("work_name");
                                    WorkPriceD = object.getString("work_price_d");
                                    WrokPriceW = object.getString("work_price_w");
                                    WrokPriceM = object.getString("work_price_m");
                                    WorkHostProfileImageUrl = object.getString("work_hostPhoto");
                                    work_hostIndex = object.getString("work_hostIndex");
                                    WorkHostProfileName = object.getString("work_hostId");
                                    WorkHostProfileCall = object.getString("work_contact");
                                    WorkTable = object.getString("work_table_max");
                                    WorkTableCurrent = object.getString("work_table_current");
                                    WorkAddress1 = object.getString("work_address_1");
                                    WorkAddress2 = object.getString("work_address_2");
                                    WorkIntroduce = object.getString("work_introduce");
                                    work_DB_Index = object.getString("work_DB_Index");
                                    reviewGuid = object.getString("reviewCount");

                                    Log.e(TAG, "onResponse: WorkImage: " + WorkImage);
                                    Log.e(TAG, "onResponse: WorkName: " + WorkName);
                                    Log.e(TAG, "onResponse: WorkPriceD: " + WorkPriceD);
                                    Log.e(TAG, "onResponse: WrokPriceW: " + WrokPriceW);
                                    Log.e(TAG, "onResponse: WrokPriceM: " + WrokPriceM);
                                    Log.e(TAG, "onResponse: WorkHostProfileImageUrl: " + WorkHostProfileImageUrl);
                                    Log.e(TAG, "onResponse: work_hostIndex: " + work_hostIndex);
                                    Log.e(TAG, "onResponse: WorkHostProfileName: " + WorkHostProfileName);
                                    Log.e(TAG, "onResponse: WorkHostProfileCall: " + WorkHostProfileCall);
                                    Log.e(TAG, "onResponse: WorkTable: " + WorkTable);
                                    Log.e(TAG, "onResponse: WorkTableCurrent: " + WorkTableCurrent);
                                    Log.e(TAG, "onResponse: WorkAddress1: " + WorkAddress1);
                                    Log.e(TAG, "onResponse: WorkAddress2: " + WorkAddress2);
                                    Log.e(TAG, "                                        ");
                                    Log.e(TAG, "onResponse: WorkIntroduce: " + WorkIntroduce);
                                    Log.e(TAG, "onResponse: work_DB_Index: " + work_DB_Index);
                                    Log.e(TAG, "                                        ");
                                    Log.e(TAG, "onResponse: reviewCount: " + reviewGuid);

                                    // 슬라이드 루트 네비게이션 바 설정
                                    Toolbar toolbar = (Toolbar) findViewById(R.id.work_space_detail_toolbar); // 툴바 연결하기, 메뉴 서랍!!
                                    setSupportActionBar(toolbar); // 툴바 띄우기

                                    toolbar.setTitleTextColor(Color.WHITE); //햄버거 버튼 추가하기, 햄버거 버튼 색상 흰색으로 지정하기
                                    setSupportActionBar(toolbar);

                                    ActionBar actionBar = getSupportActionBar(); //actionBar 객체를 가져올 수 있다.

                                    // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
                                    actionBar.setDisplayHomeAsUpEnabled(true);
                                    actionBar.setHomeButtonEnabled(true);

                                    // 이미지 경로가 아래처럼 하나의 문자열로 왔음
                                    // workSpace_image/15532610640.jpg│workSpace_image/15532610641.jpg│workSpace_image/15532610642.jpg│workSpace_image/15532610643.jpg│workSpace_image/15532610644.jpg
                                    // 경로에서 │를 제거하고 문자열을 하나씩 배열에 넣음
                                    ImageArray = WorkImage.split("│");

                                    // 경로들이 배열에 잘 담겼는지 반복문으로 꺼내보기
                                    imageList = new ArrayList<>();
                                    for (int i1 = 0; i1 < ImageArray.length; i1++)
                                    {
                                        String d = ImageArray[i1];
                                        imageList.add(Uri.parse(("http://115.68.231.84/" + d)));
                                        Log.e(TAG, "onResponse: ImageArray:" + d);
                                    }
                                    Log.e(TAG, "onResponse: imageList" + imageList.toString());

                                    // 뷰페이저 세팅
                                    work_info_image_pager = (ViewPager) findViewById(R.id.work_info_image); //뷰페이저
                                    pagerAdapter = new ViewPagerAdapter(getApplicationContext());
                                    work_info_image_pager.setAdapter(pagerAdapter);

                                    int pageTotal = ImageArray.length;
                                    Log.e(TAG, "instantiateItem: pageTotal: 총 페이지: " + pageTotal);
                                    work_info_image_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                                    {
                                        @Override
                                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                                        {
                                            // 이 부분을 선언하지 않으면 액티비티에 접근할 때
                                            // 현재 페이지 수가 뜨지 않음.
                                            int pageCul = position + 1;
                                            DetailViewPage.setText(pageCul + " / " + pageTotal);
                                        }

                                        @Override
                                        public void onPageSelected(int position)
                                        {
                                            // 현재 몇 페이지인지 감지하는 부분
                                            int pageCul = position + 1;
                                            DetailViewPage.setText(pageCul + " / " + pageTotal);
//                                            Log.e(TAG, "instantiateItem: pageCul: 현재 페이지: onPageSelected: " + pageCul +" / " +pageTotal);
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(int state)
                                        {

                                        }
                                    });

                                    work_info_name = findViewById(R.id.work_info_name); // 사무실 이름
                                    work_info_price_d = findViewById(R.id.work_info_price_d); // 가격
                                    work_info_price_w = findViewById(R.id.work_info_price_w); // 가격
                                    work_info_price_m = findViewById(R.id.work_info_price_m); // 가격
                                    work_info_host_profile_image = findViewById(R.id.work_info_host_profile_image); // 호스트 프로필 이미지 (서클 이미지뷰)
                                    work_info_host_profile_name = findViewById(R.id.work_info_host_profile_name); // 호스트 이름
                                    work_info_host_profile_call = findViewById(R.id.work_info_host_profile_call); // 호스트 연락처
                                    work_info_table_max = findViewById(R.id.work_info_table_max); // 총 테이블 수
                                    work_info_table = findViewById(R.id.work_info_table); // 남은 테이블 수
                                    work_info_introduce = findViewById(R.id.work_info_introduce); // 소개
                                    work_info_address_1 = findViewById(R.id.work_info_address_1); // 주소
                                    work_info_address_2 = findViewById(R.id.work_info_address_2); // 주소
                                    review_guid = findViewById(R.id.review_guid); // 리뷰 갯수 안내
                                    review_list_button = findViewById(R.id.review_list_button); // 리뷰 버튼
                                    work_info_host_question = findViewById(R.id.work_info_host_question); // 문의버튼
                                    work_info_question_icon = findViewById(R.id.work_info_question_icon); // 문의 아이콘

                                    WorkQuestionLogic();


                                    // 사무실 이름
                                    work_info_name.setText(WorkName);

                                    // 가격, 하루
                                    long D = Long.parseLong(WorkPriceD);
                                    DecimalFormat formatD = new DecimalFormat("###,###");//콤마
                                    formatD.format(D);
                                    String CWorkPriceD = formatD.format(D);
                                    work_info_price_d.setText("1일: " + CWorkPriceD + " 원");

                                    // 가격, 일주일
                                    long W = Long.parseLong(WorkPriceD);
                                    DecimalFormat formatW = new DecimalFormat("###,###");//콤마
                                    formatW.format(W);
                                    String CWorkPriceW = formatW.format(W);
                                    work_info_price_w.setText("7일: " + CWorkPriceW + " 원");

                                    // 가격, 한 달
                                    long M = Long.parseLong(WorkPriceD);
                                    DecimalFormat formatM = new DecimalFormat("###,###");//콤마
                                    formatM.format(M);
                                    String CWorkPriceM = formatM.format(M);
                                    work_info_price_m.setText("28일: " + CWorkPriceM + " 원");

                                    // 호스트 프로필 = 사진, 이름, 연락처
                                    Picasso.get().
                                            load(WorkHostProfileImageUrl).
                                            memoryPolicy(MemoryPolicy.NO_CACHE).
                                            placeholder(R.drawable.logo_2).
                                            networkPolicy(NetworkPolicy.NO_CACHE).
                                            into(work_info_host_profile_image);
                                    work_info_host_profile_name.setText(WorkHostProfileName);
                                    work_info_host_profile_call.setText(makePhoneNumber(WorkHostProfileCall));

                                    // 총 테이블 수
//                                    work_info_table_max.setText("총 테이블 수: " + WorkTable);

                                    // 남은 테이블 수 = 최대 테이블 - 사용중인 테이블
                                    int Tn = Integer.parseInt(WorkTable);
                                    int TnC = Integer.parseInt(WorkTableCurrent);
                                    int Table_Total = Tn - TnC;
                                    work_info_table.setText(String.valueOf(WorkTable + " 자리 중 / " + Table_Total + " 자리 남음"));

                                    // 주소
                                    work_info_address_1.setText(WorkAddress1);
                                    work_info_address_2.setText(WorkAddress2);

                                    // 소개
                                    work_info_introduce.setText(WorkIntroduce);

                                    // 리뷰 갯수 안내
                                    if (Integer.parseInt(reviewGuid) == 0)
                                    {
                                        review_guid.setText("아직 작성된 후기가 없습니다");
                                        review_list_button.setVisibility(View.GONE);
                                    } else
                                    {
                                        review_guid.setText(" '" + reviewGuid + "' 개의 후기가 있습니다\n\n후기 목록에서 확인하세요");
                                        review_list_button.setVisibility(View.VISIBLE);

                                        // 리뷰 목록으로 이동하기
                                        review_list_button.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Intent intent = new Intent(Activity_WorkSpace_Detail_Info.this, Activity_Review_List.class);

                                                intent.putExtra("ResertumbImage", ResertumbImage);
                                                intent.putExtra("WorkName", WorkName);
                                                intent.putExtra("WorkDBIndex", WorkDBIndex);
                                                intent.putExtra("reviewCount", reviewGuid);
                                                intent.putExtra("getId", getId);

                                                startActivity(intent);
                                            }
                                        });
                                    }

                                    // 예약 페이지로 이동하기
                                    work_info_reg_button.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            Intent intent = new Intent(Activity_WorkSpace_Detail_Info.this, Activity_Work_Reservation.class);

                                            intent.putExtra("WorkDBIndex", WorkDBIndex);
                                            intent.putExtra("WorkName", WorkName);
                                            intent.putExtra("ResertumbImage", ResertumbImage);
                                            intent.putExtra("WorkPriceD", WorkPriceD);
                                            intent.putExtra("WrokPriceW", WrokPriceW);
                                            intent.putExtra("WrokPriceM", WrokPriceM);
                                            intent.putExtra("WorkHostProfileName", WorkHostProfileName);
                                            intent.putExtra("WorkHostProfileCall", WorkHostProfileCall);
                                            intent.putExtra("WorkAddress1", WorkAddress1);
                                            intent.putExtra("WorkAddress2", WorkAddress2);

                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                        } catch (JSONException e)
                        {
                            Toast.makeText(Activity_WorkSpace_Detail_Info.this, "JSONException. 로그확인", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e: " + e);
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
                        Toast.makeText(Activity_WorkSpace_Detail_Info.this, "VolleyError. 로그확인", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onErrorResponse: VolleyError error: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // post 요청할 값
                params.put("WorkDBIndex", WorkDBIndex);
                return params;
            }
        };

        // 설정한 주소로 POST 요청하기
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    String RoomNo;

    // 문의 로직. 채팅 or 전화
    @SuppressLint("LongLogTag")
    private void WorkQuestionLogic()
    {

        if (work_hostIndex.equals(getId))
        {
            Log.e(TAG, "나에겐 문의하지 않습니다.");
            Log.e(TAG, "호스트 아이디: " + work_hostIndex);
            Log.e(TAG, "내 아이디: " + getId);
            Toast.makeText(Activity_WorkSpace_Detail_Info.this, "호스트로 접속", Toast.LENGTH_SHORT).show();
        } else
        {
            // 문의 아이콘 활성화
            work_info_question_icon.setVisibility(View.VISIBLE);

            // 문의 다이얼로그 메뉴 활성화
            work_info_host_question.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // 프로필 사진 업로드 전 물어보기
                    AlertDialog.Builder work_question = new AlertDialog.Builder(Activity_WorkSpace_Detail_Info.this);

                    // 다이얼로그 세팅
                    work_question.setTitle("호스트에게 문의합니다");
                    work_question.setMessage("문의 유형을 선택해주세요");
                    work_question
//                .setCancelable(false) // 다이얼로그 실행되면 뒤로가기 비활성화
                            .setPositiveButton("채팅",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @SuppressLint("LongLogTag")
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // TODO: 서버에서 방 목록 조회하기
                                            // 1. 상대방과 대화하던 방이 조회되면 해당 방으로 이동한다
                                            // 2. 상대방와 대화하던 방이 조회되지 않으면 새 방을 생성한다.
                                            //    그리고 유저를 새로 생성된 방으로 입장하게 한다.
                                            // Chat_User_List_Room_Lookup();
                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Chat_User_List_Room_Lookup.php",
                                                    new Response.Listener<String>()
                                                    {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            try
                                                            {
                                                                JSONObject jsonObject = new JSONObject(response);

                                                                Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                                                                // 채팅방 번호
                                                                String room_No = jsonObject.getString("Room_No");

                                                                Log.e(TAG, "onResponse: Room_No" + room_No);

                                                                JSONArray jsonArray = jsonObject.getJSONArray("Room_No");

                                                                JSONObject object = jsonArray.getJSONObject(0);

                                                                // json에서 방 번호 받기
                                                                RoomNo = object.getString("CRm_Room_No");

                                                                Log.e(TAG, "onResponse: Chat_User_List_Room_Lookup() RoomNo: " + RoomNo);

                                                                Log.e(TAG, "onResponse: 문의로 채팅방 입장하기 work_hostIndex: " + work_hostIndex);
                                                                Log.e(TAG, "onResponse: 문의로 채팅방 입장하기 WorkHostProfileName: " + WorkHostProfileName);
                                                                Log.e(TAG, "onResponse: 문의로 채팅방 입장하기 WorkHostProfileImageUrl: " + WorkHostProfileImageUrl);


                                                                // 상대방과 채팅 시작
                                                                Intent intent = new Intent(Activity_WorkSpace_Detail_Info.this, Activity_ChatRoom.class);

//                                                                intent.putExtra("room_no", RoomNo);
//                                                                intent.putExtra("user_id", work_hostIndex);
//                                                                intent.putExtra("user_name", WorkHostProfileName);
//                                                                intent.putExtra("user_photo", WorkHostProfileImageUrl);

                                                                intent.putExtra("room_no", RoomNo);
                                                                intent.putExtra("user_id", work_hostIndex);
                                                                intent.putExtra("user_name", WorkHostProfileName);
                                                                intent.putExtra("user_photo", WorkHostProfileImageUrl);

                                                                startActivity(intent);

                                                                Toast.makeText(Activity_WorkSpace_Detail_Info.this, "방 번호: " + RoomNo + " / 유저 인덱스: " + work_hostIndex + " / " + WorkHostProfileName + "님과 대화합니다.", Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "방 번호: " + RoomNo + " / 유저 인덱스: " + work_hostIndex + " / " + WorkHostProfileName + "님과 대화합니다.");



                                                            } catch (JSONException e)
                                                            {
                                                                e.printStackTrace();
                                                                Toast.makeText(Activity_WorkSpace_Detail_Info.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                                                            }

                                                        }
                                                    },
                                                    new Response.ErrorListener()
                                                    {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error)
                                                        {
                                                            Toast.makeText(Activity_WorkSpace_Detail_Info.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError
                                                {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("getId", getId);
                                                    params.put("userId", work_hostIndex);
//                                                    WorkDBIndex http://115.68.231.84/Question_addList.php
                                                    return params;
                                                }
                                            };

                                            RequestQueue requestQueue = Volley.newRequestQueue(Activity_WorkSpace_Detail_Info.this);
                                            requestQueue.add(stringRequest); // 채팅방 입장 끝



                                            // todo: 문의 기록
                                            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, "http://115.68.231.84/Question_addList.php",
                                                    new Response.Listener<String>()
                                                    {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            Log.e(TAG, "onResponse: response: " + response );

                                                            try
                                                            {
                                                                JSONObject jsonObject = new JSONObject(response);
                                                                String success = jsonObject.getString("success");

                                                                // 문의 기록 저장하거나 업데이트 한 결과를 확인
                                                                Log.e(TAG, "onResponse: success: " + success );

                                                            }

                                                            catch (JSONException e)
                                                            {
                                                                e.printStackTrace();
                                                                Toast.makeText(Activity_WorkSpace_Detail_Info.this, "문의 기록 문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                                                            }
                                                        }
                                                    },
                                                    new Response.ErrorListener()
                                                    {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error)
                                                        {
                                                            Toast.makeText(Activity_WorkSpace_Detail_Info.this, "문의 기록 문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                                                            Log.e(TAG, "onErrorResponse: 문의 기록 VolleyError: " + error.toString() );
                                                        }
                                                    })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError
                                                {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("getId", getId);
                                                    params.put("PlaceNo", WorkDBIndex);
                                                    params.put("HostId", work_hostIndex);

                                                    return params;
                                                }
                                            };

                                            RequestQueue requestQueue2 = Volley.newRequestQueue(Activity_WorkSpace_Detail_Info.this);
                                            requestQueue2.add(stringRequest2);
                                        }
                                    })
                            .setNegativeButton("전화",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // 전화걸기
                                            startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + WorkHostProfileCall)));
                                        }
                                    });

                    final AlertDialog edit_dialog = work_question.create();

                    edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
                    {
                        @Override
                        public void onShow(DialogInterface arg0)
                        {
                            // 전화문의 버튼의 색상 Color.rgb(247, 202, 201) = 분홍색
                            edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                            // 채팅문의
                            edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                        }
                    });
                    edit_dialog.show(); // 다이얼로그 실행
                    // 다이얼로그 끝
                }
            });
        }
    }

    // 휴대폰 번호 형식으로 포맷하기
    public static String makePhoneNumber(String phoneNoStr)
    {
        String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";
        if (!Pattern.matches(regEx, phoneNoStr)) return null;
        return phoneNoStr.replaceAll(regEx, "$1-$2-$3");
    }

    // 옵션메뉴 불러오기
    // 메뉴 버튼 레이아웃을 따로 만들었음.
    // 그 버튼 레이아웃을 해당 액팁티에 세팅할것임.
    @SuppressLint("LongLogTag")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflater = 팽창!! 아, 뭐라고 해석해야될 지 모르겠다.
        MenuInflater menuInflater = getMenuInflater();

        // 만들어둔 메뉴 레이아웃 불러오기
        menuInflater.inflate(R.menu.memu_detail, menu);

        // 전역변수에 선언한 메뉴 변수에 해당 메소드에서 설정한 값들을 담는다.
        action = menu;

        // 호스트 본인의 글이면 수정, 삭제버튼을 활성화 한다.
        Log.e(TAG, "onCreateOptionsMenu: 당신의 인덱스: " + getId);
        Log.e(TAG, "onCreateOptionsMenu: 호스트의 인덱스: " + work_hostIndex);
        if (getId.equals(work_hostIndex))
        {
            Log.e(TAG, "onCreateOptionsMenu: 호스트로 접속했습니다. 편집 메뉴 활성화");
            action.findItem(R.id.work_detail_menu_edit).setVisible(true); // 수정
            action.findItem(R.id.work_detail_menu_delete).setVisible(true); // 삭제
            work_info_reg_button.setVisibility(View.GONE); // 본인의 홍보글에 예약버튼 비활성화
        } else
        {
            Log.e(TAG, "onCreateOptionsMenu: 손님으로 접속했습니다. 편집 메뉴 비활성화");
            action.findItem(R.id.work_detail_menu_edit).setVisible(false); // 수정
            action.findItem(R.id.work_detail_menu_delete).setVisible(false); // 삭제
            work_info_reg_button.setVisibility(View.VISIBLE); // 예약버튼 활성화
        }

        return true;
    }

    // 옵션 메뉴 클릭이벤트 설정하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // 메뉴에 아이템이 감지되면 아래 스위치문 활성화.
        switch (item.getItemId())
        {
            // 수정버튼
            case R.id.work_detail_menu_edit: // 수정버튼 누르면 아래 로직 활성화

                // 프로필 사진 업로드 전 물어보기
                AlertDialog.Builder work_edit = new AlertDialog.Builder(Activity_WorkSpace_Detail_Info.this);

                // 다이얼로그 세팅
                work_edit.setTitle("해당 정보를 수정합니다");
                work_edit
                        .setCancelable(false) // 다이얼로그 실행되면 뒤로가기 비활성화
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener()
                                {
                                    @SuppressLint("LongLogTag")
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 클릭 이벤트
                                        // 선택한 값들을 수정 액티비티로 전송하기
                                        // 수정 액티비티 onCreate에서 getExtra로 Work_Edit_Request를 감지하면
                                        // 그 아래에 전송한 값들을 모두 받는다.
                                        Intent intent = new Intent(
                                                Activity_WorkSpace_Detail_Info.this, Activity_hostAddWorkSpace.class);

                                        String request = "아";

                                        // 수정 요청코드
                                        intent.putExtra("Work_Edit_Request", request);

                                        // 수정할 주소
                                        intent.putExtra("Work_Edit_Address1", WorkAddress1);
                                        intent.putExtra("Work_Edit_Address2", WorkAddress2);

                                        // 사무실 이름
                                        intent.putExtra("Work_Edit_Name", WorkName);

                                        // 사무실 연락처
                                        intent.putExtra("Work_Edit_Contact", WorkHostProfileCall);

                                        // 최대 테이블 수
                                        // 현재 이용중인 인원 이하로 테이블 수를 낮출 수 없게 할 방법 생각해봅시다
                                        intent.putExtra("Work_Edit_TableMax", WorkTable);

                                        // 가격
                                        intent.putExtra("Work_Edit_Pd", WorkPriceD);
                                        intent.putExtra("Work_Edit_Pw", WrokPriceW);
                                        intent.putExtra("Work_Edit_Pm", WrokPriceM);
                                        intent.putExtra("work_Edit_image", imageList);

                                        // 소개 (2페이지)
                                        intent.putExtra("Work_Edit_Introduce", WorkIntroduce);

                                        // 수정할 DB의 인덱스
                                        intent.putExtra("Work_Edit_WorkDBIndex", work_DB_Index);

                                        // 수정하는 회원의 id
                                        intent.putExtra("Work_Edit_getId", work_hostIndex);

                                        Log.e(TAG, "onClick: Work_Edit_Introduce: " + WorkIntroduce);
                                        Log.e(TAG, "onClick: Work_Edit_WorkDBIndex: " + work_DB_Index);
                                        Log.e(TAG, "onClick: Work_Edit_getId: " + work_DB_Index);

                                        // 액티비티로 이동, 전송
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("아니요",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel(); // 다이얼로그 닫기
                                    }
                                });

                final AlertDialog edit_dialog = work_edit.create();

                edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
                {
                    @Override
                    public void onShow(DialogInterface arg0)
                    {
                        // 아니오 버튼의 색상 Color.rgb(247, 202, 201) = 분홍색
                        edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLUE);

                        // 네 버튼의 색상
                        edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    }
                });
                edit_dialog.show(); // 다이얼로그 실행

                return true;

            // 삭제버튼
            case R.id.work_detail_menu_delete: // 삭제버튼 누르면 아래 로직 활성화

                // 프로필 사진 업로드 전 물어보기
                AlertDialog.Builder work_delete = new AlertDialog.Builder(Activity_WorkSpace_Detail_Info.this);

                // 다이얼로그 세팅
                work_delete.setTitle("삭제 후 복구할 수 없습니다");
                work_delete
                        .setMessage("진행하시겠습니다?")
                        .setCancelable(false) // 다이얼로그 실행되면 뒤로가기 비활성화
                        .setPositiveButton("네",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        // 사무실 삭제
                                        WorkSpaceDelete();
                                    }
                                })
                        .setNegativeButton("아니요",
                                new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        dialog.cancel(); // 다이얼로그 닫기
                                    }
                                });

                final AlertDialog dialog = work_delete.create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
                {
                    @Override
                    public void onShow(DialogInterface arg0)
                    {
                        // 아니오 버튼의 색상 Color.rgb(247, 202, 201) = 분홍색
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);

                        // 네 버튼의 색상
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                    }
                });
                dialog.show(); // 다이얼로그 실행

                return true;

            // 홈화면으로 이동하기
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            {
                finish();
                return true;
            }

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    // 서버로 사무실 삭제 요청하기
    private void WorkSpaceDelete()
    {
        card_1.setVisibility(View.GONE);
        card_2.setVisibility(View.VISIBLE);

        action.findItem(R.id.work_detail_menu_edit).setVisible(false); // 수정
        action.findItem(R.id.work_detail_menu_delete).setVisible(false); // 삭제

        // 로띠 Lottie 애니메이션
        LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.delete_WorkSpace_lottie_success); // 로띠 에니메이션 위치
        uploadWorkSpace_lottie.setAnimation("delete_lottie.json");
        uploadWorkSpace_lottie.setRepeatCount(5);
        uploadWorkSpace_lottie.playAnimation();

        String WorkIndex = WorkDBIndex;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WORK_DELET,
                new Response.Listener<String>()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1"))
                            {
                                Toast.makeText(Activity_WorkSpace_Detail_Info.this, "삭제 완료.", Toast.LENGTH_SHORT).show();


                                Thread thread = new Thread()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try // 아래 설정한 시간만큼 멈춘다
                                        {
                                            sleep(5000); // 3000 = 3초, 500 = 0.5초
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        } finally
                                        {
                                            Intent intent = new Intent(Activity_WorkSpace_Detail_Info.this, Activity_Home.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(Activity_WorkSpace_Detail_Info.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse : JSONException = " + e.toString());
                            card_2.setVisibility(View.GONE);
                            card_1.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(Activity_WorkSpace_Detail_Info.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse : VolleyError = " + error.toString());
                        card_2.setVisibility(View.GONE);
                        card_1.setVisibility(View.VISIBLE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("index", WorkIndex);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed()
    {
        // 뒤로가기 누르면 종료하기
        finish();
        super.onBackPressed();
    }

    // 뷰페이저 어댑터
    public class ViewPagerAdapter extends PagerAdapter
    {

        // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
        private Context mContext = null;

        public ViewPagerAdapter()
        {

        }

        // Context를 전달받아 mContext에 저장하는 생성자 추가.
        public ViewPagerAdapter(Context context)
        {
            mContext = context;
        }

        @SuppressLint("LongLogTag")
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View view = null;

            if (mContext != null)
            {
                // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.work_detail_image_pager, container, false);

                ImageView imageView = view.findViewById(R.id.work_image_pager);

                // 뷰페이저 이미지 세팅
                Picasso.get().
                        load("http://115.68.231.84/" + ImageArray[position]).
                        memoryPolicy(MemoryPolicy.NO_CACHE).
                        placeholder(R.drawable.logo_2).
                        networkPolicy(NetworkPolicy.NO_CACHE).
                        into(imageView);
            }

            // 뷰페이저에 추가.
            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            // 뷰페이저에서 삭제.
            container.removeView((View) object);
        }

        @Override
        public int getCount()
        {
            return ImageArray.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
        {
            return (view == (View) object);
        }
    }
}
