package com.example.lgf.messenger.demo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Messenger messenger;
    private ClientMessengerHandler clientMessengerHandler = new ClientMessengerHandler();

    private static class ClientMessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_SERVER:
                    Log.i("MainActivity", msg.getData().getString(Constant.BUNDLE_KEY_MSG));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            messenger = new Messenger(service);
            Message message = Message.obtain(null, Constant.MSG_FROM_CLIENT);
            Bundle bundle = new Bundle();
            bundle.putString(Constant.BUNDLE_KEY_MSG, "from client:Hello, this is client.");
            message.setData(bundle);
            message.replyTo = new Messenger(clientMessengerHandler);
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intent = new Intent(this, RemoteService.class); // 同应用多进程时用
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName("com.example.lgf.remoteservice.demo", "com.example.lgf.remoteservice.demo.RemoteService");
        intent.setComponent(componentName);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);

    }
}
