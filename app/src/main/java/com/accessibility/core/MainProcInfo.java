package com.accessibility.core;

import android.content.Context;

import com.accessibility.R;
import com.accessibility.db.AppDBHelper;

import z.frame.ActivityManager;
import z.frame.BaseFragment;
import z.frame.ICommon;


// 主进程信息
public class MainProcInfo implements ICommon {
    public ActivityManager mActivityManager = null;

    public void init(Context ctx) {
        initCommonLibs(ctx);
        //检查AC,YY文件夹，如果是第一次由于文件可能过大过多，先对其进行压缩删除操作
        //移至SyncMgr类中具体操作
    }

    public void uninit() {

    }

    private void initCommonLibs(Context ctx) {
        mActivityManager = new ActivityManager() {
            @Override
            public void onResume(BaseFragment frag) {
                super.onResume(frag);
//                SyncMgr.onActive();// 程序激活了
            }
        };
//        app.isDebug = GlobaleParms.isDebug;
        app.init(ctx);
        app.app_name = ctx.getString(R.string.app_name);
        app.am = mActivityManager;
        app.db = new AppDBHelper();
//        app.Toast_Layout = R.layout.toast_normal;
//		app.Toast_Animtion = R.style.anim_view;
//        app.Toast_ID_Text = R.id.mTvContent;
//        app.Loading_Layout = R.layout.loading_dialog_new;
//        app.Loading_Style = R.style.LoadingDialog;
    }
}
