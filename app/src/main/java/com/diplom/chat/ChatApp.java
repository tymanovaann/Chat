package com.diplom.chat;

import android.app.Application;

import androidx.multidex.MultiDex;

public class ChatApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
