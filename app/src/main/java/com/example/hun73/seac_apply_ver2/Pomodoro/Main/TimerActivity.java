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

package com.example.hun73.seac_apply_ver2.Pomodoro.Main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hun73.seac_apply_ver2.Activity_Home;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.CurrentSession;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.GoodtimeApplication;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.NotificationHelper;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.PreferenceHelper;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.SessionType;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.TimerService;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.TimerState;
import com.example.hun73.seac_apply_ver2.Pomodoro.LabelAndColor;
import com.example.hun73.seac_apply_ver2.Session;
import com.example.hun73.seac_apply_ver2.Pomodoro.Settings.SettingsActivity;
import com.example.hun73.seac_apply_ver2.Pomodoro.Statistics.Main.SelectLabelDialog;
import com.example.hun73.seac_apply_ver2.Pomodoro.Statistics.SessionViewModel;
import com.example.hun73.seac_apply_ver2.Pomodoro.Util.Constants;
import com.example.hun73.seac_apply_ver2.Pomodoro.Util.IntentWithAction;
import com.example.hun73.seac_apply_ver2.Pomodoro.Util.OnSwipeTouchListener;
import com.example.hun73.seac_apply_ver2.Pomodoro.Util.ThemeHelper;
import com.example.hun73.seac_apply_ver2.R;
import com.example.hun73.seac_apply_ver2.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;
import de.greenrobot.event.EventBus;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import static android.view.animation.AnimationUtils.loadAnimation;
import static android.widget.Toast.LENGTH_SHORT;
import static com.example.hun73.seac_apply_ver2.Pomodoro.BL.PreferenceHelper.AMOLED;
import static com.example.hun73.seac_apply_ver2.Pomodoro.BL.PreferenceHelper.ENABLE_SCREENSAVER_MODE;
import static com.example.hun73.seac_apply_ver2.Pomodoro.BL.PreferenceHelper.ENABLE_SCREEN_ON;
import static com.example.hun73.seac_apply_ver2.Pomodoro.BL.PreferenceHelper.WORK_DURATION;
import static com.example.hun73.seac_apply_ver2.Pomodoro.Util.UpgradeActivityHelper.launchUpgradeActivity;
import static java.lang.String.format;

//TODO: remove duplicate from the other flavor
public class TimerActivity
        extends
        AppCompatActivity
        implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        SelectLabelDialog.OnLabelSelectedListener
{

    private static final String TAG = TimerActivity.class.getSimpleName();

    private final CurrentSession mCurrentSession = GoodtimeApplication.getInstance().getCurrentSession();
    private AlertDialog mDialogSessionFinished;
    private FullscreenHelper mFullscreenHelper;
    private long mBackPressedAt;

    private MenuItem mStatusButton;
    private View mBoundsView;
    private TextView mTimeLabel;
    private Toolbar mToolbar;
    private TimerActivityViewModel mViewModel;
    private SessionViewModel mSessionViewModel;

    private TextView mSessionsCounterText;

    private static final String DIALOG_SELECT_LABEL_TAG = "dialogSelectLabel";

    public void onStartButtonClick(View view)
    {
        start(SessionType.WORK);
    }

    public void onStopButtonClick()
    {
        stop();
    }

    public void onSkipButtonClick()
    {
        skip();
    }

    public void onAdd60SecondsButtonClick()
    {
        add60Seconds();
    }

    private void skip()
    {
        if (mCurrentSession.getTimerState().getValue() != TimerState.INACTIVE)
        {
            Intent skipIntent = new IntentWithAction(TimerActivity.this, TimerService.class,
                    Constants.ACTION.SKIP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                startForegroundService(skipIntent);
            } else
            {
                startService(skipIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        if (PreferenceHelper.isFirstRun())
        {
            // show app intro
            Intent i = new Intent(TimerActivity.this, MainIntroActivity.class);
            startActivity(i);

            PreferenceHelper.consumeFirstRun();
        }

        mViewModel = ViewModelProviders.of(this).get(TimerActivityViewModel.class);

        ThemeHelper.setTheme(this);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mToolbar = binding.bar;
        mTimeLabel = binding.timeLabel;

        mBoundsView = binding.main;

        setupTimeLabelEvents();

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setTitle(null);
        }

        // dismiss it at orientation change
        DialogFragment selectLabelDialog = ((DialogFragment) getSupportFragmentManager().findFragmentByTag(DIALOG_SELECT_LABEL_TAG));
        if (selectLabelDialog != null)
        {
            selectLabelDialog.dismiss();
        }
    }

    /**
     * Shows the tutorial snackbars
     */
    private void showTutorialSnackbars()
    {

        final int MESSAGE_SIZE = 4;
        int i = PreferenceHelper.getLastIntroStep();

        if (i < MESSAGE_SIZE)
        {

            List<String> messages = new ArrayList<>();

            messages.add(getString(R.string.tutorial_tap));
            messages.add(getString(R.string.tutorial_swipe_left));
            messages.add(getString(R.string.tutorial_swipe_up));
            messages.add(getString(R.string.tutorial_swipe_down));

            Snackbar s = Snackbar.make(mToolbar, messages.get(PreferenceHelper.getLastIntroStep()), Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view ->
                    {
                        int nextStep = i + 1;
                        PreferenceHelper.setLastIntroStep(nextStep);
                        showTutorialSnackbars();
                    })
                    .setAnchorView(mToolbar)
                    // TODO: extract style to xml
                    .setActionTextColor(getResources().getColor(R.color.teal200));
            s.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.gray1000));
            TextView tv = s.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            if (tv != null)
            {
                tv.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
            s.show();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupTimeLabelEvents()
    {
        mTimeLabel.setOnTouchListener(new OnSwipeTouchListener(TimerActivity.this)
        {
            @Override
            public void onSwipeRight(View view)
            {
                onSkipSession();
            }

            @Override
            public void onSwipeLeft(View view)
            {
                onSkipSession();
            }

            @Override
            public void onSwipeBottom(View view)
            {
                onStopSession();
                if (PreferenceHelper.isScreensaverEnabled())
                {
                    recreate();
                }
            }

            @Override
            public void onSwipeTop(View view)
            {
                if (mCurrentSession.getTimerState().getValue() != TimerState.INACTIVE)
                {
                    onAdd60SecondsButtonClick();
                }
            }

            @Override
            public void onClick(View view)
            {
                onStartButtonClick(view);
            }

            @Override
            public void onLongClick(View view)
            {
                Intent settingsIntent = new Intent(TimerActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }

            @Override
            public void onPress(View view)
            {
                mTimeLabel.startAnimation(loadAnimation(getApplicationContext(), R.anim.scale_reversed));
            }

            @Override
            public void onRelease(View view)
            {
                mTimeLabel.startAnimation(loadAnimation(getApplicationContext(), R.anim.scale));
                if (mCurrentSession.getTimerState().getValue() == TimerState.PAUSED)
                {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> mTimeLabel.startAnimation(
                            loadAnimation(getApplicationContext(), R.anim.blink)), 300);
                }
            }
        });
    }

    private void onSkipSession()
    {
        if (mCurrentSession.getTimerState().getValue() != TimerState.INACTIVE)
        {
            onSkipButtonClick();
        }
    }

    private void onStopSession()
    {
        if (mCurrentSession.getTimerState().getValue() != TimerState.INACTIVE)
        {
            onStopButtonClick();
        }
    }

    @Override
    public void onAttachedToWindow()
    {
        getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED
                | FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mViewModel.isActive = true;
        if (mViewModel.dialogPendingType != SessionType.INVALID)
        {
            showFinishDialog(mViewModel.dialogPendingType);
            mViewModel.dialogPendingType = SessionType.INVALID;
        }

        // initialize notification channels on the first run
        if (PreferenceHelper.isFirstRun())
        {
            new NotificationHelper(this);
        }

        // this is to refresh the current status icon color
        invalidateOptionsMenu();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);
        toggleKeepScreenOn(PreferenceHelper.isScreenOnEnabled());
        toggleFullscreenMode();

        showTutorialSnackbars();
        setTimeLabelColor();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mViewModel.isActive = false;
    }

    @Override
    protected void onDestroy()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        mStatusButton = menu.findItem(R.id.action_state);
        setupEvents();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavigationDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavigationDrawerFragment.show(getSupportFragmentManager(), bottomNavigationDrawerFragment.getTag());
                break;
            case R.id.action_current_label:
                if (PreferenceHelper.isPro())
                {
                    showEditLabelDialog();
                } else
                {
                    launchUpgradeActivity(this);
                }
                break;
            case R.id.action_sessions_counter:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.action_reset_counter_title)
                        .setMessage(R.string.action_reset_counter)
                        .setPositiveButton(android.R.string.ok, (dialog, which)
                                -> mSessionViewModel.deleteSessionsFinishedToday())
                        .setNegativeButton(android.R.string.cancel, (dialog, which) ->
                        {
                        });
                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupEvents()
    {
        mCurrentSession.getDuration().observe(TimerActivity.this, this::updateTimeLabel);
        mCurrentSession.getSessionType().observe(TimerActivity.this, sessionType ->
        {
            if (mStatusButton != null)
            {
                if (sessionType == SessionType.WORK)
                {
                    mStatusButton.setIcon(getResources().getDrawable(R.drawable.ic_status_goodtime));
                } else
                {
                    mStatusButton.setIcon(getResources().getDrawable(R.drawable.ic_break));
                }
                setStatusIconColor();
            }
        });

        mCurrentSession.getTimerState().observe(TimerActivity.this, timerState ->
        {
            if (timerState == TimerState.INACTIVE)
            {
                if (mStatusButton != null)
                {
                    mStatusButton.setVisible(false);
                }
                final Handler handler = new Handler();
                handler.postDelayed(() -> mTimeLabel.clearAnimation(), 300);
            } else if (timerState == TimerState.PAUSED)
            {
                if (mStatusButton != null)
                {
                    mStatusButton.setVisible(true);
                }
                final Handler handler = new Handler();
                handler.postDelayed(() -> mTimeLabel.startAnimation(
                        loadAnimation(getApplicationContext(), R.anim.blink)), 300);
            } else
            {
                if (mStatusButton != null)
                {
                    mStatusButton.setVisible(true);
                }
                final Handler handler = new Handler();
                handler.postDelayed(() -> mTimeLabel.clearAnimation(), 300);
            }
        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed()
    {
        if (mCurrentSession.getTimerState().getValue() != TimerState.INACTIVE)
        {
//            moveTaskToBack(true);
//            Intent intent = new Intent(this, Activity_Home.class);
//            startActivity(intent);
            finish();
        } else
        {
            if (mBackPressedAt + 2000 > System.currentTimeMillis())
            {
                super.onBackPressed();
            } else
            {
                try
                {
                    Toast.makeText(getBaseContext(), R.string.action_press_back_button, LENGTH_SHORT).show();
                } catch (Throwable th)
                {
                    // ignoring this exception
                }
            }
            mBackPressedAt = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_sessions_counter);
        alertMenuItem.setVisible(false);
        boolean sessionsCounterEnabled = PreferenceHelper.isSessionsCounterEnabled();
        if (sessionsCounterEnabled)
        {
            FrameLayout mSessionsCounter = (FrameLayout) alertMenuItem.getActionView();
            mSessionsCounterText = mSessionsCounter.findViewById(R.id.view_alert_count_textview);
            mSessionsCounter.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));

            mSessionViewModel = ViewModelProviders.of(this).get(SessionViewModel.class);
            mSessionViewModel.getAllSessionsByEndTime().observe(this, sessions ->
            {
                final LocalDate today = new LocalDate();
                int statsToday = 0;
                for (Session s : sessions)
                {
                    final LocalDate crt = new LocalDate(new Date(s.endTime));
                    if (crt.isEqual(today))
                    {
                        statsToday++;
                    }
                    if (crt.isBefore(today))
                    {
                        break;
                    }
                }
                if (mSessionsCounterText != null)
                {
                    mSessionsCounterText.setText(Integer.toString(statsToday));
                }
                alertMenuItem.setVisible(true);
            });
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Called when an event is posted to the EventBus
     *
     * @param o holds the type of the Event
     */
    public void onEventMainThread(Object o)
    {
        if (!PreferenceHelper.isAutoStartBreak() && o instanceof Constants.FinishWorkEvent)
        {
            showFinishDialog(SessionType.WORK);
        } else if (!PreferenceHelper.isAutoStartWork() && (o instanceof Constants.FinishBreakEvent
                || o instanceof Constants.FinishLongBreakEvent))
        {
            showFinishDialog(SessionType.BREAK);
        } else if (o instanceof Constants.ClearFinishDialogEvent)
        {
            if (mDialogSessionFinished != null)
            {
                mDialogSessionFinished.dismiss();
            }
        }
    }

    private void updateTimeLabel(Long millis)
    {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        seconds -= (minutes * 60);

        SpannableString currentFormattedTick;
        boolean isMinutesStyle = PreferenceHelper.getTimerStyle().equals(getResources().getString(R.string.pref_timer_style_minutes_value));
        if (isMinutesStyle)
        {
            String currentTick = String.valueOf(TimeUnit.SECONDS.toMinutes((minutes * 60) + seconds + 59));
            currentFormattedTick = new SpannableString(currentTick);
            currentFormattedTick.setSpan(new RelativeSizeSpan(1.5f), 0,
                    currentTick.length(), 0);
        } else
        {
            boolean isV1Style = PreferenceHelper.getTimerStyle().equals(getResources().getString(R.string.pref_timer_style_default_value));
            final String separator = isV1Style ? "." : ":";
            String currentTick = (minutes > 0 ? minutes + separator : "") +
                    format(Locale.US, "%02d", seconds);
            currentFormattedTick = new SpannableString(currentTick);
            if (minutes > 0)
            {
                currentFormattedTick.setSpan(new RelativeSizeSpan(isV1Style ? 2f : 1.25f), 0,
                        isV1Style ? currentTick.indexOf(separator) : currentTick.length(), 0);
            } else
            {
                currentFormattedTick.setSpan(new RelativeSizeSpan(1.25f), 0, currentTick.length(), 0);
            }
        }

        mTimeLabel.setText(currentFormattedTick);

        Log.v(TAG, "drawing the time label.");

        if (PreferenceHelper.isScreensaverEnabled() && seconds == 1 && mCurrentSession.getTimerState().getValue() != TimerState.PAUSED)
        {
            teleportTimeView();
        }

    }

    private void start(SessionType sessionType)
    {
        Intent startIntent = new Intent();
        switch (mCurrentSession.getTimerState().getValue())
        {
            case INACTIVE:
                startIntent = new IntentWithAction(TimerActivity.this, TimerService.class,
                        Constants.ACTION.START, sessionType);
                break;
            case ACTIVE:
            case PAUSED:
                startIntent = new IntentWithAction(TimerActivity.this, TimerService.class, Constants.ACTION.TOGGLE);
                break;
            default:
                Log.wtf(TAG, "Invalid timer state.");
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(startIntent);
        } else
        {
            startService(startIntent);
        }
    }

    private void stop()
    {
        Intent stopIntent = new IntentWithAction(TimerActivity.this, TimerService.class, Constants.ACTION.STOP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(stopIntent);
        } else
        {
            startService(stopIntent);
        }
    }

    private void add60Seconds()
    {
        Intent stopIntent = new IntentWithAction(TimerActivity.this, TimerService.class, Constants.ACTION.ADD_SECONDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(stopIntent);
        } else
        {
            startService(stopIntent);
        }
    }

    private void showEditLabelDialog()
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SelectLabelDialog.newInstance(this, PreferenceHelper.getCurrentSessionLabel().label, false)
                .show(fragmentManager, DIALOG_SELECT_LABEL_TAG);
    }

    private void showFinishDialog(SessionType sessionType)
    {
        if (mViewModel.isActive)
        {
            Log.i(TAG, "Showing the finish dialog.");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (sessionType == SessionType.WORK)
            {
                builder.setTitle(R.string.action_finished_session)
                        .setPositiveButton(R.string.action_start_break, (dialog, which) ->
                        {
                            start(SessionType.BREAK);
                            delayToggleFullscreenMode();
                        })
                        .setNeutralButton(R.string.dialog_close, (dialog, which) ->
                        {
                            EventBus.getDefault().post(new Constants.ClearNotificationEvent());
                            delayToggleFullscreenMode();
                        })
                        .setOnCancelListener(dialog -> toggleFullscreenMode());
            } else
            {
                builder.setTitle(R.string.action_finished_break)
                        .setPositiveButton(R.string.action_start_work, (dialog, which) ->
                        {
                            start(SessionType.WORK);
                            delayToggleFullscreenMode();
                        })
                        .setNeutralButton(android.R.string.cancel, (dialog, which) ->
                        {
                            EventBus.getDefault().post(new Constants.ClearNotificationEvent());
                            delayToggleFullscreenMode();
                        })
                        .setOnCancelListener(dialog -> toggleFullscreenMode());
            }

            mDialogSessionFinished = builder.create();
            mDialogSessionFinished.setCanceledOnTouchOutside(false);
            mDialogSessionFinished.show();
            mViewModel.dialogPendingType = SessionType.INVALID;
        } else
        {
            mViewModel.dialogPendingType = sessionType;
        }
    }

    private void toggleFullscreenMode()
    {
        if (PreferenceHelper.isFullscreenEnabled())
        {
            if (mFullscreenHelper == null)
            {
                mFullscreenHelper = new FullscreenHelper(findViewById(R.id.main), getSupportActionBar());
            } else
            {
                mFullscreenHelper.hide();
            }
        } else
        {
            if (mFullscreenHelper != null)
            {
                mFullscreenHelper.disable();
                mFullscreenHelper = null;
            }
        }
    }

    private void delayToggleFullscreenMode()
    {
        final Handler handler = new Handler();
        handler.postDelayed(this::toggleFullscreenMode, 300);
    }

    private void toggleKeepScreenOn(boolean enabled)
    {
        if (enabled)
        {
            getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
        } else
        {
            getWindow().clearFlags(FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        switch (key)
        {
            case WORK_DURATION:
                if (GoodtimeApplication.getInstance().getCurrentSession().getTimerState().getValue()
                        == TimerState.INACTIVE)
                {
                    mCurrentSession.setDuration(TimeUnit.MINUTES.toMillis(PreferenceHelper.getSessionDuration(SessionType.WORK)));
                }
                break;
            case ENABLE_SCREEN_ON:
                toggleKeepScreenOn(PreferenceHelper.isScreenOnEnabled());
                break;
            case AMOLED:
                recreate();
            case ENABLE_SCREENSAVER_MODE:
                if (!PreferenceHelper.isScreensaverEnabled())
                {
                    recreate();
                }
                break;
            default:
                break;
        }
    }

    private void setStatusIconColor()
    {
        LabelAndColor labelAndColor = PreferenceHelper.getCurrentSessionLabel();

        if (mStatusButton != null)
        {
            if (labelAndColor.label != null)
            {
                mStatusButton.getIcon().setColorFilter(
                        ThemeHelper.getColor(this, labelAndColor.color), PorterDuff.Mode.SRC_ATOP);
            } else
            {
                mStatusButton.getIcon().setColorFilter(
                        ThemeHelper.getColor(this, ThemeHelper.COLOR_INDEX_UNLABELED), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private void setTimeLabelColor()
    {
        LabelAndColor labelAndColor = PreferenceHelper.getCurrentSessionLabel();
        if (mTimeLabel != null)
        {
            if (labelAndColor.label != null)
            {
                mTimeLabel.setTextColor(ThemeHelper.getColor(this, labelAndColor.color));
            } else
            {
                mTimeLabel.setTextColor(ThemeHelper.getColor(this, ThemeHelper.COLOR_INDEX_UNLABELED));
            }
        }
    }

    @Override
    public void onLabelSelected(LabelAndColor labelAndColor)
    {
        if (labelAndColor != null)
        {
            GoodtimeApplication.getCurrentSessionManager().getCurrentSession().setLabel(labelAndColor.label);
            PreferenceHelper.setCurrentSessionLabel(labelAndColor);
        } else
        {
            GoodtimeApplication.getCurrentSessionManager().getCurrentSession().setLabel(null);
        }
        setStatusIconColor();
        setTimeLabelColor();
    }

    private void teleportTimeView()
    {

        int margin = ThemeHelper.dpToPx(this, 48);
        int maxX = mBoundsView.getWidth() - mTimeLabel.getWidth() - margin;
        int maxY = mBoundsView.getHeight() - mTimeLabel.getHeight() - margin;

        int boundX = maxX - margin;
        int boundY = maxY - margin;

        if (boundX > 0 && boundY > 0)
        {
            Random r = new Random();
            int newX = r.nextInt(boundX) + margin;
            int newY = r.nextInt(boundY) + margin;
            mTimeLabel.animate().x(newX).y(newY).setDuration(100);
        }
    }
}
