package com.zqh.crash.crashhandler.utils;

import android.content.Context;
import android.provider.Settings;

import java.util.UUID;

public class CommonUtils {

	/**
	 * 获取唯一ID信息标识
	 * 
	 * @return
	 */
	public static String getUUID() {
		String[] str = UUID.randomUUID().toString().split("-");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			sb.append(str[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 获取String from settings数据库
	 * 
	 * @param mContext
	 *            上下文
	 * @param key
	 *            存储key
	 * */
	public static String getStringFromSettings(Context mContext, String key) {
		return Settings.System.getString(mContext.getContentResolver(), key);
	}
	
	
	
}
