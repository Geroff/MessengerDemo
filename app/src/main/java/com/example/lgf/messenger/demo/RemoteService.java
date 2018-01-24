package com.example.lgf.messenger.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/22.
 */

public class RemoteService extends Service {
    private MessengerHandler messengerHandler = new MessengerHandler();
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.MSG_FROM_CLIENT:
                    Log.i("MessengerService", msg.getData().getString(Constant.BUNDLE_KEY_MSG));
                    Message message = Message.obtain(null, Constant.MSG_FROM_SERVER);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.BUNDLE_KEY_MSG, "from server: em, I have receive your message!");
                    message.setData(bundle);
                    Messenger replyTo = msg.replyTo;
                    try {
                        replyTo.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private Messenger messenger = new Messenger(messengerHandler);

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
