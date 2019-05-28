package com.example.hun73.seac_apply_ver2.WorkUseManagement;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Activity_WorkSpace_Detail_Info;
import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.Interface.Use_Work_Update;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Soon_Item;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Fragment_Use_Work_Soon extends Fragment
{

    private static final String TAG = "Fragment_Use_Work_Soon: ";
    private View View_Use_Soon;
    private Context Context_Use_Soon;

    String getId;

    // 리사이클러뷰
    private RecyclerView mRecyclerView_Soon;
    private Fragment_Use_Work_Soon.Use_Work_Soon_Adapter adapter_Use_Soon;
    private List<Use_Work_Soon_Item> Use_Soon_List;

    public Fragment_Use_Work_Soon()
    {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Use_Soon = inflater.inflate(R.layout.fragment_use_work_soon, container, false);

        Use_Soon_List = new ArrayList<>();

        // WorkUseManagement에서 전달한 유저 인덱스 값 받기
        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        // 리사이클러뷰에 담을 데이터 불러오기
        getUseSoonList();

        // 리사이클러뷰
        mRecyclerView_Soon = (RecyclerView) View_Use_Soon.findViewById(R.id.recyclerView_Soon);
        mRecyclerView_Soon.setHasFixedSize(true);
        mRecyclerView_Soon.setLayoutManager(new LinearLayoutManager(Context_Use_Soon));

        // 리사이클러뷰 새로고침
        SwipeRefreshLayout swipeRefreshLayout = View_Use_Soon.findViewById(R.id.swipe_soon);
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

                        getUseSoonList();
                    }
                }, 2000); // 2초 딜레이 후 리스트 새로 불러옴
            }
        });

        return View_Use_Soon;
    }

    long mNow;
    Date mDate;
    String diffDay;
    long diffDays;

    SimpleDateFormat mFormat = new SimpleDateFormat("MM월 dd일 / HH시 mm분");

    // 현재시간 구하기
    private String getTime()
    {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    String index;

    List<Use_Work_Update> Use_Work_Update;

    // 이용 예정 목록 불러오기
    private void getUseSoonList()
    {
        Log.e("getWrokList: ", "이용현황: 현재 이용중인 사무실 로드");
        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface UseSoonListRequest = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Use_Work_Soon_Item>> Use_Soon_Call = UseSoonListRequest.UseSoon_List(getId);

        Use_Soon_Call.enqueue(new Callback<List<Use_Work_Soon_Item>>()
        {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<Use_Work_Soon_Item>> call, Response<List<Use_Work_Soon_Item>> response)
            {

                Use_Soon_List = response.body();

                // 배열
                int[] SoonCountArray = new int[Use_Soon_List.size()];

                if (SoonCountArray.length == 0)
                {

                } else if (Use_Soon_List.size() == 0)
                {

                } else
                {
                    int countSoon;

                    // 이용 시작 시간까지 남은 초 담기
                    for (int i = 0; i < Use_Soon_List.size(); i++)
                    {
                        // 이용상태 체크
                        int Aviliability = Integer.parseInt(Use_Soon_List.get(i).getAviliability());
                        Log.e(TAG, "onResponse: Aviliability: " + Aviliability);

                        // 사무실 인덱스 받기
                        index = Use_Soon_List.get(i).getReserNo();
                        Log.e(TAG, "onResponse: index: " + index);

                        // 이용 상태가 0인 것만 담기
                        if (Aviliability == 0)
                        {
                            // 시작일 담기
                            String StartDay = Use_Soon_List.get(i).getUse_work_now_start_day();

                            // 현재부터 이용 시작일까지 일수 차이 구하기
                            String diffOfDate = doDiffOfDate(StartDay);
//                        Log.e(TAG, "onResponse: diffOfDateSoon: " + diffOfDate);

                            // 남은 일수가 0 이하인 숫자는 출력하지 않기
                            int diffOfDateInt = Integer.parseInt(diffOfDate);

                            if (diffOfDateInt >= 0)
                            {
                                try
                                {
                                    // 현재부터 종료일까지 몇 초 남았는지 구하기
                                    Date d1 = mFormat.parse(getTime()); // 현재시간
                                    Date d2 = mFormat.parse(StartDay); // 이용 시작일

                                    // 차이 계산
                                    long diff = d1.getTime() - d2.getTime();
                                    long sec = diff / 1000;
                                    String d = String.valueOf(sec);
                                    Log.e(TAG, "onResponse: d: " + d);

                                    // dd에 결과값(초) 담기
                                    String dd = d.substring(1);
                                    Log.e(TAG, "onResponse: dd: " + dd);

                                    if (dd == "")
                                    {
                                        // 배열에 초 담기
                                        SoonCountArray[i] = Integer.parseInt(d);

                                        countSoon = SoonCountArray[i];

                                        // 어레이리스트의 Count에 값 담기
                                        Use_Soon_List.get(i).setCount(countSoon);

                                        Log.e(TAG, "Use_Soon_List[" + i + "].count: " + Use_Soon_List.get(i).getCount());
                                    } else
                                    {
                                        int ddd = Integer.parseInt(dd);

//                                             배열에 초 담기
                                        SoonCountArray[i] = ddd;

                                        countSoon = SoonCountArray[i];

//                                             어레이리스트의 Count에 값 담기
                                        Use_Soon_List.get(i).setCount(countSoon);

                                        Log.e(TAG, "Use_Soon_List[" + i + "].count: " + Use_Soon_List.get(i).getCount());
                                    }

                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    Log.e(TAG, " ");

                    Collections.sort(Use_Soon_List);

                    int SaveLastIdexSoon = 0;
                    for (int j = 0; j < Use_Soon_List.size(); j++)
                    {
                        Log.e(TAG, "SaveLastIdexSoon: Use_Soon_List[" + j + "]: " + Use_Soon_List.get(j).getCount() + " 초 남음");
                        if (Use_Soon_List.get(j).getCount() < 50)
                        {
                            Log.e(TAG, "이용현황 " + j + "번 인덱스에서 " + 0 + " 을 찾았다. 업데이트 시작");

                            // 해당 사무실 인덱스의 상태를 업데이트 (예약중 -> 사용중으로)
                            updateAviliability();

                            // 0이 담긴 마지막 인덱스를 SaveLastIdex에 보관하기
                            SaveLastIdexSoon = j;
                        } else
                        {

                        }
                    }
//
                    Log.e(TAG, "onResponse: SaveLastIdex: " + SaveLastIdexSoon);

//                    if (SaveLastIdexSoon == 0)
//                    {
//
//                    } else
//                    {

//                    if (Use_Soon_List.size() == 1)
//                    {
//
//                    }
//                    else
//                    {
//                        for (int i = 0; i < SaveLastIdexSoon + 1; i++)
//                        {
//                            Use_Soon_List.remove(0);
////                        Log.e(TAG, "Collections: 삭제 후 Use_Now_List: [" + i + "] " + Use_Soon_List.get(i).getCount() + " 초 남음");
//                        }
//                    }


//                    }

                    // 결과를 출력합니다.
//                    for (int i = 0; i < Use_Soon_List.size(); i++)
//                    {
//                        int dd = Use_Soon_List.get(i).getCount();
//                        Log.e(TAG, "Collections: 삭제 후 Use_Soon_List: [" + i + "]" + dd);
//                    }

                    // 리사이클러뷰 세팅
                    adapter_Use_Soon = new Fragment_Use_Work_Soon.Use_Work_Soon_Adapter(Context_Use_Soon, Use_Soon_List);
                    mRecyclerView_Soon.setAdapter(adapter_Use_Soon);
                }
            }

            @Override
            public void onFailure(Call<List<Use_Work_Soon_Item>> call, Throwable t)
            {
                Toast.makeText(Context_Use_Soon, "리스트 로드 실패. 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e("Fragment_Use_Work_Soon", "onFailure id t: " + t.getMessage());
            }
        });
    }

    String URL_UPDATE_AVILIABILITY = "http://115.68.231.84/WUM_Soon_Update.php";

    // 이용중으로 상태 업데이트
    private void updateAviliability(/*String index*/)
    {
        Log.e("updateAviliability: ", "이용중으로 업데이트 중");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface UseSoonListRequest = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Use_Work_Update>> Fragment_Use_Work_Soon_Call = UseSoonListRequest.Fragment_Use_Work_Soon(index);

        Fragment_Use_Work_Soon_Call.enqueue(new Callback<List<Use_Work_Update>>()
        {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<Use_Work_Update>> call, Response<List<Use_Work_Update>> response)
            {
                Use_Work_Update = response.body();
                Log.e(TAG, "onResponse: 업데이트 완료 ");
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<List<Use_Work_Update>> call, Throwable t)
            {
                Log.e(TAG, "onFailure: Throwable t: " + t);
            }
        });


//        Use_Soon_Call.enqueue(new Callback<List<Use_Work_Soon_Item>>()


        // volley
        // 입력한 정보를 php POST로 DB에 전송합니다.
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPDATE_AVILIABILITY,
//                new com.android.volley.Response.Listener<String>()
//                {
//                    @SuppressLint("LongLogTag")
//                    @Override
//                    public void onResponse(String response)
//                    {
//                        Log.e(TAG, "onResponse: response = " + response);
//
//                        try
//                        {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            // 가입이 완료되면 서버에서 success를 반환한다.
//                            String success = jsonObject.getString("success");
//
//                            // 계정이 중복되면 서버에서 3을 반환한다
//                            // 중복결과 알리고 가입 중지
//                            if (success.equals("success"))
//                            {
//                                Log.e(TAG, "onResponse: success: " + success  + " 이용중인 사무실로 업데이트 했습니다.");
//                            }
//                            else
//                            {
//                                Log.e(TAG, "onResponse: error: " + success );
//                            }
//                        } catch (JSONException e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new com.android.volley.Response.ErrorListener()
//                {
//                    @SuppressLint("LongLogTag")
//                    @Override
//                    public void onErrorResponse(VolleyError error)
//                    {
//                        Log.e(TAG, "onErrorResponse: error: " + error );
//                    }
//                })
//        {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError
//            {
//                Map<String, String> params = new HashMap<>();
//                params.put("index", index);
//
//                return params;
//            }
//        };
//        RequestQueue requestQueue = (RequestQueue) Volley.newRequestQueue(Context_Use_Soon);
//        requestQueue.add(stringRequest); // stringRequest = 바로 위에 회원가입 요청메소드 실행
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
    public void onStart()
    {
        super.onStart();
    }

    // 어댑터 클래스
    public class Use_Work_Soon_Adapter extends RecyclerView.Adapter<Fragment_Use_Work_Soon.Use_Work_Soon_Adapter.UseWorkSoon_ViewHolder>
    {
        private Context mContext;

//        public Use_Work_Soon_Adapter(Context Context, List<Use_Work_Now_Item> Soon_List)
//        {
//
//        }

        public Use_Work_Soon_Adapter(Context context_use_soon, List<Use_Work_Soon_Item> use_soon_list)
        {
            mContext = context_use_soon;
            Use_Soon_List = use_soon_list;
        }

        @NonNull
        @Override
        public UseWorkSoon_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            // 아이템 레이아웃 연결
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycle_use_work_now, viewGroup, false);
            return new UseWorkSoon_ViewHolder(view);
        }

        String Count;
        int Soon_Start_Day_Count;

        @SuppressLint("LongLogTag")
        @Override
        public void onBindViewHolder(@NonNull UseWorkSoon_ViewHolder useWorkSoon_viewHolder, int i)
        {

            // 항목마다 데이터가 담길 수 있게 데이터를 분리한다.
            Use_Work_Soon_Item currentItem = Use_Soon_List.get(i);

            // 1. 사무실 이름
            String Soon_Place_Name = currentItem.getUse_work_now_place_name();

            // 2. 사무실 이미지
            String Soon_image = currentItem.getUse_work_now_image();

            // 3. 이용 시작일
            String Soon_Start_Day = currentItem.getUse_work_now_start_day();

            // 4. 이용 종료일
            String Soon_End_Day = currentItem.getUse_work_now_end_day();

            // 이용 시작까지 남은 초
            Soon_Start_Day_Count = currentItem.getCount();

            // 초를 일 시간 분으로 구하기
            int D = (int) (Soon_Start_Day_Count / (60 * 60 * 24));
            int H = (int) (Soon_Start_Day_Count - D * 60 * 60 * 24) / (60 * 60);
            int M = (int) (Soon_Start_Day_Count - D * 60 * 60 * 24 - H * 3600) / 60;

            // 5. 총 이용일
            String Soon_ToTal_Day = currentItem.getUse_work_now_total_use_day();

            // 7. 지불한 금액
            String Soon_Pay = currentItem.getUse_work_now_pay();

            // 8. 사무실 번호
            String Soon_Index = currentItem.getUse_work_now_place_number();

            Log.e(TAG, "Soon_Place_Name: " + Soon_Place_Name);
            Log.e(TAG, "Soon_image: " + Soon_image);
            Log.e(TAG, "Soon_Start_Day: " + Soon_Start_Day);
            Log.e(TAG, "Soon_Start_Count: " + Soon_Start_Day_Count);
            Log.e(TAG, "Soon_End_Day: " + Soon_End_Day);
            Log.e(TAG, "Soon_ToTal_Day: " + Soon_ToTal_Day);
            Log.e(TAG, "Soon_Pay: " + Soon_Pay);
            Log.e(TAG, "Soon_Index: " + Soon_Index);

            // 한시간
            if (Soon_Start_Day_Count < 3600)
            {
                // 남은시간 안내
                useWorkSoon_viewHolder.time_remaining.setText("곧 이용합니다");

                Count = M + "분 "/* + array[i] + " " + array.length*/;

                // 남은 기간
                Log.e(TAG, "SortTime: 남은 시간 " + M + "분 ");
//                useWorkNow_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 40, 0));
//                useWorkNow_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(254, 127, 156)); // 핑크
                useWorkSoon_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 144, 167)); // 핑크
                useWorkSoon_viewHolder.use_work_now_date_count.setText(Count);
            }

            // 하루
            else if (Soon_Start_Day_Count < 86400)
            {
                // 남은시간 안내
                useWorkSoon_viewHolder.time_remaining.setText("이용까지 남은 시간");

                Count = H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
                Log.e(TAG, "SortTime: 남은 시간 " + H + "시간 " + M + "분 ");
//                useWorkNow_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 179, 71)); // 오렌지
//                useWorkNow_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(247, 202, 201)); // 연핑크
                useWorkSoon_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 144, 167)); // 핑크
                useWorkSoon_viewHolder.use_work_now_date_count.setText(Count);
            }

            // 일
            else if (Soon_Start_Day_Count > 86400)
            {
                // 남은시간 안내
                useWorkSoon_viewHolder.time_remaining.setText("이용까지 남은 기간");

                Count = D + "일 " + H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
                Log.e(TAG, "SortTime: 남은 시간 " + D + " 일 " + H + "시간 " + M + "분 ");

                useWorkSoon_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(144, 167, 209));
                useWorkSoon_viewHolder.use_work_now_date_count.setText(Count);
            }


            // 사무실 이름 Now_Place_Name
            useWorkSoon_viewHolder.use_work_now_place_name.setText(Soon_Place_Name);

            // 서버 URL로 불러온 이미지를 세팅한다.
            Picasso.get().load(Soon_image).
                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
                    networkPolicy(NetworkPolicy.NO_CACHE).
                    into(useWorkSoon_viewHolder.use_work_now_image);

            // 이용 시작일
            useWorkSoon_viewHolder.use_work_now_start_day.setText(Soon_Start_Day);

            // 이용 종료일
            useWorkSoon_viewHolder.use_work_now_end_day.setText(Soon_End_Day);

            // 총 이용일
            useWorkSoon_viewHolder.use_work_now_total_use_day.setText(Soon_ToTal_Day + " 일");

            // 지불한 금액
            long value = Long.parseLong(Soon_Pay);
            DecimalFormat format = new DecimalFormat("###,###");//콤마
            format.format(value);
            String result_int = format.format(value);

            useWorkSoon_viewHolder.use_work_now_pay.setText("￦ " + result_int);

            // 클릭하면 사무실의 상세 페이지로 이동함
            useWorkSoon_viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();
                    Intent intent = new Intent(mContext, Activity_WorkSpace_Detail_Info.class);

                    // 상세 페이지로 사무실 인덱스를 보낸다.
                    // 상세 페이지에서 해당 인덱스로 사무실 정보를 불러온다.
                    intent.putExtra("Work_No", Soon_Index);

                    // 사무실 자세히보기 액티비티로 이동하기
                    mContext.startActivity(intent);

                    Toast.makeText(mContext, "사무실 인덱스: " + Soon_Index, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Now_Index: " + Soon_Index);
                }
            });

        }

        @Override
        public int getItemCount()
        {
            // 항목 갯수 체크
            return Use_Soon_List.size();
        }


        public class UseWorkSoon_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view; // 목록 클릭용 뷰
            public ImageView use_work_now_image; // 사무실 썸네일
            public TextView time_remaining; // 남은 시간
            public TextView use_work_now_place_name; // 사무실 이름
            public TextView use_work_now_start_day; // 입장일
            public TextView use_work_now_end_day; // 종료일
            public TextView use_work_now_date_count; // 남은 시간
            public TextView use_work_now_pay; // 지불 금액
            public TextView use_work_now_total_use_day; // 총 이용일 수

            public UseWorkSoon_ViewHolder(@NonNull View itemView) // 뷰 연결
            {
                super(itemView);
                view = itemView;
                use_work_now_image = itemView.findViewById(R.id.use_work_now_image);
                time_remaining = itemView.findViewById(R.id.time_remaining);
                use_work_now_place_name = itemView.findViewById(R.id.use_work_now_place_name);
                use_work_now_start_day = itemView.findViewById(R.id.use_work_now_start_day);
                use_work_now_end_day = itemView.findViewById(R.id.use_work_now_end_day);
                use_work_now_date_count = itemView.findViewById(R.id.use_work_now_date_count);
                use_work_now_pay = itemView.findViewById(R.id.use_work_now_pay);
                use_work_now_total_use_day = itemView.findViewById(R.id.use_work_now_total_use_day);
            }
        }

    }

}
