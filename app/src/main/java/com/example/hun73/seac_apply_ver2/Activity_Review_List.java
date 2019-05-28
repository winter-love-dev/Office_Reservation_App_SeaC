package com.example.hun73.seac_apply_ver2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.RecyclerView.Review_List_Adpater;
import com.example.hun73.seac_apply_ver2.RecyclerView.Review_List_Item;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Review_List extends AppCompatActivity
{
    private static final String TAG = "Activity_Review_List: ";

    private TextView
            review_work_name,   // 사무실 이름
            review_guid        // 리뷰 개수
                    ;

    private ImageView
            review_image        // 리뷰 이미지
            ;

    private String
            WorkName,        // 사무실 이름
            reviewCount,     // 리뷰 개수
            ResertumbImage,  // 리뷰 이미지
            WorkDBIndex,     // 사무실 인덱스
            getId            // 유저 인덱스
                    ;

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    private Review_List_Adpater adapter_Review_List;
    private List<Review_List_Item> Review_List;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__review__list);

        Intent intent = getIntent();

        // 해당 사무실에 남겨진 후기를 조회해서 리사이클러뷰에 세팅하기
        WorkDBIndex = intent.getExtras().getString("WorkDBIndex"); // 사무실 인덱스
        WorkName = intent.getExtras().getString("WorkName"); // 사무실 이름
        ResertumbImage = intent.getExtras().getString("ResertumbImage"); // 사무실 이미지
        reviewCount = intent.getExtras().getString("reviewCount"); // 리뷰 갯수
        getId = intent.getExtras().getString("getId"); // 리뷰 갯수

        Log.e(TAG, "requestReviewList: WorkDBIndex: " + WorkDBIndex);
        Log.e(TAG, "requestReviewList: WorkName: " + WorkName);
        Log.e(TAG, "requestReviewList: ResertumbImage: " + ResertumbImage);
        Log.e(TAG, "requestReviewList: reviewCount: " + reviewCount);
        Log.e(TAG, "requestReviewList: reviewCount: " + getId);

        // 리스트 초기화 해주기
        Review_List = new ArrayList<>();

        mRecyclerView = findViewById(R.id.review_list); // 리사이클러뷰
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리사이클러뷰 새로고침
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_review_list);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // cancle the Visual indication of a refresh
                        swipeRefreshLayout.setRefreshing(false);

                        // 리뷰목록 요청하기
                        requestReviewList();
                    }
                }, 2000); // 2초 딜레이 후 리스트 새로 불러옴
            }
        });


        // getIntent 하기
        getData();

        // 리뷰목록 요청하기
        requestReviewList();
    }

    // 리뷰목록 요청하기
    private void requestReviewList()
    {
        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface UseNowListRequest = retrofit.create(ApiInterface.class);

        // 이 사무실에 남겨진 리뷰들을 요청한다.
        Call<List<Review_List_Item>> Use_Review_Call = UseNowListRequest.ReviewList(WorkDBIndex);

        Use_Review_Call.enqueue(new Callback<List<Review_List_Item>>()
        {
            @Override
            public void onResponse(Call<List<Review_List_Item>> call, Response<List<Review_List_Item>> response)
            {
                Review_List = response.body();

                for (int i = 0; i < Review_List.size(); i++)
                {
                    Log.e(TAG, "onResponse: getWork_Name: " + Review_List.get(i).getReview_name());
                    Log.e(TAG, "onResponse: getThum_Image_Url: " + Review_List.get(i));
                }

                // 리사이클러뷰 세팅
                adapter_Review_List = new Review_List_Adpater(Activity_Review_List.this, Review_List);
                mRecyclerView.setAdapter(adapter_Review_List);
            }

            @Override
            public void onFailure(Call<List<Review_List_Item>> call, Throwable t)
            {
                Toast.makeText(Activity_Review_List.this, "리스트 로드 실패. 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure id t: " + t.getMessage());
            }
        });
    }

    private void getData()
    {

        review_work_name = findViewById(R.id.review_work_name);     // 사무실 이름
        review_guid = findViewById(R.id.review_guid);               // 리뷰 개수
        review_image = findViewById(R.id.review_image);             // 사무실 이미지

        // 슬라이드 루트 네비게이션 바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.review_list_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar(); //actionBar 객체를 가져올 수 있다.

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 사무실 이름
        review_work_name.setText(WorkName);

        // 리뷰 갯수
        review_guid.setText(reviewCount + " 개의 후기가 있습니다");

        // 서버 URL로 불러온 이미지를 세팅한다.
        Picasso.get().load(ResertumbImage).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(review_image);
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

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }
}
