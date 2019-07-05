package com.example.hun73.seac_apply_ver2.Wowza;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hun73.seac_apply_ver2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Wowza_Chat_Adapter extends RecyclerView.Adapter<Wowza_Chat_Adapter.Wowza_Chat_ViewHolder>
{
    private String TAG = "Wowza_Chat_Adapter";

    private Context mContext;
    private List<Wowza_Chat_Item> mChatList;

    public Wowza_Chat_Adapter(Context Context, List<Wowza_Chat_Item> ChatList)
    {
        mContext = Context;
        mChatList = ChatList;
    }

    @NonNull
    @Override
    public Wowza_Chat_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_item_wowza, parent, false);

        return new Wowza_Chat_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Wowza_Chat_ViewHolder holder, int position)
    {
        Wowza_Chat_Item wowza_chat_item = mChatList.get(position);

        String
                chat_user_photo = wowza_chat_item.getUserPhoto()    // 상대방 프로필 이미지
                , SenderNick = wowza_chat_item.getSenderNick()        // 상대방 이름
                , SenderId = wowza_chat_item.getSenderId()        // 작성자 아이디
                , time_send = wowza_chat_item.getTime()               // 메시지 전송한 시간
                , time_receive = wowza_chat_item.getTime()            // 메시지 수신받은 시간
                , chat_message_content = wowza_chat_item.getMessage() // 메시지 내용
                , Message_Type = wowza_chat_item.getMType()           // 메시지 타입
        ;

        Log.e(TAG, "onBindViewHolder: chat_user_photo: " + chat_user_photo );
        Log.e(TAG, "onBindViewHolder: SenderNick: " + SenderNick );
        Log.e(TAG, "onBindViewHolder: chat_user_id: " + SenderId );
        Log.e(TAG, "onBindViewHolder: time_send: " + time_send );
        Log.e(TAG, "onBindViewHolder: time_receive: " + time_receive );
        Log.e(TAG, "onBindViewHolder: chat_message_content: " + chat_message_content );
        Log.e(TAG, "onBindViewHolder: Message_Type: " + Message_Type );

        // 유저 이미지
        Picasso.get().load(chat_user_photo).
                placeholder(R.drawable.logo_2).
                resize(30, 30).
                into(holder.wowza_chat_item_photo);

        // 발신자 이름
        holder.wowza_chat_item_name.setText(SenderNick);

        // 발신자 메시지
        holder.wowza_chat_item_message.setText(chat_message_content);
    }

    @Override
    public int getItemCount()
    {
        return mChatList.size();
    }

    public class Wowza_Chat_ViewHolder extends RecyclerView.ViewHolder
    {

        public View view; // 리스트 아이템
        public CircleImageView wowza_chat_item_photo;
        public TextView wowza_chat_item_name, wowza_chat_item_message;

        public Wowza_Chat_ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            view = itemView;
            wowza_chat_item_photo = itemView.findViewById(R.id.wowza_chat_item_photo);
            wowza_chat_item_name = itemView.findViewById(R.id.wowza_chat_item_name);
            wowza_chat_item_message = itemView.findViewById(R.id.wowza_chat_item_message);
        }
    }
}
