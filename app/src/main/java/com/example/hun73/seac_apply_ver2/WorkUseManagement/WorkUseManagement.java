package com.example.hun73.seac_apply_ver2.WorkUseManagement;

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

import android.view.MenuItem;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.SessionManager;

import java.util.HashMap;

public class WorkUseManagement extends AppCompatActivity
{
    private static final String TAG = "WorkUseManagement: ";

    private Context mContext;
    private TabLayout mTabLayout;

    private ViewPager mViewPager;
    private ContentsPagerAdapter mContentPagerAdapter;

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_use_management);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.use_management_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        // 세션생성
        sessionManager = new SessionManager(this);

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        mTabLayout = (TabLayout) findViewById(R.id.use_management_tab);

        mTabLayout.addTab(mTabLayout.newTab().setText("이용중"));
        mTabLayout.addTab(mTabLayout.newTab().setText("이용예정"));
        mTabLayout.addTab(mTabLayout.newTab().setText("이용완료"));
        mTabLayout.addTab(mTabLayout.newTab().setText("내 후기"));

        mViewPager = (ViewPager) findViewById(R.id.pager_content);
        mContentPagerAdapter = new ContentsPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        mViewPager.setAdapter(mContentPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                // TODO : tab의 상태가 선택 상태로 변경.
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {
                // TODO : tab의 상태가 선택되지 않음으로 변경.
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
                // TODO : 이미 선택된 상태의 tab이 사용자에 의해 다시 선택됨.
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
    public class ContentsPagerAdapter extends FragmentStatePagerAdapter
    {
        private int mPageCount;

        public ContentsPagerAdapter(FragmentManager fm, int pageCount)
        {
            super(fm);
            this.mPageCount = pageCount;
        }

        @Override
        public Fragment getItem(int position)
        {

            switch (position)
            {
                case 0:
                    Fragment_Use_Work_Now fragment_use_work_now = new Fragment_Use_Work_Now();

                    // 프래그먼트로 값 전달
                    Bundle bundle_Now = new Bundle();
                    bundle_Now.putString("getId", getId);
                    fragment_use_work_now.setArguments(bundle_Now);

                    return fragment_use_work_now;

                case 1:
                    Fragment_Use_Work_Soon fragment_use_work_soon = new Fragment_Use_Work_Soon();

                    // 프래그먼트로 값 전달
                    Bundle bundle_soon = new Bundle();
                    bundle_soon.putString("getId", getId);
                    fragment_use_work_soon.setArguments(bundle_soon);

                    return fragment_use_work_soon;

                case 2:
                    Fragment_Use_Work_Complete fragment_use_work_complete = new Fragment_Use_Work_Complete();

                    Bundle bundle_comp = new Bundle();
                    bundle_comp.putString("getId", getId);
                    fragment_use_work_complete.setArguments(bundle_comp);

                    return fragment_use_work_complete;

                case 3:
                    Fragment_My_Work_Review fragment_my_work_review = new Fragment_My_Work_Review();

                    Bundle bundle_review = new Bundle();
                    bundle_review.putString("getId", getId);
                    fragment_my_work_review.setArguments(bundle_review);

                    return fragment_my_work_review;

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
