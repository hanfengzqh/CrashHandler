package com.zqh.crash.crashhandler.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreUtil {
	private static final String CONFIG = "crash_config";
	
	private static final String KEY = "key";
	

	// 存储boolean类型数据
	public static void putBoolean(Context context, String key, boolean value) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		preferences.edit().putBoolean(key, value).commit();
	}

	// 获取boolean数据
	public static boolean getBoolean(Context context, String key,boolean defvalue) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		return preferences.getBoolean(key, defvalue);
	}
	
	// 存储String类型数据
	public static void putString(Context context, String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		preferences.edit().putString(key, value).commit();
	}

	//获取String类型数
	public static String getString(Context context, String key, String defvalue) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		return preferences.getString(key, defvalue);
	}

	// 存储int类型数据
	public static void putInteger(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		preferences.edit().putInt(key, value).commit();
	}

	// 获取int类型
	public static int getInteger(Context context, String key, int defvalue) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		return preferences.getInt(key, defvalue);
	}
	
	// 存储int类型数据
	public static void putLong(Context context, String key, long value) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		preferences.edit().putLong(key, value).commit();
	}

	// 获取int类型
	public static long getLong(Context context, String key, int defvalue) {
		SharedPreferences preferences = context.getSharedPreferences(CONFIG, 0);
		return preferences.getLong(key, defvalue);
	}
	
	/**
	 * 存储密码键盘
	 * */
	// 存储int类型数据
	public static void putMapInteger(Context context, String key, int value) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, 0);
		preferences.edit().putInt(key, value).commit();
	}

	// 获取int类型
	public static int getMapInteger(Context context, String key, int defvalue) {
		SharedPreferences preferences = context.getSharedPreferences(KEY, 0);
		return preferences.getInt(key, defvalue);
	}
}
