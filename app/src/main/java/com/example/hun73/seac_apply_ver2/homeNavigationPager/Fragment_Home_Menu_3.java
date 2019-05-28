package com.example.hun73.seac_apply_ver2.homeNavigationPager;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.RecyclerView.Chat_Item_Room_List;
import com.example.hun73.seac_apply_ver2.chat.Activity_ChatRoom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Home_Menu_3 extends Fragment
{
    private static final String TAG = "Fragment_Home_Menu_3";
    private View View_Chat_List;
    private Context Context_Chat_List;

    String getId;

    public static RecyclerView mRecyclerView;
    public static Slide_Chat_List_Adpater slide_chat_list_adpater;
    public static List<Chat_Item_Room_List> chat_item_room_listList;

    public Fragment_Home_Menu_3()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View_Chat_List = inflater.inflate(R.layout.fragment_fragment_home_menu_3, container, false);

        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        ChatRoomList_Request();

        chat_item_room_listList = new ArrayList<>();

        mRecyclerView = View_Chat_List.findViewById(R.id.slide_recycler_view_chat_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Context_Chat_List));

        return View_Chat_List;
    }

    public void ChatRoomList_Request()
    {
        Log.e(TAG, "ChatUserList_Request: 대화가 진행중인 방 목록 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface ChatRoomRequest = retrofit.create(ApiInterface.class);

        Call<List<Chat_Item_Room_List>> listCall = ChatRoomRequest.ChatRoomList(getId);

        listCall.enqueue(new Callback<List<Chat_Item_Room_List>>()
        {
            @Override
            public void onResponse(Call<List<Chat_Item_Room_List>> call, Response<List<Chat_Item_Room_List>> response)
            {
                chat_item_room_listList = response.body();
                Log.e(TAG, "onResponse: 대화목록 요청 완료");

                slide_chat_list_adpater = new Slide_Chat_List_Adpater(Context_Chat_List, chat_item_room_listList);
                mRecyclerView.setAdapter(slide_chat_list_adpater);
            }

            @Override
            public void onFailure(Call<List<Chat_Item_Room_List>> call, Throwable t)
            {
//                Toast.makeText(Context_Chat_List, "대화목록 조회 실패, 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    // 리사이클러뷰 어댑터
    public static class Slide_Chat_List_Adpater extends RecyclerView.Adapter<Slide_Chat_List_Adpater.Slide_Chat_List_ViewHolder>
    {

        private Context mContext;

        public Slide_Chat_List_Adpater(Context Context, List<Chat_Item_Room_List> List)
        {
            mContext = Context;
            chat_item_room_listList = List;
        }

        @NonNull
        @Override
        public Slide_Chat_List_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_list_home, viewGroup, false);

            return new Slide_Chat_List_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Slide_Chat_List_ViewHolder slide_chat_list_viewHolder, int i)
        {
            Chat_Item_Room_List currentItem = chat_item_room_listList.get(i);

            String
                    RoomNo = currentItem.getCRm_Room_No()
                    ,outMember = null
                    ,CRm_Last_Seen_Time = currentItem.getCRm_Last_Seen_Time()
                    ,MemberIndex = currentItem.getMemberIndex()
                    ,MemberName = currentItem.getMemberName()
                    ,MemberPhoto = currentItem.getMemberPhoto()
                    ,LastMessage = currentItem.getMR_Message()
                    ,MessageType = currentItem.getMR_Message_Type()
            ;

            Log.e(TAG, "onBindViewHolder: RoomNo: " + RoomNo);
            Log.e(TAG, "onBindViewHolder: MessageType: " + MessageType);

            // 서버 URL로 불러온 이미지를 세팅한다.
            Picasso.get().load(MemberPhoto).
                    placeholder(R.drawable.logo_2).
                    resize(50, 50).
                    into(slide_chat_list_viewHolder.slide_chat_photo);

            slide_chat_list_viewHolder.slide_chat_name.setText(MemberName);
            // slide_chat_list_viewHolder.slide_chat_new_message_count.setText(ChatRoomMCount);
            slide_chat_list_viewHolder.slide_chat_date.setText(CRm_Last_Seen_Time);

            if(TextUtils.isEmpty(MessageType))
            {
                Log.e(TAG, "onBindViewHolder: 메시지 유형이 확인되지 않습니다.");
            }

            // 텍스트 메시지 수신시 텍스트만 보이기
            else if(MessageType.equals("1"))
            {
                slide_chat_list_viewHolder.slide_chat_last_message.setText(LastMessage);
            }

            // 이미지 메시지 수신시 '사진'이라고 명시해주기
            else if (MessageType.equals("2"))
            {
                LastMessage = "사진";
                slide_chat_list_viewHolder.slide_chat_last_message.setText(LastMessage);
            }


            // 채팅방으로 이동하기
            slide_chat_list_viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();

                    // 상대방과 채팅 시작
                    Intent intent = new Intent(mContext, Activity_ChatRoom.class);

                    intent.putExtra("room_no", RoomNo);
                    intent.putExtra("user_id", MemberIndex);
                    intent.putExtra("user_name", MemberName);
                    intent.putExtra("user_photo", MemberPhoto);

                    mContext.startActivity(intent);

                    Toast.makeText(mContext, "방 번호: " + RoomNo + " / 유저 인덱스: " + MemberIndex + " / " + MemberName + "님과 대화합니다.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "방 번호: " + RoomNo + " / 유저 인덱스: " + MemberIndex + " / " + MemberName + "님과 대화합니다.");
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return chat_item_room_listList.size();
        }

        public class Slide_Chat_List_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;

            public CircleImageView slide_chat_photo;

            public TextView
                    slide_chat_name,
                    slide_chat_new_message_count,
                    slide_chat_date,
                    slide_chat_last_message;

            public Slide_Chat_List_ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;
                slide_chat_photo = itemView.findViewById(R.id.slide_chat_photo);
                slide_chat_name = itemView.findViewById(R.id.slide_chat_name);
                slide_chat_new_message_count = itemView.findViewById(R.id.slide_chat_new_message_count);
                slide_chat_date = itemView.findViewById(R.id.slide_chat_date);
                slide_chat_last_message = itemView.findViewById(R.id.slide_chat_last_message);
            }
        }
    }

}
