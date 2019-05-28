package com.example.hun73.seac_apply_ver2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.RecyclerView.Home_Adapter;
import com.example.hun73.seac_apply_ver2.RecyclerView.Home_Item;
//import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Now_Adapter;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Now_Item;
import com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_1;
import com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_2;
import com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_3;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import static com.example.hun73.seac_apply_ver2.WorkUseManagement.Fragment_Use_Work_Now.CountArray;

public class Activity_Home extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    // 메뉴서랍
    private TextView nav_mypage, nav_home, slide_type, slide_name, slide_add_work_space, slide_doing_chat, nav_activity_use_management, nav_count;

    private LinearLayout nav_use_guide_layout;

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    // 프로필 사진을 표시할 서클 이미지뷰
    CircleImageView profile_image;

    // 세션에서 프로필 이미지 불러오기
    String getPhoto;

    // 요청 전송할 서버 주소
//    private static String URL_READ = "http://34.73.32.3/read_detail.php"; // gcp 유저 정보 불러오기

//    private static String URL_READ = "http://115.68.231.84/read_detail.php"; //iwinv

    // 리사이클러뷰
    private RecyclerView mRecyclerView;
    private Home_Adapter adapter_home;
    private List<Home_Item> List;

    private List<Use_Work_Now_Item> Use_Now_List;

    long mNow;
    Date mDate;
    String diffDay;
    long diffDays;

    SimpleDateFormat mFormat = new SimpleDateFormat("MM월 dd일 / HH시 mm분");

    // 슬라이드 네비게이션 페이저.... 될까?
    private Context mContext;
    private TabLayout mTabLayout;

    public static ViewPager mViewPager;
    private SlideRootNavigationPagerAdapter mContentPagerAdapter;

    private String TAG = "Activity_Home : ";

    // 로그아웃 할 시 홈화면 종료하기
    public static Activity Activity_Home;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__home);

        Activity_Home = Activity_Home.this;

        List = new ArrayList<>();

        // 세션생성
        sessionManager = new SessionManager(this);

        // 로그인 체크 메소드.
        // isLogin() 메소드의 값이 false면 (true가 아니면)
        // 로그인 액티비티로 이동하기
        sessionManager.checkLogin();

//        // 유저 정보 불러오기
//        getUserDetail();

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        // 슬라이드 루트 네비게이션 바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        toolbar.setTitleTextColor(Color.WHITE); //햄버거 버튼 추가하기, 햄버거 버튼 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // 슬라이드 네비게이션 페이지에 툴바 설정
        new SlidingRootNavBuilder(this) // 슬라이딩 루트바 설정!!! ( drawable layout 의 커스텀 레이아웃? 개념 )
                .withMenuLayout(R.layout.slide_root_pager_bar) // 슬라이딩 루트바로 쓸 레이아웃 지정
                .withToolbarMenuToggle(toolbar) // 햄버거 버튼 연결하기
                .inject(); // 슬라이드 루트 네비게이션 바 끝

        // 리사이클러뷰 새로고침
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_layout);
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

                        getWrokList();
                    }
                }, 2000); // 2초 딜레이 후 리스트 새로 불러옴
            }
        });

        // 리사이클러뷰
        mRecyclerView = findViewById(R.id.recyclerView_Home);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러뷰 시작
        getWrokList();

        // 리스트 초기화 해주기
        Use_Now_List = new ArrayList<>();

        // 남은시간 불러오기
//        getUseNowList();

        // 슬라이드 네이게이션 페이저
        slidePager();

        // todo: 서비스 중지하기
//        Intent stopService = new Intent(this, MyService.class);
//        stopService(stopService);
        // 로그아웃 버튼 클릭할 시 서비스 중지하도록 만듦

        // todo: 서비스 끝
        // todo: ----------------------
    }

    // 슬라이드 네이게이션 페이저
    private void slidePager()
    {
        mContext = getApplicationContext();

        mTabLayout = findViewById(R.id.home_frag_tab);
        mViewPager = findViewById(R.id.home_frag_pager);

//        mTabLayout.getTabAt(0).setIcon(R.drawable.logo_3);
        mTabLayout.addTab(mTabLayout.newTab().setText("내정보"));
        mTabLayout.addTab(mTabLayout.newTab().setText("대화목록"));
        mTabLayout.addTab(mTabLayout.newTab().setText("유저목록"));

        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mContentPagerAdapter = new SlideRootNavigationPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mContentPagerAdapter);


        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });

    }

    // 슬라이드 네비게이션 페이저 어댑터
    public class SlideRootNavigationPagerAdapter extends FragmentStatePagerAdapter
    {
        private int mPageCount;

        public SlideRootNavigationPagerAdapter(FragmentManager fm, int PageCount)
        {
            super(fm);
            this.mPageCount = PageCount;
        }

        @Override
        public Fragment getItem(int i)
        {
            switch (i)
            {
                case 0: // 네이게이션 1: 내정보
                    Fragment_Home_Menu_1 fragment_home_menu_1 = new Fragment_Home_Menu_1();

                        Bundle MyInfo = new Bundle();
                        MyInfo.putString("getId", getId);
                        fragment_home_menu_1.setArguments(MyInfo);

                    return fragment_home_menu_1;

                case 1: // 네비게이션 2: 대화목록
                    Fragment_Home_Menu_3 fragment_home_menu_3 = new Fragment_Home_Menu_3();

                    Bundle chatList = new Bundle();
                    chatList.putString("getId", getId);
                    fragment_home_menu_3.setArguments(chatList);

                    return fragment_home_menu_3;

                case 2: // 네비게이션 3: 유저목록

                    Fragment_Home_Menu_2 fragment_home_menu_2 = new Fragment_Home_Menu_2();

                    Bundle userList = new Bundle();
                    userList.putString("getId", getId);
                    fragment_home_menu_2.setArguments(userList);

                    return fragment_home_menu_2;


                default:
                    return null;
            }
        }

        @Override
        public int getCount()
        {
            return mPageCount;
        }
    }

    // 현재시간 구하기
    private String getTime()
    {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    // 사무실 데이터 리스트 요청하기
    private void getWrokList()
    {
        Log.e("getWrokList", "받아오기 시작");
        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        ApiInterface WorkListRequest = retrofit.create(ApiInterface.class);

        //defining the call
        Call<List<Home_Item>> call = WorkListRequest.WorkList_Home("id");

        //calling the api
        call.enqueue(new Callback<List<Home_Item>>()
        {
            @Override
            public void onResponse(Call<List<Home_Item>> call, retrofit2.Response<List<Home_Item>> response)
            {
                Log.e(TAG, "call onResponse = " + response.body());
                Log.e(TAG, "call onResponse = " + response.body().toString());

                List = response.body();

                for (int i = 0; i < List.size(); i++)
                {
                    Log.e(TAG, "onResponse: getWork_Name: " + List.get(i).getWork_Name());
                    Log.e(TAG, "onResponse: getThum_Image_Url: " + List.get(i).getThum_Image_Url());
                }

                adapter_home = new Home_Adapter(Activity_Home.this, List);
                mRecyclerView.setAdapter(adapter_home);
            }

            @Override
            public void onFailure(Call<List<Home_Item>> call, Throwable t)
            {
                Toast.makeText(Activity_Home.this, "리스트 로드 실패. 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e("getWrokList", "onFailure id t: " + t.getMessage());
            }
        });
    }

    // 문자열을 비트맵으로 변환하기.
    public Bitmap StringToBitMap(String encodedBitMapString)
    {
        try
        {
            byte[] encodeByte = Base64.decode(encodedBitMapString, Base64.DEFAULT);

            Bitmap profileBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return profileBitmap;

        } catch (Exception e)
        {
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onRefresh()
    {

    }

    // url의 이미지를 다운로드 받는다.
    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bitMapImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bitMapImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try
            {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e)
            {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            Log.e(TAG, "onPostExecute: getPhoto download_Bitmap result = " + result);
            // 다운로드 받은 비트맵을 문자열로 변환 후 이미지 세션에 전환한다.

            if (result == null)
            {
                // result가 비어있으면 아무것도 하지않음
            } else
            {
                getStringImage(result);

                bitMapImage.setImageBitmap(result);
            }
        }
    }

    // 비트맵을 문자열로 변환하는 메소드
    public String getStringImage(Bitmap bitmap)
    {
        // 바이트 배열 사용.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        //
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // 세션 생성
        sessionManager.createProfileImageSession(encodedImage);

        // 인코딩 결과를 반환한다.
        return encodedImage;
    }

    // 바로 입장할 때 두날짜의 차이 구하기 (00일)
    public String doDiffOfDate(String getDiffday)
    {
        // 선택한 날짜
        String start = getTime();

        // 종료일
        String end = getDiffday;

//        Log.e(TAG, "doDiffOfDate: end = getDiffday" + end);

        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
            Date beginDate = formatter.parse(start);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);

//            Log.e(TAG, "doDiffOfDate: diffDays: " + diffDays);

            diffDay = String.valueOf(diffDays);

//            Log.e(TAG, "doDiffOfDate: diffDay: " + diffDay);

        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return diffDay;
    }

    @Override
    protected void onStop()
    {
        // 액티비티 종료하기
        super.onStop();
    }
}





// ViewId
// 메뉴서랍
//        profile_image = findViewById(R.id.slide_profile_image); // 프로필 이미지
//        nav_home = findViewById(R.id.nav_activity_home); // 홈화면
//        nav_mypage = findViewById(R.id.nav_activity_my_page); // 마이페이지
//        slide_name = findViewById(R.id.slide_name); // 유저 이름
//        slide_type = findViewById(R.id.slide_user_type); // 유저 유형
//        slide_add_work_space = findViewById(R.id.nav_activity_add_work_space); // 사업장 등록
//        nav_use_guide_layout = findViewById(R.id.nav_use_guide_layout); // 남은 시간 영역
//        nav_count = findViewById(R.id.nav_count); // 남은 시간 출력

// 이용현황 액티비티
//        nav_activity_use_management = findViewById(R.id.nav_activity_use_management);
//        nav_activity_use_management.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(Activity_Home.this, WorkUseManagement.class);
//                startActivity(intent);
//                finish();
//            }
//        });


//        // 채팅 액티비티로 이동

//        slide_doing_chat.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////                Intent intent = new Intent(Activity_Home.this, Activity_Chat_Main.class);
////                startActivity(intent);
//                Intent intent = new Intent(Activity_Home.this, Chat_Fragment_Manager.class);
//                startActivity(intent);
//                finish();
//            }
//        });

// 액티비티 이동하기_사무실등록
//        slide_add_work_space.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(Activity_Home.this, Activity_hostAddWorkSpace.class);
//                // 유저의 정보를 다음 액티비티로 보낸다.
//                // 사무실 등록할 때 사무실의 주인을 테이블에 전송하기 위함
////                String request = "에";
////                intent.putExtra("Work_Edit_Request", request);
//                startActivity(intent);
//                finish();
//            }
//        });

//        // 액티비티 이동하기_마이페이지
//        nav_mypage.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(Activity_Home.this, Activity_MyPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });
