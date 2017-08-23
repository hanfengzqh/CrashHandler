package com.zqh.crash.crashhandler.app;

import android.provider.Settings;
import android.text.TextUtils;

import com.zqh.crash.crashhandler.utils.CrashHandler;
import com.zqh.crash.crashhandler.utils.CrashUploadUtil;

import java.io.File;

/**
 * 全局异常处理类
 * @author zqh
 */
public class MyCrashHandler extends CrashHandler {

	private static MyCrashHandler instance = null;
	private String CRASH_FILE_PATH = "";
	public static final String DEVICE_CRASH_URL = "http://172.16.3.245:3521/api/carsh/job";
	public static final String DEVICE_CRASH_URL_KEY = "device_crash_url_key";
	private OutEventExec mOutEventExec;

	/**
	 * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
	 */
	private MyCrashHandler() {

	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static MyCrashHandler getInstance() {
		// 防止多线程访问安全，这里使用了双重锁
		if (instance == null) {
			synchronized (MyCrashHandler.class) {
				if (instance == null) {
					instance = new MyCrashHandler();
				}
			}
		}
		return instance;
	}

	/**
	 * 继承DRCrashHandler需要复写的方法 设置 1、程序崩溃信息的提示信息 2、记录崩溃信息的日志文件路径
	 */
	@Override
	public void initParams() {
		CRASH_FILE_PATH = mContext.getDir("stacktraces", 0)
				.getAbsolutePath()
				+ File.separator + "crash" + ".stacktrace";
		if (TextUtils.isEmpty(Settings.System.getString(
				mContext.getContentResolver(), DEVICE_CRASH_URL_KEY))) {
			Settings.System.putString(mContext.getContentResolver(),
					DEVICE_CRASH_URL_KEY, DEVICE_CRASH_URL);
		}

		setDRCrashFilePath(CRASH_FILE_PATH);
	}

	/**
	 * 继承DRCrashHandler需要复写的方法 自己实现上传至服务器的代码即可
	 */
	@Override
	public void sendToServer(File crashFile) {
		String server_url = Settings.System.getString(
				mContext.getContentResolver(), DEVICE_CRASH_URL_KEY);
		if (TextUtils.isEmpty(server_url)) {
			server_url = DEVICE_CRASH_URL;
		}
		CrashUploadUtil.upLoadHttp(crashFile,mContext,server_url);
	}

	@Override
	public void outEventDealWith() {
		if (mOutEventExec != null) {
			mOutEventExec.outEventExec();
		}
	}

	public interface OutEventExec {
		public void outEventExec();
	}

	public void setOutEventExec(OutEventExec mOutEventExec) {
		this.mOutEventExec = mOutEventExec;
	}
}
