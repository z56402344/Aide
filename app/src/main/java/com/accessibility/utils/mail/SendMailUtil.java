package com.accessibility.utils.mail;

import android.os.Looper;

import com.accessibility.MainActivity;

import java.io.File;

import z.frame.BaseAct;


//发送邮件的util
public class SendMailUtil {

    //不知道邮箱的端口号可以看这篇文章  https://blog.csdn.net/zr_wb/article/details/52413674
    //qq
//    private static final String SMTP_HOST = "smtp.qq.com";
//    private static final String SMTP_PORT = "25";
//    private static final String FROM_ADDRESS = "***@qq.com";
//    private static final String SMTP_KEY = "****";

//    //163
    private static final String SMTP_HOST = "smtp.163.com";
    private static final String SMTP_PORT = "465"; //或者465  994
    private static final String FROM_ADDRESS = "***@163.com";//账号
    private static final String SMTP_KEY = "****";//密码
    private static final String TO_ADD = "codewxkf@163.com";


    public static void send(final File file,String toAdd){
        final MailInfo mailInfo = creatMail(file.getName(),toAdd);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo,file);
            }
        }).start();
    }


    public static void send(final BaseAct act, String title, String content){
        final MailInfo mailInfo = creatMail(title,content);
        final MailSender sms = new MailSender();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isSend = sms.sendTextMail(mailInfo);
                Looper.prepare();
                act.handleAction(MainActivity.IA_SEND_SMS,isSend?0:-1,null);
                Looper.loop();
            }
        }).start();
    }

    private static MailInfo creatMail(String title,String content) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(SMTP_HOST);
        mailInfo.setMailServerPort(SMTP_PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADDRESS); // 你的邮箱地址
        mailInfo.setPassword(SMTP_KEY);// 你的邮箱密码
        mailInfo.setFromAddress(FROM_ADDRESS); // 发送的邮箱
        mailInfo.setToAddress(TO_ADD); // 发到哪个邮件去
        mailInfo.setSubject(title+"的通话记录"); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        return mailInfo;
    }

}
