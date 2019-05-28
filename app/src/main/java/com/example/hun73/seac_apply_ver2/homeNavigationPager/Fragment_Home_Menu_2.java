package com.example.hun73.seac_apply_ver2.homeNavigationPager;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.RecyclerView.Chat_Item_User_List;
import com.example.hun73.seac_apply_ver2.chat.Activity_ChatRoom;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Home_Menu_2 extends Fragment
{
    private static final String TAG = "Fragment_Home_Menu_2";
    private View View_User_List;
    private Context Context_User_List;

    private String getId;

    private RecyclerView mRecyclerView;
    private Slide_User_List_Adapter mAdapter_user_list;
    private List<Chat_Item_User_List> mList_User;

    public Fragment_Home_Menu_2()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment

        View_User_List = inflater.inflate(R.layout.fragment_fragment_home_menu_2, container, false);

        Context_User_List = getActivity().getApplicationContext();

        if (getArguments() != null)
        {
            getId = getArguments().getString("getId"); // 전달한 key 값
            Log.e(TAG, "onCreateView: getId: " + getId);
        }

        // 채팅 유저목록 불러오기
        ChatUserList_Request();

        // 리스트 초기화
        mList_User = new ArrayList<>();

        // 리사이클러뷰 세팅
        mRecyclerView = View_User_List.findViewById(R.id.slide_recycler_view_user_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Context_User_List));

        return View_User_List;
    }

    // 채팅 유저목록 불러오기
    private void ChatUserList_Request()
    {
        Log.e(TAG, "ChatUserList_Request: 채팅 유저 불러오기");

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Defining retrofit api service
        ApiInterface ChatUserRequest = retrofit.create(ApiInterface.class);

        Call<List<Chat_Item_User_List>> ChatUserList = ChatUserRequest.ChatUserList(getId);

        ChatUserList.enqueue(new Callback<List<Chat_Item_User_List>>()
        {
            @Override
            public void onResponse(Call<List<Chat_Item_User_List>> call, Response<List<Chat_Item_User_List>> response)
            {
                mList_User = response.body();
                Log.e(TAG, "onResponse: 유저목록 요청 완료");

                // 리사이클러뷰 데이터 세팅
                mAdapter_user_list = new Slide_User_List_Adapter(Context_User_List, mList_User);
                mRecyclerView.setAdapter(mAdapter_user_list);
            }

            @Override
            public void onFailure(Call<List<Chat_Item_User_List>> call, Throwable t)
            {
                Toast.makeText(Context_User_List, "유저목록 조회 실패, 로그 확인", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: t: " + t.getMessage());
            }
        });
    }

    // 리사이클러뷰 어댑터
    public class Slide_User_List_Adapter extends RecyclerView.Adapter<Slide_User_List_Adapter.Slide_User_List_Adapter_ViewHolder>
    {
        private Context mContext;

//        private String ID_UserIndex, UserName, UserPhoto;

        public Slide_User_List_Adapter(Context context_chat_list, List<Chat_Item_User_List> mList_user)
        {
            mContext = context_chat_list;
            mList_User = mList_user;
        }

        @NonNull
        @Override
        public Slide_User_List_Adapter_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
        {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_user_list_home, viewGroup, false);

            return new Slide_User_List_Adapter_ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Slide_User_List_Adapter_ViewHolder slide_user_list_adapter_viewHolder, int i)
        {
            Chat_Item_User_List currentItem = mList_User.get(i);

            String
                    UserEmail = currentItem.getEmail(), // 유저 이메일
                    UserType = currentItem.getType() // 유저 타입
                            ;

            String ID_UserIndex = currentItem.getUserIndex(); // 유저의 테이블 인데스
            String UserName = currentItem.getName(); // 유저의 이름
            String UserPhoto = currentItem.getPhoto(); // 유저의 사진

            Log.e(TAG, "onBindViewHolder: ID_UserIndex: " + ID_UserIndex);
            Log.e(TAG, "onBindViewHolder: UserName: " + UserName);
            Log.e(TAG, "onBindViewHolder: UserEmail: " + UserEmail);
            Log.e(TAG, "onBindViewHolder: UserType: " + UserType);
            Log.e(TAG, "onBindViewHolder: UserPhoto: " + UserPhoto);

            // 서버 URL로 불러온 이미지를 세팅한다.
            Picasso.get().load(UserPhoto).
                    memoryPolicy(MemoryPolicy.NO_CACHE).
                    placeholder(R.drawable.logo_2).
                    networkPolicy(NetworkPolicy.NO_CACHE).
                    resize(50, 50).
                    into(slide_user_list_adapter_viewHolder.chat_user_list_photo);

            // 유저 이름
            slide_user_list_adapter_viewHolder.chat_user_list_name.setText(UserName);

            if (UserType.equals("1"))
            {
                slide_user_list_adapter_viewHolder.chat_user_list_type.setText("크리에이터 회원");
            } else if (UserType.equals("2"))
            {
                slide_user_list_adapter_viewHolder.chat_user_list_type.setText("호스트 회원");
            }

            slide_user_list_adapter_viewHolder.view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mContext = v.getContext();

                    AlertDialog.Builder work_question = new AlertDialog.Builder(mContext);

                    Log.e(TAG, "onClick: view.setOnClickListener: " + ID_UserIndex );
                    Log.e(TAG, "onClick: view.setOnClickListener: " + UserName );

                    // 다이얼로그 세팅
                    work_question.setTitle("'" + UserName + "' 님과 대화합니다.");
//                        work_question.setMessage("문의 유형을 선택해주세요");
                    work_question
//                     .setCancelable(false) // 다이얼로그 실행되면 뒤로가기 비활성화
                            .setPositiveButton("네",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @SuppressLint("LongLogTag")
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            // TODO: 서버에서 방 목록 조회하기
                                            // 1. 상대방과 대화하던 방이 조회되면 해당 방으로 이동한다
                                            // 2. 상대방와 대화하던 방이 조회되지 않으면 새 방을 생성한다.
                                            //    그리고 유저를 새로 생성된 방으로 입장하게 한다.
//                                            Chat_User_List_Room_Lookup();

                                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Chat_User_List_Room_Lookup.php",
                                                    new com.android.volley.Response.Listener<String>()
                                                    {
                                                        @Override
                                                        public void onResponse(String response)
                                                        {
                                                            try
                                                            {
                                                                JSONObject jsonObject = new JSONObject(response);

                                                                Log.e(TAG, "onResponse: jsonObject: " + jsonObject);

                                                                // 채팅방 번호
                                                                String room_No = jsonObject.getString("Room_No");

                                                                Log.e(TAG, "onResponse: Room_No" + room_No);

                                                                JSONArray jsonArray = jsonObject.getJSONArray("Room_No");

                                                                JSONObject object = jsonArray.getJSONObject(0);

                                                                // json에서 방 번호 받기
                                                                String RoomNo = object.getString("CRm_Room_No");

                                                                Log.e(TAG, "onResponse: Chat_User_List_Room_Lookup() RoomNo: " + RoomNo);

                                                                // 상대방과 채팅 시작
                                                                Intent intent = new Intent(mContext, Activity_ChatRoom.class);

                                                                intent.putExtra("room_no", RoomNo);
                                                                intent.putExtra("user_id", ID_UserIndex);
                                                                intent.putExtra("user_name", UserName);
                                                                intent.putExtra("user_photo", UserPhoto);

                                                                mContext.startActivity(intent);

                                                                Toast.makeText(mContext, "방 번호: " + RoomNo + " / 유저 인덱스: " + ID_UserIndex + " / " + UserName + "님과 대화합니다.", Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "방 번호: " + RoomNo + " / 유저 인덱스: " + ID_UserIndex + " / " + UserName + "님과 대화합니다.");

                                                            } catch (JSONException e)
                                                            {
                                                                e.printStackTrace();
                                                                Toast.makeText(Context_User_List, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                                                                Log.e(TAG, "onResponse: JSONException e: " + e.toString());
                                                            }

                                                        }
                                                    },
                                                    new com.android.volley.Response.ErrorListener()
                                                    {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error)
                                                        {
                                                            Toast.makeText(Context_User_List, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                            {
                                                @Override
                                                protected Map<String, String> getParams() throws AuthFailureError
                                                {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("getId", getId);
                                                    params.put("userId", ID_UserIndex);

                                                    return params;
                                                }
                                            };

                                            RequestQueue requestQueue = Volley.newRequestQueue(Context_User_List);
                                            requestQueue.add(stringRequest);
                                        }
                                    })
                            .setNegativeButton("닫기",
                                    new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {

                                        }
                                    });

                    final AlertDialog edit_dialog = work_question.create();

                    edit_dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
                    {
                        @Override
                        public void onShow(DialogInterface arg0)
                        {
                            edit_dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                            edit_dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                        }
                    });
                    edit_dialog.show(); // 다이얼로그 실행
                    // 다이얼로그 끝
                }
            });
        }

        // TODO: 서버에서 방 목록 조회하기
        // 1. 상대방과 대화하던 방이 조회되면 해당 방으로 이동한다
        // 2. 상대방와 대화하던 방이 조회되지 않으면 새 방을 생성한다.
        //    그리고 유저를 새로 생성된 방으로 입장하게 한다.
//        private void Chat_User_List_Room_Lookup()
//        {
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Chat_User_List_Room_Lookup.php",
//                    new com.android.volley.Response.Listener<String>()
//                    {
//                        @Override
//                        public void onResponse(String response)
//                        {
//                            try
//                            {
//                                JSONObject jsonObject = new JSONObject(response);
//
//                                Log.e(TAG, "onResponse: jsonObject: " + jsonObject);
//
//                                // 채팅방 번호
//                                String room_No = jsonObject.getString("Room_No");
//
//                                Log.e(TAG, "onResponse: Room_No" + room_No);
//
//                                JSONArray jsonArray = jsonObject.getJSONArray("Room_No");
//
//                                JSONObject object = jsonArray.getJSONObject(0);
//
//                                // json에서 방 번호 받기
//                                String RoomNo = object.getString("CRm_Room_No");
//
//                                Log.e(TAG, "onResponse: Chat_User_List_Room_Lookup() RoomNo: " + RoomNo);
//
//                                // 상대방과 채팅 시작
//                                Intent intent = new Intent(mContext, Activity_ChatRoom.class);
//
//                                intent.putExtra("room_no", RoomNo);
//                                intent.putExtra("user_id", ID_UserIndex);
//                                intent.putExtra("user_name", UserName);
//                                intent.putExtra("user_photo", UserPhoto);
//
//                                mContext.startActivity(intent);
//
//                                Toast.makeText(mContext, "방 번호: " + RoomNo + " / 유저 인덱스: " + ID_UserIndex + " / " + UserName + "님과 대화합니다.", Toast.LENGTH_SHORT).show();
//                                Log.e(TAG, "방 번호: " + RoomNo + " / 유저 인덱스: " + ID_UserIndex + " / " + UserName + "님과 대화합니다.");
//
//                            } catch (JSONException e)
//                            {
//                                e.printStackTrace();
//                                Toast.makeText(Context_User_List, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
//                                Log.e(TAG, "onResponse: JSONException e: " + e.toString());
//                            }
//
//                        }
//                    },
//                    new com.android.volley.Response.ErrorListener()
//                    {
//                        @Override
//                        public void onErrorResponse(VolleyError error)
//                        {
//                            Toast.makeText(Context_User_List, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//            {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError
//                {
//                    Map<String, String> params = new HashMap<>();
//                    params.put("getId", getId);
//                    params.put("userId", ID_UserIndex);
//
//                    return params;
//                }
//            };
//
//            RequestQueue requestQueue = Volley.newRequestQueue(Context_User_List);
//            requestQueue.add(stringRequest);
//        }

        @Override
        public int getItemCount()
        {
            return mList_User.size();
        }

        public class Slide_User_List_Adapter_ViewHolder extends RecyclerView.ViewHolder
        {
            public View view;

            public CircleImageView chat_user_list_photo;

            public TextView
                    chat_user_list_name, chat_user_list_type;


            public Slide_User_List_Adapter_ViewHolder(@NonNull View itemView)
            {
                super(itemView);

                view = itemView;
                chat_user_list_photo = itemView.findViewById(R.id.chat_user_list_photo);
                chat_user_list_name = itemView.findViewById(R.id.chat_user_list_name);
                chat_user_list_type = itemView.findViewById(R.id.chat_user_list_type);
            }
        }
    }

}
