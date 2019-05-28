package com.example.hun73.seac_apply_ver2.RecyclerView;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Activity_WorkSpace_Detail_Info;
import com.example.hun73.seac_apply_ver2.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.RecyclerView_Home_ViewHolder>
{
    private Context mContext;
    private List<Home_Item> mHomeList;

    public Home_Adapter(Context context, List<Home_Item> HomeList)
    {
        mContext = context;
        mHomeList = HomeList;
    }

    @NonNull
    @Override
    public RecyclerView_Home_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_home, viewGroup, false);

        return new RecyclerView_Home_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView_Home_ViewHolder recyclerView_home_viewHolder, int i)
    {
        Home_Item currentItem = mHomeList.get(i);

        // 사무실 인덱스
        String Work_No = currentItem.getWork_No();
        Log.e(TAG, "onBindViewHolder: Work_No: " + Work_No);

        // 썸네일 경로
        String thum_Image_Url = currentItem.getThum_Image_Url();

        // 사무실 이름
        String work_Name = currentItem.getWork_Name();

        // 테이블 수 최대
        String Work_Table = currentItem.getWork_Table();

        // 테이블 수 현재 이용중인
        String Work_Table_current = currentItem.getWork_table_current();

        // 남은 테이블 수 구해서 보여주기
        // (총 테이블수) - (현재 이용중인 테이블 수) = 남은 테이블 수
        int Tn = Integer.parseInt(Work_Table);
        int TnC = Integer.parseInt(Work_Table_current);

        // 남은 테이블 수
        int Table_Total = Tn - TnC;

        Log.e(TAG, "onBindViewHolder: Work_Table_current: " + Work_Table_current);

        // 사무실 주소
        String Work_Address1 = currentItem.getWork_Address1();

        // 사무실 가격 하루
        String Work_Price = currentItem.getWork_Price();

        long value = Long.parseLong(Work_Price);
        DecimalFormat format = new DecimalFormat("###,###");//콤마
        format.format(value);
        String result_int = format.format(value);

        // 서버 URL로 불러온 이미지를 세팅한다.
        Picasso.get().load("http://115.68.231.84/"+thum_Image_Url).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(recyclerView_home_viewHolder.imageView);

        recyclerView_home_viewHolder.work_name.setText(work_Name);
        recyclerView_home_viewHolder.work_table.setText("남은 테이블: " + Table_Total);
        recyclerView_home_viewHolder.work_address1.setText(Work_Address1);
        recyclerView_home_viewHolder.work_price.setText(result_int+"원");

        Log.e(TAG, "onBindViewHolder work_Name: " + work_Name);
        Log.e(TAG, "onBindViewHolder Work_Table: " + Work_Table);
        Log.e(TAG, "onBindViewHolder Work_Address1: " + Work_Address1);
        Log.e(TAG, "onBindViewHolder Work_Price: " + Work_Price);
        Log.e(TAG, "onBindViewHolder thum_Image_Url: http://115.68.231.84/" +thum_Image_Url);

        String ReserThumbImage = "http://115.68.231.84/"+thum_Image_Url;

        recyclerView_home_viewHolder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContext = v.getContext();
                Intent intent = new Intent(mContext, Activity_WorkSpace_Detail_Info.class);

                // 사무실 DB 테이블 인덱스를 아래 startActivity에 담은 액티비티로 보내기
                // 다음 액티비티에서
                intent.putExtra("Work_No", Work_No);
                intent.putExtra("ReserThumbImage", ReserThumbImage);

                // 사무실 자세히보기 액티비티로 이동하기
                mContext.startActivity(intent);

                Toast.makeText(mContext, "사무실 인덱스: " + Work_No, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick: Work_No: " + Work_No );
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mHomeList.size();
    }

    public class RecyclerView_Home_ViewHolder extends RecyclerView.ViewHolder
    {
        public View view; // 리스트 아이템
        public ImageView imageView; // 사무실 썸네일
        public TextView work_name; // 사무실 이름
        public TextView work_table; // 테이블 수
        public TextView work_address1; // 주소
        public TextView work_price; // 가격

        public RecyclerView_Home_ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            work_name = itemView.findViewById(R.id.work_name);
            work_table = itemView.findViewById(R.id.work_table);
            work_address1 = itemView.findViewById(R.id.work_address1);
            work_price = itemView.findViewById(R.id.work_price);
        }
    }
}
