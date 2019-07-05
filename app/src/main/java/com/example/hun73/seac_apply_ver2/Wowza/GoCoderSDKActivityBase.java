/**
 * This is sample code provided by Wowza Media Systems, LLC.  All sample code is intended to be a reference for the
 * purpose of educating developers, and is not intended to be used in any production environment.
 * <p>
 * IN NO EVENT SHALL WOWZA MEDIA SYSTEMS, LLC BE LIABLE TO YOU OR ANY PARTY FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION,
 * EVEN IF WOWZA MEDIA SYSTEMS, LLC HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <p>
 * WOWZA MEDIA SYSTEMS, LLC SPECIFICALLY DISCLAIMS ANY WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. ALL CODE PROVIDED HEREUNDER IS PROVIDED "AS IS".
 * WOWZA MEDIA SYSTEMS, LLC HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
 * <p>
 * © 2015 – 2018 Wowza Media Systems, LLC. All rights reserved.
 */

package com.example.hun73.seac_apply_ver2.Wowza;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.Wowza.config.GoCoderSDKPrefs;
import com.example.hun73.seac_apply_ver2.Wowza.ui.AboutFragment;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastAPI;
import com.wowza.gocoder.sdk.api.broadcast.WOWZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.data.WOWZDataMap;
import com.wowza.gocoder.sdk.api.errors.WOWZError;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.monitor.WOWZStreamingStat;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;
import com.wowza.gocoder.sdk.api.status.WOWZStatusCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.example.hun73.seac_apply_ver2.Wowza.Activity_Broadcast_Setting.broadcast_title;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyId;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyName;

public abstract class GoCoderSDKActivityBase extends Activity
        implements WOWZStatusCallback
{

    private final static String TAG = GoCoderSDKActivityBase.class.getSimpleName();

    // todo: 앱 라이센스 키는 스트리밍엔진 무료 라이센스와 별개임. 스트리밍엔진 키 적용하면 안 됨
//    private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-5442-0101-750D-4A14-FB5C";
//    private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-A144-010C-9E08-5FE6-6AA7";
    private static final String SDK_SAMPLE_APP_LICENSE_KEY = "GOSK-9A46-010C-59DC-CECF-EDBB";

    // 권한 요청 코드
    private static final int PERMISSIONS_REQUEST_CODE = 0x1;

    // 권한 요청 목록
    protected String[] mRequiredPermissions = {};

    // 방송 잠금? 방송 시작?
    private static Object sBroadcastLock = new Object();

    // 방송 종료하기
    private static boolean sBroadcastEnded = true;

    // indicates whether this is a full screen activity or note / 구글 번역: 이것이 전체 화면 액티비티인지 또는 노트인지를 나타냅니다.
    // 카메라 화면으로 액티비티 꽉 채우기
    protected static boolean sFullScreenActivity = true;

    // GoCoder SDK top level interface
    // 구글 번역: GoCoder SDK 최상위 인터페이스
    protected static WowzaGoCoder sGoCoderSDK = null;

    // 부여된 사용 권한 혹은 승인된 사용 권한
    protected boolean mPermissionsGranted = false;

    // 권한이 요청되었습니다.
    private boolean hasRequestedPermissions = false;

    // 와우자 방송
    protected WOWZBroadcast mWZBroadcast = null;

    //네트워크 로그 수준?
    protected int mWZNetworkLogLevel = WOWZLog.LOG_LEVEL_DEBUG;

    // 방송 얻기?
    public WOWZBroadcast getBroadcast()
    {
        return mWZBroadcast;
    }

    // goCoder SDK 쉐어드인 듯?
    protected GoCoderSDKPrefs mGoCoderSDKPrefs;

    // 카메라 인터페이스
    private CameraActivityBase.PermissionCallbackInterface callbackFunction = null;

    // 방송 구조?
    protected WOWZBroadcastConfig mWZBroadcastConfig = null;

    // 방송 구조 얻기?
    public WOWZBroadcastConfig getBroadcastConfig()
    {
        return mWZBroadcastConfig;
    }

    // 방송정보 업로드 할 주소
    private String URL_LOGIN = "http://115.68.231.84/BroadCast_start.php";

    // 방송 방 번호
    public static String Wowza_Host_Rommno;

    // todo: TCP 서버 접속 준비
//    Socket socket;
    CameraActivity.SocketClient client;

//     접속할 서버 와이파이 설정
//    String ipad2 = "192.168.0.16"; // 8사
//    int port = 12346;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "onCreate: 플레이어 액티비티 실행됨");

        // 라이센스 인증하기
        if (sGoCoderSDK == null)
        {
            // Enable detailed logging from the GoCoder SDK

            // 와우자 접속 상태로 전환한다.
            WOWZLog.LOGGING_ENABLED = true;

            // Initialize the GoCoder SDK
            // goCoderSDK 초기화
            // 접속자의 라이센스키를 입력해서 인증하기
            sGoCoderSDK = WowzaGoCoder.init(this, SDK_SAMPLE_APP_LICENSE_KEY);

            if (sGoCoderSDK == null)
            {
                // 사용자 인증 안 될 시 오류 알림
                Log.e(TAG, "onCreate: 라이센스 인증 안 됨");
                WOWZLog.error(TAG, WowzaGoCoder.getLastError());
            } else
            {
                Log.e(TAG, "onCreate: 라이센스 인증 완료");
            }
        }


        // 로그인이 완료 되었음
        if (sGoCoderSDK != null)
        {
            Log.e(TAG, "onCreate: 설정 불러오기(접속할 ip 주소 등의 정보)");
            // Create a new instance of the preferences mgr
            // 환경 설정 mgr의 새 인스턴스 만들기
            // mgr이 뭐지...?
            // 아마 이 부분은 설정을 저장하는 부분인 듯 ( 접속할 ip 주소 등등 )
            mGoCoderSDKPrefs = new GoCoderSDKPrefs();

            // Create an instance for the broadcast configuration
            // 방송 구성을위한 인스턴스 만들기
            mWZBroadcastConfig = new WOWZBroadcastConfig(WOWZMediaConfig.FRAME_SIZE_1280x720);

            // Create a broadcaster instance
            // 브로드 캐스터 인스턴스 만들기
            mWZBroadcast = new WOWZBroadcast();
            mWZBroadcast.setLogLevel(WOWZLog.LOG_LEVEL_DEBUG);
        }

    }

    // 해당 기기에서 카메라 액티비티를 사용하기 위한 권한 요청하기
    protected void hasDevicePermissionToAccess(CameraActivityBase.PermissionCallbackInterface callback)
    {
        Log.e(TAG, "hasDevicePermissionToAccess: 메소드 실행. 해당 기기에서 카메라 액티비티를 사용하기 위한 권한 요청하기");

        // 콜백함순
        this.callbackFunction = callback;

        // 브로드캐스트 인스턴스가 만들어 졌다면
        if (mWZBroadcast != null)
        {
            boolean result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {

                result = (mRequiredPermissions.length > 0 ? WowzaGoCoder.hasPermissions(this, mRequiredPermissions) : true);

                // 퍼미션 허가가 되지 않았다면 다시 요청하기
                if (!result && !hasRequestedPermissions)
                {
                    Log.e(TAG, "hasDevicePermissionToAccess: 퍼미션 허가가 되지 않았다면 다시 요청하기");
                    ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
                    hasRequestedPermissions = true;
                } else
                {
                    Log.e(TAG, "hasDevicePermissionToAccess: 퍼미션 허가 완료");
                    this.callbackFunction.onPermissionResult(result);
                }
            } else
            {
                Log.e(TAG, "hasDevicePermissionToAccess: 퍼미션 허가 완료");
                this.callbackFunction.onPermissionResult(result);
            }
        }
    }


    // 액세스 권한이있는 장치
    protected boolean hasDevicePermissionToAccess(String source)
    {
        Log.e(TAG, "hasDevicePermissionToAccess(String source): 실행됨");

        String[] permissionRequestArr = new String[]
                {
                        source
                };
        boolean result = false;
        if (mWZBroadcast != null)
        {
            result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                Log.e(TAG, "hasDevicePermissionToAccess(String source): 버전 체크 후 권한 요청목록 불러오기");
                result = (mRequiredPermissions.length > 0 ? WowzaGoCoder.hasPermissions(this, permissionRequestArr) : true);
                Log.e(TAG, "hasDevicePermissionToAccess(String source): result: " + result);
            }
        }
        return result;
    }

    // 액세스 권한이있는 장치
    protected boolean hasDevicePermissionToAccess()
    {
        Log.e(TAG, "hasDevicePermissionToAccess():  실행됨");
        boolean result = false;
        if (mWZBroadcast != null)
        {
            result = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                result = (mRequiredPermissions.length > 0 ? WowzaGoCoder.hasPermissions(this, mRequiredPermissions) : true);
                if (!result && !hasRequestedPermissions)
                {
                    ActivityCompat.requestPermissions(this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
                    hasRequestedPermissions = true;
                }
            }
        }
        return result;
    }

    /**
     * Android Activity lifecycle methods
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        mPermissionsGranted = this.hasDevicePermissionToAccess();
        if (mPermissionsGranted)
        {
            syncPreferences();
        }
    }

    @Override
    protected void onPause()
    {
        WOWZLog.debug("GoCoderSDKActivityBase - onResume");
        // Stop any active live stream
        if (mWZBroadcast != null && mWZBroadcast.getStatus().isRunning())
        {
            endBroadcast(true);
        }

        // 방송 종료하기
        endBroadcastToMysql();

        super.onPause();
    }

    /**
     * Click handler for the in button
     */
    public void onAbout(View v)
    {
        // Display the About fragment
        AboutFragment aboutFragment = AboutFragment.newInstance();
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, aboutFragment)
                .addToBackStack(null)
                .commit();
    }

    // Return correctly from any fragments launched and placed on the back stack
    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0)
        {
            fm.popBackStack();
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        Log.e(TAG, "onRequestPermissionsResult: 실행됨. 권한요청 결과 불러오기");
        mPermissionsGranted = true;
        switch (requestCode)
        {
            case PERMISSIONS_REQUEST_CODE:
            {
                for (int grantResult : grantResults)
                {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                    {
                        mPermissionsGranted = false;
                    }
                }
            }
        }
        if (this.callbackFunction != null)
            this.callbackFunction.onPermissionResult(mPermissionsGranted);
    }

    /**
     * Enable Android's sticky immersive full-screen mode
     * See http://developer.android.com/training/system-ui/immersive.html#sticky
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        Log.e(TAG, "onWindowFocusChanged: 실행됨. 전체화면");

        if (sFullScreenActivity && hasFocus)
            hideSystemUI();
    }

    public void hideSystemUI()
    {
        Log.e(TAG, "hideSystemUI: 실행됨. UI 숨기기");
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void showSystemUI()
    {
        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null)
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    /**
     * WOWZStatusCallback interface methods
     */
    @Override
    public void onWZStatus(final WOWZStatus goCoderStatus)
    {
        Log.e(TAG, "onWZStatus: 실행됨");
        WOWZLog.debug("GOCODERSDKACTIVITYBASE", goCoderStatus.toString());
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                if (goCoderStatus.isReady())
                {
                    // Keep the screen on while the broadcast is active
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                    // Since we have successfully opened up the server connection, store the connection info for auto complete
                    GoCoderSDKPrefs.storeHostConfig(PreferenceManager.getDefaultSharedPreferences(GoCoderSDKActivityBase.this), mWZBroadcastConfig);
                } else if (goCoderStatus.isIdle())
                {
                    // Clear the "keep screen on" flag
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }

            }
        });
    }

    @Override
    public void onWZError(final WOWZStatus goCoderStatus)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                WOWZLog.error(TAG, goCoderStatus.getLastError());
            }
        });
    }

    protected synchronized WOWZStreamingError startBroadcast()
    {
        Log.e(TAG, "startBroadcast: 실행됨");
        WOWZStreamingError configValidationError = null;

        if (mWZBroadcast.getStatus().isIdle())
        {

            // Set the detail level for network logging output
            // 네트워크 로깅 출력의 세부 수준 설정
            mWZBroadcast.setLogLevel(mWZNetworkLogLevel);

            //
            // An example of adding metadata values to the stream for use with the onMetadata()
            // method of the IMediaStreamActionNotify2 interface of the Wowza Streaming Engine Java
            // API for server modules.

            // onMetadata ()에서 사용할 메타 데이터 값을 스트림에 추가하는 예제
            // Wowza 스트리밍 엔진의 IMediaStreamActionNotify2 인터페이스의 메소드 Java
            // 서버 모듈 용 API.


            // See http://www.wowza.com/resources/serverapi/com/wowza/wms/stream/IMediaStreamActionNotify2.html
            // for additional usage information on IMediaStreamActionNotify2.
            // IMediaStreamActionNotify2에 관한 추가 사용법 정보를 해당 링크에서 참조하십시오
            //

            // Add stream metadata describing the current device and platform
            // 현재 장치 및 플랫폼을 설명하는 스트림 메타 데이터 추가
            WOWZDataMap streamMetadata = new WOWZDataMap();
            streamMetadata.put("androidRelease", Build.VERSION.RELEASE);
            streamMetadata.put("androidSDK", Build.VERSION.SDK_INT);
            streamMetadata.put("deviceProductName", Build.PRODUCT);
            streamMetadata.put("deviceManufacturer", Build.MANUFACTURER);
            streamMetadata.put("deviceModel", Build.MODEL);

            mWZBroadcastConfig.setStreamMetadata(streamMetadata);

            //
            // An example of adding query strings for use with the getQueryStr() method of
            // the IClient interface of the Wowza Streaming Engine Java API for server modules.

            // getQueryStr () 메소드와 함께 사용할 쿼리 문자열을 추가하는 예제
            // 서버 모듈 용 Wowza Streaming Engine Java API의 IClient 인터페이스.

            // See http://www.wowza.com/resources/serverapi/com/wowza/wms/client/IClient.html#getQueryStr()
            // for additional usage information on getQueryStr().

            // getQueryStr ()에 대한 추가 사용법 정보는 해당 링크를 참조하십시오.

            try
            {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

                // Add query string parameters describing the current app
                // 현재 앱을 설명하는 검색어 문자열 매개 변수 추가
                WOWZDataMap connectionParameters = new WOWZDataMap();
                connectionParameters.put("appPackageName", pInfo.packageName);
                connectionParameters.put("appVersionName", pInfo.versionName);
                connectionParameters.put("appVersionCode", pInfo.versionCode);

                mWZBroadcastConfig.setConnectionParameters(connectionParameters);

            } catch (PackageManager.NameNotFoundException e)
            {
                WOWZLog.error(TAG, e);
            }

            WOWZLog.info(TAG, "=============== Broadcast Configuration ===============\n"
                    + mWZBroadcastConfig.toString()
                    + "\n=======================================================");

            Log.e(TAG, "startBroadcast: Configuration: " + mWZBroadcastConfig.toString());

            configValidationError = mWZBroadcastConfig.validateForBroadcast();

            if (configValidationError == null)
            {


                /// Setup abr bitrate and framerate listeners. EXAMPLE
//                mWZBroadcastConfig.setABREnabled(false);
//                ListenToABRChanges abrHandler = new ListenToABRChanges();
//                mWZBroadcast.registerAdaptiveBitRateListener(abrHandler);
//                mWZBroadcast.registerAdaptiveFrameRateListener(abrHandler);
//                mWZBroadcastConfig.setFrameRateLowBandwidthSkipCount(1);
                mWZBroadcast.startBroadcast(mWZBroadcastConfig, this);
                Log.e(TAG, "startBroadcast: 방송 시작");

                // todo: 방송을 시작하면 서버에 방송정보 등록하기
                // 등록할 정보
                // 방송 이름
                // 송출자 아이디
                // 송출 시작시간 (서버에서 Date/Time 으로 등록할 것)
                // 송출중인 ip
                uploadBroadcastInfo();

            }
        } else
        {
            WOWZLog.error(TAG, "startBroadcast() called while another broadcast is active");
        }
        return configValidationError;
    }

    String endBroadCast;

    // todo: 방송을 시작하면 서버에 방송정보 등록하기
    private void uploadBroadcastInfo()
    {
        // 입력한 정보를 php POST로 DB에 전송합니다.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG, "onResponse: response = " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);

                            // 가입이 완료되면 서버에서 success를 반환한다.
                            Wowza_Host_Rommno = jsonObject.getString("success");

                            Log.e(TAG, "onResponse: 방송정보 등록완료. 방송 인덱스: " + Wowza_Host_Rommno);

                            String roomAndUserData = Wowza_Host_Rommno + "@" + MyId + "@" + MyName;
                            Log.e(TAG, "onResponse: roomAndUserData: " + roomAndUserData);

                            // 방번호와 유저의 이름으로 서버에 접속한다
                            Log.e(TAG, "onCreate: roomAndUserData: " + roomAndUserData);
                            client = new CameraActivity.SocketClient(roomAndUserData);
                            client.start();

                            // 계정이 중복되면 서버에서 3을 반환한다
                            // 중복결과 알리고 가입 중지
                            if (Wowza_Host_Rommno.equals("0"))
                            {
                                Log.e(TAG, "onResponse: 문제발생. 로그 확인" );
                                Log.e(TAG, "onResponse: response = " + response);
                            }

                            endBroadCast = Wowza_Host_Rommno;
                            Log.e(TAG, "onResponse: endBroadCast: " + endBroadCast );

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(GoCoderSDKActivityBase.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(GoCoderSDKActivityBase.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "VolleyError: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                params.put("MyId", MyId);
                params.put("broadcast_title", broadcast_title);
                params.put("broadcast_status", "true");

                return params;
            }
        };

        Log.e(TAG, "uploadBroadcastInfo: MyId: " + MyId);
        Log.e(TAG, "uploadBroadcastInfo: broadcast_title: " + broadcast_title);
        Log.e(TAG, "uploadBroadcastInfo: broadcast_status: " + "true ('방송 진행중'으로 mySql에 등록하기)");

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); // 방송정보 등록 요청하기
    }

    class ListenToABRChanges implements WOWZBroadcastAPI.AdaptiveChangeListener
    {
        @Override
        public int adaptiveBitRateChange(WOWZStreamingStat broadcastStat, int newBitRate)
        {
            WOWZLog.debug(TAG, "adaptiveBitRateChange[" + newBitRate + "]");
            Log.e(TAG, "adaptiveBitRateChange: 적응 형 BitRate 변경");

            return 500;
        }

        @Override
        public int adaptiveFrameRateChange(WOWZStreamingStat broadcastStat, int newFrameRate)
        {
            WOWZLog.debug(TAG, "adaptiveFrameRateChange[" + newFrameRate + "]");
            Log.e(TAG, "adaptiveFrameRateChange: 적응 형 FrameRate 변경\n");
            return 20;
        }
    }


    // todo: 방송 종료
    protected synchronized void endBroadcast(boolean appPausing)
    {
        Log.e(TAG, "endBroadcast: 실행됨. 방송 종료");

        // todo: mysql에 방송 종료사실 알리기
        endBroadcastToMysql();

        WOWZLog.debug("MP4", "endBroadcast");
        if (!mWZBroadcast.getStatus().isIdle())
        {
            WOWZLog.debug("MP4", "endBroadcast-notidle");
            if (appPausing)
            {
                // Stop any active live stream
                // 활성 라이브 스트림을 중지하십시오.
                Log.e(TAG, "endBroadcast: 방송 일시중지 시도하기");
                sBroadcastEnded = false;

                mWZBroadcast.endBroadcast(new WOWZStatusCallback()
                {
                    @Override
                    public void onWZStatus(WOWZStatus wzStatus)
                    {
                        WOWZLog.debug("MP4", "onWZStatus::" + wzStatus.toString());
                        synchronized (sBroadcastLock)
                        {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                            Log.e(TAG, "onWZStatus: 방송 일시중지 됨");

                        }
                    }

                    @Override
                    public void onWZError(WOWZStatus wzStatus)
                    {
                        WOWZLog.debug("MP4", "onWZStatus::" + wzStatus.getLastError());
                        WOWZLog.error(TAG, wzStatus.getLastError());
                        synchronized (sBroadcastLock)
                        {
                            sBroadcastEnded = true;
                            sBroadcastLock.notifyAll();
                            Log.e(TAG, "onWZError: 에러 알림");
                        }
                    }
                });

                while (!sBroadcastEnded)
                {
                    try
                    {
                        sBroadcastLock.wait();
                    } catch (InterruptedException e)
                    {
                        Log.e(TAG, "endBroadcast: e: " + e.toString());
                    }
                }
            } else
            {
                mWZBroadcast.endBroadcast(this);
                Log.e(TAG, "endBroadcast: 방송 종료됨");

//                 todo: mysql에 방송 종료사실 알리기
//                endBroadcastToMysql();
            }

        } else
        {
            WOWZLog.error(TAG, "endBroadcast() called without an active broadcast");
            Log.e(TAG, "endBroadcast: 종료 실패 _ endBroadcast() called without an active broadcast");
        }
    }

    // todo: mysql에 방송 종료사실 알리기
    private void endBroadcastToMysql()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://115.68.231.84/Broadcast_end.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e(TAG, "onResponse: response = " + response);

                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            Wowza_Host_Rommno = jsonObject.getString("success");
                            Log.e(TAG, "onResponse: 방송종료 완료. 종료한 방송 인덱스: " + endBroadCast);
                            if (endBroadCast.equals("1"))
                            {
                                Log.e(TAG, "onResponse: Mysql에서 방송 상태를 '종료'로 갱신했습니다. 종료한 방 번호: " + endBroadCast );
                                Log.e(TAG, "onResponse: response = " + response);
                            }
                            else if (endBroadCast.equals("0"))
                            {
                                Log.e(TAG, "onResponse: 방송 종료중 문제발생. 로그 확인" );
                                Log.e(TAG, "onResponse: response = " + response);
                            }

                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.e(TAG, "onResponse: JSONException e: " + e );
                            Toast.makeText(GoCoderSDKActivityBase.this, "문제발생." + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
//                        Toast.makeText(GoCoderSDKActivityBase.this, "문제발생." + error.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "VolleyError: " + error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();
                params.put("broadcastIndex", endBroadCast);
                Log.e(TAG, "getParams: endBroadCast: " + endBroadCast );
                return params;
            }
        };

        Log.e(TAG, "uploadBroadcastInfo: 종료할 방송 인덱스: " + endBroadCast);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); // 방송정보 등록 요청하기
    }

    protected synchronized void endBroadcast()
    {
        endBroadcast(false);
    }

    public void syncPreferences()
    {
        Log.e(TAG, "syncPreferences: 실행됨");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWZNetworkLogLevel = Integer.valueOf(prefs.getString("wz_debug_net_log_level", String.valueOf(WOWZLog.LOG_LEVEL_DEBUG)));

        int bandwidthMonitorLogLevel = Integer.valueOf(prefs.getString("wz_debug_bandwidth_monitor_log_level", String.valueOf(0)));
        WOWZBroadcast.LOG_STAT_SUMMARY = (bandwidthMonitorLogLevel > 0);
        WOWZBroadcast.LOG_STAT_SAMPLES = (bandwidthMonitorLogLevel > 1);

        if (mWZBroadcastConfig != null)
            GoCoderSDKPrefs.updateConfigFromPrefs(prefs, mWZBroadcastConfig);
    }

    /**
     * Display an alert dialog containing an error message.
     *
     * @param errorMessage The error message text
     */
    protected void displayErrorDialog(String errorMessage)
    {
        // Log the error message
        try
        {
            WOWZLog.error(TAG, "ERROR: " + errorMessage);

            // Display an alert dialog containing the error message
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            //AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setMessage(errorMessage)
                    .setTitle(R.string.dialog_title_error);
            builder.setPositiveButton(R.string.dialog_button_close, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.dismiss();

                    endBroadcastToMysql();
                }
            });

            builder.create().show();
        } catch (Exception ex)
        {
        }
    }

    /**
     * Display an alert dialog containing the error message for
     * an error returned from the GoCoder SDK.
     *
     * @param goCoderSDKError An error returned from the GoCoder SDK.
     */
    protected void displayErrorDialog(WOWZError goCoderSDKError)
    {
        displayErrorDialog(goCoderSDKError.getErrorDescription());
    }
}
