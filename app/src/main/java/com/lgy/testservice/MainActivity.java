package com.lgy.testservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.lgy.util.LogUtils;
import com.lgy.util.UtilsBridge;
import com.lgy.util.event.LGYEventBus;
import com.lgy.util.event.NormalEvent;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class MainActivity extends AppCompatActivity {

    private Disposable disposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UtilsBridge.getInstance().init(this);

        testIPCBindService();
//        Intent intent = new Intent(this,MyService.class);
//        startService(intent);
//        bindService(intent, new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                LogUtils.e("onServiceConnected");
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                LogUtils.e("onServiceDisconnected");
//            }
//        }, Context.BIND_AUTO_CREATE);
//        disposable = LGYEventBus.getInstance().toObservable(NormalEvent.class, new Consumer<NormalEvent>() {
//            @Override
//            public void accept(NormalEvent normalEvent) throws Throwable {
//                if (normalEvent.getTag() == "LGY") {
//                    LogUtils.e(normalEvent.getData());
//
//                }
//            }
//        });

    }

    private IBinder mBinder;
    private void testIPCBindService(){
        Intent intent = new Intent();
        intent.setAction("lgy.IPCService");
        intent.setPackage("com.lgy.testservice");
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                IInterface iInterface = service.queryLocalInterface(IPCService.DESCRIPTOR);
                if (iInterface != null && iInterface instanceof IPCService) {
                    //本地服务则直接调用服务
                    mBinder = service;
                }else{
                    //如果是其他进程的服务，则需要调用transact方法来发送参数，并通过transact方法里的reply参数获取结果
                    //当然，标准的做法是把下面的代码放到远程服务的Proxy里，通过通过asInterface方法来判断是返回本地binder还是Proxy的binder
                    //可以查看app\build\generated\aidl_source_output_dir\debug\out\com\lgy\testservice\下的文件，里面有针对.aidl文件自动生成的类
                    android.os.Parcel data = android.os.Parcel.obtain();
                    android.os.Parcel reply = android.os.Parcel.obtain();
                    String result = null;
                    int postion = 1;
                    try {
                        //写入信息
                        data.writeInterfaceToken("IPCService");
                        data.writeInt(postion);
                        //发送消息
                        service.transact(0x001, data, reply, 0);
                        //读取返回信息
                        reply.readException();
                        //result即返回的结果
                        result = reply.readString();
                    }catch (RemoteException e) {
                        e.printStackTrace();
                    } finally {
                        //释放资源
                        reply.recycle();
                        data.recycle();
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },Context.BIND_AUTO_CREATE);
    }

    private void testFilterStartService(){
        Intent intent = new Intent();
        intent.setAction("lgy.Service");
        intent.setPackage("com.lgy.testservice");
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LGYEventBus.getInstance().unRegister(disposable);
    }
}