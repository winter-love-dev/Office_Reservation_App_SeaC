//package com.example.hun73.seac_apply_ver2.RecyclerView;
//
//import android.content.Context;
//import android.content.Intent;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.hun73.seac_apply_ver2.Activity_WorkSpace_Detail_Info;
//import com.example.hun73.seac_apply_ver2.R;
//import com.squareup.picasso.MemoryPolicy;
//import com.squareup.picasso.NetworkPolicy;
//import com.squareup.picasso.Picasso;
//
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import static com.android.volley.VolleyLog.TAG;
//
//public class Use_Work_Now_Adapter extends RecyclerView.Adapter<Use_Work_Now_Adapter.UseWorkNow_ViewHolder>
//{
//    private Context mContext;
//    private List<Use_Work_Now_Item> mNowList; // 리스트 아이템 클래스 연결
//
//    long mNow;
//    Date mDate;
//    String diffDay;
//    long diffDays;
//
//    SimpleDateFormat mFormat = new SimpleDateFormat("MM월 dd일 / HH시 mm분");
//
//    public Use_Work_Now_Adapter(Context Context, List<Use_Work_Now_Item> Now_List)
//    {
//        mContext = Context;
//        mNowList = Now_List;
//    }
//
//    @NonNull
//    @Override
//    public UseWorkNow_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
//    {
//        // 아이템 레이아웃 연결
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recycle_use_work_now, viewGroup, false);
//
//        return new UseWorkNow_ViewHolder(view);
//    }
//
//    String Now_End_Day;
//    String Count;
//
//    @Override
//    public void onBindViewHolder(@NonNull UseWorkNow_ViewHolder useWorkNow_viewHolder, int i)
//    {
//        // 항목마다 데이터가 담길 수 있게 데이터를 분리한다.
//        Use_Work_Now_Item currentItem = mNowList.get(i);
//
//        // 1. 사무실 이름
//        String Now_Place_Name = currentItem.getUse_work_now_place_name();
//
//        // 2. 사무실 이미지
//        String Now_image = currentItem.getUse_work_now_image();
//
//        // 3. 이용 시작일
//        String Now_Start_Day = currentItem.getUse_work_now_start_day();
//
//        // 4. 이용 종료일
//        Now_End_Day = currentItem.getUse_work_now_end_day();
//
//        // 5. 총 이용일
//        String Now_ToTal_Day = currentItem.getUse_work_now_total_use_day();
//
//        // 6. 남은 시간
////        String Now_Date_Count = ??;
//        // a. 현재 시간을 불러온다.
//        // b. 종료일과 현재 시간을 비교한다.
//        // c. 두 날짜의 차이를 구한다.
//        // d. 결과값을 남은 시간에 세팅한다.
//
//        // 남은시간 메소드로 구현하자
//
//        // 7. 지불한 금액
//        String Now_Pay = currentItem.getUse_work_now_pay();
//
//        // 8. 사무실 번호
//        String Now_Index = currentItem.getUse_work_now_place_number();
//
//        Log.e(TAG, "Now_Place_Name: " + Now_Place_Name);
//        Log.e(TAG, "Now_image: " + Now_image);
//        Log.e(TAG, "Now_Start_Day: " + Now_Start_Day);
//        Log.e(TAG, "Now_End_Day: " + Now_End_Day);
//        Log.e(TAG, "Now_ToTal_Day: " + Now_ToTal_Day);
//        Log.e(TAG, "Now_Pay: " + Now_Pay);
//        Log.e(TAG, "Now_Index: " + Now_Index);
//
//        // 사무실 이름 Now_Place_Name
//        useWorkNow_viewHolder.use_work_now_place_name.setText(Now_Place_Name);
//
//        // 서버 URL로 불러온 이미지를 세팅한다.
//        Picasso.get().load(Now_image).
//                memoryPolicy(MemoryPolicy.NO_CACHE).
//                placeholder(R.drawable.logo_2).
//                networkPolicy(NetworkPolicy.NO_CACHE).
//                into(useWorkNow_viewHolder.use_work_now_image);
//
//        // 이용 시작일
//        useWorkNow_viewHolder.use_work_now_start_day.setText(Now_Start_Day);
//
//        // 이용 종료일
//        useWorkNow_viewHolder.use_work_now_end_day.setText(Now_End_Day);
//
//        // 시간 정렬한 결과값
//        useWorkNow_viewHolder.use_work_now_date_count.setText("");
//
//        // 총 이용일
//        useWorkNow_viewHolder.use_work_now_total_use_day.setText(Now_ToTal_Day + " 일");
//
//        // 남은시간
////        useWorkNow_viewHolder.use_work_now_place_name.setText(Now_Date_Count);
//        // a. 현재 시간을 불러온다.
//        // b. 종료일과 현재 시간을 비교한다.
//        // c. 두 날짜의 차이를 구한다.
//        // d. 결과값을 남은 시간에 세팅한다.
//
//        // 지불한 금액
//        long value = Long.parseLong(Now_Pay);
//        DecimalFormat format = new DecimalFormat("###,###");//콤마
//        format.format(value);
//        String result_int = format.format(value);
//
//        useWorkNow_viewHolder.use_work_now_pay.setText("￦ " + result_int);
//
//        // 클릭하면 사무실의 상세 페이지로 이동함
//        useWorkNow_viewHolder.view.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                mContext = v.getContext();
//                Intent intent = new Intent(mContext, Activity_WorkSpace_Detail_Info.class);
//
//                // 상세 페이지로 사무실 인덱스를 보낸다.
//                // 상세 페이지에서 해당 인덱스로 사무실 정보를 불러온다.
//                intent.putExtra("Work_No", Now_Index);
//
//                // 사무실 자세히보기 액티비티로 이동하기
//                mContext.startActivity(intent);
//
//                Toast.makeText(mContext, "사무실 인덱스: " + Now_Index, Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "onClick: Now_Index: " + Now_Index);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        // 리스트 안에 데이터 구하기
//        return mNowList.size();
//    }
//
//    // 현재시간 구하기
//    private String getTime()
//    {
//        mNow = System.currentTimeMillis();
//        mDate = new Date(mNow);
//        return mFormat.format(mDate);
//    }
//
//    // 시간 정렬하기
//    private void SortTime()
//    {
//        int[] array  = new int[mNowList.size()];
//
//        try
//        {
//            // 현재 시간과 종료일까지의 차이 구하기
//            Date d1 = mFormat.parse(getTime());
//            Date d2 = mFormat.parse(Now_End_Day);
//
//            // 현재 시간 - 종료일
//            long diff = d1.getTime() - d2.getTime();
//
//            // 현재부터 종료일까지 '몇 초' 남았는지 구하기
//            long sec = diff / 1000;
//            String ddd = String.valueOf(sec);
//
//            // 결과값이 '-00초'라서 -를 걸러내고 숫자만 secc에 담기
//            String ddf = ddd.substring(1);
//            int secc = Integer.parseInt(ddf);
//
//            // secc에 담긴 초수 확인하기
//            Log.e(TAG, "SortTime: secc: " + secc);
//
//            // 결과를 전부 배열에 담기
//            for (int j = 0; j < array.length; j++)
//            {
//                array[j] = secc;
//                Log.e(TAG, "SortTime: secc: " + secc);
//            }
//
//            // 배열에 담긴 수를 가장 장은 수부터 내림차순 정렬하기
//            Arrays.sort(array);
//
//            // 초를 일 시간 분으로 구하기
//            int D = (int) (secc / (60 * 60 * 24));
//            int H = (int) (secc - D * 60 * 60 * 24) / (60 * 60);
//            int M = (int) (secc - D * 60 * 60 * 24 - H * 3600) / 60;
////            int S = (int) (secc % 60);
//////            doDiffOfDate(Now_End_Day)
//
//            for (int i = 0; i < array.length; i++)
//            {
//
//                // 인덱스마다 값이 잘 담겼는지 확인하기
//                Log.e(TAG, "SortTime: sort array[" + i + "] " + array[i]);
//
//                // 한 시간 미만 남았을 때
//                if (secc <= 3600)
//                {
//                    Count = M + "분 "/* + array[i] + " " + array.length*/;
//                    // 남은 기간
//
//                    Log.e(TAG, "SortTime: 남은 시간 " + M + "분 ");
//                }
////
////            // secc의 숫자가 86400 초 보다 같거나 낮으면
//                else if (secc <= 86400)
//                {
//                    Count = H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
//                    Log.e(TAG, "SortTime: 남은 시간 " + H + "시간 " + M + "분 ");
//                }
//
////            // secc의 숫자가 86400초보다 같거나 더 높으면
//                else if (secc >= 86400)
//                {
//                    Count = D + "일 " + H + "시간 " + M + "분 "/* + array[i] + " " + array.length*/;
//                    Log.e(TAG, "SortTime: 남은 시간 " + D + " 일 " + H + "시간 " + M + "분 ");
//                }
//            }
//
//        } catch (ParseException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static class UseWorkNow_ViewHolder extends RecyclerView.ViewHolder
//    {
//        public View view; // 목록 클릭용 뷰
//        public ImageView use_work_now_image; // 사무실 썸네일
//        public TextView use_work_now_place_name; // 사무실 이름
//        public TextView use_work_now_start_day; // 입장일
//        public TextView use_work_now_end_day; // 종료일
//        public TextView use_work_now_date_count; // 남은 시간
//        public TextView use_work_now_pay; // 지불 금액
//        public TextView use_work_now_total_use_day; // 총 이용일 수
//
//        public UseWorkNow_ViewHolder(@NonNull View itemView) // 뷰 연결
//        {
//            super(itemView);
//            view = itemView;
//            use_work_now_image = itemView.findViewById(R.id.use_work_now_image);
//            use_work_now_place_name = itemView.findViewById(R.id.use_work_now_place_name);
//            use_work_now_start_day = itemView.findViewById(R.id.use_work_now_start_day);
//            use_work_now_end_day = itemView.findViewById(R.id.use_work_now_end_day);
//            use_work_now_date_count = itemView.findViewById(R.id.use_work_now_date_count);
//            use_work_now_pay = itemView.findViewById(R.id.use_work_now_pay);
//            use_work_now_total_use_day = itemView.findViewById(R.id.use_work_now_total_use_day);
//        }
//    }
//}
