package com.example.hun73.seac_apply_ver2.chat;

import android.content.Context;
import android.graphics.Color;
import com.google.android.material.tabs.TabLayout;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.SessionManager;

import java.util.HashMap;

public class Chat_Fragment_Manager extends AppCompatActivity
{
    private static final String TAG = "Chat_Fragment_Manager: ";

    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private ChatPagerAdapter mContentPagerAdapter;

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_fragment_manager);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_frag_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        // 세션생성
        sessionManager = new SessionManager(this);

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);
        Log.e(TAG, "onCreate: getId: " + getId );

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        mTabLayout = (TabLayout) findViewById(R.id.chat_frag_tab);

        mTabLayout.addTab(mTabLayout.newTab().setText("유저목록"));
        mTabLayout.addTab(mTabLayout.newTab().setText("대화목록"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("친구신청"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.chat_frag_pager);
        mContentPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
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

    @Override
    public void onBackPressed()
    {
        finish();

        super.onBackPressed();
    }

    // 맨 위 타이틀바 뒤로가기 버튼 관리
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            { //titleBar의 back키 눌렀을 때 동작
                finish(); // 홈화면으로 이동하기
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    // 뷰페이저 클래스
    public class ChatPagerAdapter extends FragmentStatePagerAdapter
    {
        private int mPageCount;

        public ChatPagerAdapter(FragmentManager fm, int PageCount)
        {
            super(fm);
            this.mPageCount = PageCount;
        }


        @Override
        public Fragment getItem(int position)
        {
            switch (position)
            {
                case 0:
                    Fragment_User_List fragment_user_list = new Fragment_User_List();

//                    // 프래그먼트로 값 전달
                    Bundle userList = new Bundle();
                    userList.putString("getId", getId);
                    fragment_user_list.setArguments(userList);

                    return fragment_user_list;

                case 1:
                    Fragment_Chat_List fragment_chat_list = new Fragment_Chat_List();

                    // 프래그먼트로 값 전달
                    Bundle chatList = new Bundle();
                    chatList.putString("getId", getId);
                    fragment_chat_list.setArguments(chatList);

                    return fragment_chat_list;

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
}
