package com.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.accessibility.utils.AccessibilityLog;

import java.util.List;

/**
 * Created by popfisher on 2017/7/11.
 */

@TargetApi(16)
public class AccessibilityOperator {

    private Context mContext;
    private static AccessibilityOperator mInstance = new AccessibilityOperator();
    private AccessibilityEvent mAccessibilityEvent;
    private AccessibilityService mAccessibilityService;

    private AccessibilityOperator() {
    }

    public static AccessibilityOperator getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void updateEvent(AccessibilityService service, AccessibilityEvent event) {
        if (service != null && mAccessibilityService == null) {
            mAccessibilityService = service;
        }
        if (event != null) {
            mAccessibilityEvent = event;
        }
    }

    public boolean isServiceRunning() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Short.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            if (info.service.getClassName().equals(mContext.getPackageName() + ".AccessibilitySampleService")) {
                return true;
            }
        }
        return false;
    }

    private AccessibilityNodeInfo getRootNodeInfo() {
        if (mAccessibilityEvent == null || mAccessibilityService == null){
            return null;
        }
        AccessibilityEvent curEvent = mAccessibilityEvent;
        AccessibilityNodeInfo nodeInfo = null;
        if (Build.VERSION.SDK_INT >= 21) {
            //1.sdk21,系统大于等于6.0以上使用这个方法getWindows()获取到多个AccessibilityWindowInfo
            //2.debug知道MI4手机的第二个元素是当前app的window，所有listWindowInfo.get(1)获取到AccessibilityWindowInfo
            //3.
            List<AccessibilityWindowInfo> listWindowInfo = mAccessibilityService.getWindows();
            if (listWindowInfo != null && listWindowInfo.size() > 0){
                AccessibilityWindowInfo windowInfo =  listWindowInfo.get(listWindowInfo.size() - 1);
                nodeInfo = windowInfo.getRoot();
                AccessibilityLog.printLog("nodeInfo: " + nodeInfo);
            }
        }else if (Build.VERSION.SDK_INT >= 16) {
            // 使用getRootInActiveWindow，这样不依赖当前的事件类型，但是有些时候不一定能够获取到对应的AccessibilityNodeInfo对象找不到对应的元素
            if (mAccessibilityService != null) {
                nodeInfo = mAccessibilityService.getRootInActiveWindow();
                AccessibilityLog.printLog("nodeInfo: " + nodeInfo);
            }
            // 下面这个必须依赖当前的AccessibilityEvent
//            if(curEvent!= null && (nodeInfo == null || nodeInfo.getChildCount() == 0)){
//                nodeInfo = curEvent.getSource();
//            }
        } else {
            nodeInfo = curEvent.getSource();
        }
        return nodeInfo;
    }

    /**
     * 根据Text搜索所有符合条件的节点, 模糊搜索方式
     */
    public List<AccessibilityNodeInfo> findNodesByText(String text) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
           return nodeInfo.findAccessibilityNodeInfosByText(text);
        }
        return null;
    }

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     * @param viewId
     */
    public List<AccessibilityNodeInfo> findNodesById(String viewId) {
        AccessibilityNodeInfo nodeInfo = getRootNodeInfo();
        if (nodeInfo != null) {
            if (Build.VERSION.SDK_INT >= 18) {
                return nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
            }
        }
        return null;
    }

    public boolean clickByText(String text) {
        return performClick(findNodesByText(text));
    }

    /**
     * 根据View的ID搜索符合条件的节点,精确搜索方式;
     * 这个只适用于自己写的界面，因为ID可能重复
     * api要求18及以上
     * @param viewId
     * @return 是否点击成功
     */
    public boolean clickById(String viewId) {
        return performClick(findNodesById(viewId));
    }

    private boolean performClick(List<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfos != null && !nodeInfos.isEmpty()) {
            AccessibilityNodeInfo node;
            for (int i = 0; i < nodeInfos.size(); i++) {
                node = nodeInfos.get(i);
                // 获得点击View的类型
                AccessibilityLog.printLog("View类型：" + node.getClassName());
                AccessibilityLog.printLog("修改click前，node.isEnabled()=" + node.isEnabled()+"， node.isClickable()"+node.isClickable());
//                if (!node.isClickable()){
                    //setContextClickable(),这个方法不管用
//                    node.setContextClickable(true);
//                }
//                AccessibilityLog.printLog("修改click后，node.isEnabled()=" + node.isEnabled()+"， node.isClickable()"+node.isClickable());
                // 进行模拟点击
                if (node.isEnabled()) {
                    //1.首先获取目标节点是否可以可以点击isClickable()=true,如果可以点击，点击肯定成功
                    //2.如果isClickable()=false,则获取到其父布局查询是否可以点击，否则继续找其父布局
                    //说明：如果这个按钮或者TextView手动可以点击的情况下，则其父布局中至少有一个的isClickable()=true的
                    boolean isSuccess = node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    AccessibilityLog.printLog("isSuccess)=" + isSuccess);
                    if (!isSuccess){
                        isSuccess = node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    AccessibilityLog.printLog("Parent isSuccess)=" + isSuccess);


                    return isSuccess;
                }
            }
        }
        return false;
    }

    public boolean clickBackKey() {
        return performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    private boolean performGlobalAction(int action) {
        return mAccessibilityService.performGlobalAction(action);
    }
}
