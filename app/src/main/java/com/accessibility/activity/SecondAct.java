package com.accessibility.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.accessibility.R;
import com.accessibility.utils.PWDUtil;
import com.k12lib.afast.log.Logger;

import z.frame.BaseAct;


/**
 * 第二层页面，msg，设置
 */
public class SecondAct extends BaseAct {
    public static String TAG = SecondAct.class.getSimpleName();

    private TextView mTvPWD,mTCode,mTvAct;
    private EditText mEtActCode;
    private String mCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_second);
        initView();
        fillData();
    }

    private void initView() {
        mTvPWD = (TextView) findViewById(R.id.mTvPWD);
        mTCode = (TextView) findViewById(R.id.mTCode);
        mTvAct = (TextView) findViewById(R.id.mTvAct);
        mEtActCode = (EditText) findViewById(R.id.mEtActCode);
    }

    private void fillData() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm == null || TextUtils.isEmpty(cm.getText())){
            return;
        }
        mCode = cm.getText().toString();
        mTCode.setText(mCode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.mBtnNew:
            long tm = PWDUtil.checkTimes(mCode);
            String devid = PWDUtil.parseDeviceID(mCode,0);
            mTvPWD.setText(tm+"");
            long newtm = tm + 60*60*24*30;
            String actCode = PWDUtil.encryption2(devid,String.valueOf(tm),String.valueOf(newtm));
            Logger.i(TAG, "log devid=" + devid+", tm="+tm+", newtm="+newtm+", actCode="+actCode);
            mTvAct.setText(actCode);
            break;
        case R.id.mBtnCopy:
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(mTvAct.getText());
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
            break;
        }
    }

}
