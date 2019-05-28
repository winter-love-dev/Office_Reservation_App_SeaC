package com.example.hun73.seac_apply_ver2.RecyclerView;

//public class ChattingRoomAdapter extends RecyclerView.Adapter<ChattingRoomAdapter.ChattingRoom_ViewHolder>
//{
//    private Context mContext;
//    private List<chattingMessageContent> chattingMessageContent;
//    private String TAG = "ChattingRoomAdapter: ";
//
//
//
//    public ChattingRoomAdapter(Context Context, List<chattingMessageContent> List)
//    {
//        mContext = Context;
//        chattingMessageContent = List;
//    }
//
//    @NonNull
//    @Override
//    public ChattingRoom_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
//    {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_room_message, viewGroup, false);
//        return new ChattingRoom_ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ChattingRoom_ViewHolder chattingRoom_viewHolder, int i)
//    {
//        chattingMessageContent currentItem = chattingMessageContent.get(i);
//
//        // 메시지 모드 불러오기
//        // 모드 = 1 전송모드
//        // 모드 = 2 수신모드
//        int mode = currentItem.getMode();
//
//        String
//                chat_user_photo = currentItem.getUserPhoto()    // 상대방 프로필 이미지
//                ,   chat_user_name = currentItem.getSenderNick()    // 상대방 이름
//                ,   chat_user_id = currentItem.getSenderId()        // 상대방 아이디
//                ,   time_send = currentItem.getTime()               // 메시지 전송한 시간
//                ,   time_receive = currentItem.getTime()            // 메시지 수신받은 시간
//                ,   chat_message_content = currentItem.getMessage() // 메시지 내용
//                ;
//
//        // 말풍선의 위치를 오른쪽으로 배치하기 위한 준비
////        LinearLayout.LayoutParams params =
////                (LinearLayout.LayoutParams) chattingRoom_viewHolder.message_gravity.getLayoutParams();
//
//        // 메시지 모드 1 = 수신모드 (말풍선 왼쪽 배치)
//        if (mode == 1)
//        {
//            // 전송시간 숨기기
//            chattingRoom_viewHolder.time_send.setVisibility(View.GONE);
//
//            // 말풍선 색상을 수신모드로 바꾸기.
//            chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_2);
//
//            // 유저 이미지
//            Picasso.get().load(chat_user_photo).
//                    memoryPolicy(MemoryPolicy.NO_CACHE).
//                    placeholder(R.drawable.logo_2).
//                    networkPolicy(NetworkPolicy.NO_CACHE).
//                    into(chattingRoom_viewHolder.chat_user_photo);
//
//            chattingRoom_viewHolder.chat_user_name.setText(chat_user_name);
//
//            chattingRoom_viewHolder.time_receive.setText(time_receive);
//
//            chattingRoom_viewHolder.chat_message_content.setText(chat_message_content);
//
//            Log.e(TAG, " " );
//            Log.e(TAG, " ===== ===== 리사이클러뷰 수신 ===== ===== mode: " + mode);
//            Log.e(TAG, "메시지 수신: " + chat_message_content);
//            Log.e(TAG, "받은 시간" + time_receive);
//            Log.e(TAG, "이름: " + chat_user_name + " / 아이디: " + chat_user_id + "이미지: " + chat_user_photo);
//            Log.e(TAG, " ===== ===== ===== ===== ===== ===== ===== =====");
//            Log.e(TAG, " " );
//        }
//
//        // 전송모드 (말풍선 오른쪽 배치)
//        else if (mode == 2)
//        {
//            // 사진, 이름, 수신 시간 비활성화
//            chattingRoom_viewHolder.chat_user_photo.setVisibility(View.GONE);
//            chattingRoom_viewHolder.chat_user_name.setVisibility(View.GONE);
//            chattingRoom_viewHolder.time_receive.setVisibility(View.GONE);
//
//            // 1. 말풍선의 위치를 오른쪽으로 배치한다.
//            // 2. 말풍선 색상을 전송모드로 바꾸기.
//            chattingRoom_viewHolder.message_gravity.setGravity(Gravity.RIGHT);
//            chattingRoom_viewHolder.chat_message_content.setBackgroundResource(R.drawable.item_chat_type_1);
//
//            // 내용, 보낸 시간
//            chattingRoom_viewHolder.chat_message_content.setText(chat_message_content);
//            chattingRoom_viewHolder.time_send.setText(time_send);
//
//            Log.e(TAG, " " );
//            Log.e(TAG, " ===== ===== 리사이클러뷰 전송 ===== ===== mode: " + mode);
//            Log.e(TAG, "메시지 수신: " + chat_message_content);
//            Log.e(TAG, "보낸 시간" + time_send);
//            Log.e(TAG, " ===== ===== ===== ===== ===== ===== ===== =====");
//            Log.e(TAG, " " );
//        }
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        return chattingMessageContent.size();
//    }
//
//    public class ChattingRoom_ViewHolder extends RecyclerView.ViewHolder
//    {
//        public View view;
//
//        public LinearLayout message_gravity;
//
//        public CircleImageView chat_user_photo;
//
//        public TextView
//                chat_user_name
//                ,   time_send
//                ,   time_receive
//                ,   chat_message_content
//                ;
//
//        public ChattingRoom_ViewHolder(@NonNull View itemView)
//        {
//            super(itemView);
//            view = itemView;
//            message_gravity = itemView.findViewById(R.id.message_gravity);
//            chat_user_photo = itemView.findViewById(R.id.chat_user_photo);
//            chat_user_name = itemView.findViewById(R.id.chat_user_name);
//            time_send = itemView.findViewById(R.id.time_send);
//            time_receive = itemView.findViewById(R.id.time_receive);
//            chat_message_content = itemView.findViewById(R.id.chat_message_content);
//        }
//    }
//}
