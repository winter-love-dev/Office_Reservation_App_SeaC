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

package com.example.hun73.seac_apply_ver2.Pomodoro.Statistics.Main;

import com.example.hun73.seac_apply_ver2.Pomodoro.Util.StringUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import static com.example.hun73.seac_apply_ver2.Pomodoro.Statistics.Main.SpinnerProductiveTimeType.DAY_OF_WEEK;
import static com.example.hun73.seac_apply_ver2.Pomodoro.Statistics.Main.SpinnerProductiveTimeType.HOUR_OF_DAY;


/**
 * Custom formatter used to print the day of the week or the hour of the day.
 */
class ProductiveTimeXAxisFormatter implements IAxisValueFormatter {

    private final SpinnerProductiveTimeType mType;
    ProductiveTimeXAxisFormatter(SpinnerProductiveTimeType type) {
        mType = type;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if ((mType == HOUR_OF_DAY) && (value < 24) && (value >= 0)) {
            return StringUtils.toHourOfDay((int) value);
        } else if ((mType == DAY_OF_WEEK) && (value < 7) && (value >= 0)) {
            return StringUtils.toDayOfWeek((int) (value + 1));
        } else {
            return "";
        }
    }
}
