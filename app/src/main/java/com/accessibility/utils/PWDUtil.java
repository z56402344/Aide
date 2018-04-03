package com.accessibility.utils;

import android.text.TextUtils;

import com.k12lib.afast.log.Logger;

import java.util.Random;

import z.frame.ICommon;

/**
 * 加密&解密
 */
public class PWDUtil implements ICommon {
    public static String TAG = PWDUtil.class.getSimpleName();

    public static String[] mCodeX = {"y", "p", "m", "g", "c", "n",
            "a", "o", "d", "z", "h",
            "s", "x", "v", "r", "l",
            "i", "e", "b", "j", "t",
            "w", "f", "u", "k", "q"};

    public static String[] mCodeY = {"7", "y", "p", "2", "m", "g",
            "c", "8", "n", "6", "a", "o",
            "d", "1", "z", "h", "s", "x",
            "5", "v", "r", "0", "l", "i",
            "4", "e", "b", "j", "t", "w",
            "9", "f", "u", "k", "3", "q"};

    //加密 mac地址+开始时间戳+固定码
    public static String encryption() {
        long ms = System.currentTimeMillis() / 1000;
        StringBuilder sb = new StringBuilder(app.deviceId);
//        sb.append(ms);
        Logger.i(TAG, "deviceId=" + app.deviceId + ", ms=" + ms);
        Logger.i(TAG, "加密=" + sb.toString());
        String str = String.valueOf(ms);
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char a = arr[i];
            String b = mCodeX[Integer.parseInt(a + "")];
            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
        }
        Logger.i(TAG, "desc=" + sb.toString());
        return sb.toString();
    }

    public static String encryption2(String deviceId, String oldtms, String newtms) {
        StringBuilder sb = new StringBuilder(deviceId);
        Logger.i(TAG, "deviceId=" + deviceId + ", oldtms=" + oldtms);
        char[] oldArr = oldtms.toCharArray();
        for (int i = 0; i < oldArr.length; i++) {
            char a = oldArr[i];
            String b = mCodeX[Integer.parseInt(a + "")];
            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
        }
        char[] newArr = newtms.toCharArray();
        for (int i = 0; i < newArr.length; i++) {
            char a = newArr[i];
            String b = mCodeX[Integer.parseInt(a + "")];
            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
        }
        Logger.i(TAG, "desc=" + sb.toString());
        return sb.toString();
    }

    //解密 固定码+mac地址+结束时间戳
    public static boolean decrypt(String code) {
        //校验mac地址
        Logger.i(TAG, "激活 code=" + code);
        String deviceId = parseDeviceID(code,1);
        Logger.i(TAG, "激活 deviceId=" + deviceId);
        if (TextUtils.isEmpty(deviceId) || !deviceId.equals(app.deviceId)) {
            //不是同一台机器
            return false;
        }
        long newms = checkTimes(code);
        long curms = System.currentTimeMillis() / 1000;
        Logger.i(TAG, "激活 newms=" + newms+", curms="+curms);
        if (curms < newms) {
            return true;
        }
        return false;
    }

    public static String parseDeviceID(String code, int type) {
        if (TextUtils.isEmpty(code)) {
            return null;
        }
        int len = code.length();
        int end = type == 0?len - 20:len - 40;
        String deviceId = code.substring(0, end);
        Logger.i(TAG, "解析后 deviceId=" + deviceId);
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        if (!deviceId.equals(app.deviceId)) {
            //不是同一台机器
            return null;
        }
        return deviceId;
    }

    public static long checkTimes(String code) {
        if (TextUtils.isEmpty(code)) {
            return -1;
        }
        int len = code.length();
        String copyCode = code.substring(len - 20, len);
        char[] chars = copyCode.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i += 2) {
            String str = chars[i] + "";
            for (int j = 0; j < mCodeX.length; j++) {
                if (str.equals(mCodeX[j])) {
                    sb.append(j);
                    break;
                }
            }
        }
        Logger.i(TAG, "解析后 ms=" + sb.toString());
        try {
            return Long.parseLong(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
