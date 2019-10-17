package com.accessibility.net;

public interface IKey {
	// _timestamp
	String kTimestamp = "_timestamp";

	int AppCodeB = 0; // 应用层正确code
    String CODE = "errorcode";
    String MSG = "errormessage";
    String RES = "result";

	String FMTERR = "服务器返回错误";
	String UIDERR = "服务器ID为空,请重新登录";

	int E4000 = 4000;//Token 失效，需要重新登录
	int E4201 = 4201;//Token 错误或过期，需要重新登录
	int E4101 = 4101;//Token 错误或过期，需要重新登录


}
