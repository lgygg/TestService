package com.lgy.testservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lgy.util.LogUtils;
import com.lgy.util.event.LGYEventBus;
import com.lgy.util.event.NormalEvent;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyService extends Service {
    public MyService() {
        LogUtils.e(getClass().getCanonicalName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("onCreate");
        CharSequence date = android.text.format.DateFormat.format("yyyy-mm-dd hh-mm-ss",Calendar.getInstance().getTime());
        NormalEvent<CharSequence> event = new NormalEvent<CharSequence>(date,"LGY");
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                event.setData(android.text.format.DateFormat.format("yyyy-mm-dd hh-mm-ss",Calendar.getInstance().getTime()));
                LGYEventBus.getInstance().post(NormalEvent.class,event);
            }
        },0,1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e("onBind");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.e("onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
    }
}