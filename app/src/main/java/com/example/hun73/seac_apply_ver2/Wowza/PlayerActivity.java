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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hun73.seac_apply_ver2.Interface.ApiClient;
import com.example.hun73.seac_apply_ver2.Interface.ApiInterface;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.Wowza.config.GoCoderSDKPrefs;
import com.example.hun73.seac_apply_ver2.Wowza.ui.DataTableFragment;
import com.example.hun73.seac_apply_ver2.Wowza.ui.MultiStateButton;
import com.example.hun73.seac_apply_ver2.Wowza.ui.StatusView;
import com.example.hun73.seac_apply_ver2.Wowza.ui.TimerView;
import com.example.hun73.seac_apply_ver2.Wowza.ui.VolumeChangeObserver;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.data.WOWZDataMap;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerConfig;
import com.wowza.gocoder.sdk.api.player.WOWZPlayerView;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.hun73.seac_apply_ver2.Wowza.CameraActivity.wiwza_ipad2;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyId;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyName;
import static com.example.hun73.seac_apply_ver2.Wowza.Wowza_Main.MyPhoto;

public class PlayerActivity extends GoCoderSDKActivityBase
{
    final private static String TAG = PlayerActivity.class.getSimpleName();

    // Stream player view
    private WOWZPlayerView mStreamPlayerView = null;
    private WOWZPlayerConfig mStreamPlayerConfig = null;

    // UI controls
    private MultiStateButton mBtnPlayStream = null;
    private MultiStateButton mBtnSettings = null;
    private MultiStateButton mBtnMic = null;
    private MultiStateButton mBtnScale = null;
    private SeekBar mSeekVolume = null;
    private ProgressDialog mBufferingDialog = null;

    private StatusView mStatusView = null;
    private TextView mHelp = null;
    private TimerView mTimerView = null;
    private ImageButton mStreamMetadata = null;
    private boolean mUseHLSPlayback = false;

    private VolumeChangeObserver mVolumeSettingChangeObserver = null;

    // todo: 채팅 UI
    private ImageView wowza_player_caht_button, wowza_player_caht_send_button;
    private LinearLayout wowza_player_caht_area;
    private EditText wowza_player_caht_message_content;

    private TextView wowza_player_end_message;

    RecyclerView message_recyclerview;
    Wowza_Chat_Adapter wowza_chat_adapter;

    List<Wowza_Chat_Item> wowza_chat_List;
    Wowza_Chat_Item wowza_chat_item;

    private int chatAreaOnOffSwitch = 0;

    // todo: TCP 채팅 준비
    // 수신용 핸들러
    // 서버에서 전송하는 메시지를 감지한다.
    // 메시지를 받고 리사이클러뷰를 갱신함
    Handler msgHandler;

    // [0] 나에게 메시지를 보낸 사람,
    // [1] 메시지 내용,
    // [2] 보낸 시간을 담는 배열
    String[] msgFilter;

    // 상대방 이름
    String targetNickName, targetID;

    // 상대방 사진, 내 사진
    String ReceiverPhoto;

    // 방 번호
    String RoomNo;

    // 내 아이디, 내 이름.
    // 액티비티가 onCreate 실행하면
    public static String loginUserId;
    String loginUserNick;

    Socket socket;
    SocketClient client;

    // 접속할 TCP 서버 주소 설정
//    String ipad2 = "192.168.0.16"; // 8사
//    String ipad2 = "192.168.1.101"; // 5사
//    String ipad2 = "192.168.1.14"; // 1사
    int port = 12346;

    // 메시지 전송 스레드
    // 메시지 입력 후 전송버튼 클릭했을 때, 연결된 소켓으로 메시지를 보냄
    SendThread send;
    ReceiveThread recevie;

    // 메시지 타입
    String Type;

    // 현재 시간 받아오기
    long mNow;
    SimpleDateFormat mFormat = new SimpleDateFormat("aa hh:mm");
    Date mDate;
    String time;

    int Message_views = 1;

    // todo: TCP 채팅준비 끝

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_player);

        // 퍼미션 요청하기
        mRequiredPermissions = new String[]{};

        // todo: 카메라 디스플레이, 재생화면
        mStreamPlayerView = (WOWZPlayerView) findViewById(R.id.vwStreamPlayer);

        // todo: 방송 송출 및 시청 아이콘
        mBtnPlayStream = (MultiStateButton) findViewById(R.id.ic_play_stream);
        mBtnSettings = (MultiStateButton) findViewById(R.id.ic_settings);
        mBtnMic = (MultiStateButton) findViewById(R.id.ic_mic);
        mBtnScale = (MultiStateButton) findViewById(R.id.ic_scale);

        mTimerView = (TimerView) findViewById(R.id.txtTimer);
        mStatusView = (StatusView) findViewById(R.id.statusView);
        mStreamMetadata = (ImageButton) findViewById(R.id.imgBtnStreamInfo);

        wowza_player_end_message = findViewById(R.id.wowza_player_end_message);

        // 방송 시청할 때 활성화 하는 텍스트
        // Press the gear icon to select a stream and then press the play button.
        mHelp = (TextView) findViewById(R.id.streamPlayerHelp);

        mSeekVolume = (SeekBar) findViewById(R.id.sb_volume);

        mTimerView.setVisibility(View.GONE);

        if (sGoCoderSDK != null)
        {
            mTimerView.setTimerProvider(new TimerView.TimerProvider()
            {
                @Override
                public long getTimecode()
                {
                    return mStreamPlayerView.getCurrentTime();
                }

                @Override
                public long getDuration()
                {
                    return mStreamPlayerView.getDuration();
                }
            });

            mSeekVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    if (mStreamPlayerView != null && mStreamPlayerView.isPlaying())
                    {
                        mStreamPlayerView.setVolume(progress);
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar)
                {
                }

                public void onStopTrackingTouch(SeekBar seekBar)
                {
                }
            });

            // listen for volume changes from device buttons, etc.
            Log.e(TAG, "onCreate: 장치 버튼 등에서 볼륨 변경 내용을 듣습니다.");
            mVolumeSettingChangeObserver = new VolumeChangeObserver(this, new Handler());
            getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, mVolumeSettingChangeObserver);
            mVolumeSettingChangeObserver.setVolumeChangeListener(new VolumeChangeObserver.VolumeChangeListener()
            {
                @Override
                public void onVolumeChanged(int previousLevel, int currentLevel)
                {
                    if (mSeekVolume != null)
                        mSeekVolume.setProgress(currentLevel);

                    if (mStreamPlayerView != null && mStreamPlayerView.isPlaying())
                    {
                        mStreamPlayerView.setVolume(currentLevel);
                    }
                }
            });

            mBtnScale.setState(mStreamPlayerView.getScaleMode() == WOWZMediaConfig.FILL_VIEW);

            // The streaming player configuration properties
            Log.e(TAG, "onCreate: 스트리밍 플레이어 구성 속성");
            mStreamPlayerConfig = new WOWZPlayerConfig();

            mBufferingDialog = new ProgressDialog(this);
            mBufferingDialog.setTitle(R.string.status_buffering);
            mBufferingDialog.setMessage(getResources().getString(R.string.msg_please_wait));
            mBufferingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    cancelBuffering(dialogInterface);
                }
            });

        } else
        {
            Log.e(TAG, "onCreate: mHelp View 비활성화. 시청 시작");
            mHelp.setVisibility(View.GONE);
            mStatusView.setErrorMessage(WowzaGoCoder.getLastError().getErrorDescription());
        }


        // todo: TCP 채팅 시작
        wowza_player_caht_button = findViewById(R.id.wowza_player_caht_button);
        wowza_player_caht_area = findViewById(R.id.wowza_player_caht_area);

        // todo: 채팅 리사이클러뷰 세팅
        wowza_chat_List = new ArrayList<>();
        message_recyclerview = findViewById(R.id.wowza_player_caht_recycler_view);

        message_recyclerview.setHasFixedSize(true);
        message_recyclerview.setLayoutManager(new LinearLayoutManager(PlayerActivity.this));

        wowza_chat_adapter = new Wowza_Chat_Adapter(PlayerActivity.this, (ArrayList<Wowza_Chat_Item>) wowza_chat_List);
        message_recyclerview.setAdapter(wowza_chat_adapter);

        // todo: 현재시간 받아오기
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        time = mFormat.format(mDate);

        // todo: 채팅서버 입장하는 유저의 정보
//        Intent intent = getIntent();
//        targetID = intent.getExtras().getString("user_id");
//        targetNickName = intent.getExtras().getString("user_name");
//        ReceiverPhoto = intent.getExtras().getString("user_photo");
//        RoomNo = intent.getExtras().getString("room_no");
        targetID = MyId;
        targetNickName = MyName;
        ReceiverPhoto = MyPhoto;
        RoomNo = MyId;

        // todo: 방 입장하기 (방 번호, 내 아이디)
        String roomAndUserData = Wowza_Host_Rommno + "@" + targetID + "@" + MyName;
        Log.e(TAG, "onCreate: roomAndUserData: " + roomAndUserData);
        client = new SocketClient(roomAndUserData);
        client.start();

        Log.e(TAG, "onCreate: targetID: " + targetID);
        Log.e(TAG, "onCreate: targetNickName: " + targetNickName);
        Log.e(TAG, "onCreate: ReceiverPhoto: " + ReceiverPhoto);
        Log.e(TAG, "onCreate: : RoomNo: " + RoomNo);

        // todo: 채팅 영역 활성, 비활성 버튼
        wowza_player_caht_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (chatAreaOnOffSwitch == 0)
                {
                    wowza_player_caht_area.setVisibility(View.VISIBLE);
                    chatAreaOnOffSwitch = 1;
//                    wowza_player_caht_area.setVisibility(View.GONE);
                } else if (chatAreaOnOffSwitch == 1)
                {
                    wowza_player_caht_area.setVisibility(View.GONE);
                    chatAreaOnOffSwitch = 0;
//                    wowza_player_caht_area.setVisibility(View.VISIBLE);
                }
            }
        });

//        // todo: 서버로 나의 접속 사실 알리기
//        send = new SendThread(socket, "System: " + MyName +"님이 접속했습니다.");
//        Log.e(TAG, " 나의 접속안내 보내기 ");
//        send.start();

        // todo: 메시지 수신 핸들러
        RecieveMessage();

        // todo: 메시지 전송 스레드
        SendMessage();
    }

    @Override
    protected void onDestroy()
    {
        if (mVolumeSettingChangeObserver != null)
            Log.e(TAG, "onDestroy: 실행됨");
        getApplicationContext().getContentResolver().unregisterContentObserver(mVolumeSettingChangeObserver);

        super.onDestroy();
    }

    /**
     * Android Activity class methods
     */

    @Override
    protected void onResume()
    {
        super.onResume();

        syncUIControlState();
    }

    @Override
    protected void onPause()
    {
        if (mStreamPlayerView != null && mStreamPlayerView.isPlaying())
        {
            mStreamPlayerView.stop();

            // Wait for the streaming player to disconnect and shutdown...
            Log.e(TAG, "onPause: 스트리밍 플레이어가 연결을 끊고 종료 할 때까지 기다립니다.");
            mStreamPlayerView.getCurrentStatus().waitForState(WOWZState.IDLE);
        }

        super.onPause();
    }

    private boolean isNetworkAvailable()
    {
        Log.e(TAG, "isNetworkAvailable: 실행됨");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isPlayerConfigReady()
    {

        return false;
    }

    /*
    Click handler for network pausing
     */
    public void onPauseNetwork(View v)
    {
        Log.e(TAG, "onPauseNetwork: 실행됨");
        Button btn = (Button) findViewById(R.id.pause_network);
        if (btn.getText().toString().trim().equalsIgnoreCase("pause network"))
        {
            Log.e(TAG, "onPauseNetwork: pause network");
            WOWZLog.info("Pausing network...");
            btn.setText("Unpause Network");
            mStreamPlayerView.pauseNetworkStack();
        } else
        {
            WOWZLog.info("Unpausing network... btn.getText(): " + btn.getText());
            btn.setText("Pause Network");
            mStreamPlayerView.unpauseNetworkStack();
            Log.e(TAG, "onPauseNetwork: else pause network");
        }
    }

    /**
     * Click handler for the playback button
     */
    public void onTogglePlayStream(View v)
    {
        Log.e(TAG, "onTogglePlayStream: 실행됨");
        if (mStreamPlayerView.isPlaying())
        {
            mStreamPlayerView.stop();
        } else if (mStreamPlayerView.isReadyToPlay())
        {
            if (!this.isNetworkAvailable())
            {
                displayErrorDialog("No internet connection, please try again later.");
                return;
            }

//            if(!this.isPlayerConfigReady()){
//                displayErrorDialog("Please be sure to include a host, stream, and application to playback a stream.");
//                return;
//            }

            Log.e(TAG, "onTogglePlayStream(): mHelp View 비활성화. 시청 시작");
            mHelp.setVisibility(View.GONE);
            WOWZStreamingError configValidationError = mStreamPlayerConfig.validateForPlayback();
            if (configValidationError != null)
            {
                mStatusView.setErrorMessage(configValidationError.getErrorDescription());
            } else
            {
                // Set the detail level for network logging output
                mStreamPlayerView.setLogLevel(mWZNetworkLogLevel);

                // Set the player's pre-buffer duration as stored in the app prefs
                float preBufferDuration = GoCoderSDKPrefs.getPreBufferDuration(PreferenceManager.getDefaultSharedPreferences(this));

                mStreamPlayerConfig.setPreRollBufferDuration(preBufferDuration);

                // Start playback of the live stream
                mStreamPlayerView.play(mStreamPlayerConfig, this);
            }

        }
    }

    /**
     * WOWZStatusCallback interface methods
     */
    @Override
    public synchronized void onWZStatus(WOWZStatus status)
    {
        Log.e(TAG, "onWZStatus: 실행됨");

        final WOWZStatus playerStatus = new WOWZStatus(status);

        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                WOWZStatus status = new WOWZStatus(playerStatus.getState());
                switch (playerStatus.getState())
                {

                    case WOWZPlayerView.STATE_PLAYING:
                        // Keep the screen on while we are playing back the stream
                        // 스트림을 재생하는 동안 화면을 켜 놓으십시오.
                        Log.e(TAG, "onWZStatus run: WOWZPlayerView.STATE_PLAYING");
                        Log.e(TAG, "onWZStatus run: 스트림을 재생하는 동안 화면을 켜 놓으십시오.");
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        if (mStreamPlayerConfig.getPreRollBufferDuration() == 0f)
                        {
                            mTimerView.startTimer();
                            Log.e(TAG, "onWZStatus run: 타이머 시작");
                        }

                        // Since we have successfully opened up the server connection, store the connection info for auto complete
                        // 서버 연결을 성공적으로 열었으므로 자동 완성을위한 연결 정보를 저장하십시오.

                        GoCoderSDKPrefs.storeHostConfig(PreferenceManager.getDefaultSharedPreferences(PlayerActivity.this), mStreamPlayerConfig);
                        Log.e(TAG, "onWZStatus run: 서버 연결을 성공적으로 열었으므로 자동 완성을위한 연결 정보를 저장하십시오.");

                        // Log the stream metadata
                        // 스트림 메타 데이터 로깅
                        WOWZLog.debug(TAG, "Stream metadata:\n" + mStreamPlayerView.getMetadata());
                        break;

                    case WOWZPlayerView.STATE_READY_TO_PLAY:
                        Log.e(TAG, "onWZStatus run: onWZStatus run: WOWZPlayerView.STATE_READY_TO_PLAY");

                        // Clear the "keep screen on" flag
                        Log.e(TAG, "onWZStatus run: '화면 유지'플래그 지우기");

                        WOWZLog.debug(TAG, "STATE_READY_TO_PLAY player activity status!");
                        if (playerStatus.getLastError() != null)
                            displayErrorDialog(playerStatus.getLastError());

                        playerStatus.clearLastError();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        mTimerView.stopTimer();
                        Log.e(TAG, "onWZStatus run: 타이머 중단");
                        // 호스트의 방송 종료 판단을 타이머 중단 시점에서 할까?
                        // 그래야겠다!!

                        // todo: 방송 알림표시 해주기
                        wowza_player_end_message.setVisibility(View.VISIBLE);

                        // todo: 종료한 방송을 방송목록에서 없애기 위해 새로고침 한다
                        // 보류... ㅠㅠ


                        break;

                    case WOWZPlayerView.STATE_PREBUFFERING_STARTED:
                        Log.e(TAG, "onWZStatus run: STATE_PREBUFFERING_STARTED");

                        WOWZLog.debug(TAG, "Dialog for buffering should show...");
                        showBuffering();
                        break;

                    case WOWZPlayerView.STATE_PREBUFFERING_ENDED:
                        Log.e(TAG, "onWZStatus run: STATE_PREBUFFERING_ENDED");

                        WOWZLog.debug(TAG, "Dialog for buffering should stop...");
                        hideBuffering();
                        // Make sure player wasn't signaled to shutdown
                        // 플레이어가 종료 신호를 받았는지 확인하십시오.
                        Log.e(TAG, "onWZStatus run: STATE_PREBUFFERING_ENDED: 플레이어가 종료 신호를 받았는지 확인하십시오");
                        if (mStreamPlayerView.isPlaying())
                        {
                            Log.e(TAG, "onWZStatus run: STATE_PREBUFFERING_ENDED: mStreamPlayerView.isPlaying(): 타이머 시작");

                            mTimerView.startTimer();
                        }
                        break;

                    default:
                        break;
                }
                syncUIControlState();
            }
        });
    }

    @Override
    public synchronized void onWZError(final WOWZStatus playerStatus)
    {
        Log.e(TAG, "onWZError: 실행됨");
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(TAG, "onWZError: run: displayErrorDialog(playerStatus.getLastError())");
                displayErrorDialog(playerStatus.getLastError());
                syncUIControlState();
            }
        });
    }

    /**
     * Click handler for the mic/mute button
     */
    public void onToggleMute(View v)
    {
        Log.e(TAG, "onToggleMute: 실행됨");
        mBtnMic.toggleState();

        if (mStreamPlayerView != null)
            mStreamPlayerView.mute(!mBtnMic.isOn());

        mSeekVolume.setEnabled(mBtnMic.isOn());
    }

    public void onToggleScaleMode(View v)
    {
        Log.e(TAG, "onToggleScaleMode: 실행됨");
        int newScaleMode = mStreamPlayerView.getScaleMode() == WOWZMediaConfig.RESIZE_TO_ASPECT ? WOWZMediaConfig.FILL_VIEW : WOWZMediaConfig.RESIZE_TO_ASPECT;
        mBtnScale.setState(newScaleMode == WOWZMediaConfig.FILL_VIEW);
        mStreamPlayerView.setScaleMode(newScaleMode);
    }

    /**
     * Click handler for the metadata button
     */
    public void onStreamMetadata(View v)
    {
        Log.e(TAG, "onStreamMetadata: 실행됨");
        WOWZDataMap streamMetadata = mStreamPlayerView.getMetadata();
        WOWZDataMap streamStats = mStreamPlayerView.getStreamStats();
//        WOWZDataMap streamConfig = mStreamPlayerView.getStreamConfig().toDataMap();
        WOWZDataMap streamConfig = new WOWZDataMap();
        WOWZDataMap streamInfo = new WOWZDataMap();

        streamInfo.put("- Stream Statistics -", streamStats);
        streamInfo.put("- Stream Metadata -", streamMetadata);
        //streamInfo.put("- Stream Configuration -", streamConfig);

        DataTableFragment dataTableFragment = DataTableFragment.newInstance("Stream Information", streamInfo, false, false);

        // Display/hide the data table fragment
        getFragmentManager().beginTransaction()
                .add(android.R.id.content, dataTableFragment)
                .addToBackStack("metadata_fragment")
                .commit();
    }

    /**
     * Click handler for the settings button
     */
    public void onSettings(View v)
    {
        // Display the prefs fragment
        Log.e(TAG, "onSettings: 환경 설정 프래그먼트 표시");
        GoCoderSDKPrefs.PrefsFragment prefsFragment = new GoCoderSDKPrefs.PrefsFragment();
        prefsFragment.setFixedSource(true);
        prefsFragment.setForPlayback(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Update the state of the UI controls
     */
    private void syncUIControlState()
    {
        Log.e(TAG, "syncUIControlState: 실행됨");

        boolean disableControls = (!(mStreamPlayerView.isReadyToPlay() || mStreamPlayerView.isPlaying()) || sGoCoderSDK == null);

        if (disableControls)
        {
            Log.e(TAG, "syncUIControlState: disableControls");
            mBtnPlayStream.setEnabled(false);
            mBtnSettings.setEnabled(false);
            mSeekVolume.setEnabled(false);
            mBtnScale.setEnabled(false);
            mBtnMic.setEnabled(false);
            mStreamMetadata.setEnabled(false);
        } else
        {
            Log.e(TAG, "syncUIControlState: enableControls");
            mBtnPlayStream.setState(mStreamPlayerView.isPlaying());
            mBtnPlayStream.setEnabled(true);

            if (mStreamPlayerConfig.isAudioEnabled())
            {
                mBtnMic.setVisibility(View.VISIBLE);
                mBtnMic.setEnabled(true);

                mSeekVolume.setVisibility(View.VISIBLE);
                mSeekVolume.setEnabled(mBtnMic.isOn());
                mSeekVolume.setProgress(mStreamPlayerView.getVolume());

                Log.e(TAG, "syncUIControlState: mBtnMic, mSeekVolume: VISIBLE");

            } else
            {
                mSeekVolume.setVisibility(View.GONE);
                mBtnMic.setVisibility(View.GONE);
                Log.e(TAG, "syncUIControlState: mBtnMic, mSeekVolume: GONE");
            }

            mBtnScale.setVisibility(View.VISIBLE);
            mBtnScale.setVisibility(mStreamPlayerView.isPlaying() && mStreamPlayerConfig.isVideoEnabled() ? View.VISIBLE : View.GONE);
            mBtnScale.setEnabled(mStreamPlayerView.isPlaying() && mStreamPlayerConfig.isVideoEnabled());

            mBtnSettings.setEnabled(!mStreamPlayerView.isPlaying());
            mBtnSettings.setVisibility(mStreamPlayerView.isPlaying() ? View.GONE : View.VISIBLE);
            Log.e(TAG, "syncUIControlState: 설정버튼 활성화");

            mStreamMetadata.setEnabled(mStreamPlayerView.isPlaying());
            mStreamMetadata.setVisibility(mStreamPlayerView.isPlaying() ? View.VISIBLE : View.GONE);
        }
    }

    private void showBuffering()
    {
        try
        {
            if (mBufferingDialog == null) return;
            mBufferingDialog.show();
            Log.e(TAG, "showBuffering: 버퍼링 중... ");
        } catch (Exception ex)
        {
        }
    }

    private void cancelBuffering(DialogInterface dialogInterface)
    {
        if (mStreamPlayerConfig.getHLSBackupURL() != null || mStreamPlayerConfig.isHLSEnabled())
        {
            mStreamPlayerView.stop(true);
            Log.e(TAG, "cancelBuffering: 버퍼링 종료");
        } else if (mStreamPlayerView != null && mStreamPlayerView.isPlaying())
        {
            mStreamPlayerView.stop(true);
            Log.e(TAG, "cancelBuffering: 버퍼링 종료");
        }
    }

    private void hideBuffering()
    {
        if (mBufferingDialog.isShowing())
            Log.e(TAG, "hideBuffering: 버퍼링 숨김");
        mBufferingDialog.dismiss();
    }

    @Override
    public void syncPreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mWZNetworkLogLevel = Integer.valueOf(prefs.getString("wz_debug_net_log_level", String.valueOf(WOWZLog.LOG_LEVEL_DEBUG)));

        mStreamPlayerConfig.setIsPlayback(true);
        if (mStreamPlayerConfig != null)
            GoCoderSDKPrefs.updateConfigFromPrefsForPlayer(prefs, mStreamPlayerConfig);
    }


    // todo: 메시지 수신 핸들러
    private void RecieveMessage()
    {

        msgHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 1111)
                {
                    Log.e(TAG, " = getUserDetail(): ReceiveThread에서 전달받음 = ");

                    // 메세지가 왔다면.
//                                                Toast.makeText(Activity_ChatRoom.this, "메세지 : " + msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "받은 메시지: " + msg.obj.toString());

//                                                msgFilter = msg.obj.toString().split("@");
                    msgFilter = msg.obj.toString().split("│");

                    // 수신
                    wowza_chat_item = new Wowza_Chat_Item(
                            1,
                            "1",
                            msgFilter[5],
                            "1",
                            msgFilter[1],
                            msgFilter[2],
                            msgFilter[6],
                            1
                    );

                    Log.e(TAG, "handleMessage: 보낸사람[5]      : " + msgFilter[5]);
                    Log.e(TAG, "handleMessage: 내용[1]      : " + msgFilter[1]);
                    Log.e(TAG, "handleMessage: 시간[2]      : " + msgFilter[2]);
                    Log.e(TAG, "handleMessage: 사진[6]      : " + msgFilter[6]);

                    wowza_chat_List.add(wowza_chat_item); // 어레이 리스트
                    wowza_chat_adapter.notifyDataSetChanged();

                    // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
                    message_recyclerview.smoothScrollToPosition(wowza_chat_List.size());
                }
            }
        };
    }


    // todo: 메시지 전송 스레드
    private void SendMessage()
    {
        wowza_player_caht_send_button = findViewById(R.id.wowza_player_caht_send_button);
        wowza_player_caht_message_content = findViewById(R.id.wowza_player_caht_message_content);

        wowza_player_caht_message_content.addTextChangedListener(new TextWatcher()
        {

            // 변화 감지
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            // 입력 후
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String message = wowza_player_caht_message_content.getText().toString();

                Log.e(TAG, "setSendBtn: SendMessage(): message: " + message);
                if (TextUtils.isEmpty(message) || message.equals(""))
                {
                    // Toast.makeText(Activity_ChatRoom.this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();

                    // 보낼 내용이 없을 땐 파란 불 끔
                    // send_button.getResources().getDrawable(R.drawable.ic_send_message);
//                    send_button.setImageResource(R.drawable.ic_send_message);
                    wowza_player_caht_send_button.setImageResource(R.drawable.ic_wowza_chat_send_button);
                    wowza_player_caht_send_button.setEnabled(false); // 전송버튼 비활성화

                } else
                {
                    // 보낼 내용이 없을 땐 파란 불 켬
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message_2);
                    wowza_player_caht_send_button.setImageResource(R.drawable.ic_send_message_2);
                    wowza_player_caht_send_button.setEnabled(true); // 전송버튼 활성화

                    wowza_player_caht_send_button.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 텍스트 메시지: 1
                            // 사진 메시지: 2
                            int mode = 1;

                            Log.e(TAG, "setSendBtn: loginUserId: " + loginUserId + " / loginUserNick: " + loginUserNick);

                            wowza_chat_item = null;
                            wowza_chat_item = new Wowza_Chat_Item(1, targetID, targetNickName, "1", message, time, ReceiverPhoto, 1);

                            Log.e(TAG, " ");
                            Log.e(TAG, " ===== 전송 중 ===== ");
//                            Log.e(TAG, "mode: " + 1 + " / MyPhoto: " + MyPhoto + " / senderId: " + senderId);
                            Log.e(TAG, "senderNick: " + loginUserId + " / message: " + message);
                            Log.e(TAG, "time: " + time);
                            Log.e(TAG, " ===== ======= ===== ");
                            Log.e(TAG, " ");

//                    chat_area.append(senderNick + ": " + message + "\n");

                            wowza_chat_List.add(wowza_chat_item);
//                    chattingRoomAdapter.notifyDataSetChanged();
                            message_recyclerview.smoothScrollToPosition(wowza_chat_List.size());

                            Type = "1";

                            send = new SendThread(socket, message);
                            Log.e(TAG, " ");
                            Log.e(TAG, " ===== SendThread ===== ");
                            Log.e(TAG, " message: " + message);
                            Log.e(TAG, " socket: " + socket);
                            Log.e(TAG, " ===== ======= ===== ");
                            Log.e(TAG, " ");
                            send.start();

//                    // 리사이클러뷰의 마지막 아이템 위치로 보내기
//                    message_area.scrollToPosition(messageData.size() - 1);

                            // 채팅 입력창 비워주기
                            wowza_player_caht_message_content.setText(null);

                            // 보낼 내용이 없을 땐 파란 불 끔
//                    send_button.getResources().getDrawable(R.drawable.ic_send_message);
                            wowza_player_caht_send_button.setImageResource(R.drawable.ic_wowza_chat_send_button);
                            wowza_player_caht_send_button.setEnabled(false); // 전송버튼 비활성화
                            // 키보드 내려주기
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(message_content.getWindowToken(), 0);

//                    Toast.makeText(Activity_ChatRoom.this, "전송", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            // 입력하기 전
            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
    }


    // todo: 채팅을 위한 내부 스레드 클래스
    // 내부클래스 ( 접속용 )
    class SocketClient extends Thread
    {
        DataInputStream in = null;
        DataOutputStream output = null;
        String roomAndUserData; // 방 정보 ( 방번호 /  접속자 아이디 )
//        ReceiveThread recevie;

        public SocketClient(String roomAndUserData)
        {
            this.roomAndUserData = roomAndUserData;
        }

        public void run()
        {
            try
            {
                // 채팅 서버에 접속 ( 연결 )  ( 서버쪽 ip와 포트 )
                socket = new Socket(wiwza_ipad2/*서버 ip*/, port/*포트*/);

                Log.e(TAG, "SocketClient run: 접속 시도");

                // 메세지를 서버에 전달 할 수 있는 통로 ( 만들기 )
                output = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                // 서버에 초기 데이터 전송  ( 방번호와 접속자 아이디가 담겨서 간다 ) -  식별자 역할을 하게 될 거임.
                output.writeUTF(roomAndUserData);

                // (메세지 수신용 쓰레드 생성 ) 리시브 쓰레드 시작
                recevie = new ReceiveThread(socket);
                recevie.start();

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    } //SocketClient의 끝

    // 내부 클래스  ( 메세지 전송용 )
    class SendThread extends Thread
    {
        Socket socket; // ip와 포트?
        String sendmsg;
        DataOutputStream output;

        public SendThread(Socket socket, String sendmsg)
        {
            Log.e(TAG, "SendThread: 도달했음");

            this.socket = socket;
            this.sendmsg = sendmsg;
            try
            {
                // 채팅 서버로 메세지를 보내기 위한  스트림 생성.
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // 서버로 메세지 전송 ( 이클립스 서버단에서 temp 로 전달이 된다.
        public void run()
        {
            Log.e(TAG, "run: SendThread: 실행됨");
            try
            {
                if (output != null)
                {
                    if (sendmsg != null)
                    {
//                        int roomNo = 1;
                        // 여기서 방번호와 상대방 아이디 까지 해서 보내줘야 할거같다 .
                        // 서버로 메세지 전송하는 부분

                        // 방 번호 @ 유저 인덱스 @ 유저 이름 @ 메시지
//                        output.writeUTF(roomNo + "@" + targetID + "@" + targetNickName + "@" + sendmsg);

//                         방 번호 @ 내 아이디 @ 상대 아이디 @ 메시지
//                        output.writeUTF(RoomNo + "@" + loginUserId + "@" + targetID + "@" + sendmsg + "@" + Type);
//                        output.writeUTF(RoomNo + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views);
                        output.writeUTF(Wowza_Host_Rommno + "│" + loginUserId + "│" + targetID + "│" + sendmsg + "│" + Type + "│" + Message_views + "│" + targetNickName + "│" + ReceiverPhoto);

                        Log.e(TAG, "(SendThread) roomNo: " + Wowza_Host_Rommno + "/ loginUserId    : " + loginUserId);
                        Log.e(TAG, "(SendThread) targetID: " + targetID + "/ targetNickName: " + targetNickName);
                        Log.e(TAG, "(SendThread) sendmsg: " + sendmsg);
                        Log.e(TAG, "(SendThread) Type: " + Type + "_ 1: 텍스트 / 2: 사진");

                        sendmsg = null;

                        // 리사이클러뷰의 아이템 위치를 아래로 내리기
//                        message_area.scrollToPosition(messageData.size() - 1);
                    }
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    // ( 메세지 수신용 ) - 서버로부터 받아서, 핸들러에서 처리하도록 할 거.
    class ReceiveThread extends Thread
    {
        //        Socket socket = null;
        Socket socket;
        DataInputStream input = null;

        public ReceiveThread(Socket socket)
        {
            this.socket = socket;

            try
            {
                // 채팅 서버로부터 메세지를 받기 위한 스트림 생성.
                input = new DataInputStream(socket.getInputStream());
                Log.e(TAG, "ReceiveThread try input: 연결됨 ");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void run()
        {
            try
            {
                while (input != null)
                {
                    // 채팅 서버로 부터 받은 메세지
                    String msg = input.readUTF();
                    Log.e(TAG, "ReceiveThread: run(): String msg = null?: " + msg);

                    if (msg != null)
                    {
                        Log.e(TAG, "ReceiveThread: run() = input.readUTF(): " + msg);

                        // 핸들러에게 전달할 메세지 객체
                        Message hdmg = msgHandler.obtainMessage();

                        Log.e(TAG, "ReceiveThread: hdmg = msgHandler.obtainMessage(): " + hdmg);

                        // 핸들러에게 전달할 메세지의 식별자
                        hdmg.what = 1111;

                        // 메세지의 본문
                        hdmg.obj = msg;

                        // 핸들러에게 메세지 전달 ( 화면 처리 )
                        msgHandler.sendMessage(hdmg);

                        // 방 목록 갱신하기
//                        ChatRoomList_Request();

                        Log.e(TAG, "ReceiveThread: run()에서 onCreate의 핸들러로 메시지 전달함 ");

                        // 채팅 스크롤 자동으로 맨 아래로 포커스 해주기
//                        message_area.smoothScrollToPosition(messageData.size());
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "run: Exception e: " + e.toString());
//                Toast.makeText(Activity_ChatRoom.this, "Exception e: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
