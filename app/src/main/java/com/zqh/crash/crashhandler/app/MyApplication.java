package com.zqh.crash.crashhandler.app;

import android.app.Application;
import android.os.Build;
import android.util.Log;


public class MyApplication extends Application implements MyCrashHandler.OutEventExec {
	private static MyApplication instance;

	@Override
	public void onCreate() {
		super.onCreate();
		this.instance = this;
		Log.e("zqh","DRApplication: initCrashHandler()");
		MyCrashHandler crashHandler = MyCrashHandler.getInstance();
		crashHandler.init(this);
		crashHandler.setOutEventExec(this);
	}
	
	/**
	 * 手机的版本 比如5.1.1
	 */
	public String getOsVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * 手机名称 比如"devicemodel": "AOSP on HammerHead",
	 * 
	 * @return
	 */
	public String getDevice() {
		return Build.MODEL;
	}

	public static MyApplication getInstance() {
		return instance;
	}
	@Override
	public void outEventExec() {
		Log.d("zqh", "outEventExec 执行 ");
	}
}
