package com.accessibility.utils;

        import android.util.Log;

        import com.accessibility.BuildConfig;

/**
 * Log
 */

public class AccessibilityLog {

    private static final String TAG = "AccessibilityService";
    public static void printLog(String logMsg) {
        if (!BuildConfig.DEBUG) return;
        Log.d(TAG, logMsg);
    }
}
