package com.example.hun73.seac_apply_ver2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.Service.MyService;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.hun73.seac_apply_ver2.homeNavigationPager.Fragment_Home_Menu_1.slide_profile_image;

// 마이페이지.
public class Activity_MyPage extends AppCompatActivity
{
    // 로그 태그
    private static final String TAG = Activity_MyPage.class.getSimpleName();

    // View Id 지정

    // 텍스트 뷰
    private TextView
            name, // 이름
            email, // 이메일
            type; // 회원유형

    // 버튼
    private Button
            button_logout, // 로그아웃
            button_photo_upload, // 프로필사진 업로드
            button_host_register, // 호스트 등록
            button_host_add_space; // 작업공간 등록

    // 세션 선언.
    // 회원정보 변경 또는 로그아웃
    SessionManager sessionManager;

    // 세션으로 불러온 유저의 '유저 번호' 불러오기
    String getId;

    // 세션에서 프로필 이미지 불러오기
    String getPhoto;

    // 요청 전송할 서버 주소 gcp
//    private static String URL_READ = "http://34.73.32.3/read_detail.php"; // 유저 정보 불러오기
//    private static String URL_EDIT = "http://34.73.32.3/edit_detail.php"; // 유저 정보 수정하기
//    private static String URL_UPLOAD = "http://34.73.32.3/profile_image_upload.php"; // 프로필사진

    // 요청 전송할 서버 주소 iwinv
    private static String URL_READ = "http://115.68.231.84/read_detail.php"; // 유저 정보 불러오기
    private static String URL_EDIT = "http://115.68.231.84/edit_detail.php"; // 유저 정보 수정하기
    private static String URL_UPLOAD = "http://115.68.231.84/profile_image_upload.php"; // 프로필사진

    // 타이틀바에 프로필 수정 및 저장버튼 활성화.
    private Menu action;

    // 프로필 사진을 저장할 비트맵
    private Bitmap bitmap;

    // 프로필 사진을 표시할 서클 이미지뷰
    CircleImageView profile_image;

    // 이메일 중복될 시 원래 이메일로 되돌리기
    private String strEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__mypage);

        //actionBar 객체를 가져올 수 있다.
        ActionBar actionBar = getSupportActionBar();

        // 메뉴바에 '<-' 버튼이 생긴다.(두개는 항상 같이다닌다)
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // 텍트스뷰
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        profile_image = findViewById(R.id.profile_image);
        type = findViewById(R.id.user_type);

        // 버튼
        button_photo_upload = findViewById(R.id.button_photo);
        button_logout = findViewById(R.id.button_logout);
        button_host_register = findViewById(R.id.button_activity_host_register);
        button_host_add_space = findViewById(R.id.button_activity_host_add_work_space);

        // 세션생성
        sessionManager = new SessionManager(this);

        // 로그인 체크 메소드.
        // isLogin() 메소드의 값이 false면 (true가 아니면)
        // 로그인 액티비티로 이동하기
        sessionManager.checkLogin();

        // 로그인 한 유저의 '정보 불러오기'메소드 실행하기.
        getUserDetail();

        // Map에 저장된 유저 정보 불러오기 (이름, 이메일, 유저번호)
        HashMap<String, String> user = sessionManager.getUserDetail();

        // 불러온 유저의 정보 중 '유저 번호'를 아래 getId에 담기
        getId = user.get(sessionManager.ID);

        // 로그아웃 버튼 (세션 초기화 버튼)
        button_logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // todo: 서비스 중지하기
                Intent stopService = new Intent(Activity_MyPage.this, MyService.class);
                stopService(stopService);

                // 로그아웃 하면 홈 화면도 함께 종료하기
                Activity_Home activity_home = (Activity_Home) Activity_Home.Activity_Home;
                activity_home.finish();

                // 세션에 담긴 값을 초기화 한다.
                // 초기화 후 로그인 페이지로 이동한다.
                sessionManager.logout();
            }
        });

        // 프로필 사진 업로드 프로세스를 실행한다.
        button_photo_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 아래 chooseFile() 메소드를 실행한다.
                chooseFile();
            }
        });
    }

    // 유저 상세정보 불러오기 (이름, 이메일)
    private void getUserDetail()
    {
        // 프로그레스 다이얼로그 활성화.
        // 유저 정보를 서버에서 요청받는 동안 해당 다이얼로그 활성화.
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("회원정보 불러오는 중..");
        progressDialog.show();

        // Volley로 서버 요청을 보내기 위해 데이터 세팅.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>()
                {
                    // JSON 형태로 결과 요청을 받는다.
                    // 요청받은 결과가 response에 저장된다.
                    @Override
                    public void onResponse(String response)
                    {
                        // 요청 응답 받음.
                        // 다이얼로그 비활성.
                        progressDialog.dismiss();
                        Log.i(TAG, response.toString());

                        try
                        {
                            // JSON 형식으로 응답받음.
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e(TAG, "getUserDetail: response = " + response);

                            // 전달받은 json에서 success를 받는다.
                            // {"success":"??"}
                            String success = jsonObject.getString("success");

                            // 키값이 read인 json에 담긴 value들을 배열에 담는다.
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            // success의 값이 아래와 같으면 아래 반복문을 진행한다
                            // {"success":"??"} = ??에 담긴 값이 1일 때 아래 반복문을 진행
                            if (success.equals("1"))
                            {
                                // 키값이 read인 배열에 담긴 값들을 모두 불러온다.
                                // 불러올 값 = 이름, 이메일, 프로필 이미지
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    Log.e(TAG, "onResponse: JSONObject object = " + object.length());

                                    String strName = object.getString("name").trim();
                                    strEmail = object.getString("email").trim();
                                    String strType = object.getString("type").trim();
                                    Log.e(TAG, "onResponse: strType = " + strType);
                                    // 프로필 페이지에서 이름과 이메일 영역에
                                    // 로그인 중인 유저의 이름, 이메일, 프로필 사진을 출력한다.
                                    name.setText(strName);
                                    email.setText(strEmail);

                                    // 회원 유형 출력하기
                                    if (strType.equals("1")) // 일반회원
                                    {
                                        // 일반회원 = 크리에이터
                                        type.setText("크리에이터 회원");

                                        // 호스트가 아님.
                                        // 호스트 신청버튼 활성화.
                                        button_host_register.setVisibility(View.VISIBLE);

                                        // 호스트 신청버튼
                                        button_host_register.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Intent intent = new Intent(Activity_MyPage.this, Activity_hostRegist.class);
                                                startActivity(intent);
                                            }
                                        });

                                        // 공간등록 버튼 비활성화
                                        button_host_add_space.setVisibility(View.GONE);
                                    } else if (strType.equals("2")) // 호스트
                                    {
                                        type.setText("호스트 회원");

                                        // 호스트 회원임.
                                        // 호스트 신청버튼 비활성화.
                                        button_host_register.setVisibility(View.GONE);

                                        // 공간등록 버튼 활성화
                                        button_host_add_space.setVisibility(View.GONE); // 홈 액티비티로 옮겼음 현재 필요 업음
                                    } else if (strType.equals("0")) // 운영자
                                    {
                                        // 운영자 역할 안 정해짐.
                                        type.setText("운영자");

                                        // 둘 다 비활성화
                                        button_host_register.setVisibility(View.GONE);
                                        button_host_add_space.setVisibility(View.GONE);
                                    }

                                    HashMap<String, String> user = sessionManager.getUserDetail();
                                    getPhoto = user.get(sessionManager.PROFILEIMAGE);
//                                    Log.e(TAG, "onResponse: getPhoto = " + getPhoto);

                                    // 문자열을 비트맵으로 변환한다.
                                    Bitmap bitmap = StringToBitMap(getPhoto);
                                    profile_image.setImageBitmap(bitmap);

//                                    Picasso.get().
//                                            load(String.valueOf(bitmap)).
//                                            memoryPolicy(MemoryPolicy.NO_CACHE).
//                                            placeholder(R.drawable.logo_2).
//                                            networkPolicy(NetworkPolicy.NO_CACHE).
//                                            into(profile_image);

                                    if (TextUtils.isEmpty(getPhoto))
                                    {
                                        // toastCustom_loadPhoto(); // 사진 로딩알림 토스트

                                        String strImage = object.getString("image").trim();

                                        // 서버 URL로 불러온 이미지를 세팅한다.
                                        Picasso.get().load(strImage).
                                                memoryPolicy(MemoryPolicy.NO_CACHE).
                                                placeholder(R.drawable.logo_2).
                                                networkPolicy(NetworkPolicy.NO_CACHE).
                                                into(profile_image);

                                        // 이미지 다운로드 클래스.
                                        // 서버로 불러온 URL 주소의 이미지를 다운로드 한다. 이미지뷰에 세팅한다.
                                        // 다운로드 한 이미지를 비트맵에 담아서 문자열 변환 후 세션에 저장한다.
//                                        DownloadImageTask downloadImageTask
//                                                = (DownloadImageTask)
//                                                new DownloadImageTask(
//                                                        (ImageView) findViewById(R.id.profile_image)) // URL의 이미지 세팅하기
//                                                        .execute(strImage); // 이미지 URL

//                                        Log.e(TAG, "onResponse: downloadImageTask = " + downloadImageTask);
//                                        sessionManager.createProfileImageSession(strImage);
                                    }
                                    // 토스트로 사진 로딩중 알림
//                                    toastCustom_loadPhoto();
                                    // Picasso.get().load(strImage).into(profile_image);
                                }
                            }
                        } // try에 포함된 로직 중 틀린 코드가 있으면 예외상황으로 간주함.
                        catch (JSONException e) // 에러 알림
                        {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(Activity_MyPage.this, "에러발생!" + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: getUserDetail JSONException e = " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener() // 응답 실패할 시 에러 알림
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        progressDialog.dismiss();
//                        Toast.makeText(Activity_MyPage.this, "에러발생!" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: getUserDetail VolleyError = " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                // 로그인 한 유저의 '유저 번호'를 해쉬맵에 담는다.
                params.put("id", getId);

                return params;
            }
        };

        // stringRequest에서 지정한 서버 주소로 POST를 전송한다.
        // 위에 프로세스가 requestQueue에 담으면 실행됨.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    // url의 이미지를 다운로드 받는다.
//    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
//    {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage)
//        {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls)
//        {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try
//            {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e)
//            {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result)
//        {
//            Log.e(TAG, "onPostExecute: getPhoto download_Bitmap result = " + result);
//            // 다운로드 받은 비트맵을 문자열로 변환 후 이미지 세션에 전환한다.
//
//            if (result == null)
//            {
//                // result가 비어있으면 아무것도 하지않음
//            } else
//            {
//                getStringImage(result);
//
//                bmImage.setImageBitmap(result);
//            }
//        }
//    }

    // 문자열을 비트맵으로 변환하기.
    public Bitmap StringToBitMap(String encodedBitMapString)
    {
        try
        {

            byte[] encodeByte = Base64.decode(encodedBitMapString, Base64.DEFAULT);

            Bitmap profileBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            return profileBitmap;

        } catch (Exception e)
        {
            e.getMessage();
            return null;
        }

    }

    // 사진 로딩중 알림 띄우기 (토스트 메시지 커스텀)
    private void toastCustom_loadPhoto()
    {
        LayoutInflater inflater = getLayoutInflater();

        //toast_design.xml 파일의 toast_design_root 속성을 로드
        View toastDesign = inflater.inflate(R.layout.toast_design_photo_load, (ViewGroup) findViewById(R.id.toast_design_root));


        TextView text = toastDesign.findViewById(R.id.TextView_toast_design);
        text.setText("사진 로딩 중"); // toast_design.xml 파일에서 직접 텍스트를 지정 가능

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -330); // CENTER를 기준으로 0, 0 위치에 메시지 출력
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastDesign);
        toast.show();
    }

    // 액티비티 시작할 때 로그인중인 유저의 정보 불러오기
    // 액티비티 시작할 때 세션에 담긴 유저의 정보를 불러오기
    // 액티비티 시작할 때 세션에 담긴 유저의 '정보 불러오기'메소드를 실행한다.
    // 해당 메소드 바로 위에 있음
    @Override
    protected void onResume()
    {
        super.onResume();
        // 로그인 한 유저의 '정보 불러오기'메소드 실행하기.
        // getUserDetail();
        // 현재 onResume 에서 사용 중단.
        // 사진 갱신이 바로바로 안 되는 문제가 있음.
        // onActivity로 이동 후 갱신문제 해결.
        // 당분간 지켜보고 문제 없으면 onResume()에 포함된 getUserDetail() 삭제할 예정
    }

    // 옵션메뉴 불러오기
    // 메뉴 버튼 레이아웃을 따로 만들었음.
    // 그 버튼 레이아웃을 해당 액팁티에 세팅할것임.
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflater = 팽창!! 아, 뭐라고 해석해야될 지 모르겠다.
        MenuInflater menuInflater = getMenuInflater();

        // 만들어둔 메뉴 레이아웃 불러오기
        menuInflater.inflate(R.menu.menu_action, menu);

        // 전역변수에 선언한 메뉴 변수에 해당 메소드에서 설정한 값들을 담는다.
        action = menu;

        // 저장 버튼을 비활성화.
        // 수정 버튼을 누른 후 활성화 된다.
        // 수정 버튼 눌러서 활성화 후 저장 버튼을 누른다.
        // 저장 버튼을 누르면 수정한 유저 정보를 서버로 전송한다.
        action.findItem(R.id.menu_save).setVisible(false);

        return true;
    }

    // 옵션 메뉴 작동순서 설정하기
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // 메뉴에 아이템이 감지되면 아래 스위치문 활성화.
        switch (item.getItemId())
        {
            case R.id.menu_edit: // 수정버튼 누르면 아래 로직 활성화

                // 수정 버튼을 누르면 이름과 이메일 입력칸에 입력모드 활성화
                name.setFocusableInTouchMode(true);
                email.setFocusableInTouchMode(true);

                // 수정 버튼을 누르면 키보드 활성화
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(name, InputMethodManager.SHOW_IMPLICIT);

                // 수정버튼 누르면 수정버튼을 비활성화 한다.
                action.findItem(R.id.menu_edit).setVisible(false);

                // 수정버튼 누르면 저장버튼을 활성화 한다.
                action.findItem(R.id.menu_save).setVisible(true);

                return true;

            case R.id.menu_save: // 저장버튼 누르면 아래 로직 활성화

                // 저장버튼 누르면 바로아래 유저정보 수정하기 메소드 활성화
                SaveEditDetail();

                // 수정버튼을 다시 활성화
                action.findItem(R.id.menu_edit).setVisible(true);

                // 저장버튼을 비활성화
                action.findItem(R.id.menu_save).setVisible(false);

                // 입력 키보드 비활성화
                name.setFocusableInTouchMode(false);
                email.setFocusableInTouchMode(false);
                name.setFocusable(false);
                email.setFocusable(false);

                return true;

            // 맨 위 툴바 뒤로가기 눌렀을 때 동작
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
            {
//                Intent intent = new Intent(this, Activity_Home.class);
//                startActivity(intent);
                finish();
                return true;
            }

            default:

                return super.onOptionsItemSelected(item);
        }
    }

    // 유저정보 수정 후 저장하기 메소드.
    // 유저 정보 수정 후 저장버튼 누르기. (이름, 이메일)
    // 저장버튼 누르면 수정한 정보를 서버로 전달해서 UPDATE 쿼리 실행 요청하기
    // UPDATE 쿼리를 이용해서 해당 유저의 정보를 새로 입력한 값으로 갱신하기
    // 갱신된 유저 정보는 다시 getUserDetail 메소드에서 불러옴
    private void SaveEditDetail()
    {
        // 수정한 입력값 받아오기 (이름, 이메일)
        final String nameResponse = this.name.getText().toString().trim();
        final String emailResponse = this.email.getText().toString().trim();

        // 빈 칸을 모두 채우면 요청 전송하기
        if (!TextUtils.isEmpty(nameResponse) && !TextUtils.isEmpty(emailResponse))
        {
            // 유저의 '유저 번호'불러오기
            final String id = getId;

            // 서버로 요청하는 동안 다이얼로그 활성화
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("갱신 중...");
//        progressDialog.show();
//            Toast.makeText(this, "갱신 중...", Toast.LENGTH_SHORT).show();

            // 볼리를 이용해서 서버로 수정 요청하기
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_EDIT,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            // 요청결과 응답받기
                            // 응답받은 후 다이얼로그 종료하기.
                            // 다이얼로그 사용을 잠시 중단한다. 업로드 시각효과 사용을 중단하고 토스트로 대체한다.
//                        progressDialog.dismiss();
                            Log.e(TAG, "onResponse: response = " + response);

                            try
                            {
                                // json으로 응답받은 결과를 열어보자.
                                JSONObject jsonObject = new JSONObject(response);

                                // 계정이 중복되면 서버에서 match를 반환한다
                                // json 결과에 포함된 결과중 키값이 success인 결과를 문자열에 담는다.
                                String success = jsonObject.getString("success");

                                // 중복결과 알리고 가입 중지
                                if (success.equals("3"))
                                {
                                    Toast.makeText(Activity_MyPage.this, "이미 사용중인 이메일입니다. ", Toast.LENGTH_SHORT).show();
                                    android.view.animation.Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha); // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                                    email.startAnimation(anim);  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기
                                    email.setText(null); // 입력값 비우고
                                    email.setText(strEmail); // 원래 이메일로 되돌리기
//                                    return;
                                } else if (success.equals("1")) // success의 value가 1이면 아래 조건문을 실행한다.
                                {
                                    // 결과 화면에 띄워서 알림
                                    Toast.makeText(Activity_MyPage.this, "수정완료!", Toast.LENGTH_SHORT).show();

                                    // 수정된 유저의 정보로 세션을 다시 생성한다.
                                    sessionManager.createSession(nameResponse, emailResponse, id);
                                }
                            } catch (JSONException e) // 수정 실패알림
                            {
                                e.printStackTrace();
                                // 다이얼로그 종료
                                // 다이얼로그 사용을 잠시 중단한다. 자세한 내용은 onResponse바로 아래 주석 참고
//                            progressDialog.dismiss();

                                // 실패원인 기록
                                Toast.makeText(Activity_MyPage.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "onResponse : JSONException = " + e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() // 서버 응답 실패처리
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            // 다이얼로그 종료
                            // 다이얼로그 사용을 잠시 중단한다. 자세한 내용은 onResponse바로 아래 주석 참고
//                        progressDialog.dismiss();

                            // 실패원인 기록
                            Toast.makeText(Activity_MyPage.this, "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse : VolleyError = " + error.toString());
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError
                {
                    // 요청 결과를 Map에 담는다.
                    // map의 이름은 params
                    // put으로 param에 포함시킬 key와 value 생성.
                    Map<String, String> params = new HashMap<>();
                    params.put("name", nameResponse);
                    params.put("email", emailResponse);
                    params.put("id", id);
                    return params;
                }
            };

            // stringRequest에서 설정한 메소드와 요청 주소로 요청 시작!
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } else
        {
            // 이름 빈 칸 체크
            if (TextUtils.isEmpty(nameResponse)) // 이메일 입력칸이 비어있으면 경고하기
            {
                Toast.makeText(Activity_MyPage.this, "이름을 입력하세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                name.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // 포커스 하러 돌아가! return 안하면 제기능 못 함
            }

            // 이메일 빈 칸 체크
            if (TextUtils.isEmpty(emailResponse)) // 패스워드 입력칸이 비어있으면 경고하기
            {
                Toast.makeText(Activity_MyPage.this, "이메일을 입력하세요!", Toast.LENGTH_SHORT).show(); // 토스트 메시지를 띄우고
                email.requestFocus(); // 해당 EditText로 커서를 포커스 한다.
                return; // return 안하면 제기능 못 함
            }
        }
    }

    // 사진 선택
    private void chooseFile()
    {
        // 디바이스에 저장된 모든 이미지를 선택할 수 있는 액티비티로 이동한다.
//        Intent intent = new Intent();
//        intent.setType("image/*"); // '*' = '모든'이라는 의미. 디바이스에 저장된 모든 이미지
//        intent.setAction(Intent.ACTION_GET_CONTENT); // 이미지 선택하러 가라.
//
//        // createChooser = 다른 앱으로 사용자 보내기
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        // 프로필 사진 업로드 전 물어보기
        AlertDialog.Builder photo_upload_Dialog = new AlertDialog.Builder(Activity_MyPage.this);

        // 다이얼로그 세팅
        photo_upload_Dialog.setTitle("이미지 업데이트");
        photo_upload_Dialog
                .setMessage("새로운 이미지로 업데이트 합니다.\n진행 하시겠습니까?")
                .setCancelable(false) // 다이얼로그 실행되면 뒤로가기 비활성화
                .setPositiveButton("네",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                CropImage.activity() // 크롭하기 위한 이미지를 가져온다.
                                        .setGuidelines(CropImageView.Guidelines.ON) // 이미지를 크롭하기 위한 도구 ,Guidelines를
                                        .setAspectRatio(1, 1) // 수직, 수평 비율 설정 (1:1 비율로)
                                        .start(Activity_MyPage.this); // 실행한다.
                            }
                        })
                .setNegativeButton("아니요",
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel(); // 다이얼로그 닫기
                            }
                        });

        final AlertDialog dialog = photo_upload_Dialog.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() // 다이얼로그 색상 설정
        {
            @Override
            public void onShow(DialogInterface arg0)
            {
                // 아니오 버튼의 색상 Color.rgb(247, 202, 201) = 분홍색
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);

                // 네 버튼의 색상
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            }
        });
        dialog.show(); // 다이얼로그 실행
    }

    // 위 메소드에서 선택된 사진을 받는다.
    // '사진 선택' 작업에 대한 결과 받기
    // 바로 위에 메소드인 chooseFile()에서는 사진 선택 액팁티로 이동해서 사진을 선택한다.
    // 선택한 사진을 해당 메소드에서 받는다 onActivityResult();
    // 응답 코드는 '1'. 응답 코드를 입력하면 선택한 사진을 받는 작업을 수행할 수 있게 된다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 응답코드 입력 완료 && 응답결과 성공!
        //                      && 인텐트에 담긴 data = 사진 선택 여부가 null이면 안 됨.
        //                      && data에 사진 선택이 확인되면 getData()로 사진의 경로를 받아온다.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK && result != null) // requestCode == 1 && /*&& data.getData() != null*/
            {
                // 사진의 경로를 담는다
                Uri filePath = result.getUri();
                try
                {
                    // 바로 위 변수인 filePath에 담은 경로로 이미지를 가져온다.
                    // bitmap 변수에 담는다.
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                    // 이미지 세션
                    // 프로필 이미지 영역에 비트맵에 담은 이미지를 실행한다.
                    profile_image.setImageBitmap(bitmap);

                    // 홈화면의 프로필 사진 세팅하기
                    slide_profile_image.setImageBitmap(bitmap);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                // 아래 메소드를 실행한다.
                // '유저 번호'와 프로필 이미지를 문자열로 변환해서 아래 메소드로 전달한다.
                // bitmap을 getStringImage 메소드에 담아서 아래 메소드로 전달한다.
                UploadPicture(getId, getStringImage(bitmap));
            }
        }
    }

    // 서버로 사진 전송하기
    private void UploadPicture(final String id, final String photo)
    {
        // 다이얼로그 활성화.
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("갱신 중...");
//        progressDialog.show();

        // 서버로 요청할 메소드와 주소 담기
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) // 요청받은 json 결과를 response 에 담는다.
                    {
                        // 다이얼로그 종료
//                        progressDialog.dismiss();
                        Log.i(TAG, "onResponse: UploadPicture String response = " + response.toString());

                        try
                        {
                            // response에 담긴 결과를 json형식으로 인코딩하기
                            // json형식으로 인코딩하는 이유는?
                            // json에 담긴 값들을 key value로 인식하게 하기 위함.
                            JSONObject jsonObject = new JSONObject(response);

                            // json형식으로 인코딩 된 결과중 success인 키를 저장한다.
                            // 아 혹시.... 키가 아니라 키의 value를 저장하는 거 아닌가?
                            // 일단 그렇게 가정하고 해석해보자.
                            String success = jsonObject.getString("success");

                            // success의 value가 1이면 성공으로 간주하고 유저에게 알린다.
                            if (success.equals("1"))
                            {
                                Toast.makeText(Activity_MyPage.this, "등록완료", Toast.LENGTH_SHORT).show();
                            }

                            // 위에 내가 가정한 게 맞는 지 응용분과 상의해봤음.
                            // 맞다고 함!

                            //  + 응용 분의 TIP.
                            // 서버에서 응답오는 key를 모르는 경우가 있을 수 있다.
                            // 이럴 때 key를 추출하는 방법도 이씀.
                            // key를 추출하는 방법은 스스로 알아보세요!

                        } catch (JSONException e)
                        {
                            e.printStackTrace();

                            // 다이얼로그 종료
//                            progressDialog.dismiss();

                            // 오류 원인 기록
                            Toast.makeText(Activity_MyPage.this, "다시 시도해주세요", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onResponse: JSONException e = " + e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) // 응답이 안오면 실패로 간주.
                    {
                        // 다이얼로그 종료
//                        progressDialog.dismiss();

                        // 오류 원인 기록
//                        Toast.makeText(Activity_MyPage.this, "다시 시도해주세요" + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onResponse: UploadPicture VolleyError error = " + error.toString());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                // params에 id와 photo를 저장함.
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("photo", photo);

                return params;
            }
        };

        // stringRequest 에서 설정한 메소드와 서버 주소로 요청을 전송한다.
        // 서버 주소에 있는 php 파일을 실행한다.
        // 실행 결과를 전달받는다.
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // 비트맵을 문자열로 변환하는 메소드
    public String getStringImage(Bitmap bitmap)
    {
        // 바이트 배열 사용.
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // 비트맵을 변환한다. 원래 100%였던 것을 50%의 품질로. 그리고 바이트 배열화 시킨다.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);

        byte[] imageByteArray = byteArrayOutputStream.toByteArray();

        // 베이스 64? 뭔 소린지 모르겠다.
        // 위키를 참고했다. (https://ko.wikipedia.org/wiki/베이스64)
        // 64진법이라고 한다. 64진법으로 인코딩 하는건가?
        String encodedImage = Base64.encodeToString(imageByteArray, Base64.DEFAULT);

        // 세션 생성
        sessionManager.createProfileImageSession(encodedImage);

        // 인코딩 결과를 반환한다.
        return encodedImage;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
//        Intent intent = new Intent(this, Activity_Home.class);
//        startActivity(intent);
        finish();
    }
}
