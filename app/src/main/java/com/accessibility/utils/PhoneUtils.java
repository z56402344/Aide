package com.accessibility.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.gsm.SmsManager;
import android.text.TextUtils;

import com.accessibility.bean.CallLogBean;
import com.accessibility.bean.SmsBean;
import com.k12lib.afast.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import z.frame.BaseAct;

/**
 * Created by Du on 2019/10/16.
 */

public class PhoneUtils {

//    全部短信：content://sms/
//    收件箱：content://sms/inbox
//    发件箱：content://sms/sent
//    草稿箱：content://sms/draft
    public static Uri SMS_INBOX = Uri.parse("content://sms/inbox");

    public  ArrayList<CallLogBean> getCallLog(Activity act) {
        ArrayList<CallLogBean> infos = new ArrayList<>();
        ContentResolver cr = act.getBaseContext().getContentResolver();
        Uri uri = CallLog.Calls.CONTENT_URI;
        String[] projection = new String[] { CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.TYPE };
        if (ActivityCompat.checkSelfPermission(act.getBaseContext(), Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(act,
                    new String[]{Manifest.permission.READ_CALL_LOG}, 1000);

        }
        Cursor cursor = cr.query(uri, projection, null, null, CallLog.Calls.DATE + " DESC");
        int size = cursor.getCount()>20?20:cursor.getCount();
        while (cursor.moveToNext() && infos.size() < size) {
            CallLogBean callLogBena = new CallLogBean();
            callLogBena.date = cursor.getLong(1);
            callLogBena.number = cursor.getString(0);
            callLogBena.type = cursor.getInt(2);
            infos.add(callLogBena);
        }
        cursor.close();
        return infos;
    }

    public SmsBean getSmsFromPhone(Activity act) {
        ContentResolver cr = act.getContentResolver();
        String[] projection = new String[] { "body","address" };//"_id", "address", "person",, "date", "type
        String where = " date >  "+ (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)return null;
        if (cur.moveToFirst()) {
            SmsBean bean = new SmsBean();
            bean.number = cur.getString(cur.getColumnIndex("address"));//手机号
            bean.body = cur.getString(cur.getColumnIndex("body"));
            return bean;
        }else{
            return null;
        }
    }

    public void sendSMSS(BaseAct baseAct,String phone, String content) {
        if (!StringUtil.isEmpty(content) && !StringUtil.isEmpty(phone)) {
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> strings = manager.divideMessage(content);
            for (int i = 0; i < strings.size(); i++) {
                manager.sendTextMessage(phone, null, content, null, null);
            }
            baseAct.showShortToast("发送成功");
        } else {
            baseAct.showShortToast("手机号或内容不能为空");
        }
    }

    public static String getData(long time){
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String getCallType(int type){
        String typeString = "";
        switch (type){
            case CallLog.Calls.INCOMING_TYPE:
                typeString = "呼入";
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                typeString = "呼出";
                break;
            case CallLog.Calls.MISSED_TYPE:
                typeString = "未接";
                break;
            default:
                break;
        }
        return typeString;
    }

    public static int getSubString(String str){
        if (TextUtils.isEmpty(str))return -1;
        if (str.contains("##")){
            int start = str.indexOf("#");
            int end = str.lastIndexOf("#");
            try{
                String type = str.subSequence(start+2,end).toString();
               return Integer.parseInt(type);
            }catch (Exception e){
                e.printStackTrace();
                return -1;
            }
        }else{
            return -1;
        }
    }
}
