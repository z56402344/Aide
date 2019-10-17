package com.accessibility.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import z.frame.JsonTree;
import z.http.ZHttpParams;

// http回复的json包体
public class HttpResp extends JsonTree implements IKey {
    public int id; // 用户自定义id
	public ZHttpParams mParam;

    // 解析结果字符串
    public void parse(String body) {
        setData(JSON.parseObject(body));
    }

    // 错误码
    public int getCode() {
        return getInt(-1, CODE);
    }

    // 错误/提示信息
    public String getMsg() {
        String msg = getString(MSG);
        return msg != null ? msg : "";
    }

    public String getResp() {
        String res = getString(RES);
        return res != null ? res : "";
    }

	// 错误信息
	public static String filter(int code, String msg, String def) {
		return code==AppCodeB&&!TextUtils.isEmpty(msg)?msg:def;
	}
}
