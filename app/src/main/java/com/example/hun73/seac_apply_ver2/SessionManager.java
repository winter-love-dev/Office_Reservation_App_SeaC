package com.example.hun73.seac_apply_ver2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.hun73.seac_apply_ver2.Activity_Home;
import com.example.hun73.seac_apply_ver2.Activity_Login;
import com.example.hun73.seac_apply_ver2.Activity_MyPage;

import java.util.HashMap;

public class SessionManager
{
    // 쉐어드 선언
    SharedPreferences sharedPreferences;

    // 저장된 쉐어드 값을 불러와서 담을 editor 변수
    public SharedPreferences.Editor editor;

    // 기기의 자원사용 요청
    public Context context;

    // Shared의 모드를 PRIVATE_MODE로 설정함.
    // Shared의 용도 정리
    // 0: 읽기, 쓰기 가능
    int PRIVATE_MODE = 0;
    // MODE_WORLD_READABLE : 읽기 공유
    // MODE_WORLD_WRITEABLE : 쓰기 공유

    // shared로 저장할 키값 지정
    // 여러 메소드로 나뉘어야 하기 때문에 전역 변수로 설정한다
    private static final String PREF_NAME = "LOGIN"; // 쉐어드의 이름
    private static final String LOGIN = "Is_LOGIN"; // 로그인 여부 체크
    public static final String NAME = "NAME"; //
    public static final String EMAIL = "EMAIL";
    public static final String ID = "ID";
    public static final String PROFILEIMAGE = "PROFILEIMAGE"; // 프로필 이미지


    // 세션에 값을 담기 위해선 기기에 자원요청(context)이 필요함.
    public SessionManager(Context context)
    {
        // 전역 변수에 설정해둔 context변수에 세션의 context를 담아서 자원사용을 허용한다.
        this.context = context;

        // "LOGIN" 쉐어드에 저장된 값을 읽기, 쓰기 모드로 불러오기 (0)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        // "LOGIN" 쉐어드에 담긴 값을 편집한다.
        // 편집할 내용은 아래 메소드 createSession에서 진행함.
        editor = sharedPreferences.edit();
    }

    // 세션 생성하기.
    // 이름, 이메일, 유저번호는 로그인, 홈 액티비티에서 생성한다.
    // 홈 액티비티의 경우, 유저 정보를 수정후 그 결과를 다시
    // 저장해야 하기 때문에 해당 메소드를 한 번 더 선언하게 되는 것임.
    public void createSession(String name, String email, String id)
    {
        // "Is_LOGIN" : true = default가 false인 값을 true로 바꾼다.
        editor.putBoolean(LOGIN, true);
        editor.putString(NAME, name); //  "NAME" : 이름
        editor.putString(EMAIL, email); // "EMAIL" : 이메일
        editor.putString(ID, id); // "ID" : 유저 번호

        editor.apply(); // 위에 값들을 저장한다.
    }

    // 프로필 이미지 쉐어드
    public void createProfileImageSession(String profileImage)
    {
        editor.putString(PROFILEIMAGE, profileImage);

        editor.commit(); // 위에 값을 저장한다.
    }

    // "Is_LOGIN"에 감긴 기본값은 false이며 아무것도 들어있지 않음을 의미함, null임을 의미함.
    public boolean isLogin()
    {
        return sharedPreferences.getBoolean(LOGIN, false);
    }


    // 로그인 여부 확인 = checkLogin()
    // 앱을 실행하면 인트로 액티비티를 지나서 홈 액티비티로 이동한다.
    // 만약 홈 액티비티에서 로그인 유저가 감지되지 않으면 로그인 액티비티로 이동한다.

    // isLogin() 메소드의 상태가 false & true인지 확인한다.
    public void checkLogin()
    {
        // isLogin() 메소드가 비어있으면 (true가 아니면)
        // 로그인 액티비티로 이동하기
        if (!this.isLogin())
        {
            // 로그인 액티비티로 이동!
            Intent i = new Intent(context, Activity_Login.class);

            // 인텐트를 실행하기 위해 context권한이 필요함.
            context.startActivity(i);

            // 홈 액티비티 종료
            ((Activity_Home) context).finish();
        }
    }

    // 쉐어드에 담은 유저 정보를 맵으로 저장한다.
    public HashMap<String, String> getUserDetail()
    {
        // 로그인 한 유저의 이름, 이메일, 유저번호를 쉐어드에서 불러온다.
        // 불러온 결과를 각각 Map에 저장한다.
        HashMap<String, String> user = new HashMap<>();
        user.put(NAME, sharedPreferences.getString(NAME, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(ID, sharedPreferences.getString(ID, null));
        user.put(PROFILEIMAGE, sharedPreferences.getString(PROFILEIMAGE, null) );
        return user;
    }

    // 로그아웃 후 세션 삭제
    public void logout()
    {
        // 쉐어드에 담긴 유저정보 모두 초기화
        editor.clear();

        // 초기화 결과 저장하기
        editor.commit();
        // commit과 apply의 차이.. 문서를 봐도 잘 모르겠다.
        // http://sukjinni.blogspot.com/2015/09/sharedpreferences-commit-apply.html
        // 둘의 차이에 대한 설명을 링크로 남김.
        // 나중에 다시 보면 이해 될까.

        // 로그인 중인 유저의 정보를 초기화 후 로그인 액티비티로 이동한다.
        Intent i = new Intent(context, Activity_Login.class);
        context.startActivity(i);
        ((Activity_MyPage) context).finish();
    }
}
