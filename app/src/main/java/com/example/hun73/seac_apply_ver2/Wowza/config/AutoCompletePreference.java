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

package com.example.hun73.seac_apply_ver2.Wowza.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.wowza.gocoder.sdk.api.configuration.WOWZStreamConfig;
import com.wowza.gocoder.sdk.api.logging.WOWZLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.example.hun73.seac_apply_ver2.Wowza.Activity_Broadcast_Setting.broadcast_title;

public class AutoCompletePreference extends EditTextPreference
{
    private final static String TAG = AutoCompletePreference.class.getSimpleName();

    private final static String AUTO_COMPLETE_SUFFIX = "_auto_complete";
    private final static String AUTO_COMPLETE_HOST_CONFIG_PREFIX = "wz_live_host_config_";

    private AutoCompleteTextView mAutoCompleteEditText = null;

    private static String autoCompleteKey(String prefKey)
    {
        if (prefKey == null || prefKey.trim().length() == 0) return null;
        return prefKey + AUTO_COMPLETE_SUFFIX;
    }

    private static String hostConfigKey(String hostAddress)
    {
        if (hostAddress == null || hostAddress.trim().length() == 0) return null;
        return AUTO_COMPLETE_HOST_CONFIG_PREFIX + hostAddress.trim().toLowerCase();
    }

    private static String[] getAutoCompleteList(SharedPreferences sharedPrefs, String prefKey)
    {
        if (!sharedPrefs.contains(autoCompleteKey(prefKey))) return new String[0];

        Set<String> currentSet = sharedPrefs.getStringSet(autoCompleteKey(prefKey), null);
        if (currentSet == null) return new String[0];

        return currentSet.toArray(new String[currentSet.size()]);
    }

    private static void updateAutoCompleteList(SharedPreferences sharedPrefs, String prefKey, String newValue)
    {
        Log.e(TAG, "updateAutoCompleteList: 실행됨" );

        if (prefKey == null || prefKey.trim().length() == 0) return;
        if (newValue == null || newValue.trim().length() == 0) return;

        Set<String> currentSet = sharedPrefs.getStringSet(autoCompleteKey(prefKey), null);
        TreeSet<String> curValues = currentSet != null ? new TreeSet<String>(currentSet) : new TreeSet<String>();

        for (String str : curValues)
        {
            if (str.equalsIgnoreCase(newValue.trim()))
                return;
        }
        curValues.add(newValue.trim());

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(autoCompleteKey(prefKey), curValues);
        editor.apply();
    }

    public static void clearAutoCompleteList(SharedPreferences sharedPrefs, String prefKey)
    {
        if (prefKey == null || prefKey.trim().length() == 0) return;
        if (!sharedPrefs.contains(autoCompleteKey(prefKey))) return;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(autoCompleteKey(prefKey));
        editor.apply();
    }

    public static void clearAllAutoCompleteLists(SharedPreferences sharedPrefs)
    {

        clearAutoCompleteList(sharedPrefs, "wz_live_port_number");
        clearAutoCompleteList(sharedPrefs, "wz_live_app_name");
        clearAutoCompleteList(sharedPrefs, "wz_live_stream_name");
        clearAutoCompleteList(sharedPrefs, "wz_live_username");

        String[] hosts = getAutoCompleteList(sharedPrefs, "wz_live_host_address");
        clearAutoCompleteList(sharedPrefs, "wz_live_host_address");

        SharedPreferences.Editor editor = sharedPrefs.edit();
        for (String hostAddress : hosts)
        {
            String hostConfigKey = hostConfigKey(hostAddress);
            if (sharedPrefs.contains(hostConfigKey))
                editor.remove(hostConfigKey);
        }
        editor.apply();
    }

    static String ConnectionAddress = "192.168.0.62";

    public static WOWZStreamConfig loadAutoCompleteHostConfig(SharedPreferences sharedPrefs, String hostAddress)
    {
        Log.e(TAG, "loadAutoCompleteHostConfig: 실행됨. 방송 설정 가져오기" );
        if (hostAddress == null || hostAddress.trim().length() == 0) return null;

        String hostConfigKey = AutoCompletePreference.hostConfigKey(hostAddress);
        if (!sharedPrefs.contains(hostConfigKey)) return null;

        String storedConfig = sharedPrefs.getString(hostConfigKey, null);
        if (storedConfig == null) return null;

        String storedValues[] = TextUtils.split(storedConfig, ";");
        if (storedValues.length != 4)
        {
            removeAutoCompleteHostConfig(sharedPrefs, hostAddress);
            return null;
        }

        try
        {
            int port_number = Integer.parseInt(storedValues[0]);

            WOWZStreamConfig hostConfig = new WOWZStreamConfig();
            hostConfig.setHostAddress(ConnectionAddress);
            hostConfig.setPortNumber(port_number);
//            if (storedValues[1].trim().length() > 0) hostConfig.setApplicationName(storedValues[1]);
            if (storedValues[1].trim().length() > 0) hostConfig.setApplicationName(storedValues[1]);

//            if (storedValues[2].trim().length() > 0) hostConfig.setStreamName(storedValues[2]);
            if (storedValues[2].trim().length() > 0) hostConfig.setStreamName(broadcast_title);

            if (storedValues[3].trim().length() > 0) hostConfig.setUsername(storedValues[3]);

            return hostConfig;
        } catch (NumberFormatException e)
        {
            removeAutoCompleteHostConfig(sharedPrefs, hostAddress);
            return null;
        }
    }

    // 자동 완성 호스트 구성 저장
    public static void storeAutoCompleteHostConfig(SharedPreferences sharedPrefs, WOWZStreamConfig streamConfig)
    {
        Log.e(TAG, "storeAutoCompleteHostConfig: 실행됨");
        String hostAddress = streamConfig.getHostAddress();
        if (hostAddress == null || hostAddress.trim().length() == 0) return;
        WOWZLog.debug("UPDATING SHARED HOST CONFIG!");

        AutoCompletePreference.updateAutoCompleteHostsList(sharedPrefs, hostAddress);

        AutoCompletePreference.updateAutoCompleteList(sharedPrefs, "wz_live_port_number", Integer.toString(streamConfig.getPortNumber()));
        AutoCompletePreference.updateAutoCompleteList(sharedPrefs, "wz_live_app_name", streamConfig.getApplicationName());

        //        AutoCompletePreference.updateAutoCompleteList(sharedPrefs, "wz_live_stream_name", streamConfig.getStreamName());
        AutoCompletePreference.updateAutoCompleteList(sharedPrefs, "wz_live_stream_name", broadcast_title);
        Log.e(TAG, "storeAutoCompleteHostConfig: broadcast_title: " + broadcast_title );

        AutoCompletePreference.updateAutoCompleteList(sharedPrefs, "wz_live_username", streamConfig.getUsername());

        String hostConfigKey = AutoCompletePreference.hostConfigKey(hostAddress);
        String storedConfig = (
                streamConfig.getPortNumber())
                + ";" +
                (streamConfig.getApplicationName() != null ? streamConfig.getApplicationName().trim() : "")
                + ";" +
                (broadcast_title != null ? broadcast_title.trim() : "")
                + ";" +
                (streamConfig.getUsername() != null ? streamConfig.getUsername().trim() : ""
                );

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(hostConfigKey, storedConfig);
        editor.apply();
    }

    // updateAutoCompleteHostsList
    private static boolean updateAutoCompleteHostsList(SharedPreferences sharedPrefs, String hostAddress)
    {
        Log.e(TAG, "updateAutoCompleteHostsList: 실행됨" );
        if (hostAddress == null || hostAddress.trim().length() == 0) return false;

        Set<String> currentSet = sharedPrefs.getStringSet(autoCompleteKey("wz_live_host_address"), null);
        ArrayList<String> currentList = (currentSet != null ? new ArrayList<String>(currentSet) : new ArrayList<String>());

        String currentEntry = null;
        for (String storedHost : currentList)
        {
            if (storedHost.equalsIgnoreCase(hostAddress.trim()))
                currentEntry = storedHost;
        }
        if (currentEntry != null) currentSet.remove(currentEntry);

        currentList.add(0, hostAddress);
        currentSet = new HashSet<String>(currentList);

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putStringSet(autoCompleteKey("wz_live_host_address"), currentSet);
        editor.apply();

        return true;
    }

    public static void removeAutoCompleteHostConfig(SharedPreferences sharedPrefs, String hostAddress)
    {
        Log.e(TAG, "removeAutoCompleteHostConfig: 실행됨" );
        if (hostAddress == null || hostAddress.trim().length() == 0) return;

        String hostConfigKey = AutoCompletePreference.hostConfigKey(hostAddress);
        if (!sharedPrefs.contains(hostConfigKey)) return;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(hostConfigKey);
        editor.apply();
    }

    public static void logAutoCompleteLists(SharedPreferences sharedPrefs)
    {
        String[] hosts = getAutoCompleteList(sharedPrefs, "wz_live_host_address");

        StringBuilder logData = new StringBuilder(
                "wz_live_host_address = " + Arrays.toString(hosts) + "\n" +
                        "wz_live_port_number  = " + Arrays.toString(getAutoCompleteList(sharedPrefs, "wz_live_port_number")) + "\n" +
                        "wz_live_app_name     = " + Arrays.toString(getAutoCompleteList(sharedPrefs, "wz_live_app_name")) + "\n" +
//                        "wz_live_stream_name  = " + Arrays.toString(getAutoCompleteList(sharedPrefs, "wz_live_stream_name")) + "\n" +
                        "wz_live_app_name     = " + broadcast_title + "\n" +
                        "wz_live_username     = " + Arrays.toString(getAutoCompleteList(sharedPrefs, "wz_live_username")) + "\n");

        for (String hostAddress : hosts)
        {
            logData.append("\nhostConfig for " + hostAddress + ":\n\n");
            WOWZStreamConfig hostConfig = loadAutoCompleteHostConfig(sharedPrefs, hostAddress);
            logData.append(hostConfig.toString() + "\n");
        }

        WOWZLog.debug(TAG, logData.toString());
    }

    public AutoCompletePreference(Context context)
    {
        super(context);
    }

    public AutoCompletePreference(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoCompletePreference(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindDialogView(View view)
    {
        Log.e(TAG, "onBindDialogView: 실행됨" );
        super.onBindDialogView(view);

        // find the current EditText object
        final EditText editText = (EditText) view.findViewById(android.R.id.edit);

        // construct a new editable autocomplete object with the appropriate params
        // and id that the TextEditPreference is expecting
        // 적절한 매개 변수를 사용하여 편집 가능한 자동 완성 객체를 새로 만듭니다.
        // TextEditPreference가 기대하는 ID입니다.
        mAutoCompleteEditText = new AutoCompleteTextView(getContext());

        mAutoCompleteEditText.setLayoutParams(editText.getLayoutParams());
        mAutoCompleteEditText.setImeOptions(editText.getImeOptions());
        mAutoCompleteEditText.setInputType(editText.getInputType());
        mAutoCompleteEditText.setKeyListener(editText.getKeyListener());
        mAutoCompleteEditText.setMaxLines(editText.getMaxLines());
        mAutoCompleteEditText.setText(editText.getText());

        mAutoCompleteEditText.setSelectAllOnFocus(true);
        mAutoCompleteEditText.setThreshold(1);

        // remove old edit text field from the existing layout hierarchy
        ViewGroup vg = (ViewGroup) editText.getParent();
        vg.removeView(editText);

        mAutoCompleteEditText.setId(android.R.id.edit);

        // Set auto complete values stored in shared preferences as a string set
        // 공유 환경 설정에 저장된 자동 완성 값을 문자열 세트로 설정
        String[] autoCompleteList = getAutoCompleteList(getSharedPreferences(), getKey());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, autoCompleteList);
        mAutoCompleteEditText.setAdapter(adapter);

        mAutoCompleteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean focused)
            {
                if (focused && mAutoCompleteEditText != null && mAutoCompleteEditText.getAdapter().getCount() > 0)
                    mAutoCompleteEditText.showDropDown();
            }
        });

        mAutoCompleteEditText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (((AutoCompleteTextView) view).getText().length() == 0)
                    mAutoCompleteEditText.showDropDown();
            }
        });

        // add the new view to the layout
        vg.addView(mAutoCompleteEditText);
    }

    /**
     * Because the base class does not handle this correctly
     * we need to query our injected AutoCompleteTextView for
     * the value to save
     *
     * 기본 클래스가 올바르게 처리하지 못하기 때문에
     * 우리는 삽입 된 AutoCompleteTextView를 질의 할 필요가있다.
     * 저장할 값
     */
    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mAutoCompleteEditText != null)
        {
            String value = mAutoCompleteEditText.getText().toString();
            if (callChangeListener(value))
            {
                setText(value);
            }
        }
    }

    /**
     * Again we need to override methods from the base class
     * 다시 기본 클래스의 메소드를 오버라이드해야합니다
     */
    @Override
    public EditText getEditText()
    {
        return mAutoCompleteEditText;
    }
}
