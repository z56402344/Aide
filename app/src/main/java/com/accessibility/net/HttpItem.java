package com.accessibility.net;

import com.k12lib.afast.log.Logger;

import java.io.File;

import z.db.ShareDB;
import z.http.ZHttpCenter;
import z.http.ZHttpItem;

// 封装http请求
public class HttpItem extends ZHttpItem implements IKey {
    public static final String TAG = "HttpItem";
	public static interface ILis {
    }

    public static interface IOKLis extends ILis {
//        boolean onHttpOK(String msg, HttpResp resp);
		boolean onHttpOK(String msg, HttpResp resp, String body);
    }

    public static interface IErrLis extends ILis {
        void onHttpError(int id, int errCode, String errMsg, Throwable e);
    }

    public static interface IFinishLis extends ILis {
        void onHttpFinish(int id, boolean bOK);
    }
	public static interface IOKErrLis extends IOKLis, IErrLis {
	}

    public static interface IAllLis extends IOKLis, IErrLis, IFinishLis {
    }

    private ILis mLis;

    // 设置自定义id
    public HttpItem setId(int id) {
	    super.setId(id);
        return this;
    }

    // 设置结果监听
    public HttpItem setListener(ILis lis) {
        mLis = lis;
        return this;
    }

    public HttpItem setUrl(String url) {
	    super.setUrl(url);
        return this;
    }

    public HttpItem put(String k, int v) {
	    getParams().put(k, v);
        return this;
    }

    public HttpItem put(String k, double v) {
	    getParams().put(k, v);
        return this;
    }

    public HttpItem put(String k, String v) {
	    getParams().put(k, v);
        return this;
    }

	public boolean put(String k, File v) {
		getParams().put(k, v, null);
		return true;
	}
	// 清空参数
	public void clearParams() {
		getParams().removeAll();
	}
	// 获取设定的参数
	public String getStringParams(String k) {
		return getParams().urlParams.get(k).value;
	}
	public int getIntParams(String k) {
		return Integer.valueOf(getParams().urlParams.get(k).value);
	}

	public String fullId() {
		return mLis+"["+getId()+"]";
//		return (mLis!=null?mLis.getClass().getName()+"@"+mLis.hashCode():"null@null")+"["+id+"]";
	}
	@Override
	public void onFinish(ZHttpItem hi, int status, String body, Exception e) {
		if (mbCancel) return;
		boolean bOK = false;
		if (status==200&&e==null) {
			// 成功
			Logger.w(TAG, fullId()+"onOkHttpSuccess:" + status + ", body=" + body);
			try {
				if (body!=null) {
					HttpResp resp = new HttpResp();
					resp.parse(body);
					resp.mParam = mParams;
					status = resp.getCode();
					ShareDB.Sec sec = new ShareDB.Sec("debug");
					boolean isOpenCode = sec.getBoolean("isOpenCode");
					if (isOpenCode){
						status = sec.getInt("errorcode");
						sec.put("isOpenCode",false);
						sec.save(true);
					}
					String msg = resp.getMsg();
//					if (status==AppCodeB) {
						bOK = onOK(msg, resp ,body);
//					} else {
//						if (status == E4000 || status == E4201 || status == E4101){
//							//E4201 == 6008需要重新登录
//							ShareDB.Sec secTime = new ShareDB.Sec(IUser.SEC_AUTOLOGIN);
//							int time = secTime.getInt("autoLoginTime", 0);
//							if ( time != 0){
//								secTime.put("autoLoginTime", 0);
//								secTime.save(false);
////								UserLogin.autoLogin();
//								LocalCenter.send(HomeFrag.IA_AutoLogin, 0, 0);
//							}
//						}
//						onOkHttpError(status, body, null);
//					}
				} else {
					onError(-1, FMTERR, null); // 包体格式错误
				}
			} catch (Exception ex) {
				onError(-1, FMTERR, ex); // 包体格式错误
				ex.printStackTrace();
			}
		} else {
			Logger.w(TAG, fullId()+"onFailure:" + status + ", body=" + body);
			onError(status, body == null ? "" : body, e);
			onFinish(false);
		}
		onFinish(bOK);
	}
	private boolean onOK(String msg, HttpResp resp){

		return onOK(msg,resp,null);
	}

	private boolean onOK(String msg, HttpResp resp , String body) {
//	    UmBuilder.reportHttp(resp.getCode(), resp.getMsg(), mUrl);
        if (mLis != null && (mLis instanceof IOKLis)) {
            IOKLis lis = (IOKLis) mLis;
            resp.id = mId;
            return lis.onHttpOK(msg, resp ,body);
        }
        return true;
    }

    // 错误分支
    // errCode>=300 http层的错误
    // 否则 数据/解析错误
    public void onError(int errCode, String errMsg, Throwable e) {
//	    UmBuilder.reportHttp(errCode, errMsg, mUrl);
//	    if (errCode>AppCodeB||errCode==-1) {
//		    StringBuilder sb = UmengErr.buildFull(null, mUrl, "errCode=" + (errCode - AppCodeB), "errMsg=" + errMsg);
//		    UmengErr.report(sb);
//	    }
        if (mLis != null && (mLis instanceof IErrLis)) {
            IErrLis lis = (IErrLis) mLis;
            lis.onHttpError(mId, errCode, errMsg, e);
        } else {
	        if (e!=null) e.printStackTrace();
        }
    }

    // 请求结束
    public void onFinish(boolean bOK) {
        if (mLis != null) {
            if (mLis instanceof IFinishLis) {
                IFinishLis lis = (IFinishLis) mLis;
                mLis = null;
                lis.onHttpFinish(mId, bOK);
            } else {
                mLis = null;
            }
        }
    }

    // 发送请求
    public void post(Object o) {
	    mDebug = true;
	    mUseGzip = true;
	    setKey(o);
		HttpIFCtx.instance().addPublicParams(getParams());
		setCtx(ZHttpCenter.getCtx());
	    Logger.w(TAG, fullId()+"request:" + mUrl +"?"+ getParams().toOrderString());
	    request();
    }


}
