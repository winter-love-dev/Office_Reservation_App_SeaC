package com.example.hun73.seac_apply_ver2.RecyclerView;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.WorkUseManagement.Activity_Review_Detail;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Review_List_Adpater extends RecyclerView.Adapter<Review_List_Adpater.ReviewList_ViewHolder>
{
    private Context mContext;
    private List<Review_List_Item> Review_List;
    private String TAG = "Review_List_Adpater: ";

    public Review_List_Adpater(Context context, List<Review_List_Item> List)
    {
        mContext = context;
        Review_List = List;
    }

    @NonNull
    @Override
    public ReviewList_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_review_list, viewGroup, false);

        return new ReviewList_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewList_ViewHolder reviewList_viewHolder, int i)
    {
        Review_List_Item currentItem = Review_List.get(i);

        String
               Date = currentItem.getReview_upload_date()   // 작성일
        ,      write = currentItem.getReview_content()      // 내용
        ,      user_image = currentItem.getReview_wsImage() // 이미지
        ,      index = currentItem.getReview_index()        // 리뷰 인덱스
        ,      name = currentItem.getReview_name()          // 유저 이름
        ;

        reviewList_viewHolder.review_list_item_date.setText(Date);

        // 유저 이미지
        Picasso.get().load(user_image).
                memoryPolicy(MemoryPolicy.NO_CACHE).
                placeholder(R.drawable.logo_2).
                networkPolicy(NetworkPolicy.NO_CACHE).
                into(reviewList_viewHolder.review_list_item_user_image);

        // 유저 이름
        reviewList_viewHolder.review_list_item_user_name.setText(name);


//        Log.e(TAG, "onBindViewHolder: write.substring: " + write.substring(0, 30) );

        // 내용 (subStr)
        reviewList_viewHolder.review_list_item_write.setText(write.substring(0, 20) + "...");
        reviewList_viewHolder.review_list_item_more_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, Activity_Review_Detail.class);

                intent.putExtra("review_index", index);

                mContext.startActivity(intent);

                Toast.makeText(mContext, "후기 인덱스: " + index, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick: review_index: " + index );
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return Review_List.size();
    }

    public class ReviewList_ViewHolder extends RecyclerView.ViewHolder
    {
        public View view; // 목록 클릭용 뷰
        public TextView
                review_list_item_date, // 작성일
                review_list_item_write, // 내용
                review_list_item_user_name // 유저 이름
        ;

        public Button review_list_item_more_button; // 더보기 버튼

        public CircleImageView review_list_item_user_image; // 유저 이미지

        public ReviewList_ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            review_list_item_date = itemView.findViewById(R.id.review_list_item_date);
            review_list_item_write = itemView.findViewById(R.id.review_list_item_write);
            review_list_item_more_button = itemView.findViewById(R.id.review_list_item_more_button);
            review_list_item_user_name = itemView.findViewById(R.id.review_list_item_user_name);
            review_list_item_user_image = itemView.findViewById(R.id.review_list_item_user_image);
        }
    }

}

