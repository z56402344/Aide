package com.accessibility.net;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class OkHttpItem {

    public static final int onFailure = 0;
    public static final int onResponse = 1;

    private IOKHttpErrLis mCallBack = null;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
            case onFailure:
                if (mCallBack == null) return false;
                String errorMsg = (String) msg.obj;
                mCallBack.onOkHttpError(errorMsg);
                break;
            case onResponse:
                if (mCallBack == null) return false;
                String body = (String) msg.obj;
                Log.i("onResponse", body);
                mCallBack.onOkHttpSuccess(body);
                break;
            }

            return false;
        }
    });

    public void sendHandler(int what, int arg1, int arg2, Object obj) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }


    public interface IOKHttpErrLis {
        void onOkHttpSuccess(String body);

        void onOkHttpError(String msg);
    }
}
