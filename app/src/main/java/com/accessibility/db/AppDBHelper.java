package com.accessibility.db;

import com.accessibility.db.table.SyncTable;

import java.util.ArrayList;

import z.db.BaseDBHelper;
import z.db.ShareDB;
import z.frame.ICommon;


/** 说明： 数据库的帮助类 */
public class AppDBHelper extends BaseDBHelper implements ICommon {

	public static final int VerUser = 4;//用户库版本
	public static final int VerGlobal = 1;//公共库版本

	@Override
	protected int getVer(boolean isGlobal) {
		return isGlobal?VerGlobal:VerUser;
	}
	@Override
	protected String getUserId() {
		return "uid-null";
//		return IUser.Dao.getUserId();
	}
	@Override
	protected void addTables(ArrayList<ILTable> tl, boolean isGlobal) {
		if (isGlobal) {
			tl.add(new ShareDB.Table()); // 全局配置信息表
		} else {
			tl.add(new SyncTable());
//			tl.add(new DicDB.Table()); // 单词表
		}
	}
}
