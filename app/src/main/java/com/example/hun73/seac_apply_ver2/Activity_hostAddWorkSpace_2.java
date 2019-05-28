package com.example.hun73.seac_apply_ver2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import id.zelory.compressor.Compressor;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_hostAddWorkSpace_2 extends AppCompatActivity
{

    private final String TAG = "Activity_hostAddWorkSpace_2";

    // 바텀 이미지 피커
    private TextView btn, btn_edit, imageCount;
    private List<Uri> selectedUriList; // 이미지 피커에서 추출한 uri 리스트

    private String imagePath;
    private List<String> imagePathList;

    private List<String> selectedUriList_To_StringList; // 위에서 추출한 uri 리스트를 볼리로 보내기 위해 이 리스트에 담는다.
    private Disposable singleImageDisposable;

    private Disposable multiImageDisposable;

    private ViewGroup mSelectedImagesContainer;
    private RequestManager requestManager;

    //    List<Uri> RequesturiList;
    // 뷰 변수
    private EditText WorkSpaceIntroduce; // 소개입력
    private Button WorkSpaceWriteDone, WorkSpaceEditDone; // 작성, 수정버튼

    // 사무실 등록 양식 담을 변수
    public String IntentAddress1;
    public String IntentAddress2;
    public String IntentWorkSpaceName;
    public String IntentWorkSpaceContact;
    public String IntentWorkSpaceTableMax;
    public String IntentWorkSpacePd;
    public String IntentWorkSpacePw;
    public String IntentWorkSpacePm;
    public String getWorkSpaceIntroduce;
    public String Work_Edit_Introduce;
    public String Work_Edit_WorkDBIndex;
    public String Work_Edit_getId;

    public String
            EditAdress1,
            EditAdress2,
            EditWorkName,
            EditWorkContac,
            EditWorkTableMax,
            EditWorkPd,
            EditWorkPw,
            EditWorkPm,
            EditIntro,
            EditWorkDBIndex,
            EditgetId;

    // 세션 선언.
    // 사업장을 등록하려는 유저의 정보를
    // 사무실 등록 테이블에 저장하기 위한 용도
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;
    private String URL_WORK_SPACE_UPLOAD = "http://34.73.32.3/workSapce_uplaod.php";

    private LinearLayout card_1, card_2, card_3;

    File imageFile;

    private List<Uri> work_Edit_image;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_add_work_space_2);

        // 사무실 등록버튼, 입력완료 버튼
        btn = findViewById(R.id.btn);
        btn_edit = findViewById(R.id.btn_edit);
        imageCount = findViewById(R.id.imageCount);
        WorkSpaceWriteDone = findViewById(R.id.work_space_write_done);
        WorkSpaceEditDone = findViewById(R.id.work_space_edit_done);

        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.addSpace_toolbar); // 툴바 연결하기, 메뉴 서랍!!
        setSupportActionBar(toolbar); // 툴바 띄우기

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        toolbar.setTitleTextColor(Color.WHITE); // 툴바 타이틀 색상 흰색으로 지정하기
        setSupportActionBar(toolbar);

        WorkSpaceIntroduce = findViewById(R.id.work_space_introduce);

        Intent intent = getIntent();
        String request = intent.getStringExtra("Work_Edit_Set");
        Log.e(TAG, "onCreate: Work_Edit_Set: " + request);
        // 수정요청 감지되면 아래 else if 프로세스 실행하기
        if (request == null)
        {

        } else
        {
            if (request.equals("아아"))
            {
                Log.e(TAG, "수정요청 감지됨 Work_Edit_Request: " + request);

                WorkSpaceWriteDone.setVisibility(View.GONE);
                WorkSpaceEditDone.setVisibility(View.VISIBLE);

                Work_Edit_Introduce = intent.getExtras().getString("Work_Edit_Introduce"); // 소개
                work_Edit_image = (List<Uri>) intent.getSerializableExtra("work_Edit_image");
                Log.e(TAG, "onCreate: work_Edit_image: " + work_Edit_image);

                btn.setVisibility(View.GONE);
                btn_edit.setVisibility(View.VISIBLE);
                setMultiShowButtonEdit();
                setMultiShowButtonEdit2();

                // 사무실 소개 영역에 수정할 값 세팅하기
                WorkSpaceIntroduce.setText(Work_Edit_Introduce);

                WorkSpaceEditDone.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // 수정 프로세스 시작
                        Work_Edit_Set();
                    }
                });
            }
        }

        // 세션생성
        sessionManager = new SessionManager(this);

        card_1 = findViewById(R.id.card_1);
        card_2 = findViewById(R.id.card_2);
//        card_3 = findViewById(R.id.card_3);

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        // 다중 이미지 선택버튼
        setMultiShowButton();

        // 사무실 등록버튼. 앱에서 입력한 값을 서버로 전송하기
        RequestAddWorkSpace();
    }

    // 수정하기
    @SuppressLint("LongLogTag")
    private void Work_Edit_Set()
    {
        Intent intent = getIntent();
        IntentAddress1 = intent.getExtras().getString("IntentAddress1"); // 주소
        IntentAddress2 = intent.getExtras().getString("IntentAddress2"); // 주소
        IntentWorkSpaceName = intent.getExtras().getString("IntentWorkSpaceName"); // 사무실 이름
        IntentWorkSpaceContact = intent.getExtras().getString("IntentWorkSpaceContact"); // 연락처
        IntentWorkSpaceTableMax = intent.getExtras().getString("IntentWorkSpaceTable"); // 테이블
        IntentWorkSpacePd = intent.getExtras().getString("IntentWorkSpacePd"); // 가격
        IntentWorkSpacePw = intent.getExtras().getString("IntentWorkSpacePw"); // 가격
        IntentWorkSpacePm = intent.getExtras().getString("IntentWorkSpacePm"); // 가격

        Work_Edit_WorkDBIndex = intent.getExtras().getString("Work_Edit_WorkDBIndex"); // 테이블 인덱스
        Work_Edit_getId = intent.getExtras().getString("Work_Edit_getId"); // 호스트 아이디

        // 값 확인
        Log.e(TAG, "수정 진행중인 값 IntentAddress1: " + IntentAddress1);
        Log.e(TAG, "수정 진행중인 값 IntentAddress2: " + IntentAddress2);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpaceName: " + IntentWorkSpaceName);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpaceContact: " + IntentWorkSpaceContact);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpaceTableMax: " + IntentWorkSpaceTableMax);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpacePd: " + IntentWorkSpacePd);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpacePw: " + IntentWorkSpacePw);
        Log.e(TAG, "수정 진행중인 값 IntentWorkSpacePm: " + IntentWorkSpacePm);
        Log.e(TAG, "                                                        ");
        Log.e(TAG, "수정 진행중인 값 Work_Edit_Introduce: " + Work_Edit_Introduce);
        Log.e(TAG, "수정 진행중인 값 Work_Edit_WorkDBIndex: " + Work_Edit_WorkDBIndex);
        Log.e(TAG, "수정 진행중인 값 Work_Edit_getId: " + Work_Edit_getId);

        // 사무실 소개 입력값 받기
        getWorkSpaceIntroduce = WorkSpaceIntroduce.getText().toString();

        if (imagePathList == null) // 사진 비어있는지 체크
        {
            // 버튼 흔들기
            android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
            WorkSpaceEditDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

            Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
            WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
            return;
        }

        if (imagePathList.isEmpty()) // 사진 빈칸 체크. 사진 선택 후 다시 사진 선택 취소 했는데, 넘어가려 하지 않는지 체크.
        {
            // 버튼 흔들기
            android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
            WorkSpaceEditDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

            Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
            WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
            return;
        }

        // 수정 시작
        WorkEditTask workEditTask = new WorkEditTask();
        workEditTask.onPreExecute();
        workEditTask.doInBackground();

        // 전송 시작
        // 로띠 Lottie 애니메이션
//        LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.uploadWorkSpace_lottie); // 로띠 에니메이션 위치
//        uploadWorkSpace_lottie.setAnimation("upload_loading.json");
//        uploadWorkSpace_lottie.setRepeatCount(100);
//        uploadWorkSpace_lottie.playAnimation();
//        card_1.setVisibility(View.GONE);
//        card_2.setVisibility(View.VISIBLE);
//
//        Toast.makeText(getApplicationContext(), "수정 중", Toast.LENGTH_SHORT).show();
//
//        EditWork(IntentAddress1,
//                IntentAddress2,
//                IntentWorkSpaceName,
//                IntentWorkSpaceContact,
//                IntentWorkSpaceTableMax, // 이용중인 인원보다 낮게 수정할 수 없게 제한할 방법 생각해봅시다.
//                IntentWorkSpacePd,
//                IntentWorkSpacePw,
//                IntentWorkSpacePm,
//                getWorkSpaceIntroduce,
//                imagePathList,
//                Work_Edit_WorkDBIndex,
//                Work_Edit_getId);

//        EditWork(IntentAddress1,
//                IntentAddress2,
//                IntentWorkSpaceName,
//                IntentWorkSpaceContact,
//                IntentWorkSpaceTableMax, // 이용중인 인원보다 낮게 수정할 수 없게 제한할 방법 생각해봅시다.
//                IntentWorkSpacePd,
//                IntentWorkSpacePw,
//                IntentWorkSpacePm,
//                getWorkSpaceIntroduce,
//                imagePathList,
//                Work_Edit_WorkDBIndex,
//                Work_Edit_getId);
    }

    // 서버로 수정 요청 보내기
    // 서버로 수정할 값을 보내서 수정 쿼리를 실행한다.
    @SuppressLint("LongLogTag")
    private void EditWork(String intentAddress1,
                          String intentAddress2,
                          String intentWorkSpaceName,
                          String intentWorkSpaceContact,
                          String intentWorkSpaceTableMax,
                          String intentWorkSpacePd,
                          String intentWorkSpacePw,
                          String intentWorkSpacePm,
                          String getWorkSpaceIntroduce,
                          String UrlList,
                          List<String> imagePathList,
                          String work_edit_workDBIndex,
                          String work_edit_getId)
    {
        Log.e(TAG, "EditWork: 수정요청 시작");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        Log.e(TAG, "EditWork: intentAddress1: " + intentAddress1);
        Log.e(TAG, "EditWork: intentAddress2: " + intentAddress2);
        Log.e(TAG, "EditWork: intentWorkSpaceName: " + intentWorkSpaceName);
        Log.e(TAG, "EditWork: intentWorkSpaceContact: " + intentWorkSpaceContact);
        Log.e(TAG, "EditWork: intentWorkSpaceTableMax: " + intentWorkSpaceTableMax);
        Log.e(TAG, "EditWork: intentWorkSpacePd: " + intentWorkSpacePd);
        Log.e(TAG, "EditWork: intentWorkSpacePw: " + intentWorkSpacePw);
        Log.e(TAG, "EditWork: intentWorkSpacePm: " + intentWorkSpacePm);
        Log.e(TAG, "EditWork: getWorkSpaceIntroduce: " + getWorkSpaceIntroduce);
        Log.e(TAG, "EditWork: work_edit_workDBIndex: " + work_edit_workDBIndex);
        Log.e(TAG, "EditWork: work_edit_getId: " + work_edit_getId);

        // "포스트", 포스트에 담을 값
        builder.addFormDataPart("EditAddress1", intentAddress1)
                .addFormDataPart("EditAddress2", intentAddress2)
                .addFormDataPart("EditWorkSpaceName", intentWorkSpaceName)
                .addFormDataPart("EditWorkSpaceContact", intentWorkSpaceContact)
                .addFormDataPart("EditWorkSpaceTableMax", intentWorkSpaceTableMax)
                .addFormDataPart("EditWorkSpacePd", intentWorkSpacePd)
                .addFormDataPart("EditWorkSpacePw", intentWorkSpacePw)
                .addFormDataPart("EditWorkSpacePm", intentWorkSpacePm)
                .addFormDataPart("EditWorkSpaceIntroduce", getWorkSpaceIntroduce)
                .addFormDataPart("UrlList", UrlList)
                .addFormDataPart("Work_Edit_WorkDBIndex", work_edit_workDBIndex)
                .addFormDataPart("HostId", work_edit_getId);

        for (int i = 0; i < imagePathList.size(); i++)
        {
            // 이미지 압축하기
            try
            {
                File file = new File(imagePathList.get(i));/*imagePathList.get(i)*/
                file = new Compressor(this).setQuality(20).compressToFile(file);
                builder.addFormDataPart("ImageUri[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        MultipartBody requestBody = builder.build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // gcp
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://34.73.32.3/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();

        // iwinv
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://115.68.231.84/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiInterface workUpdate = retrofit.create(ApiInterface.class);
        Call<ResponseBody> result = workUpdate.multipartEdit(requestBody);

        result.enqueue(new Callback<ResponseBody>()
        {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.e(TAG, "수정완료");
                Log.e(TAG, "onResponse response: " + response);
                Log.e(TAG, "onResponse response: " + response.body());
                Log.e(TAG, "onResponse response: " + response.body().toString());
                Log.e(TAG, "onResponse call: " + call);
                Toast.makeText(Activity_hostAddWorkSpace_2.this, "수정완료\n홈 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();

                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try // 아래 설정한 시간만큼 멈춘다
                        {
                            sleep(4000); // 3000 = 3초, 500 = 0.5초
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            // 전송 성공하면 이전 액티비티를 종료한다.
                            // 디테일 페이지까지 종료 필수.
                            Activity_hostAddWorkSpace activity_hostAddWorkSpace = (Activity_hostAddWorkSpace) Activity_hostAddWorkSpace.activity;
                            activity_hostAddWorkSpace.finish();

                            // 그리고 홈화면으로 이동한다.
                            Intent intent = new Intent(Activity_hostAddWorkSpace_2.this, Activity_Home.class);
                            startActivity(intent);

                            // Intent intent = new Intent(Activity_hostAddWorkSpace_2.this, Activity_Home.class);
                            // startActivity(intent);
                            // 전송 성공하면 해당 액티비티도 함께 종료한다.
                            finish();
                        }
                    }
                };
                thread.start();
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Toast.makeText(Activity_hostAddWorkSpace_2.this, "수정실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Call<ResponseBody> call: " + call.toString());
                Log.e(TAG, "error t: " + t.getMessage());

                card_2.setVisibility(View.GONE);
                card_1.setVisibility(View.VISIBLE);
            }
        });
    }

    // 서버로 사무실 등록 요청하기
    @SuppressLint("LongLogTag")
    private void RequestAddWorkSpace()
    {
        WorkSpaceWriteDone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = getIntent();
                IntentAddress1 = intent.getExtras().getString("IntentAddress1"); // 주소
                IntentAddress2 = intent.getExtras().getString("IntentAddress2"); // 주소
                IntentWorkSpaceName = intent.getExtras().getString("IntentWorkSpaceName"); // 사무실 이름
                IntentWorkSpaceContact = intent.getExtras().getString("IntentWorkSpaceContact"); // 연락처
                IntentWorkSpaceTableMax = intent.getExtras().getString("IntentWorkSpaceTable"); // 테이블
                IntentWorkSpacePd = intent.getExtras().getString("IntentWorkSpacePd"); // 가격
                IntentWorkSpacePw = intent.getExtras().getString("IntentWorkSpacePw"); // 가격
                IntentWorkSpacePm = intent.getExtras().getString("IntentWorkSpacePm"); // 가격

                // 사무실 소개 입력값 받기
                getWorkSpaceIntroduce = WorkSpaceIntroduce.getText().toString();
                Log.e(TAG, "RequestAddWorkSpace: getWorkSpaceIntroduce: " + getWorkSpaceIntroduce);

//                // 로띠 Lottie 애니메이션
//                LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.uploadWorkSpace_lottie); // 로띠 에니메이션 위치
//                uploadWorkSpace_lottie.setAnimation("upload_loading.json");
//                uploadWorkSpace_lottie.setRepeatCount(100);
//                uploadWorkSpace_lottie.playAnimation();
//                card_1.setVisibility(View.GONE);
//                card_2.setVisibility(View.VISIBLE);

                if (selectedUriList == null) // 사진 비어있는지 체크
                {
                    // 버튼 흔들기
                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                    return;
                }

                if (selectedUriList.isEmpty()) // 사진 빈칸 체크. 사진 선택 후 다시 사진 선택 취소 했는데, 넘어가려 하지 않는지 체크.
                {
                    // 버튼 흔들기
                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                    return;
                }

                WorkUpTask workUpTask = new WorkUpTask();
                workUpTask.onPreExecute();
                workUpTask.doInBackground();


//                imageUriList = selectedUriList;

//                imageUriList = new ArrayList<>();
//                int count = data.getClipData().getItemCount();

                // selectedUriList // 사진
                // 확인완료. 잘 넘어온다

//                if (TextUtils.isEmpty(getWorkSpaceIntroduce)) // 소개 영역 빈칸체크
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
//                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (selectedUriList == null) // 사진 비어있는지 체크
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
//                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }
//
//                if (selectedUriList.isEmpty()) // 사진 빈칸 체크. 사진 선택 후 다시 사진 선택 취소 했는데, 넘어가려 하지 않는지 체크.
//                {
//                    // 버튼 흔들기
//                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
//                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
//
//                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
//                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
//                    return;
//                }

                // 값이 잘 넘어오는지 로그로 확인하기
                Log.e(TAG, "IntentAddress1: " + IntentAddress1);
                Log.e(TAG, "IntentAddress2: " + IntentAddress2);
                Log.e(TAG, "IntentWorkSpaceName: " + IntentWorkSpaceName);
                Log.e(TAG, "IntentWorkSpaceContact: " + IntentWorkSpaceContact);
                Log.e(TAG, "IntentWorkSpaceTableMax: " + IntentWorkSpaceTableMax);
                Log.e(TAG, "IntentWorkSpacePd: " + IntentWorkSpacePd);
                Log.e(TAG, "IntentWorkSpacePw: " + IntentWorkSpacePw);
                Log.e(TAG, "IntentWorkSpacePm: " + IntentWorkSpacePm);
                Log.e(TAG, "getWorkSpaceIntroduce: " + getWorkSpaceIntroduce);
//                Log.e(TAG, "urilist: " + urilist);
                Log.e(TAG, "getId: " + getId);

                for (int i = 0; i < imagePathList.size(); i++)
                {
                    imageFile = new File(imagePathList.get(i));/*imagePathList.get(i)*/
                    Log.e(TAG, "imageFile: " + imageFile);
                    Log.e(TAG, "imagePathList.get(i): " + imagePathList.get(i));
                    //builder.addFormDataPart("ImageUri", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                    //builder.addFormDataPart("ImageUri[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
                }

//                uploadWork(IntentAddress1,
//                        IntentAddress2,
//                        IntentWorkSpaceName,
//                        IntentWorkSpaceContact,
//                        IntentWorkSpaceTableMax,
//                        IntentWorkSpacePd,
//                        IntentWorkSpacePw,
//                        IntentWorkSpacePm,
//                        getWorkSpaceIntroduce,
//                        imagePathList,
//                        getId);
            }
        });
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

    // 레트로핏 통신하기
    @SuppressLint("LongLogTag")
    private void uploadWork(
            String IntentAddress1,
            String IntentAddress2,
            String IntentWorkSpaceName,
            String IntentWorkSpaceContact,
            String IntentWorkSpaceTableMax,
            String IntentWorkSpacePd,
            String IntentWorkSpacePw,
            String IntentWorkSpacePm,
            String getWorkSpaceIntroduce,
            List<String> imageFile,
            String HostId
    )
    {
//        RequestBody filePart = RequestBody.create(MultipartBody.FORM, IntentWorkSpaceName);

//        File originalFile = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile.get(i));

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // "포스트", 포스트에 담을 값
        builder.addFormDataPart("IntentAddress1", IntentAddress1)
                .addFormDataPart("IntentAddress2", IntentAddress2)
                .addFormDataPart("IntentWorkSpaceName", IntentWorkSpaceName)
                .addFormDataPart("IntentWorkSpaceContact", IntentWorkSpaceContact)
                .addFormDataPart("IntentWorkSpaceTableMax", IntentWorkSpaceTableMax)
                .addFormDataPart("IntentWorkSpacePd", IntentWorkSpacePd)
                .addFormDataPart("IntentWorkSpacePw", IntentWorkSpacePw)
                .addFormDataPart("IntentWorkSpacePm", IntentWorkSpacePm)
                .addFormDataPart("getWorkSpaceIntroduce", getWorkSpaceIntroduce)
                .addFormDataPart("HostId", HostId);

        for (int i = 0; i < imagePathList.size(); i++)
        {
            try
            {
                File file = new File(imageFile.get(i));/*imagePathList.get(i)*/
                file = new Compressor(this).setQuality(20).compressToFile(file);
                builder.addFormDataPart("ImageUri[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
            } catch (IOException e)
            {
                e.printStackTrace();
            }


//            Log.e(TAG, "file: " + file);
//            Log.e(TAG, "imagePathList.get(i): " + imagePathList.get(i));
//            RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//             builder.addFormDataPart("ImageUri[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }
//        builder.addFormDataPart("HostId", HostId);

        MultipartBody requestBody = builder.build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // gcp
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://34.73.32.3/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();

        // iwinv
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://115.68.231.84/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiInterface workUpdate = retrofit.create(ApiInterface.class);
        Call<ResponseBody> result = workUpdate.multipartUpload(requestBody);

        result.enqueue(new Callback<ResponseBody>()
        {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Log.e(TAG, "onResponse response: " + response);
                Log.e(TAG, "onResponse response: " + response.body());
//                Log.e(TAG, "onResponse response: " + response.body().toString());
                Log.e(TAG, "onResponse call: " + call);
                Toast.makeText(Activity_hostAddWorkSpace_2.this, "전송완료\n홈 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();

                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try // 아래 설정한 시간만큼 멈춘다
                        {
                            sleep(4000); // 3000 = 3초, 500 = 0.5초
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            // 전송 성공하면 이전 액티비티를 종료한다.
                            Activity_hostAddWorkSpace activity_hostAddWorkSpace = (Activity_hostAddWorkSpace) Activity_hostAddWorkSpace.activity;
                            activity_hostAddWorkSpace.finish();

                            // Intent intent = new Intent(Activity_hostAddWorkSpace_2.this, Activity_Home.class);
                            // startActivity(intent);
                            // 전송 성공하면 해당 액티비티도 함께 종료한다.
                            finish();
                        }
                    }
                };
                thread.start();

            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                Toast.makeText(Activity_hostAddWorkSpace_2.this, "전송실패", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Call<ResponseBody> call: " + call.toString());
                Log.e(TAG, "error t: " + t.getMessage());

                card_2.setVisibility(View.GONE);
                card_1.setVisibility(View.VISIBLE);

            }
        });
    }

    // 다중 이미지 선택버튼
    private void setMultiShowButton()
    {
        requestManager = Glide.with(this);
        btn.setOnClickListener(view ->
        {
            PermissionListener permissionlistener = new PermissionListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.O) // 오레오에서만 작동하는 코드
                @SuppressLint("LongLogTag")
                @Override
                public void onPermissionGranted()
                {
                    TedBottomPicker.with(Activity_hostAddWorkSpace_2.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("선택완료")
                            .setEmptySelectionText("사진을 선택해주세요")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage(uriList ->
                            {
                                selectedUriList = uriList;
                                showUriList(uriList);
                                imageCount.setVisibility(View.VISIBLE);
                                imageCount.setText("\t" + selectedUriList.size() + "장");

                                Log.e(TAG, "uriList: " + uriList);
                                Log.e(TAG, "selectedUriList: " + selectedUriList);

                                // uri 뽑기
                                imagePathList = new ArrayList<>();
                                int count = selectedUriList.size();

                                for (int i = 0; i < count; i++)
                                {
                                    Uri imageUri = selectedUriList.get(i);
                                    Log.e(TAG, "imageUri = " + imageUri);
                                    // uri 리스트를 String List에 담기.
                                    // uri 리스트에 있는 경로를 파일 변수에 담기.

//                                    getImageFilePath(imageUri);

                                    // uri 경로를 추출해서 아래 s에 담기
                                    String s = getRealPathFromURI(imageUri);

                                    // 추출된 경로를 아래 String List에 담기
                                    imagePathList.add(s);
//                                    imagePathList.add(imageUri);
//                                    Log.e(TAG, "imagePathList = " + imagePathList);
                                }
                                Log.e(TAG, "imagePathList = " + imagePathList);
                            });
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions)
                {
                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
        });
    }

    // uri 경로를 String으로 변환하기
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("LongLogTag")
    private String getRealPathFromURI(Uri contentUri)
    {
        if (contentUri.getPath().startsWith("/storage"))
        {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try
        {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst())
            {
                return cursor.getString(columnIndex);
            }
        } finally
        {
            cursor.close();
        }
        return null;
    }

    String s;
    String URLPathList; // 서버로 수정요청을 전송한다.
    List<String> dd;

    // 다중 이미지 받기(수정)
    @SuppressLint("LongLogTag")
    private void setMultiShowButtonEdit()
    {
        Log.e(TAG, "setMultiShowButtonEdit(): 실행");
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
//        btn_edit.setOnClickListener(view ->
//        {
            PermissionListener permissionlistener = new PermissionListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.O) // 오레오에서만 작동하는 코드
                @SuppressLint("LongLogTag")
                @Override
                public void onPermissionGranted()
                {
                    TedBottomPicker.with(Activity_hostAddWorkSpace_2.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("선택완료")
                            .setEmptySelectionText("사진을 선택해주세요")
                            .setSelectedUriList(work_Edit_image)
                            .showMultiImage(uriList ->
                            {
                                work_Edit_image = uriList;
                                showEditUriList(uriList);
                                imageCount.setVisibility(View.VISIBLE);
                                imageCount.setText("\t"+work_Edit_image.size() + " 장");

                                Log.e(TAG, "edit: uriList: " + uriList);
                                Log.e(TAG, "edit: work_Edit_image: " + work_Edit_image);

                                // uri 뽑기
                                imagePathList = new ArrayList<>();
                                int count = work_Edit_image.size();

                                for (int i = 0; i < count; i++)
                                {
                                    Uri imageUri = work_Edit_image.get(i);
                                    Log.e(TAG, "imageUri = " + imageUri);
                                    // uri 리스트를 String List에 담기.
                                    // uri 리스트에 있는 경로를 파일 변수에 담기.

                                    String e = imageUri.toString();
                                    Log.e(TAG, "onPermissionGranted: e : " + e);

                                    if (e.contains("file:///"))
                                    {
                                        s = getRealPathFromURI(imageUri);
                                        Log.e(TAG, "e.contains(file:///): " + e);
                                        imagePathList.add(s);
                                    } else
                                    {
                                        imagePathList.add(e);
                                    }

                                    // 반복문 안에서 리스트에 담긴 uri와 url을 분리 할겁니다.
                                    // 해당 반복문에서 url을 추출합니다.
                                    // 추출한 url을 아래와 같은 형태로 저장해서 서버로 전송합니다.
                                    // workSpace_image/15532610640.jpg│workSpace_image/15532610641.jpg│workSpace_image/15532610642.jpg│workSpace_image/15532610643.jpg│workSpace_image/15532610644.jpg
                                    int listsize = imagePathList.size();
                                    Log.e(TAG, "onPermissionGranted: size: " + listsize);
                                    Log.e(TAG, "onPermissionGranted: URL 만 추출하기");
                                    dd = new ArrayList<>();
                                    for (int j = 0; j < imagePathList.size(); j++)
                                    {
                                        s = imagePathList.get(j);
                                        Log.e(TAG, "onPermissionGranted: imagePathList 추출");
                                        Log.e(TAG, "onPermissionGranted: for String dd = " + dd);
                                        // s에 담긴 값 중 아래 url 주소가 포함된 문자열을 골라오세요.
                                        if (s.contains("http://115.68.231.84/"))
                                        {
                                            Log.e(TAG, "s.contains(http://): " + dd);
                                            // 골라온 url에서 'http://115.68.231.84/'을 제거하고
                                            // 나머지 문자를 한 줄에 합치세요
                                            s = s.replace("http://115.68.231.84/", "");
                                            dd.add(s);
                                            URLPathList = arrayJoin("│", dd);
                                            Log.e(TAG, "onPermissionGranted: URLPathList: " + URLPathList);
                                        }
                                    }
                                }
                                Log.e(TAG, "imagePathList final: URLPathList: " + URLPathList);
                                Log.e(TAG, "imagePathList final: " + imagePathList);
                            });
                }

                // 배열에 담아서 합치기
                public String arrayJoin(String glue, List<String> array)
                {
                    String result = "";

                    for (int i = 0; i < array.size(); i++)
                    {
                        result += array.get(i);
                        if (i < array.size() - 1) result += glue;
                    }
                    return result;
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions)
                {
                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
//        });
    }

    // 다중 이미지 받기(수정)
    @SuppressLint("LongLogTag")
    private void setMultiShowButtonEdit2()
    {
        Log.e(TAG, "setMultiShowButtonEdit(): 실행");
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        btn_edit.setOnClickListener(view ->
        {
            PermissionListener permissionlistener = new PermissionListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.O) // 오레오에서만 작동하는 코드
                @SuppressLint("LongLogTag")
                @Override
                public void onPermissionGranted()
                {
                    TedBottomPicker.with(Activity_hostAddWorkSpace_2.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("선택완료")
                            .setEmptySelectionText("사진을 선택해주세요")
                            .setSelectedUriList(work_Edit_image)
                            .showMultiImage(uriList ->
                            {
                                work_Edit_image = uriList;
                                showEditUriList(uriList);
                                imageCount.setVisibility(View.VISIBLE);
                                imageCount.setText("\t"+work_Edit_image.size() + " 장");

                                Log.e(TAG, "edit: uriList: " + uriList);
                                Log.e(TAG, "edit: work_Edit_image: " + work_Edit_image);

                                // uri 뽑기
                                imagePathList = new ArrayList<>();
                                int count = work_Edit_image.size();

                                for (int i = 0; i < count; i++)
                                {
                                    Uri imageUri = work_Edit_image.get(i);
                                    Log.e(TAG, "imageUri = " + imageUri);
                                    // uri 리스트를 String List에 담기.
                                    // uri 리스트에 있는 경로를 파일 변수에 담기.

                                    String e = imageUri.toString();
                                    Log.e(TAG, "onPermissionGranted: e : " + e);

                                    if (e.contains("file:///"))
                                    {
                                        s = getRealPathFromURI(imageUri);
                                        Log.e(TAG, "e.contains(file:///): " + e);
                                        imagePathList.add(s);
                                    } else
                                    {
                                        imagePathList.add(e);
                                    }

                                    // 반복문 안에서 리스트에 담긴 uri와 url을 분리 할겁니다.
                                    // 해당 반복문에서 url을 추출합니다.
                                    // 추출한 url을 아래와 같은 형태로 저장해서 서버로 전송합니다.
                                    // workSpace_image/15532610640.jpg│workSpace_image/15532610641.jpg│workSpace_image/15532610642.jpg│workSpace_image/15532610643.jpg│workSpace_image/15532610644.jpg
                                    int listsize = imagePathList.size();
                                    Log.e(TAG, "onPermissionGranted: size: " + listsize);
                                    Log.e(TAG, "onPermissionGranted: URL 만 추출하기");
                                    dd = new ArrayList<>();
                                    for (int j = 0; j < imagePathList.size(); j++)
                                    {
                                        s = imagePathList.get(j);
                                        Log.e(TAG, "onPermissionGranted: imagePathList 추출");
                                        Log.e(TAG, "onPermissionGranted: for String dd = " + dd);
                                        // s에 담긴 값 중 아래 url 주소가 포함된 문자열을 골라오세요.
                                        if (s.contains("http://115.68.231.84/"))
                                        {
                                            Log.e(TAG, "s.contains(http://): " + dd);
                                            // 골라온 url에서 'http://115.68.231.84/'을 제거하고
                                            // 나머지 문자를 한 줄에 합치세요
                                            s = s.replace("http://115.68.231.84/", "");
                                            dd.add(s);
                                            URLPathList = arrayJoin("│", dd);
                                            Log.e(TAG, "onPermissionGranted: URLPathList: " + URLPathList);
                                        }
                                    }
                                }
                                Log.e(TAG, "imagePathList final: URLPathList: " + URLPathList);
                                Log.e(TAG, "imagePathList final: " + imagePathList);
                            });
                }

                // 배열에 담아서 합치기
                public String arrayJoin(String glue, List<String> array)
                {
                    String result = "";

                    for (int i = 0; i < array.size(); i++)
                    {
                        result += array.get(i);
                        if (i < array.size() - 1) result += glue;
                    }
                    return result;
                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions)
                {
                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
        });
    }

    @SuppressLint("LongLogTag")
    public void getImageFilePath(Uri uri)
    {
        File file = new File(uri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];

        Cursor cursor =
                getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        MediaStore.Images.Media._ID + " = ? ",
                        new String[]{image_id}, null);

        Log.e(TAG, "cursor: " + cursor);

        if (cursor != null && cursor.moveToFirst())
        {
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imagePathList.add(imagePath);
            Log.e(TAG, "imagePath: " + imagePath);
            Log.e(TAG, "imagePathList: " + imagePathList);
            cursor.close();
        }
        Log.e(TAG, "imagePathList: " + imagePathList);
    }

    private void checkPermission(PermissionListener permissionlistener)
    {
        TedPermission.with(Activity_hostAddWorkSpace_2.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @SuppressLint("LongLogTag")
    private void showUriList(List<Uri> uriList)
    {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

//        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : uriList)
        {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

//            Log.e(TAG, "uri =  " + uri);
//            Log.e(TAG, "uriList =  " + uriList);

//            RequesturiList = uriList;
//
//            Log.e(TAG, "RequesturiList =  " + RequesturiList);
//
//            Log.e(TAG, "mSelectedImagesContainer =  " + mSelectedImagesContainer);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

        }
    }

    @SuppressLint("LongLogTag")
    private void showEditUriList(List<Uri> uriList)
    {
        // Remove all views before
        // adding the new ones.
        mSelectedImagesContainer.removeAllViews();

//        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());


        for (Uri uri : uriList)
        {
            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            ImageView thumbnail = imageHolder.findViewById(R.id.media_image);

            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail);

            mSelectedImagesContainer.addView(imageHolder);

//            Log.e(TAG, "uri =  " + uri);
//            Log.e(TAG, "uriList =  " + uriList);

//            RequesturiList = uriList;
//
//            Log.e(TAG, "RequesturiList =  " + RequesturiList);
//
//            Log.e(TAG, "mSelectedImagesContainer =  " + mSelectedImagesContainer);

            thumbnail.setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));

        }
    }

    @Override
    protected void onDestroy()
    {
        if (singleImageDisposable != null && !singleImageDisposable.isDisposed())
        {
            singleImageDisposable.dispose();
        }
        if (multiImageDisposable != null && !multiImageDisposable.isDisposed())
        {
            multiImageDisposable.dispose();
        }
        super.onDestroy();
    }

    // 맨 위 툴바 뒤로가기 눌렀을 때 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    // 신규 업로드 AsyncTask
    private class WorkUpTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {

            // 로띠 Lottie 애니메이션
            LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.uploadWorkSpace_lottie); // 로띠 에니메이션 위치
            uploadWorkSpace_lottie.setAnimation("upload_loading.json");
            uploadWorkSpace_lottie.setRepeatCount(100);
            uploadWorkSpace_lottie.playAnimation();
            card_1.setVisibility(View.GONE);
            card_2.setVisibility(View.VISIBLE);

//            Toast.makeText(getApplicationContext(), "전송 중", Toast.LENGTH_SHORT).show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            uploadWork(IntentAddress1,
                    IntentAddress2,
                    IntentWorkSpaceName,
                    IntentWorkSpaceContact,
                    IntentWorkSpaceTableMax,
                    IntentWorkSpacePd,
                    IntentWorkSpacePw,
                    IntentWorkSpacePm,
                    getWorkSpaceIntroduce,
                    imagePathList,
                    getId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }

    // 수정 AsyncTask
    private class WorkEditTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute()
        {

//            // 로띠 Lottie 애니메이션
            LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.uploadWorkSpace_lottie); // 로띠 에니메이션 위치
            uploadWorkSpace_lottie.setAnimation("upload_loading.json");
            uploadWorkSpace_lottie.setRepeatCount(100);
            uploadWorkSpace_lottie.playAnimation();
            card_1.setVisibility(View.GONE);
            card_2.setVisibility(View.VISIBLE);

//            Toast.makeText(getApplicationContext(), "수정 중", Toast.LENGTH_SHORT).show();

            super.onPreExecute();
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... voids)
        {
            EditWork(IntentAddress1,
                    IntentAddress2,
                    IntentWorkSpaceName,
                    IntentWorkSpaceContact,
                    IntentWorkSpaceTableMax, // 이용중인 인원보다 낮게 수정할 수 없게 제한할 방법 생각해봅시다.
                    IntentWorkSpacePd,
                    IntentWorkSpacePw,
                    IntentWorkSpacePm,
                    getWorkSpaceIntroduce,
                    URLPathList,
                    imagePathList,
                    Work_Edit_WorkDBIndex,
                    Work_Edit_getId);

            Log.e(TAG, "doInBackground: IntentAddress1: " + IntentAddress1);
            Log.e(TAG, "doInBackground: IntentAddress2: " + IntentAddress2);
            Log.e(TAG, "doInBackground: IntentWorkSpaceName: " + IntentWorkSpaceName);
            Log.e(TAG, "doInBackground: IntentWorkSpaceContact: " + IntentWorkSpaceContact);
            Log.e(TAG, "doInBackground: IntentWorkSpaceTableMax: " + IntentWorkSpaceTableMax);
            Log.e(TAG, "doInBackground: IntentWorkSpacePd: " + IntentWorkSpacePd);
            Log.e(TAG, "doInBackground: IntentWorkSpacePw: " + IntentWorkSpacePw);
            Log.e(TAG, "doInBackground: IntentWorkSpacePm: " + IntentWorkSpacePm);
            Log.e(TAG, "doInBackground: getWorkSpaceIntroduce: " + getWorkSpaceIntroduce);
            Log.e(TAG, "doInBackground: imagePathList: " + imagePathList);
            Log.e(TAG, "doInBackground: Work_Edit_WorkDBIndex: " + Work_Edit_WorkDBIndex);
            Log.e(TAG, "doInBackground: Work_Edit_getId: " + Work_Edit_getId);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
        }
    }
}

// 사무실 등록 결과 받기. (성공, 실패여부)
//                // 성공하면 홈 화면으로 이동한다.
//                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_WORK_SPACE_UPLOAD,
//                        new Response.Listener<String>()
//                        {
//                            @Override
//                            public void onResponse(String response)
//                            {
//                                Log.e(TAG, "response : " + response);
//
//                                try
//                                {
//                                    JSONObject jsonObject = new JSONObject(response);
//
//                                    // 키값 'request'받기
//                                    String request = jsonObject.getString("request");
//
//                                    // 키값 'request'안에 value가 1이면 성공, 1이 없으면 실패
//                                    if (request.equals("1")) // 성공
//                                    {
//                                        Toast.makeText(Activity_hostAddWorkSpace_2.this, "업로드 완료", Toast.LENGTH_SHORT).show();
//
//                                        // 전송 성공하면 이전 액티비티를 종료하고 이 액티비티도 종료한다.
//                                        Activity_hostAddWorkSpace activity_hostAddWorkSpace = (Activity_hostAddWorkSpace) Activity_hostAddWorkSpace.activity;
//
//                                        activity_hostAddWorkSpace.finish();
//
////                                        Intent intent = new Intent(Activity_hostAddWorkSpace_2.this, Activity_Home.class);
////                                        startActivity(intent);
//                                        finish();
//                                    } else if (request.equals("0")) // 실패
//                                    {
//                                        Toast.makeText(Activity_hostAddWorkSpace_2.this, "문제발생. 로그 확인.", Toast.LENGTH_SHORT).show();
//
//                                        card_1.setVisibility(View.VISIBLE);
//                                        card_2.setVisibility(View.GONE);
//                                    }
//
//                                } catch (JSONException e)
//                                {
//                                    e.printStackTrace();
//                                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "문제발생. 로그 확인.", Toast.LENGTH_SHORT).show();
//                                    Log.e(TAG, "response: " + e);
//
//                                    card_1.setVisibility(View.VISIBLE);
//                                    card_2.setVisibility(View.GONE);
//                                }
//                            }
//                        },
//                        new Response.ErrorListener()
//                        {
//                            @Override
//                            public void onErrorResponse(VolleyError error)
//                            {
//                                Toast.makeText(Activity_hostAddWorkSpace_2.this, "문제발생. 로그 확인.", Toast.LENGTH_SHORT).show();
//                                Log.e(TAG, "response: " + error);
//
//                                card_1.setVisibility(View.VISIBLE);
//                                card_2.setVisibility(View.GONE);
//                            }
//                        })
//                {
//                    @Override
//                    protected Map<String, String> getParams() throws AuthFailureError
//                    {
//                        Map<String, String> params = new HashMap<>();
//                        params.put("IntentAddress1", IntentAddress1);
//                        params.put("IntentAddress2", IntentAddress2);
//                        params.put("IntentWorkSpaceName", IntentWorkSpaceName);
//                        params.put("IntentWorkSpaceContact", IntentWorkSpaceContact);
//                        params.put("IntentWorkSpaceTableMax", IntentWorkSpaceTableMax);
//                        params.put("IntentWorkSpacePd", IntentWorkSpacePd);
//                        params.put("IntentWorkSpacePw", IntentWorkSpacePw);
//                        params.put("IntentWorkSpacePm", IntentWorkSpacePm);
//                        params.put("getWorkSpaceIntroduce", getWorkSpaceIntroduce);
//                        params.put("imageUriList", String.valueOf(imagePathList));
//                        params.put("HostId", HostId);
//
//                        return params;
//                    }
//                };
//
//                RequestQueue requestQueue = Volley.newRequestQueue(Activity_hostAddWorkSpace_2.this);
//                requestQueue.add(stringRequest);


// 레트로핏

//        // 파일 경로가 여기까지 잘 담겨 왔나?
//        Log.e(TAG, "uploadWork() imagePathList: " + imagePathList);
//
////        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("http://34.73.32.3/")
//                .addConverterFactory(GsonConverterFactory.create());
//
//        Retrofit retrofit = builder.build();
//
//        ApiInterface workUpdate = retrofit.create(ApiInterface.class);
//
//        List<MultipartBody.Part> parts = new ArrayList<>();
//
//        Call<ResponseBody> call = workUpdate.multipartUpload(
//                IntentAddress1,
//                IntentAddress2,
//                IntentWorkSpaceName,
//                IntentWorkSpaceContact,
//                IntentWorkSpaceTableMax,
//                IntentWorkSpacePd,
//                IntentWorkSpacePw,
//                IntentWorkSpacePm,
//                getWorkSpaceIntroduce,
//                imageFile,
//                HostId
//        );
//
//        call.enqueue(new Callback<ResponseBody>()
//        {
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
//            {
//                Log.e(TAG, "onResponse response: " + response);
//                Log.e(TAG, "onResponse response: " + response.body());
//                Log.e(TAG, "onResponse response: " + response.body().toString());
//                Log.e(TAG, "onResponse call: " + call);
//
//                Toast.makeText(Activity_hostAddWorkSpace_2.this, "전송완료\n홈 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
//
//                // 전송완료 후 메인 액티비티 이동하기
//                // 이동 전 약간의 딜레이 주기
//                Thread thread = new Thread()
//                {
//                    @Override
//                    public void run()
//                    {
//                        try // 아래 설정한 시간만큼 멈춘다
//                        {
//                            sleep(1000); // 3000 = 3초, 500 = 0.5초
//                        } catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        } finally
//                        {
//                            // 전송 성공하면 이전 액티비티를 종료한다.
//                            Activity_hostAddWorkSpace activity_hostAddWorkSpace = (Activity_hostAddWorkSpace) Activity_hostAddWorkSpace.activity;
//                            activity_hostAddWorkSpace.finish();
//
//                            // Intent intent = new Intent(Activity_hostAddWorkSpace_2.this, Activity_Home.class);
//                            // startActivity(intent);
//                            // 전송 성공하면 해당 액티비티도 함께 종료한다.
//                            finish();
//                        }
//                    }
//                };
//                thread.start();
//            }
//
//            @SuppressLint("LongLogTag")
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t)
//            {
//                Toast.makeText(Activity_hostAddWorkSpace_2.this, "전송실패", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, "Call<ResponseBody> call: " + call.toString());
//                Log.e(TAG, "error t: " + t.getMessage());
//
//                card_2.setVisibility(View.GONE);
//                card_1.setVisibility(View.VISIBLE);
//
//            }
//        });


// 레트로핏 필드를 이용해서 서버로 전송하기
// 이미지는 전송 못 함. 일단 백업
//    사무실 등록 요청하기
//    @SuppressLint("LongLogTag")
//    private void RequestAddWorkSpace()
//    {
//        // 사무실 소개 입력값 받기
//        WorkSpaceIntroduce = findViewById(R.id.work_space_introduce);
//
//        // 사무실 등록버튼, 입력완료 버튼
//        WorkSpaceWriteDone = findViewById(R.id.work_space_write_done);
//
//        WorkSpaceWriteDone.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                // 사무실 등록 양식 데이터 수신
//                Intent intent = getIntent();
//                IntentAddress1 = intent.getExtras().getString("IntentAddress1"); // 주소
//                IntentAddress2 = intent.getExtras().getString("IntentAddress2"); // 주소
//                IntentWorkSpaceName = intent.getExtras().getString("IntentWorkSpaceName"); // 사무실 이름
//                IntentWorkSpaceContact = intent.getExtras().getString("IntentWorkSpaceContact"); // 연락처
//                IntentWorkSpaceTableMax = intent.getExtras().getString("IntentWorkSpaceTable"); // 테이블
//                IntentWorkSpacePd = intent.getExtras().getString("IntentWorkSpacePd"); // 가격
//                IntentWorkSpacePw = intent.getExtras().getString("IntentWorkSpacePw"); // 가격
//                IntentWorkSpacePm = intent.getExtras().getString("IntentWorkSpacePm"); // 가격
//                getWorkSpaceIntroduce = WorkSpaceIntroduce.getText().toString(); // 사무실 소개
//                List<String> urilist = imagePathList;
//
////                imageUriList = selectedUriList;
//
////                imageUriList = new ArrayList<>();
////                int count = data.getClipData().getItemCount();
//
//                // selectedUriList // 사진
//                // 확인완료. 잘 넘어온다
//
////                if (TextUtils.isEmpty(getWorkSpaceIntroduce)) // 소개 영역 빈칸체크
////                {
////                    // 버튼 흔들기
////                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
////                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
////
////                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "양식을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
////                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
////                    return;
////                }
////
////                if (selectedUriList == null) // 사진 비어있는지 체크
////                {
////                    // 버튼 흔들기
////                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
////                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
////
////                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
////                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
////                    return;
////                }
////
////                if (selectedUriList.isEmpty()) // 사진 빈칸 체크. 사진 선택 후 다시 사진 선택 취소 했는데, 넘어가려 하지 않는지 체크.
////                {
////                    // 버튼 흔들기
////                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
////                    WorkSpaceWriteDone.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
////
////                    Toast.makeText(Activity_hostAddWorkSpace_2.this, "사진을 선택해주세요.", Toast.LENGTH_SHORT).show();
////                    WorkSpaceIntroduce.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
////                    return;
////                }
//
//                // 값이 잘 넘어오는지 로그로 확인하기
//                Log.e(TAG, "IntentAddress1: " + IntentAddress1);
//                Log.e(TAG, "IntentAddress2: " + IntentAddress2);
//                Log.e(TAG, "IntentWorkSpaceName: " + IntentWorkSpaceName);
//                Log.e(TAG, "IntentWorkSpaceContact: " + IntentWorkSpaceContact);
//                Log.e(TAG, "IntentWorkSpaceTableMax: " + IntentWorkSpaceTableMax);
//                Log.e(TAG, "IntentWorkSpacePd: " + IntentWorkSpacePd);
//                Log.e(TAG, "IntentWorkSpacePw: " + IntentWorkSpacePw);
//                Log.e(TAG, "IntentWorkSpacePm: " + IntentWorkSpacePm);
//                Log.e(TAG, "getWorkSpaceIntroduce: " + getWorkSpaceIntroduce);
//                Log.e(TAG, "urilist: " + urilist);
//                Log.e(TAG, "getId: " + getId);
//
//                Toast.makeText(getApplicationContext(), "전송 중", Toast.LENGTH_SHORT).show();
//
//                card_1.setVisibility(View.GONE);
//                card_2.setVisibility(View.VISIBLE);
//@
//                // 로띠 Lottie 애니메이션
//                LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.uploadWorkSpace_lottie); // 로띠 에니메이션 위치
//                uploadWorkSpace_lottie.setAnimation("upload_loading.json");
//                uploadWorkSpace_lottie.setRepeatCount(100);
//                uploadWorkSpace_lottie.playAnimation();
//
//                for (int i = 0; i < imagePathList.size(); i++)
//                {
//                    imageFile = new File(imagePathList.get(i));/*imagePathList.get(i)*/
//                    Log.e(TAG, "imageFile: " + imageFile);
//                    Log.e(TAG, "imagePathList.get(i): " + imagePathList.get(i));
////            builder.addFormDataPart("ImageUri", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
////             builder.addFormDataPart("ImageUri[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
//                }
//
//                uploadWork(IntentAddress1,
//                        IntentAddress2,
//                        IntentWorkSpaceName,
//                        IntentWorkSpaceContact,
//                        IntentWorkSpaceTableMax,
//                        IntentWorkSpacePd,
//                        IntentWorkSpacePw,
//                        IntentWorkSpacePm,
//                        getWorkSpaceIntroduce,
//                        imageFile,
//                        getId);
//            }
//        });
//    }

//            new Compressor(this) // 이미지를 가로세로 200 사이즈로 압축 (필셀인가?)
//                    .setMaxWidth(200)
//                    .setMaxHeight(200)
//                    .setQuality(50) // 품질은 50프로만
//                    .compressToFile(imageFile.get(i)); // thumb_filePath_Uri에 담긴 uri 경로의 사진을 압축한다.