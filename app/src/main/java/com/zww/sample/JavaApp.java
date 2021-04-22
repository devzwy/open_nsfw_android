package com.zww.sample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.widget.Toast;

import io.github.devzwy.nsfw.NSFWHelper;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

public class JavaApp extends Application {
    @SuppressLint("NewApi")
    @Override
    public void onCreate() {
        super.onCreate();
        NSFWHelper.INSTANCE.initHelper(this, this.getFilesDir().getPath() + "/nsfw.tflite", true, 4, new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Toast.makeText(JavaApp.this, "初始化成功", Toast.LENGTH_SHORT).show();
                return null;
            }
        }, new Function1<String, Unit>() {
            @Override
            public Unit invoke(String s) {
                Toast.makeText(JavaApp.this, s, Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }
}
