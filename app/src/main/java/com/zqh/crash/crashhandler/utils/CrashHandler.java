package com.zqh.crash.crashhandler.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * 全局异常处理类
 *
 */
public abstract class CrashHandler implements UncaughtExceptionHandler {

	/** 系统默认的UncaughtException处理类 **/
	private UncaughtExceptionHandler mDefaultHandler;

	/** 程序context **/
	protected Context mContext;

	/** 存储设备信息和异常信息 **/
	private Map<String, String> mInfos = new HashMap<String, String>();

	/** 设置crash文件位置 **/
	private String mDRCrashFilePath;

	/** 生成的crash文件 **/
	private File crashFile;

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		// 1、上下文
		mContext = context;
		// 2、获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 3、初始化参数
		initParams();
		// 4、设置当前CrashHandler为默认处理异常类
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 3.1 初始化参数 <br/>
	 * 如果想使用自己的CrashHandler，则复写initParams()方，然后设置参数<br/>
	 */
	public abstract void initParams();

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (mDefaultHandler != null && !handlerException(ex)) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			outEventDealWith();
			// 程序休眠3s后退出
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			ActivityManager activityM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
			activityM.killBackgroundProcesses(mContext.getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	}

	/**
	 * 5、处理异常<br>
	 * <br>
	 * 
	 * 5.1 收集设备参数信息<br>
	 * 5.2 弹出窗口提示信息<br>
	 * 5.3 保存log和crash到文件<br>
	 * 5.4 发送log和crash到服务器<br>
	 * 
	 * @param ex
	 * @return 是否处理了异常
	 */
	private long crashTime ;
	protected boolean handlerException(Throwable ex) {

		if (ex == null) {
			return false;
		} else {
			long time = System.currentTimeMillis();
			crashTime = SharePreUtil.getLong(mContext, "CRASHTIME", 0);
			Log.d("zqh", "time-crashTime = "+(time-crashTime));
			if (time-crashTime>4000) {
				// 5.1 收集设备参数信息
				collectDeviceInfo(mContext);
				// 5.3 保存log和crash到文件
				saveLogAndCrash(ex);
				// 5.4 发送log和crash到服务器
				sendLogAndCrash();
			}
			crashTime = System.currentTimeMillis();
			SharePreUtil.putLong(mContext, "CRASHTIME", crashTime);
			return true;
		}
	}

	/**
	 * 5.1 收集设备信息
	 * 
	 * @param ctx
	 */
	protected void collectDeviceInfo(Context ctx) {

		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				String packName = pi.packageName;
				mInfos.put("versionName", versionName);
				mInfos.put("versionCode", versionCode);
				mInfos.put("packageName", packName);
				mInfos.put("sdkVersion", Build.VERSION.RELEASE);
			}
		} catch (NameNotFoundException e) {
			Log.e("zqh", "An error occured when collect package info, Error: "
					+ e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				mInfos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				Log.e("zqh",
						"An error occured when collect crash info, Error: " + e);
			}
		}
	}

	/**
	 * 5.3 保存log和crash到文件
	 * 
	 * @param ex
	 */
	protected void saveLogAndCrash(Throwable ex) {

		StringBuffer sb = new StringBuffer();

		sb.append("[DateTime: " + DateUtil.date2String(new Date()) + "]\n");
		sb.append("[DeviceInfo: ]\n");

		// 遍历infos
		for (Map.Entry<String, String> entry : mInfos.entrySet()) {
			String key = entry.getKey().toLowerCase(Locale.getDefault());
			String value = entry.getValue();
			if (!TextUtils.isEmpty(key)) {
				if (key.equals("fingerprint") 
						|| key.equals("versionname")
						|| key.equals("versioncode")
						|| key.equals("packagename")
						|| key.equals("display")
						|| key.equals("serial")
						|| key.equals("sdkversion")) {
					sb.append("" + key + ": " + value + "\n");
				}
			}
		}
		// 将错误手机到writer中
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		ex.printStackTrace(pw);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(pw);
			cause = cause.getCause();
		}
		pw.close();
		String result = writer.toString();
		sb.append("[Excetpion: ]\n");
		sb.append(result);
		// 5.3.1 记录异常到特定文件中
		saveToCrashFile(sb.toString());
	}

	/**
	 * 5.3.1写入文本
	 * 
	 * @param crashText
	 */
	protected void saveToCrashFile(String crashText) {
		Log.d("zqh", "CrashHandler is writing crash-info to CrashFile("
				+ this.mDRCrashFilePath + ")! ");

		crashFile = new File(mDRCrashFilePath);
		// 创建文件
		FileUtil.createFileAndFolder(crashFile);
		// 追加文本
		FileUtil.appendToFile(crashFile, crashText);
	}

	/**
	 * 5.4 发送log和crash到服务器
	 */
	protected void sendLogAndCrash() {
		crashFile = new File(getDRCrashFilePath());
		// 5.4.1
		sendToServer(crashFile);
	}

	/**
	 * 5.4.1 将错误报告发送到服务器
	 * 
	 * @param crashFile
	 */
	protected abstract void sendToServer(File crashFile);
	/***
	 * 5.4.2 崩溃之后需要处理的事件-外部调用
	 */
	public abstract void outEventDealWith();

	public String getDRCrashFilePath() {
		return mDRCrashFilePath;
	}

	/**
	 * 设置记录崩溃信息的文件位置
	 * 
	 * @param mDRCrashFilePath
	 */
	public void setDRCrashFilePath(String mDRCrashFilePath) {
		this.mDRCrashFilePath = mDRCrashFilePath;
	}

}
