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
        StringBuilder sb = new StringBuilder();
        int randomLen = new Random().nextInt(11);
        for (int i = 0; i < randomLen; i++) {
            sb.append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
        }
        for (int i = 0; i < app.deviceId.length(); i++) {
            sb.append(mCodeY[new Random().nextInt(mCodeY.length - 1)]).append(app.deviceId.charAt(i));
        }
        Logger.i(TAG, "deviceId=" + app.deviceId + ", ms=" + ms);
        Logger.i(TAG, "加密=" + sb.toString());
        String str = String.valueOf(ms);
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char a = arr[i];
            String b = mCodeX[Integer.parseInt(a + "")];
            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
        }
        sb.append(randomLen);
        Logger.i(TAG, "desc=" + sb.toString());
        return sb.toString();
    }

//    public static String encryption2(String deviceId, String oldtms, String newtms) {
//        Logger.i(TAG, "deviceId=" + deviceId + ", oldtms=" + oldtms+ ", newtms=" + newtms);
//        int randomLen = new Random().nextInt(11);
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < randomLen; i++) {
//            sb.append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
//        }
//        for (int i = 0; i < deviceId.length(); i++) {
//            sb.append(mCodeY[new Random().nextInt(mCodeY.length - 1)]).append(deviceId.charAt(i));
//        }
//        char[] oldArr = oldtms.toCharArray();
//        for (int i = 0; i < oldArr.length; i++) {
//            char a = oldArr[i];
//            String b = mCodeX[Integer.parseInt(a + "")];
//            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
//        }
//        char[] newArr = newtms.toCharArray();
//        for (int i = 0; i < newArr.length; i++) {
//            char a = newArr[i];
//            String b = mCodeX[Integer.parseInt(a + "")];
//            sb.append(b).append(mCodeY[new Random().nextInt(mCodeY.length - 1)]);
//        }
//        sb.append(randomLen);
//        Logger.i(TAG, "desc=" + sb.toString());
//        return sb.toString();
//    }

    //解密 固定码+mac地址+结束时间戳
    public static boolean decrypt(String code) {
        //校验mac地址
        Logger.i(TAG, "激活 code=" + code);
        String deviceId = parseDeviceID(code,1);
        Logger.i(TAG, "激活 deviceId=" + deviceId);
        if (TextUtils.isEmpty(deviceId) || !deviceId.equals(app.deviceId)) {
            //不是同一台机器
            Logger.i(TAG, "激活 不是同一台机器");
            return false;
        }
        long newms = checkTimes(code);
        long curms = System.currentTimeMillis() / 1000;
        Logger.i(TAG, "激活 newms=" + newms+", curms="+curms);
        if (curms < newms) {
            Logger.i(TAG, "激活 时间戳有问题");
            return true;
        }
        return false;
    }

    public static String parseDeviceID(String code, int type) {
        if (TextUtils.isEmpty(code)) {
            return null;
        }
        int len = code.length();
        int start = Integer.parseInt(code.charAt(len-1)+"");
        int end = type == 0?len - 21:len - 41;
        String subStr = code.substring(start, end);
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < subStr.length(); i+=2) {
            sb.append(subStr.charAt(i));
        }
        Logger.i(TAG, "解析后 deviceId=" + sb.toString());
        if (TextUtils.isEmpty(sb.toString())) {
            return null;
        }
        return sb.toString();
    }

    public static long checkTimes(String code) {
        if (TextUtils.isEmpty(code)) {
            return -1;
        }
        int len = code.length();
        String copyCode = code.substring(len - 21, len-1);
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
