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

package com.example.hun73.seac_apply_ver2.Wowza.mp4;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.VideoView;

import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.Wowza.GoCoderSDKActivityBase;
import com.example.hun73.seac_apply_ver2.Wowza.config.GoCoderSDKPrefs;
import com.example.hun73.seac_apply_ver2.Wowza.ui.MultiStateButton;
import com.example.hun73.seac_apply_ver2.Wowza.ui.StatusView;
import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.configuration.WOWZMediaConfig;
import com.wowza.gocoder.sdk.api.errors.WOWZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;
import com.wowza.gocoder.sdk.api.mp4.WOWZMP4Broadcaster;
import com.wowza.gocoder.sdk.api.mp4.WOWZMP4Util;
import com.wowza.gocoder.sdk.api.status.WOWZState;
import com.wowza.gocoder.sdk.api.status.WOWZStatus;

public class MP4BroadcastActivity extends GoCoderSDKActivityBase
{
    final private static String TAG = MP4BroadcastActivity.class.getSimpleName();

    final private static int VIDEO_SELECTED_RESULT_CODE = 1;

    // UI controls
    private MultiStateButton mBtnFileSelect = null;
    private MultiStateButton mBtnLoop = null;

    protected MultiStateButton mBtnBroadcast = null;
    protected MultiStateButton mBtnSettings = null;

    private VideoView mVideoView = null;
    private StatusView mStatusView = null;

    private WOWZMP4Broadcaster mMP4Broadcaster = null;

    private MediaPlayer mMediaPlayer = null;
    private boolean mLooping = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e(TAG, "onCreate: 실행됨" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp4_broadcast);

        mRequiredPermissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        mMediaPlayer = null;
        mLooping = true;

        mBtnBroadcast = (MultiStateButton) findViewById(R.id.ic_broadcast);
        mBtnSettings = (MultiStateButton) findViewById(R.id.ic_settings);

        mBtnFileSelect = (MultiStateButton) findViewById(R.id.ic_videos);
        mBtnLoop = (MultiStateButton) findViewById(R.id.ic_loop);

        mVideoView = (VideoView) findViewById(R.id.vwVideoPlayer);
        mStatusView = (StatusView) findViewById(R.id.statusView);

        if (sGoCoderSDK != null)
        {
            mMP4Broadcaster = new WOWZMP4Broadcaster(this);
//            mMP4Broadcaster.setOffset(10000); //set in ms
            mWZBroadcastConfig.setVideoBroadcaster(mMP4Broadcaster);
            mWZBroadcastConfig.setAudioBroadcaster(mMP4Broadcaster);

            mBtnLoop.setState(mLooping);

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                public void onPrepared(MediaPlayer mediaPlayer)
                {
                    mMediaPlayer = mediaPlayer;
                    mediaPlayer.setLooping(mLooping);

                    if (!getBroadcastConfig().isAudioEnabled())
                        mediaPlayer.setVolume(0.0f, 0.0f);
                    else
                        mediaPlayer.setVolume(0.75f, 0.75f);

                    mediaPlayer.seekTo(((int) mMP4Broadcaster.getOffset()));
                }
            });

            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer)
                {
                    mMediaPlayer = null;
                }
            });
        } else if (mStatusView != null)
        {
            mStatusView.setErrorMessage(WowzaGoCoder.getLastError().getErrorDescription());
        }
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
        super.onPause();
    }

    /**
     * Click handler for the video selector button
     */
    public void onSelectMedia(View v)
    {
        Log.e(TAG, "onSelectMedia: 실행됨" );
        if (!mPermissionsGranted)
        {
            mStatusView.setErrorMessage("The application has not been granted permission to read from external storage");
        } else
        {
            selectVideoFile();
        }
    }

    private void selectVideoFile()
    {
        Log.e(TAG, "selectVideoFile: 실행됨" );
        mBtnBroadcast.setEnabled(false);
        mBtnSettings.setEnabled(false);
        mBtnFileSelect.setEnabled(false);

        Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
        mediaChooser.addCategory(Intent.CATEGORY_OPENABLE);
        mediaChooser.setType("video/*");
        startActivityForResult(mediaChooser, VIDEO_SELECTED_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnIntent)
    {
        Log.e(TAG, "onActivityResult: 실행됨" );
        mBtnFileSelect.setEnabled(true);
        mBtnSettings.setEnabled(false);

        switch (requestCode)
        {
            case VIDEO_SELECTED_RESULT_CODE:
                if (resultCode == RESULT_OK)
                {
                    Uri fileUri = returnIntent.getData();
                    setVideoFile(fileUri);
                    Log.e(TAG, "onActivityResult: setVideoFile(fileUri): " + fileUri );
                    syncUIControlState();
/*
                    String mimeType = getContentResolver().getType(fileUri);

                    if (mimeType != null && !mimeType.equals("video/mp4")) {
                        mStatusView.setErrorMessage("The video selected is not an MP4 file");
                    } else {
                        setVideoFile(fileUri);
                        syncUIControlState();
                    }
*/
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, returnIntent);
    }

    private void setVideoFile(Uri fileUri)
    {
        Log.e(TAG, "setVideoFile: 실행됨");

        if (fileUri == null) return;

        WOWZMediaConfig mp4FileConfig = WOWZMP4Util.getFileConfig(this, fileUri);
        if (mp4FileConfig != null)
        {
            mWZBroadcastConfig.set(mp4FileConfig);
            mMP4Broadcaster.setFileUri(fileUri);
            mVideoView.setVideoURI(fileUri);

            // 시청안내 메시지 숨기기
            findViewById(R.id.vwHelp).setVisibility(View.INVISIBLE);
            Log.e(TAG, "setVideoFile: 시청안내 메시지 숨기기");

            // 플레이어 활성화
            mVideoView.setVisibility(View.VISIBLE);
            Log.e(TAG, "setVideoFile: 플레이어 활성화");

        } else
        {
            mStatusView.setErrorMessage("The format of the selected MP4 file could not be determined.");

            // 플레이어 숨기기
            mVideoView.setVisibility(View.INVISIBLE);
            Log.e(TAG, "setVideoFile: 플레이어 숨기기");

            // 시청안내 메시지 활성화
            // Press the gear icon to select a stream and then press the play button
            findViewById(R.id.vwHelp).setVisibility(View.VISIBLE);
            Log.e(TAG, "setVideoFile: 시청안내 메시지 활성화");
        }
    }

    /**
     * Click handler for the broadcast button
     */
    // todo: 방송버튼 처리
    public void onToggleBroadcast(View v)
    {
        Log.e(TAG, "onToggleBroadcast: 방송버튼 클릭함. ");

        if (mMP4Broadcaster != null && mMP4Broadcaster.getFileUri() == null)
        {
            mStatusView.setErrorMessage("An MP4 file has not been selected");
            Log.e(TAG, "onToggleBroadcast: An MP4 file has not been selected");
            return;
        }


        if (mWZBroadcast.getStatus().isIdle())
        {
            Log.e(TAG, "onToggleBroadcast: isIdle()");

            // Set the broadcast config so that it mirrors the MP4 file's format
            // 브로드 캐스트 구성을 설정하여 MP4 파일의 형식을 미러링합니다.
            WOWZMediaConfig mp4Config = mMP4Broadcaster.getVideoSourceConfig();

            mWZBroadcastConfig.setVideoSourceConfig(mp4Config);
            mWZBroadcastConfig.setVideoFrameSize(mp4Config.getVideoFrameSize());
            mWZBroadcastConfig.setVideoFramerate(mp4Config.getVideoFramerate());
            mWZBroadcastConfig.setVideoKeyFrameInterval(mp4Config.getVideoKeyFrameInterval());
            mWZBroadcastConfig.setVideoRotation(mp4Config.getVideoRotation());

            WOWZStreamingError configValidationError = mWZBroadcastConfig.validateForBroadcast();

            if (configValidationError != null)
            {
                mStatusView.setErrorMessage(configValidationError.getErrorDescription());
            } else
            {
                mWZBroadcast.startBroadcast(mWZBroadcastConfig, this);
                Log.e(TAG, "onToggleBroadcast: 방송 시작. 송출? 시청?");
            }
        } else if (mWZBroadcast.getStatus().isRunning())
        {
            if (mVideoView.isPlaying())
            {
                mVideoView.pause();
            }
            mWZBroadcast.endBroadcast(this);
            Log.e(TAG, "onToggleBroadcast: 방송버튼 클릭함. 방송 종료");
        }
    }

    /**
     * Click handler for the settings button
     */
    public void onSettings(View v)
    {
        Log.e(TAG, "onSettings: 실행됨");
        // Display the prefs fragment
        GoCoderSDKPrefs.PrefsFragment prefsFragment = new GoCoderSDKPrefs.PrefsFragment();
        prefsFragment.setFixedSource(true);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, prefsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Click handler for the loop button
     */
    public void onLoop(View v)
    {
        mLooping = mBtnLoop.toggleState();
        mMP4Broadcaster.setLooping(mLooping);
        if (mMediaPlayer != null)
            mMediaPlayer.setLooping(mLooping);
    }

    /**
     * WOWZStatusCallback interface methods
     */

    @Override
    public void onWZStatus(final WOWZStatus goCoderStatus)
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                switch (goCoderStatus.getState())
                {
                    case WOWZState.IDLE:
                        // Clear the "keep screen on" flag
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                        if (mMediaPlayer != null && mMediaPlayer.isPlaying())
                        {
                            mMediaPlayer.stop();
                            Log.e(TAG, "onWZStatus run: 플레이어 stop ");
                        }
                        break;

                    case WOWZState.RUNNING:
                        // Keep the screen on while we are broadcasting
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        int seekVal = (int) mMP4Broadcaster.getOffset();
                        WOWZLog.debug("[broadcastVideoTrack] seekval : " + seekVal);
                        mVideoView.seekTo(seekVal);
                        mVideoView.start();
                        Log.e(TAG, "onWZStatus run: 와우자 플레이어 mVideoView.start()");
                        break;
                }
                mStatusView.setStatus(goCoderStatus);
                Log.e(TAG, "onWZStatus run: mStatusView.setStatus(goCoderStatus): " + goCoderStatus.toString());
                syncUIControlState();
            }
        });
    }

    @Override
    public void onWZError(final WOWZStatus goCoderStatus)
    {
        Log.e(TAG, "onWZError: 에러발생");
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                mStatusView.setStatus(goCoderStatus);
                syncUIControlState();
            }
        });
    }

    /**
     * Update the state of the UI controls
     * UI 컨트롤의 상태 업데이트
     */
    private void syncUIControlState()
    {
        Log.e(TAG, "syncUIControlState: 실행됨" );
        boolean disableControls = (mWZBroadcast == null ||
                !(mWZBroadcast.getStatus().isIdle() || mWZBroadcast.getStatus().isRunning()));

        Uri mp4FileUri = mMP4Broadcaster != null ? mMP4Broadcaster.getFileUri() : null;

        if (disableControls)
        {
            Log.e(TAG, "syncUIControlState: UI 컨트롤 사용 중지");
            mBtnBroadcast.setEnabled(false);
            mBtnSettings.setEnabled(false);
            mBtnLoop.setEnabled(false);
            mBtnFileSelect.setEnabled(false);
        } else
        {
            Log.e(TAG, "syncUIControlState: else 실행");
            boolean isStreaming = mWZBroadcast.getStatus().isRunning();
            mBtnBroadcast.setState(isStreaming);
            mBtnBroadcast.setEnabled(mp4FileUri != null);

            mBtnSettings.setEnabled(!isStreaming);
            mBtnFileSelect.setEnabled(!isStreaming);
        }
    }
}


























