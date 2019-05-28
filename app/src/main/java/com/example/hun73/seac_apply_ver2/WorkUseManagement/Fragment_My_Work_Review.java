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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Activity_WorkSpace_Detail_Info;
import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Review_Item;
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

public class Fragment_My_Work_Review extends Fragment
{

    private static final String TAG = "Fragment_My_Work_Review: ";
    private View View_Use_Review;
    private Context Context_Use_Review;
    String Count;
    String getId;

    // 리사이클러뷰
    private RecyclerView ReviewRecyclerView;
    private Use_Work_Review_Adapter adapter_Use_Review;
    private List<Use_Work_Review_Item> Use_Review_List;
    private String index;

    public interface reviewListSizeListener
    {
        int onReviewSizeReceiveData(int size);
    }

    private reviewListSizeListener listSizeListener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (getActivity() != null && getActivity() instanceof reviewListSizeListener)
        {
            listSizeListener = (reviewListSizeListener) getActivity();
        }
    }

    public Fragment_My_Work_Review()
    {
        // Required empty public constructor
    }

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Use_Review = inflater.inflate(R.layout.fragment_my_work_review, container, false);

        // 리스트 초기화 해주기
        Use_Review_List = new ArrayList<>();

        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        // 해당 프래그먼트로 오면 리사이클러뷰 초기화 해주기

        // 리사이클러뷰에 담을 데이터 불러오기
        getUseReviewList();

        // 리사이클러뷰
        ReviewRecyclerView = (RecyclerView) View_Use_Review.findViewById(R.id.recyclerView_review);
        ReviewRecyclerView.setHasFixedSize(true);
        ReviewRecyclerView.setLayoutManager(new LinearLayoutManager(Context_Use_Review));

        // 리사이클러뷰 새로고침
        SwipeRefreshLayout swipeRefreshLayout = View_Use_Review.findViewById(R.id.swipe_review);
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

                        getUseReviewList();
                    }
                }, 2000); // 2초 딜레이 후 리스트 새로 불러옴
            }
        });

        return View_Use_Review;
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

    // 작성한 리뷰 불러오기
    private void getUseReviewList()
    {
        Log.e("getWrokList: ", "이용현황: 현재 이용중인 사무실 로드");
        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface UseNowListRequest = retrofit.create(ApiInterface.class);

        // defining the call
        Call<List<Use_Work_Review_Item>> Use_Review_Call = UseNowListRequest.UseReview_List(getId);

        Use_Review_Call.enqueue(new Callback<List<Use_Work_Review_Item>>()
        {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<List<Use_Work_Review_Item>> call, Response<List<Use_Work_Review_Item>> response)
            {
                Use_Review_List = response.body();

                if (listSizeListener != null)
                {
                    listSizeListener.onReviewSizeReceiveData(Use_Review_List.size());
                }

                Log.e(TAG, "onResponse: response.body(): " + response.body() );

                // 배열
                int[] CountArray = new int[Use_Review_List.size()];

                if (CountArray.length == 0)
                {

                } else
                {
                    int CompCount;

                    // 남은 초 담기
                    for (int i = 0; i < Use_Review_List.size(); i++)
                    {
                        // 종료일 담기
                        String EndDay = Use_Review_List.get(i).getUse_work_now_end_day();

                        // 종료일부터 현재까지 일수 차이 구하기
                        String diffOfDate = doDiffOfDate(getTime());

                        // 예약 테이블의 인덱스 받기
                        index = Use_Review_List.get(i).getReserNo();
                        Log.e(TAG, "onResponse: index: " + index);

                        String review_index = Use_Review_List.get(i).getIndex_Review();
                        String review_upload_date = Use_Review_List.get(i).getUpload_date();

                        Log.e(TAG, "onResponse: review_index: " + review_index );
                        Log.e(TAG, "onResponse: review_upload_date: " + review_upload_date);

                        try
                        {
                            // 종료일부터 현재까지 몇 초 지났는지 구하기
                            Date d1 = mFormat.parse(EndDay); // 종료일
                            Date d2 = mFormat.parse(getTime()); // 현재시간

                            // 차이 계산
                            long diff = d1.getTime() - d2.getTime();
                            long sec = diff / 1000;
                            String d = String.valueOf(sec);

                            // dd에 결과값(초) 담기
                            String dd = d.substring(1);

                            int ddd = Integer.parseInt(dd);

                            // 배열에 초 담기
                            CountArray[i] = ddd;
//                            Log.e(TAG, "onResponse: 정렬 전 CountArray[" + i + "] " + CountArray[i]);

                            CompCount = CountArray[i];

                            // 어레이리스트의 Count에 값 담기
                            Use_Review_List.get(i).setCount(CompCount);

                            Log.e(TAG, "Use_Comp_List[" + i + "].count: " + Use_Review_List.get(i).getCount());

                        } catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    Log.e(TAG, " ");

                    Collections.sort(Use_Review_List);

                    int SaveLastIdex = 0;
                    for (int j = 0; j < Use_Review_List.size(); j++)
                    {
                        Log.e(TAG, "onResponse: Use_Comp_List[" + j + "]: " + Use_Review_List.get(j).getCount() + " 초 지남");
                        if (Use_Review_List.get(j).getCount() == 0)
                        {
                            Log.e(TAG, "이용현황 " + j + "번 인덱스에서 " + 0 + " 을 찾았다.");

                            // 해당 사무실 인덱스의 상태를 업데이트 (사용중 -> 사용 완료로)
//                            updateAviliability();

                            // 0이 담긴 마지막 인덱스를 SaveLastIdex에 보관하기
                            SaveLastIdex = j;
                        }
                    }

                    Log.e(TAG, "onResponse: SaveLastIdex: " + SaveLastIdex);

                    // 결과를 출력합니다.
                    for (int i = 0; i < Use_Review_List.size(); i++)
                    {
                        int dd = Use_Review_List.get(i).getCount();
                        Log.e(TAG, "Collections: 삭제 후 Use_Comp_List: [" + i + "]" + dd);
                    }

                    // 리사이클러뷰 세팅
                    adapter_Use_Review = new Fragment_My_Work_Review.Use_Work_Review_Adapter(Context_Use_Review, Use_Review_List);
                    ReviewRecyclerView.setAdapter(adapter_Use_Review);

                    Log.e(TAG, " ");
                }
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<List<Use_Work_Review_Item>> call, Throwable t)
            {
                Toast.makeText(Context_Use_Review, "리스트 로드 실패. 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure id t: " + t.getMessage());
            }
        });
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

    // 어댑터 클래스
    public class Use_Work_Review_Adapter extends RecyclerView.Adapter<Fragment_My_Work_Review.Use_Work_Review_Adapter.UseWorkReview_ViewHolder>
    {
        private Context mContext;

        public Use_Work_Review_Adapter(Context Context, List<Use_Work_Review_Item> Now_List)
        {
            mContext = Context;
            Use_Review_List = Now_List;
        }

        @NonNull
        @Override
        public UseWorkReview_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            // 아이템 레이아웃 연결
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_use_work_complete, viewGroup, false);

            return new UseWorkReview_ViewHolder(view);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onBindViewHolder(@NonNull UseWorkReview_ViewHolder useWorkReview_viewHolder, int i)
        {
            // 후기 작성버튼을 숨기고 후기 상세페이지 버튼으로 변경
            useWorkReview_viewHolder.button_review.setVisibility(View.GONE);
            useWorkReview_viewHolder.button_review_detail.setVisibility(View.VISIBLE);

            // 항목마다 데이터가 담길 수 있게 데이터를 분리한다.
            Use_Work_Review_Item currentItem = Use_Review_List.get(i);

            String upload_time = currentItem.getUpload_date();

            // 1. 사무실 이름
            String Now_Place_Name = currentItem.getUse_work_now_place_name();

            // 2. 사무실 이미지
            String Now_image = currentItem.getUse_work_now_image();

            // 3. 이용 시작일
            String Now_Start_Day = currentItem.getUse_work_now_start_day();

            // 4. 이용 종료일
            String Now_End_Day = currentItem.getUse_work_now_end_day();

            // 5. 총 이용일
            String Now_ToTal_Day = currentItem.getUse_work_now_total_use_day();

            String reserNo = currentItem.getReserNo();

            // 종료일부터 현재까지 지난 초
            int Now_End_Day_Count = currentItem.getCount();

            Log.e(TAG, "onBindViewHolder: Now_End_Day_Count: " + Now_End_Day_Count);

            // 초를 일 시간 분으로 구하기
            int D = (int) (Now_End_Day_Count / (60 * 60 * 24));
            int H = (int) (Now_End_Day_Count - D * 60 * 60 * 24) / (60 * 60);
            int M = (int) (Now_End_Day_Count - D * 60 * 60 * 24 - H * 3600) / 60;

            // 7. 지불한 금액
            String Now_Pay = currentItem.getUse_work_now_pay();

            // 8. 사무실 번호
            String Now_Index = currentItem.getUse_work_now_place_number();

            Log.e(TAG, "Now_Place_Name: " + Now_Place_Name);
            Log.e(TAG, "Now_image: " + Now_image);
            Log.e(TAG, "Now_Start_Day: " + Now_Start_Day);
            Log.e(TAG, "Now_End_Day_Count: " + Now_End_Day_Count);
            Log.e(TAG, "Now_End_Day: " + Now_End_Day);
            Log.e(TAG, "Now_ToTal_Day: " + Now_ToTal_Day);
            Log.e(TAG, "Now_Pay: " + Now_Pay);
            Log.e(TAG, "Now_Index: " + Now_Index);

            // 사무실 이름 Now_Place_Name
            useWorkReview_viewHolder.use_work_now_place_name.setText(Now_Place_Name);

            // 서버 URL로 불러온 이미지를 세팅한다.
            Picasso.get().load(Now_image).
                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
                    networkPolicy(NetworkPolicy.NO_CACHE).
                    into(useWorkReview_viewHolder.use_work_now_image);

            // 이용 시작일
            useWorkReview_viewHolder.use_work_now_start_day.setText(Now_Start_Day);

            // 이용 종료일
            useWorkReview_viewHolder.use_work_now_end_day.setText(Now_End_Day);

            // 한 시간
            if (Now_End_Day_Count < 3600)
            {
                useWorkReview_viewHolder.time_remaining.setText("방금 종료되었습니다");
                Count = M + "분 "/* + array[i] + " " + array.length*/;

                // 남은 기간
                Log.e(TAG, "SortTime: 남은 시간 " + M + "분 ");
                useWorkReview_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 144, 167)); // 핑크
                useWorkReview_viewHolder.use_work_now_date_count.setText(Count);
            }

            // 하루
            else if (Now_End_Day_Count < 86400)
            {
                useWorkReview_viewHolder.time_remaining.setText("지난 시간");

                Count = H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
                Log.e(TAG, "SortTime: 남은 시간 " + H + "시간 " + M + "분 ");
                useWorkReview_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(255, 144, 167)); // 핑크
                useWorkReview_viewHolder.use_work_now_date_count.setText(Count);
            }

            // 일
            else if (Now_End_Day_Count > 86400)
            {
                useWorkReview_viewHolder.time_remaining.setText("지난 기간");

                Count = D + "일 " + H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
                Log.e(TAG, "SortTime: 남은 시간 " + D + " 일 " + H + "시간 " + M + "분 ");

                useWorkReview_viewHolder.use_work_now_date_count.setTextColor(Color.rgb(144, 167, 209));
                useWorkReview_viewHolder.use_work_now_date_count.setText(Count);
            }

            Log.e(TAG, "onBindViewHolder: 시간 정렬한 결과값: " + Count);

            // 총 이용일
            useWorkReview_viewHolder.use_work_now_total_use_day.setText(Now_ToTal_Day + " 일");

            // 지불한 금액
            long value = Long.parseLong(Now_Pay);
            DecimalFormat format = new DecimalFormat("###,###");//콤마
            format.format(value);
            String result_int = format.format(value);

            useWorkReview_viewHolder.use_work_now_pay.setText("￦ " + result_int);

            // 사무실 이름 Now_Place_Name
            useWorkReview_viewHolder.use_work_now_place_name.setText(Now_Place_Name);

            // 후기 작성일
            useWorkReview_viewHolder.use_work_review_date.setText(upload_time);

            // 리뷰 상세페이지로 이동
            // 리뷰 테이블의 인덱스 담아두기
            String review_index = currentItem.getIndex_Review();
            Log.e(TAG, "onBindViewHolder: String review_index: " + review_index );
            useWorkReview_viewHolder.button_review_detail.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();
                    Intent intent = new Intent(mContext, Activity_Review_Detail.class);

                    // 후기 테이블의 인덱스로 리뷰를 조회한다.
                    intent.putExtra("review_index", review_index);

                    mContext.startActivity(intent);

                    Toast.makeText(mContext, "후기 인덱스: " + review_index, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: review_index: " + review_index);
                }
            });

            // 클릭하면 사무실의 상세 페이지로 이동함
            useWorkReview_viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @SuppressLint("LongLogTag")
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();
                    Intent intent = new Intent(mContext, Activity_WorkSpace_Detail_Info.class);

                    // 상세 페이지로 사무실 인덱스를 보낸다.
                    // 상세 페이지에서 해당 인덱스로 사무실 정보를 불러온다.
                    intent.putExtra("Work_No", Now_Index);

                    // 사무실 자세히보기 액티비티로 이동하기
                    mContext.startActivity(intent);

                    Toast.makeText(mContext, "사무실 인덱스: " + Now_Index, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onClick: Now_Index: " + Now_Index);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return Use_Review_List.size();
        }

        public class UseWorkReview_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view; // 목록 클릭용 뷰
            public ImageView use_work_now_image; // 사무실 썸네일
            public Button button_review, button_review_detail; // 리뷰 작성 버튼, 리뷰 확인 버튼
            public TextView time_remaining; // 남은 시간
            public TextView use_work_now_place_name; // 사무실 이름
            public TextView use_work_now_start_day; // 입장일
            public TextView use_work_now_end_day; // 종료일
            public TextView use_work_now_date_count; // 남은 시간
            public TextView use_work_now_pay; // 지불 금액
            public TextView use_work_now_total_use_day, use_work_review_date; // 총 이용일 수, 리뷰 작성일

            public UseWorkReview_ViewHolder(@NonNull View itemView) // 뷰 연결
            {
                super(itemView);
                view = itemView;
                use_work_now_image = itemView.findViewById(R.id.use_work_comp_image);
                button_review = itemView.findViewById(R.id.button_review);
                time_remaining = itemView.findViewById(R.id.time_remaining_comp);
                button_review_detail = itemView.findViewById(R.id.button_review_detail);
                use_work_review_date = itemView.findViewById(R.id.use_work_review_date);
                use_work_now_place_name = itemView.findViewById(R.id.use_work_comp_place_name);
                use_work_now_start_day = itemView.findViewById(R.id.use_work_comp_start_day);
                use_work_now_end_day = itemView.findViewById(R.id.use_work_comp_end_day);
                use_work_now_date_count = itemView.findViewById(R.id.use_work_comp_date_count);
                use_work_now_pay = itemView.findViewById(R.id.use_work_comp_pay);
                use_work_now_total_use_day = itemView.findViewById(R.id.use_work_comp_total_use_day);
            }
        }

    }

}
