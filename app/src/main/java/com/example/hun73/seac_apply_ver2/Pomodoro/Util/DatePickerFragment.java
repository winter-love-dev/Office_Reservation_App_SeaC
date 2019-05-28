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

package com.example.hun73.seac_apply_ver2.Pomodoro.Util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;

import com.example.hun73.seac_apply_ver2.R;

import org.joda.time.LocalDate;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment
{

    private DatePickerDialog.OnDateSetListener listener;
    private Calendar calendar;

    public DatePickerFragment() {
    }

    public static DatePickerFragment newInstance(DatePickerDialog.OnDateSetListener listener, Calendar calendar) {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.listener = listener;
        dialog.calendar = calendar;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog d =  new DatePickerDialog(
                getActivity(),
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        ? R.style.DialogTheme : AlertDialog.THEME_HOLO_DARK,
                listener, year, month, day);
        d.getDatePicker().setMaxDate(new LocalDate().toDateTimeAtStartOfDay().getMillis());

        return  d;
    }
}
