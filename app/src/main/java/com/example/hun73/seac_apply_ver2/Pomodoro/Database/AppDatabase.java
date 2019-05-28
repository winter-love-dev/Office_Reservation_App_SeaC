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

package com.example.hun73.seac_apply_ver2.Pomodoro.Database;

import android.content.Context;


import com.example.hun73.seac_apply_ver2.Pomodoro.LabelAndColor;
import com.example.hun73.seac_apply_ver2.Session;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Session.class, LabelAndColor.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase
{

    private static final Object LOCK = new Object();
    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(Context context) {
        if (INSTANCE == null || !INSTANCE.isOpen()) {
            synchronized (LOCK) {
                if (INSTANCE == null || !INSTANCE.isOpen()) {
                    INSTANCE =  Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "goodtime-db")
                            .setJournalMode(JournalMode.TRUNCATE)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static void closeInstance() {
        if(INSTANCE.isOpen()) {
            INSTANCE.getOpenHelper().close();
        }
    }

    public abstract SessionDao sessionModel();

    public abstract LabelAndColorDao labelAndColor();
}
