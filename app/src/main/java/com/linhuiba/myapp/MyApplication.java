package com.linhuiba.myapp;

import android.app.Application;

import io.flutter.view.FlutterMain;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlutterMain.startInitialization(this);
    }
}
