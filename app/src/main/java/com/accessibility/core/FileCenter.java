package com.accessibility.core;


import android.os.Environment;

import java.io.File;


public class FileCenter {

    // 是否存在sd卡
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    // 获取存取根目录
    public static File getRootDir() {
        File root = hasSDCard() ? Environment.getExternalStorageDirectory()
                : MainApplication.getApplication().getFilesDir();
        root = new File(root, "ztime");
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    // 获取根目录路径
    public static String getRootPath() {
        return getRootDir().getAbsolutePath();
    }

    //获取作业的文件夹
    public static File getTaskRootDir() {
        return new File(getRootDir(), "task");
    }

    public static String getTaskRootDirPath() {
        return new File(getRootDir(), "task").getAbsolutePath();
    }



}
