/*
 * Copyright 2016-2019 Adrian Cotfas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.example.hun73.seac_apply_ver2.Pomodoro.BL;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

//import com.apps.adrcotfas.goodtime.Main.TimerActivity;
//import com.apps.adrcotfas.goodtime.R;
//import com.apps.adrcotfas.goodtime.Util.Constants;
//import com.apps.adrcotfas.goodtime.Util.IntentWithAction;

import com.example.hun73.seac_apply_ver2.Pomodoro.Main.TimerActivity;
import com.example.hun73.seac_apply_ver2.Pomodoro.Pomodoro_Util.Constants;
import com.example.hun73.seac_apply_ver2.Pomodoro.Pomodoro_Util.IntentWithAction;
import com.example.hun73.seac_apply_ver2.R;

import java.util.concurrent.TimeUnit;

import androidx.core.app.NotificationCompat;

/**
 * Class responsible with creating and updating notifications for the foreground {@link TimerService}
 * and triggering notifications for events like finishing a session or updating the remaining time.
 * The notifications are customized according to {@link PreferenceHelper}.
 */
public class NotificationHelper extends ContextWrapper {

    private static final String TAG = NotificationHelper.class.getSimpleName();
    private static final String GOODTIME_NOTIFICATION = "goodtime.notification";
    public static final int GOODTIME_NOTIFICATION_ID = 42;
    private static final int START_WORK_ID = 33;
    private static final int SKIP_BREAK_ID = 34;
    private static final int START_BREAK_ID = 35;
    private static final int RESUME_ID = 36;
    private static final int PAUSE_ID = 37;
    private static final int STOP_ID = 38;

    private final NotificationManager mManager;
    private final NotificationCompat.Builder mBuilder;

    public NotificationHelper(Context context) {
        super(context);
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            initChannels();
        }
        mBuilder = new NotificationCompat.Builder(this, GOODTIME_NOTIFICATION)
                .setSmallIcon(R.drawable.logo_4)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(createActivityIntent())
                .setOngoing(true)
                .setShowWhen(false);
    }

    public void notifyFinished(SessionType sessionType) {
        Log.v(TAG, "notifyFinished");
        clearNotification();
        mManager.notify(GOODTIME_NOTIFICATION_ID, getFinishedSessionBuilder(sessionType).build());
    }

    public void updateNotificationProgress(CurrentSession currentSession) {
        Log.v(TAG, "updateNotificationProgress");
        mManager.notify(GOODTIME_NOTIFICATION_ID,
                getInProgressBuilder(currentSession)
                        .setOnlyAlertOnce(true)
                        .setContentText(buildProgressText(currentSession.getDuration().getValue()))
                        .build());
    }

    public void clearNotification() {
        mManager.cancelAll();
    }

    private CharSequence buildProgressText(Long duration) {
        long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(duration);
        long minutesLeft = secondsLeft / 60;
        secondsLeft %= 60;

        return (minutesLeft > 9 ? minutesLeft : "0" + minutesLeft) + ":" +
                (secondsLeft > 9 ? secondsLeft : "0" + secondsLeft);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannels() {
        NotificationChannel channelInProgress = new NotificationChannel(
                GOODTIME_NOTIFICATION, getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW);
        channelInProgress.setBypassDnd(true);
        channelInProgress.setShowBadge(true);
        channelInProgress.setSound(null, null);
        mManager.createNotificationChannel(channelInProgress);
    }

    /**
     * Get a "in progress" notification
     *
     * @param currentSession the current session
     * @return the builder
     */
    @SuppressLint("RestrictedApi")
    public NotificationCompat.Builder getInProgressBuilder(CurrentSession currentSession) {
        Log.v(TAG, "createNotification");

        mBuilder.mActions.clear();
        if (currentSession.getSessionType().getValue() == SessionType.WORK) {
            if (currentSession.getTimerState().getValue() == TimerState.PAUSED) {
                mBuilder.addAction(buildStopAction(this))
                        .addAction(buildResumeAction(this))
                        .setContentTitle(getString(R.string.action_paused_title))
                        .setContentText(getString(R.string.action_continue));
            } else {
                mBuilder.addAction(buildStopAction(this))
                        .addAction(buildPauseAction(this))
                        .setContentTitle(getString(R.string.action_progress_work))
                        .setContentText(buildProgressText(currentSession.getDuration().getValue()));
            }
        } else {
            mBuilder.addAction(buildStopAction(this))
                    .setContentTitle(getString(R.string.action_progress_break))
                    .setContentText(buildProgressText(currentSession.getDuration().getValue()));
        }

        return mBuilder;
    }

    private NotificationCompat.Builder getFinishedSessionBuilder(SessionType sessionType) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, GOODTIME_NOTIFICATION)
                .setSmallIcon(R.drawable.logo_4)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(createActivityIntent())
                .setOngoing(false)
                .setShowWhen(false);
        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender();
        if (sessionType == SessionType.WORK) {
            builder.setContentTitle(getString(R.string.action_finished_session))
                    .setContentText(getString(R.string.action_continue))
                    .addAction(buildStartBreakAction(this))
                    .addAction(buildSkipBreakAction(this));
            extender.addAction(buildStartBreakAction(this));
            extender.addAction(buildSkipBreakAction(this));
        } else {
            builder.setContentTitle(getString(R.string.action_finished_break))
                    .setContentText(getString(R.string.action_continue))
                    .addAction(buildStartWorkAction(this));
            extender.addAction(buildStartWorkAction(this));
        }
        builder.extend(extender);
        return builder;
    }

    private PendingIntent createActivityIntent() {
        Intent openMainIntent = new Intent(this, TimerActivity.class);
        return PendingIntent.getActivity(this, 0, openMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static NotificationCompat.Action buildStopAction(Context context) {

        PendingIntent stopPendingIntent = PendingIntent.getService(context, STOP_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.STOP), PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_stop,
                context.getString(R.string.action_stop),
                stopPendingIntent).build();
    }

    private static NotificationCompat.Action buildResumeAction(Context context) {
        PendingIntent togglePendingIntent = PendingIntent.getService(context, RESUME_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.TOGGLE), PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_notification_resume,
                context.getString(R.string.action_resume),
                togglePendingIntent).build();
    }

    private static NotificationCompat.Action buildPauseAction(Context context) {

        PendingIntent togglePendingIntent = PendingIntent.getService(context, PAUSE_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.TOGGLE), PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_notification_pause,
                context.getString(R.string.action_pause),
                togglePendingIntent).build();
    }

    private NotificationCompat.Action buildStartWorkAction(Context context) {
        PendingIntent togglePendingIntent = PendingIntent.getService(context, START_WORK_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.START, SessionType.WORK), PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_notification_resume,
                context.getString(R.string.action_start_work),
                togglePendingIntent).build();
    }

    private NotificationCompat.Action buildStartBreakAction(Context context) {
        PendingIntent togglePendingIntent = PendingIntent.getService(context, START_BREAK_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.START, SessionType.BREAK), PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_notification_resume,
                context.getString(R.string.action_start_break),
                togglePendingIntent).build();
    }

    private NotificationCompat.Action buildSkipBreakAction(Context context) {
        PendingIntent togglePendingIntent = PendingIntent.getService(context, SKIP_BREAK_ID,
                new IntentWithAction(context, TimerService.class, Constants.ACTION.START, SessionType.WORK), PendingIntent.FLAG_ONE_SHOT);

        return new NotificationCompat.Action.Builder(
                R.drawable.ic_notification_skip,
                context.getString(R.string.action_skip_break),
                togglePendingIntent).build();
    }
}
