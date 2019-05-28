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

import android.app.Application;

import com.example.hun73.seac_apply_ver2.Pomodoro.BL.SessionType;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class TimerActivityViewModel extends AndroidViewModel
{

    /**
     * If the main activity is visible or not
     */
    public boolean isActive;
    /**
     * The session type of the pending dialog. INVALID if no finished session dialog should be shown
     */
    public SessionType dialogPendingType;
    public TimerActivityViewModel(@NonNull Application application) {
        super(application);
        isActive = false;
        dialogPendingType = SessionType.INVALID;
    }
}
