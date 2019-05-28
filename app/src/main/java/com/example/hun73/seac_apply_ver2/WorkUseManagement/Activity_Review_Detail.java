package com.example.hun73.seac_apply_ver2.WorkUseManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Activity_WorkSpace_Detail_Info;
import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Review_Detail extends AppCompatActivity
{

    private static final String TAG = "Activity_Review_Detail: ";
    private String review_index;

    private List<Review_Detail_Itam> Review_info;

    private ImageView review_image; // 사무실 이미지
    private TextView
            review_detail_upload_date,  // 작성일
            review_detail_place_name,   // 사무실 이름
            review_detail_start_day,    // 이용 시작일
            review_detail_end_day,      // 이용 종료일
            review_detail_total_day,    // 총 이용일
            review_detail_pay,          // 총 이용금액
            review_detail_user_name,    // 유저 이름
            review_detail_write         // 리뷰 내용
                    ;

    private CircleImageView review_detail_user_image; // 유저 사진

    Toolbar toolbar;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__review__detail);

        // 리스트 초기화 해주기
        Review_info = new ArrayList<>();

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.review_detail_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // 리뷰 정보 불러오기
        getReviewDetail();

    }

    // 리뷰 정보 불러오기
    @SuppressLint("LongLogTag")
    private void getReviewDetail()
    {
        Intent intent = getIntent();
        review_index = intent.getExtras().getString("review_index");
        Log.e(TAG, "onCreate: review_index: " + review_index);

        Log.e("getWrokList: ", "이용현황: 현재 이용중인 사무실 로드");

        review_image = findViewById(R.id.review_image);
        review_detail_upload_date = findViewById(R.id.review_detail_upload_date);
        review_detail_place_name = findViewById(R.id.review_detail_place_name);
        review_detail_start_day = findViewById(R.id.review_detail_start_day);
        review_detail_end_day = findViewById(R.id.review_detail_end_day);
        review_detail_total_day = findViewById(R.id.review_detail_total_day);
        review_detail_pay = findViewById(R.id.review_detail_pay);
        review_detail_user_name = findViewById(R.id.review_detail_user_name);
        review_detail_write = findViewById(R.id.review_detail_write);
        review_detail_user_image = findViewById(R.id.review_detail_user_image);

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface UseNowListRequest = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Review_Detail_Itam>> Use_Review_Call = UseNowListRequest.ReviewDetail(review_index);

        Use_Review_Call.enqueue(new Callback<List<Review_Detail_Itam>>()
        {
            @Override
            public void onResponse(Call<List<Review_Detail_Itam>> call, Response<List<Review_Detail_Itam>> response)
            {
                String
                        WorkImage,           // 사무실 이미지
                        ReviewUploadDate,   // 리뷰 작성일
                        WorkPlaceName,      // 장소 이름
                        UseStartDay,        // 이용 시작일
                        UseEndDay,          // 이용 종료일
                        UseTotalDay,        // 총 이용일

                        UserImage,          // 유저 프로필 이미지
                        UserName,           // 유저 이름
                        ReviewContent,      // 리뷰 내용
                        IndexWorkSpace,     // 사무실 인덱스
                        IndexUser;          // 유저 인덱스

                int UsePrice;               // 이용금액


                Review_info = response.body();

                // 사무실 이미지
                WorkImage = Review_info.get(0).getReview_wsImage();

                // 후기 작성일
                ReviewUploadDate = Review_info.get(0).getReview_upload_date();

                // 사무실 이름
                WorkPlaceName = Review_info.get(0).getReview_wsPlaceName();

                // 입장일
                UseStartDay = Review_info.get(0).getReview_StartDay();

                // 종료일
                UseEndDay = Review_info.get(0).getReview_ReserEndDay();

                // 총 이용일 수
                UseTotalDay = Review_info.get(0).getReview_UseDayTotal();

                // 이용금액 & 지불한 금액
                UsePrice = Review_info.get(0).getReview_ReserPay();

                // 유저 프로필 이미지
                UserImage = Review_info.get(0).getReview_photo();

                // 유저 이름
                UserName = Review_info.get(0).getReview_name();

                // 리뷰 내용
                ReviewContent = Review_info.get(0).getReview_content();

                // 사무실 인덱스
                IndexWorkSpace = Review_info.get(0).getReview_ReserWorkNo();

                // 유저 인덱스
                IndexUser = Review_info.get(0).getReview_ReserUserID();

                Log.e(TAG, "onResponse: review_index: " + review_index);
                Log.e(TAG, "onResponse: WorkImage: " + WorkImage);
                Log.e(TAG, "onResponse: ReviewUploadDate: " + ReviewUploadDate);
                Log.e(TAG, "onResponse: WorkPlaceName: " + WorkPlaceName);
                Log.e(TAG, "onResponse: UseStartDay: " + UseStartDay);
                Log.e(TAG, "onResponse: UseEndDay: " + UseEndDay);
                Log.e(TAG, "onResponse: UseTotalDay: " + UseTotalDay);
                Log.e(TAG, "onResponse: UserImage: " + UserImage);
                Log.e(TAG, "onResponse: UserName: " + UserName);
                Log.e(TAG, "onResponse: IndexWorkSpace: " + IndexWorkSpace);
                Log.e(TAG, "onResponse: IndexUser: " + IndexUser);
                Log.e(TAG, "onResponse: UsePrice: " + UsePrice);
                Log.e(TAG, "onResponse: ReviewContent: " + ReviewContent);

                // 사무실 사진
                Picasso.get().load(WorkImage).
                        memoryPolicy(MemoryPolicy.NO_CACHE).
                        placeholder(R.drawable.logo_2).
                        networkPolicy(NetworkPolicy.NO_CACHE).
                        into(review_image);

                // 리뷰 작성일
                review_detail_upload_date.setText("작성일 - " + ReviewUploadDate);

                // 사무실 이름
                review_detail_place_name.setText(WorkPlaceName);

                // 이용 시작, 종료, 총 이용일
                review_detail_start_day.setText(UseStartDay);
                review_detail_end_day.setText(UseEndDay);
                review_detail_total_day.setText(UseTotalDay + "일");


                long value = Long.parseLong(String.valueOf(UsePrice));
                DecimalFormat format = new DecimalFormat("###,###");//콤마
                format.format(value);
                String result_int = format.format(value);

                // 총 이용금액
                review_detail_pay.setText(result_int + "원");

                // 유저이름
                review_detail_user_name.setText(UserName);
                toolbar.setTitle(UserName + "님의 후기");

                Picasso.get().load(UserImage).
                        memoryPolicy(MemoryPolicy.NO_CACHE).
                        placeholder(R.drawable.logo_2).
                        networkPolicy(NetworkPolicy.NO_CACHE).
                        into(review_detail_user_image);

                review_detail_write.setText(ReviewContent);

                // 사무실 바로가기
                LinearLayout space_info = findViewById(R.id.space_info);
                space_info.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Activity_Review_Detail.this, Activity_WorkSpace_Detail_Info.class);

                        intent.putExtra("Work_No", IndexWorkSpace);

                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Review_Detail_Itam>> call, Throwable t)
            {
                Toast.makeText(Activity_Review_Detail.this, "문제발생, 로그확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: Throwable t: " + t);
            }
        });
    }

//    @Override
//    public void onBackPressed()
//    {
//        finish();
//        super.onBackPressed();
//    }


//    @Override
//    public void onBackPressed()
//    {
//        finish();
//        super.onBackPressed();
//    }

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
