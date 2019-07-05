/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * © 2015 – 2018 Wowza Media Systems, LLC. All rights reserved.
 */

package com.example.hun73.seac_apply_ver2.Wowza;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.volley.VolleyLog.TAG;
import static com.example.hun73.seac_apply_ver2.Wowza.Activity_Broadcast_Setting.broadcast_title;
import static com.example.hun73.seac_apply_ver2.Wowza.GoCoderSDKActivityBase.Wowza_Host_Rommno;

public class Wowza_Main extends AppCompatActivity
{
    private static final String MAIN_ACTIVITY_TAG = "Wowza_Main";

    private RecyclerView mRecyclerView;
    private static List<BroadCastListItem> mBroadCastListItem;
    private Wowza_BroadCast_Adapter wowza_broadCast_adapter;

    private TextView wowza_main_activity_camera_activity,
            wowza_main_activity_player_activity;

    // 방송 입장 후 TCP 채팅 서버로 입장하기 위한 정보
    public static String
            MyId // 내 아이디
        ,   MyName // 내 이름
        ,   MyPhoto // 내 사진
        ,   Wowza_Host_Id
        ;

    private String userType;

    // todo: 현재 접속중인 wifi IP주소 불러오기
    public String getWifiAddress()
    {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN))
        {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString = null;

        try
        {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException e)
        {
            Log.e(TAG, "getWifiAddress: e: " + e);
            Log.e(TAG, "WIFIIP: Unable to get host address. ");

            e.printStackTrace();
        }

        return ipAddressString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wowza_main);

        Log.e(MAIN_ACTIVITY_TAG, "onCreate: Wowza_Main 실행됨");


        // todo: 현재 접속중인 wifi IP주소 불러오기
        Log.e(TAG, "onCreate: getWifiAddress(): " + getWifiAddress() );


        // 방송 입장 후 TCP 채팅 서버로 입장하기 위한 정보
        Intent intent = getIntent();
        MyId = intent.getExtras().getString("getId");
        MyName = intent.getExtras().getString("strName");
        MyPhoto = intent.getExtras().getString("strImage");
        userType = intent.getExtras().getString("strType");

        // todo: 값 확인하기
        Log.e(MAIN_ACTIVITY_TAG, "onCreate: 방송 입장 후 TCP 채팅 서버로 입장하기 위한 정보");
        Log.e(MAIN_ACTIVITY_TAG, "onCreate: MyId: " + MyId);
        Log.e(MAIN_ACTIVITY_TAG, "onCreate: MyName: " + MyName);
        Log.e(MAIN_ACTIVITY_TAG, "onCreate: MyPhoto: " + MyPhoto);
        Log.e(MAIN_ACTIVITY_TAG, "onCreate: userType: " + userType);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.wowza_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        // View ID Find
        wowza_main_activity_camera_activity = findViewById(R.id.wowza_main_activity_camera_activity);
        wowza_main_activity_player_activity = findViewById(R.id.wowza_main_activity_player_activity);

        // userType 1 = 일반회원, 2 = 호스트 회원
        // todo: 방송 송출 권한은 호스트만 부여됨
        if (userType.equals("1"))
        {
//            wowza_main_activity_camera_activity.setVisibility(View.GONE); // 일반 회원은 GONE 처리 필요
            wowza_main_activity_camera_activity.setVisibility(View.VISIBLE);
        }
        else if (userType.equals("2"))
        {
            wowza_main_activity_camera_activity.setVisibility(View.VISIBLE);
        }

        // 송출화면
        wowza_main_activity_camera_activity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Intent intent = new Intent(Wowza_Main.this, CameraActivity.class);
                Intent intent = new Intent(Wowza_Main.this, Activity_Broadcast_Setting.class);
                startActivity(intent);
            }
        });

        // 시청화면
        wowza_main_activity_player_activity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Wowza_Main.this, PlayerActivity.class);
                startActivity(intent);
            }
        });


        // todo: 리사이클러뷰 시작
        mRecyclerView = findViewById(R.id.wowza_broadcast_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 리스트 초기화 해주기
        mBroadCastListItem = new ArrayList<>();

        // todo: 방송목록 요청하기
        getBroadCastList();

        // todo: 방송목록 새로고침
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.wowza_swipe_layout);
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

                        getBroadCastList();
                    }
                }, 2000); // 2초 딜레이 후 리스트 새로 불러옴
            }
        });

    } /** onCreate 끝*/

    // todo: 방송목록 요청하기
    public void getBroadCastList()
    {
        Log.e(MAIN_ACTIVITY_TAG, "getBroadCastList: 방송목록 조회 시작" );

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //Defining retrofit api service
        ApiInterface BraodCastListRequest = retrofit.create(ApiInterface.class);

        //defining the call
        Call<List<BroadCastListItem>> listCall = BraodCastListRequest.BroadCastListItem("id");

        listCall.enqueue(new Callback<List<BroadCastListItem>>()
        {
            @Override
            public void onResponse(Call<List<BroadCastListItem>> call, Response<List<BroadCastListItem>> response)
            {
                Log.e(MAIN_ACTIVITY_TAG, "list call onResponse = " + response.body());
                Log.e(MAIN_ACTIVITY_TAG, "list call onResponse = " + response.body().toString());

                mBroadCastListItem = response.body();

                // 넘어온 값 확인하기
                for (int i = 0; i < mBroadCastListItem.size(); i++)
                {
                    Log.e(MAIN_ACTIVITY_TAG, "onResponse: BroadCastTitle: " + mBroadCastListItem.get(i).getBroadCastTitle());
                    Log.e(MAIN_ACTIVITY_TAG, "onResponse: HostName: " + mBroadCastListItem.get(i).getHostName());
                    Log.e(MAIN_ACTIVITY_TAG, "onResponse: BIndex: " + mBroadCastListItem.get(i).getBIndex());
                }

                wowza_broadCast_adapter = new Wowza_BroadCast_Adapter(Wowza_Main.this, mBroadCastListItem);
                mRecyclerView.setAdapter(wowza_broadCast_adapter);
            }

            @Override
            public void onFailure(Call<List<BroadCastListItem>> call, Throwable t)
            {
                Toast.makeText(Wowza_Main.this, "리스트 로드 실패. 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e("BroadCastListItem", "onFailure id t: " + t.getMessage());
            }
        });
    } /** 방송목록 요청하기 끝*/

    // Return correctly from any fragments launched and placed on the back stack
    // 백 스택에서 시작되어 배치 된 조각에서 올바르게 반환 _구글 번역
    @Override
    public void onBackPressed()
    {
        Log.e(MAIN_ACTIVITY_TAG, "onBackPressed: 뒤로가기 누름");
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0)
        {
            fm.popBackStack();
        } else
        {
            super.onBackPressed();
        }
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

    // todo: 방송목록 리사이클러뷰 어댑터
    public static class Wowza_BroadCast_Adapter extends RecyclerView.Adapter<Wowza_BroadCast_Adapter.Wowza_BroadCast_List_ViewHolder>
    {
        private Context mContext;

        public Wowza_BroadCast_Adapter(Context context, List<BroadCastListItem> broadCastListItems)
        {
            mContext = context;
            mBroadCastListItem = broadCastListItems;
        }

        @NonNull
        @Override
        public Wowza_BroadCast_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_broad_cast_list, parent, false);
            return new Wowza_BroadCast_List_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Wowza_BroadCast_List_ViewHolder holder, int position)
        {
            BroadCastListItem currentItem = mBroadCastListItem.get(position);

            String HostId               = currentItem.getHostId() // 호스트 id
                , HostName              = currentItem.getHostName() // 호스트 이름
                , HostPhoto             = currentItem.getHostPhoto() // 호스트 프로필 사진
                , BroadCastTitle        = currentItem.getBroadCastTitle() // 방송 제목
                , BroadCastStartTime    = currentItem.getBroadCastStartTime() // 방송 시작시간
                , BIndex                = currentItem.getBIndex() // 데이터 인덱스
                ;

            // todo: 넘어온 값 확인하기
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: 넘어온 값 확인하기" );

            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: BIndex: " + BIndex );
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: BroadCastTitle: " + BroadCastTitle );
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: BroadCastStartTime: " + BroadCastStartTime );
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: HostId: " + HostId );
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: HostName: " + HostName );
            Log.e(MAIN_ACTIVITY_TAG, "onBindViewHolder: HostPhoto: " + HostPhoto );

            // todo: 항목에 값 세팅하기

            // 호스트 사진 세팅
            Picasso.get().load(HostPhoto).
                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
                    networkPolicy(NetworkPolicy.NO_CACHE).
                    into(holder.broad_list_host_photo);

            // 사진 끝 둥글게 말기
            GradientDrawable drawable=
                    (GradientDrawable) mContext.getDrawable(R.drawable.item_image_round_corner_1);
            holder.broad_list_host_photo.setBackground(drawable);
            holder.broad_list_host_photo.setClipToOutline(true);

            // 호스트 이름 세팅
            holder.broad_list_host_name.setText(HostName);

            // 방송 제목 세팅
            holder.broad_list_title.setText(BroadCastTitle);

            // 방송 시작시간 세팅
            holder.broad_list_start_time.setText(BroadCastStartTime);

            // todo: 항목 클릭 후 방송 입장하기
            holder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();
                    Intent intent = new Intent(mContext, PlayerActivity.class);

                    // todo: 입장할 방 이름 설정하기
                    broadcast_title = BroadCastTitle;

                    // todo: 입장할 TCP 서버 방 번호 입력하기
//                    Wowza_Host_Id = HostId;
                    Wowza_Host_Rommno = BIndex;

                    // todo: 방송 제목으로 방 구분함.
//                    intent.putExtra("BroadCastTitle", BroadCastTitle);
                    // 일단 방송 제목으로 방송입장 테스트 먼저 하자.

                    // todo: TCP 채팅을 위한 정보 전달
//                    intent.putExtra("room_no", BIndex); // 채팅 방 번호는 데이터 인덱스
//                    intent.putExtra("user_id", MyId); // 나의 아이디 전달
//                    intent.putExtra("user_name", MyName); // 나의 이름 전달
//                    intent.putExtra("user_photo", MyPhoto); // 나의 사진 전달

                    // 입장 시작
                    mContext.startActivity(intent);

                    Toast.makeText(mContext, HostName + "님 방송을 시청합니다. ", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: BroadCastTitle: " + BroadCastTitle );
                }
            });

        }

        @Override
        public int getItemCount()
        {
            return mBroadCastListItem.size();
        }

        // todo: 뷰홀더
        public class Wowza_BroadCast_List_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view; // 리스트 아이템
            public ImageView broad_list_host_photo;
            public TextView broad_list_title, broad_list_host_name, broad_list_start_time;

            public Wowza_BroadCast_List_ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                view = itemView;
                broad_list_host_photo = itemView.findViewById(R.id.broad_list_host_photo);
                broad_list_title = itemView.findViewById(R.id.broad_list_title);
                broad_list_host_name = itemView.findViewById(R.id.broad_list_host_name);
                broad_list_start_time = itemView.findViewById(R.id.broad_list_start_time);
            }

        } /** 뷰홀더 끝*/

    } /** 어댑터 끝*/
}

