package com.accessibility;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.accessibility.core.FidAll;
import com.accessibility.utils.AccessibilityLog;

import java.util.Random;

/**
 * 点击事件主逻辑
 */

public class AccessibilityNormalSample extends Activity implements View.OnClickListener {

    public static final int FID = FidAll.AccessibilityNormalSampleFID;
    public static final int IA_CLICK = FID+3;

    private EditText mEtTime, mEtName;
    private Button mBtnStop, mBtnStart;

//    private DelayAction mDelay = new DelayAction(); // 防止点击太频繁
    private String mName = "确认";//轮询的查找的按钮名称
    private int mTime = 130;//轮询的间隔时间
    private boolean isStop = true;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case IA_CLICK:
                if (isStop) {
                    return;
                }
                postHandler();
                break;
            default:
                super.handleMessage(msg);
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_normal_sample);
        initViews();
    }

    private void initViews() {
        mEtTime = (EditText) findViewById(R.id.mEtTime);
        mEtName = (EditText) findViewById(R.id.mEtName);
        mBtnStop = (Button) findViewById(R.id.mBtnStop);
        mBtnStart = (Button) findViewById(R.id.mBtnStart);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.open_accessibility_setting:
            OpenAccessibilitySettingHelper.jumpToSettingPage(this);
            break;
        case R.id.mBtnStart:
            mBtnStop.setVisibility(View.VISIBLE);
            mBtnStart.setVisibility(View.GONE);
            isStop = false;
            mName = mEtName.getText().toString();
            mTime = Integer.valueOf(mEtTime.getText().toString());
            postHandler();
            break;
        case R.id.mBtnStop:
            mBtnStop.setVisibility(View.GONE);
            mBtnStart.setVisibility(View.VISIBLE);
            isStop = true;
            mHandler.removeMessages(IA_CLICK);
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void postHandler() {
        Random random = new Random();
        int postTime = mTime + random.nextInt(150);
        AccessibilityLog.printLog("postTime="+postTime);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                simulationClickByText2();
                mHandler.sendEmptyMessage(IA_CLICK);
            }
        }, postTime);
    }


    private void simulationClickByText2() {
        boolean result = AccessibilityOperator.getInstance().clickByText(mName);
        AccessibilityLog.printLog(result ? mName+"点击成功" : mName+"点击失败");
    }

    private void simulationClickByText() {
        boolean result = AccessibilityOperator.getInstance().clickByText("复选框开关");
        AccessibilityLog.printLog(result ? "复选框模拟点击成功" : "复选框模拟点击失败");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("单选按钮");
                AccessibilityLog.printLog(result ? "单选按钮模拟点击成功" : "单选按钮模拟点击失败");
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("OFF");
                AccessibilityLog.printLog(result ? "OnOff开关模拟点击成功" : "OnOff开关模拟点击失败");
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickByText("退出本页面");
                AccessibilityLog.printLog(result ? "退出本页面模拟点击成功" : "退出本页面模拟点击失败");
            }
        }, 6000);
    }

    private void simulationClickById() {
        boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_checkbox");
        AccessibilityLog.printLog(result ? "复选框模拟点击成功" : "复选框模拟点击失败");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_radiobutton");
                AccessibilityLog.printLog(result ? "单选按钮模拟点击成功" : "单选按钮模拟点击失败");
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_togglebutton");
                AccessibilityLog.printLog(result ? "OnOff开关模拟点击成功" : "OnOff开关模拟点击失败");
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                boolean result = AccessibilityOperator.getInstance().clickById("com.accessibility:id/normal_sample_back");
//                AccessibilityLog.printLog(result ? "退出本页面模拟点击成功" : "退出本页面模拟点击失败");
                // 下面这个模拟点击系统返回键
                boolean result = AccessibilityOperator.getInstance().clickBackKey();
                AccessibilityLog.printLog(result ? "返回键模拟点击成功" : "返回键模拟点击失败");
            }
        }, 6000);
    }

}
