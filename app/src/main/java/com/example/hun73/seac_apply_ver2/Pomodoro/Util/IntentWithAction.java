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

import android.content.Context;
import android.content.Intent;

//import com.apps.adrcotfas.goodtime.BL.SessionType;
import com.example.hun73.seac_apply_ver2.Pomodoro.BL.SessionType;

//import static com.apps.adrcotfas.goodtime.Util.Constants.SESSION_TYPE;
import static com.example.hun73.seac_apply_ver2.Pomodoro.Util.Constants.SESSION_TYPE;

public class IntentWithAction extends Intent {

    public IntentWithAction(Context context, Class<?> cls, String action) {
        super(context, cls);
        this.setAction(action);
    }

    public IntentWithAction(Context context, Class<?> cls, String action, SessionType sessionType) {
        super(context, cls);
        this.setAction(action);
        this.putExtra(SESSION_TYPE, sessionType.toString());
    }
}
