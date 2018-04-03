package com.accessibility;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.accessibility.activity.SecondAct;
import com.accessibility.core.FileCenter;
import com.accessibility.utils.PWDUtil;
import com.k12lib.afast.log.Logger;

import z.util.Cache;

public class AccessibilityMainActivity extends Activity implements View.OnClickListener {
    public static String TAG = AccessibilityMainActivity.class.getSimpleName();
    public static String KEY_CODE = "code";
    public static String KEY_ACTCODE = "actcode";
    public static String KEY_FILE = "0";

    private TextView mTvPWD;
    private EditText mEtActCode;
    private Button mBtnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_main);
        initView();
        fillData();
    }

    private void initView() {
        mBtnEnter = (Button) findViewById(R.id.mBtnEnter);
        mTvPWD = (TextView) findViewById(R.id.mTvPWD);
        mEtActCode = (EditText) findViewById(R.id.mEtActCode);
    }

    private void fillData() {
        AccessibilityOperator.getInstance().init(this);
        Cache cache = new Cache(FileCenter.getRootDir().getAbsolutePath(), KEY_FILE, true);
        cache.load();
        String code = cache.getString(KEY_CODE, "");
        String actCode = cache.getString(KEY_ACTCODE, "");
        mTvPWD.setText(code);
        mEtActCode.setText(actCode);
        Logger.i(TAG, "code="+code+" ,actCode="+actCode);
        if (PWDUtil.decrypt(actCode)) {
            mBtnEnter.setVisibility(View.VISIBLE);
        }else {

        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.mBtnEnter:
            startActivity(new Intent(this, AccessibilityNormalSample.class));
            break;
        case R.id.mBtnNewCode:
            startActivity(new Intent(this, SecondAct.class));
            break;
        case R.id.mBtnNew: {
            String code = mTvPWD.getText().toString();
            if (TextUtils.isEmpty(code) || code.equals("")) {
                mTvPWD.setText(PWDUtil.encryption());
            } else {
                Toast.makeText(this, "序列号已存在", Toast.LENGTH_SHORT).show();
            }
        }
        break;
        case R.id.mBtnCopy:
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 将文本内容放到系统剪贴板里。
            cm.setText(mTvPWD.getText());
            Toast.makeText(this, "复制成功", Toast.LENGTH_SHORT).show();
            break;
        case R.id.mBtnAct: {
            //1.激活校验
            //2.保存序列号及激活码
            String actCode = mEtActCode.getText().toString().trim();
            String code = mTvPWD.getText().toString().trim();
            if (PWDUtil.decrypt(actCode)) {
                Cache cache = new Cache(FileCenter.getRootDir().getAbsolutePath(), KEY_FILE, true);
                cache.load();
                cache.put(KEY_CODE, code);
                cache.put(KEY_ACTCODE, actCode);
                cache.save();
                mBtnEnter.setVisibility(View.VISIBLE);
                Logger.i(TAG, "激活成功 >>> ");
            } else {
                Toast.makeText(this, "激活码错误", Toast.LENGTH_SHORT).show();
            }
        }
        break;
        }
    }

}
