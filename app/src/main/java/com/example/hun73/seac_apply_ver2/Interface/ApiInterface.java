package com.example.hun73.seac_apply_ver2.Interface;

import com.android.volley.Response;
import com.example.hun73.seac_apply_ver2.RecyclerView.Chat_Item_Room_List;
import com.example.hun73.seac_apply_ver2.RecyclerView.Chat_Item_User_List;
import com.example.hun73.seac_apply_ver2.RecyclerView.Home_Item;
import com.example.hun73.seac_apply_ver2.Model.WorkUpModel;
import com.example.hun73.seac_apply_ver2.RecyclerView.Review_List_Item;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Now_Item;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Review_Item;
import com.example.hun73.seac_apply_ver2.RecyclerView.Use_Work_Soon_Item;
import com.example.hun73.seac_apply_ver2.RecyclerView.chattingMessageContent;
import com.example.hun73.seac_apply_ver2.WorkUseManagement.Fragment_Use_Work_Soon;
import com.example.hun73.seac_apply_ver2.WorkUseManagement.Review_Detail_Itam;
import com.example.hun73.seac_apply_ver2.Wowza.BroadCastListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

import javax.xml.transform.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface
{
    @FormUrlEncoded
    @POST("workSpace_upload.php")
    Call<WorkUpModel> WorkUpdate(
            // 서버로 보낼 값을 아래 변수에 담는다.
            // 값이 담긴 변수를 아래 필드에 담는다.
            // 필드 이름으로 POST 를 받는다.
            @Field("IntentAddress1") String IntentAddress1,
            @Field("IntentAddress2") String IntentAddress2,
            @Field("IntentWorkSpaceName") String IntentWorkSpaceName,
            @Field("IntentWorkSpaceContact") String IntentWorkSpaceContact,
            @Field("IntentWorkSpaceTableMax") String IntentWorkSpaceTableMax,
            @Field("IntentWorkSpacePd") String IntentWorkSpacePd,
            @Field("IntentWorkSpacePw") String IntentWorkSpacePw,
            @Field("IntentWorkSpacePm") String IntentWorkSpacePm,
            @Field("getWorkSpaceIntroduce") String getWorkSpaceIntroduce,
            @Field("imageFile") File imageFile, // 이미지 파일
            @Field("HostId") String HostId
    );

    // 홈화면에 게시물 불러오기
    @FormUrlEncoded
    @POST("WorkList_Home.php")
    Call<List<Home_Item>> WorkList_Home(@Field("id") String id);

    // 방송목록 불러오기
    @FormUrlEncoded
    @POST("Broadcast_list_request.php")
    Call<List<BroadCastListItem>> BroadCastListItem(@Field("id") String id);

    // 현재 이용중인 사무실 불러오기
    @FormUrlEncoded
    @POST("WUM_Now.php")
    Call<List<Use_Work_Now_Item>> UseNow_List(@Field("getId") String id);
    //    Call<List<Use_Work_Now_Item>> UseNow_List(@Field("Now_List") String id);

    // 예약중인 사무실 불러오기
    @FormUrlEncoded
    @POST("WUM_Soon.php")
    Call<List<Use_Work_Soon_Item>> UseSoon_List(@Field("getId") String id);

    // 이용이 종료된 사무실 불러오기
    @FormUrlEncoded
    @POST("WUM_Comp.php")
    Call<List<Use_Work_Soon_Item>> UseComp_List(@Field("getId") String id);

    // 리뷰를 작성한 사무실 불러오기
    @FormUrlEncoded
    @POST("WUM_Review.php")
    Call<List<Use_Work_Review_Item>> UseReview_List(@Field("getId") String id);

    // 예약중 -> 이용중으로 상태 업데이트
    @FormUrlEncoded
    @POST("WUM_Soon_Update.php")
    Call<List<Use_Work_Update>> Fragment_Use_Work_Soon(@Field("index") String index);

    // 이용중 -> 이용 완료로 상태 업데이트
    @FormUrlEncoded
    @POST("WUM_Now_Update.php")
    Call<List<Use_Work_Update>> Fragment_Use_Work_Now(@Field("index") String index);

    // 후기 상세정보 불러오기
    @FormUrlEncoded
    @POST("Review_detail.php")
    Call<List<Review_Detail_Itam>> ReviewDetail(@Field("review_index") String review_index);

    // 후기 목록 불러오기
    @FormUrlEncoded
    @POST("Review_List.php")
    Call<List<Review_List_Item>> ReviewList(@Field("review_index") String review_index);

    // 사업장 다중이미지 업로드
    @POST("workSpace_upload.php")
    Call<ResponseBody> multipartUpload(@Body RequestBody file);

    // 사업장 정보 수정하기
    @POST("workSpace_Edit.php")
    Call<ResponseBody> multipartEdit(@Body RequestBody file2);

    // 유저 목록 불러오기
    @FormUrlEncoded
//    @POST("Chat_User_List.php")
    @POST("Questioner_List.php")
    Call<List<Chat_Item_User_List>> ChatUserList(@Field("user_index") String user_index);

    // 대화목록 불러오기
    @FormUrlEncoded
    @POST("Chat_Running_List.php")
    Call<List<Chat_Item_Room_List>> ChatRoomList(@Field("user_index") String user_index);

    // 채팅방에서 대화기록 불러오기
    @FormUrlEncoded
    @POST("Chat_Message_getRecord.php")
    Call<List<chattingMessageContent>> Chat_getMessage(@Field("room_no") String room_no);


}

//IntentAddress1
//IntentAddress2
//IntentWorkSpaceName
//IntentWorkSpaceContact
//IntentWorkSpaceTableMax
//IntentWorkSpacePd
//IntentWorkSpacePw
//IntentWorkSpacePm
//getWorkSpaceIntroduce
//HostId
