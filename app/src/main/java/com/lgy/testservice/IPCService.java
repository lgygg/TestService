package com.lgy.testservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.lgy.util.LogUtils;

public class IPCService extends Service {
 
    public static final String DESCRIPTOR = "IPCService";
    private IBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        ComponentName componentName = new ComponentName(getPackageName(),"com.lgy.testservice.IPCService");
        try {
            ServiceInfo serviceInfo = getPackageManager().getServiceInfo(componentName, PackageManager.GET_META_DATA);
            Bundle bundle = serviceInfo.metaData;
            LogUtils.e("metadata:"+bundle.getString("lgy.IPCService"));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.e("onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new IPCBinder();
        }
        return binder;
    }
 
    private final class IPCBinder extends Binder {

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code){
                case 0x001: {
                    //读取发送来的消息
                    data.enforceInterface(DESCRIPTOR);
                    int postion = data.readInt();
                    //处理
                    String result = null;
                    switch (postion) {
                        case 0:
                            result = "我是0";
                            break;
                        case 1:
                            result = "我是1";
                            break;
                        case 2:
                            result = "我是2";
                            break;
                        default:
                            result = "我是默认";
                            break;
                    }
                    //返回数据
                    reply.writeNoException();
                    reply.writeString(result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }
 
    }
}