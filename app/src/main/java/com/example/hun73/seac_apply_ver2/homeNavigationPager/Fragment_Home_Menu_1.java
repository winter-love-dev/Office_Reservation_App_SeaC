package com.example.hun73.seac_apply_ver2.homeNavigationPager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.ActivityOpenCvCamera;
import com.example.hun73.seac_apply_ver2.Activity_Home;
import com.example.hun73.seac_apply_ver2.Activity_MyPage;
import com.example.hun73.seac_apply_ver2.Activity_hostAddWorkSpace;
import com.example.hun73.seac_apply_ver2.ERC20_Wallet.WalletActivity;
import com.example.hun73.seac_apply_ver2.Pomodoro.Main.TimerActivity;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.WorkUseManagement.WorkUseManagement;
import com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Home_Menu_1 extends Fragment
{
    private static final String TAG = "Fragment_Home_Menu_1";
    private View View_Home_Pager_1;
    private Context Context_Home_Pager_1;

    private String getId;
    public static CircleImageView
                  slide_profile_image;

    private TextView
            slide_name,
            slide_user_type,
            nav_activity_my_page,
            nav_activity_use_management,
            nav_activity_add_work_space,
            nav_activity_Chat,
            nav_activity_pomodoro_timer,
            nav_activity_opencv_camera,
            nav_activity_erc20_wallet,
            nav_activity_broadcast,
            nav_use_guide,
            nav_count;

    private LinearLayout nav_use_guide_layout;

    private static String URL_READ = "http://115.68.231.84/read_detail.php"; //iwinv

    public Fragment_Home_Menu_1()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Home_Pager_1 = inflater.inflate(R.layout.fragment_fragment__home__menu_1, container, false);

        Context_Home_Pager_1 = getActivity().getApplicationContext();

        slide_profile_image = View_Home_Pager_1.findViewById(R.id.slide_profile_image);
        slide_name = View_Home_Pager_1.findViewById(R.id.slide_name);
        slide_user_type = View_Home_Pager_1.findViewById(R.id.slide_user_type);
        nav_activity_my_page = View_Home_Pager_1.findViewById(R.id.nav_activity_my_page);
        nav_activity_use_management = View_Home_Pager_1.findViewById(R.id.nav_activity_use_management);
        nav_activity_pomodoro_timer = View_Home_Pager_1.findViewById(R.id.nav_activity_pomodoro_timer);
        nav_activity_opencv_camera = View_Home_Pager_1.findViewById(R.id.nav_activity_opencv_camera);
        nav_activity_erc20_wallet = View_Home_Pager_1.findViewById(R.id.nav_activity_erc20_wallet);
        nav_activity_broadcast = View_Home_Pager_1.findViewById(R.id.nav_activity_broadcast);

        nav_activity_Chat = View_Home_Pager_1.findViewById(R.id.nav_activity_Chat);

        nav_use_guide = View_Home_Pager_1.findViewById(R.id.nav_use_guide);
        nav_count = View_Home_Pager_1.findViewById(R.id.nav_count);
        nav_use_guide_layout = View_Home_Pager_1.findViewById(R.id.nav_use_guide_layout);
        nav_activity_add_work_space = View_Home_Pager_1.findViewById(R.id.nav_activity_add_work_space);

        nav_activity_Chat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Activity_Home.mViewPager.setCurrentItem(1);
            }
        });

        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        // 액티비티 이동하기_마이페이지
        nav_activity_my_page.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Home_Pager_1, Activity_MyPage.class);
                Context_Home_Pager_1.startActivity(intent);
            }
        });

        // 이용 현황으로 이동하기
        nav_activity_use_management.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Home_Pager_1, WorkUseManagement.class);
                Context_Home_Pager_1.startActivity(intent);
            }
        });

        // 뽀모도로 타이머로 이동하기
        nav_activity_pomodoro_timer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Home_Pager_1, TimerActivity.class);
                Context_Home_Pager_1.startActivity(intent);
            }
        });

        // 얼굴인식 카메라로 이동하기
        nav_activity_opencv_camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Home_Pager_1, ActivityOpenCvCamera.class);
                Context_Home_Pager_1.startActivity(intent);
            }
        });

        // 지갑으로 이동하기
        nav_activity_erc20_wallet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Context_Home_Pager_1, WalletActivity.class);
                Context_Home_Pager_1.startActivity(intent);
            }
        });

        // 유저 정보 불러오기
        getUserDetail();

        return View_Home_Pager_1;
    }

    // 유저 상세정보 불러오기 (이름, 이메일)
    private void getUserDetail()
    {
        // 프로그레스 다이얼로그 활성화.
        // 유저 정보를 서버에서 요청받는 동안 해당 다이얼로그 활성화.

        // Volley로 서버 요청을 보내기 위해 데이터 세팅.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>()
                {
                    // JSON 형태로 결과 요청을 받는다.
                    // 요청받은 결과가 response에 저장된다.
                    @Override
                    public void onResponse(String response)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        Log.i(TAG, response.toString());

                        try
                        {
                            // JSON 형식으로 응답받음.
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "getUserDetail: response = " + response);

                            // 전달받은 json에서 success를 받는다.
                            // {"success":"??"}
                            String success = jsonObject.getString("success");

                            // 키값이 read인 json에 담긴 value들을 배열에 담는다.
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            // success의 값이 아래와 같으면 아래 반복문을 진행한다
                            // {"success":"??"} = ??에 담긴 값이 1일 때 아래 반복문을 진행
                            if (success.equals("1"))
                            {
                                // 키값이 readX인 배열에 담긴 값들을 모두 불러온다.
                                // 불러올 값 = 이름, 이메일, 프로필 이미지
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    Log.e(TAG, "onResponse: JSONObject object = " + object.length());

                                    String strName = object.getString("name").trim();
                                    String strType = object.getString("type").trim();
                                    String strImage = object.getString("image").trim();
//                                    Log.e(TAG, "onResponse: strType = " + strType );
                                    Log.e(TAG, "onResponse: strImage = " + strImage);

                                    // 프로필 페이지에서 이름과 이메일 영역에
                                    // 로그인 중인 유저의 이름, 이메일, 프로필 사진을 출력한다.
                                    slide_name.setText(strName);

                                    // 회원 유형 출력하기
                                    if (strType.equals("1")) // 일반회원
                                    {
                                        // 일반회원 = 크리에이터
                                        slide_user_type.setText("크리에이터 회원");
                                        nav_activity_add_work_space.setVisibility(View.GONE);
//                                        // 호스트가 아님.
//                                        // 호스트 신청버튼 활성화.
//                                        button_host_register.setVisibility(View.VISIBLE);


//                                        // 공간등록 버튼 비활성화
//                                        button_host_add_space.setVisibility(View.GONE);
                                    } else if (strType.equals("2")) // 호스트
                                    {
                                        slide_user_type.setText("호스트 회원");

                                        //공간등록 버튼 활성화
                                        nav_activity_add_work_space.setVisibility(View.VISIBLE);
                                        nav_activity_add_work_space.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Context_Home_Pager_1 = v.getContext();
                                                Intent intent = new Intent(Context_Home_Pager_1, Activity_hostAddWorkSpace.class);
                                                Context_Home_Pager_1.startActivity(intent);
                                            }
                                        });
                                    } else if (strType.equals("0")) // 운영자
                                    {
                                        // 운영자 역할 안 정해짐.
                                        slide_user_type.setText("운영자");

                                        // 둘 다 비활성화
//                                        button_host_register.setVisibility(View.GONE);
//                                        button_host_add_space.setVisibility(View.GONE);
                                    }

                                    Picasso.get().
                                            load(String.valueOf(strImage)).
                                            memoryPolicy(MemoryPolicy.NO_CACHE).
                                            placeholder(R.drawable.logo_2).
                                            networkPolicy(NetworkPolicy.NO_CACHE).
                                            into(slide_profile_image);

                                    if (TextUtils.isEmpty(strImage))
                                    {
                                        Log.e(TAG, "onResponse: strImage: " + strImage);

                                        // 서버 URL로 불러온 이미지를 세팅한다.
                                        Picasso.get().load(strImage).
                                                memoryPolicy(MemoryPolicy.NO_CACHE).
                                                placeholder(R.drawable.logo_2).
                                                networkPolicy(NetworkPolicy.NO_CACHE).
                                                into(slide_profile_image);

                                    }


                                    // todo: 방송 페이지로 이동하기
                                    nav_activity_broadcast.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            Intent intent = new Intent(Context_Home_Pager_1, Wowza_Main.class);

                                            // 방송 입장 후 채팅을 하기위한 내 정보 전달
                                            intent.putExtra("strImage", strImage);
                                            intent.putExtra("strName", strName);
                                            intent.putExtra("getId", getId);
                                            intent.putExtra("strType", strType);

                                            Context_Home_Pager_1.startActivity(intent);
                                        }
                                    });
                                }
                            }
                        } // try에 포함된 로직 중 틀린 코드가 있으면 예외상황으로 간주함.
                        catch (JSONException e) // 에러 알림
                        {
                            e.printStackTrace();
                            Toast.makeText(Context_Home_Pager_1, "에러발생!" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: getUserDetail JSONException e = " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() // 응답 실패할 시 에러 알림
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e(TAG, "onResponse: getUserDetail VolleyError = " + error.toString());
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

//        Context_Home_Pager_1.getApplicationContext();
        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
//
        RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(Context_Home_Pager_1);
        requestQueue.add(stringRequest);
    }

}
