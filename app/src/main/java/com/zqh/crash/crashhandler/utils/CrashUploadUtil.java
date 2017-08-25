package com.zqh.crash.crashhandler.utils;


import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.zqh.crash.crashhandler.app.MyApplication;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrashUploadUtil {

	public static final String MULTIPART_FORM_DATA = "multipart/form-data"; // 指明要上传的文件格式

	/**
	 * 上传文件
	 * @param crashFile
	 */
	public static void upLoadFile(final File crashFile,String server_url){
		String uuid = CommonUtils.getUUID();
		// 设备sn 122006000075
		String dsnCode = android.os.Build.SERIAL;
		// 发行商id 00100017
		String vidCode = CommonUtils.getStringFromSettings(
				MyApplication.getInstance(), "publisher_id");

		final okhttp3.OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
		OkHttpClient okHttpClient = httpBuilder
				.connectTimeout(100, TimeUnit.SECONDS) // 设置请求超时时间
				.writeTimeout(150, TimeUnit.SECONDS).build();

		// 根据文件格式封装文件
		RequestBody requestFile = RequestBody.create(
				MediaType.parse(MULTIPART_FORM_DATA), crashFile);

		// 初始化请求体对象，设置Content-Type以及文件数据流
		RequestBody requestBody = new MultipartBody.Builder()
				// 建立请求的内容
				.setType(MultipartBody.FORM)
				// multipart/form-data
				.addFormDataPart("uuid", uuid)
				.addFormDataPart("dSn", dsnCode)// 设备SN
				.addFormDataPart("uploadTime", SystemClock.currentThreadTimeMillis()+"")// 发行商sn
				.addFormDataPart("vId", vidCode)// 发行商id
				// 第一个参数是服务器接收的名称，第二个是上传文件的名字，第三个是上传的文件
				.addFormDataPart("logfile", crashFile.getName(), requestFile)
				.build();

		// 封装OkHttp请求对象，初始化请求参数
		Request request = new Request.Builder().url(server_url) // 上传url地址
				.post(requestBody) // post请求体
				.build();

		// 发起异步网络请求
		okHttpClient.newCall(request).enqueue(new Callback() {

			@Override
			public void onResponse(Call call, okhttp3.Response response)
					throws IOException {
				Log.d("zqh", "response = " + response.body().string());
				deleteFile(crashFile);
			}

			@Override
			public void onFailure(Call call, IOException e) {
				Log.e("zqh", "error = " + e.toString());
				crashFile.delete();
			}
		});

	}


	/**
	 * 上传文件内容
	 * @param crashFileLog
	 *            crash文件内容
	 */
	public static void upLoadHttp(final File crashFileLog, Context mContext,String server_url) {

		String uuid = CommonUtils.getUUID();
		// 设备sn 122006000075
		String dsnCode = android.os.Build.SERIAL;
		// 发行商id 00100017
		String vidCode = CommonUtils.getStringFromSettings(
				mContext, "publisher_id");

		StringBuilder readFile = FileUtil.readFile(crashFileLog, "UTF-8");
		
		Log.e("zqh", "str2HexStr = "+HexUtils.str2HexStr(readFile.toString()));
		String md5SixString = MD5Util.getMD5SixString2(readFile.toString());
		Log.e("zqh", "md5SixString = "+md5SixString);

		String crashFilelog = getParamsCrashFileLog(uuid, dsnCode, vidCode,
				readFile);
		Log.d("zqh", "crashFileLog = " + crashFilelog);

		// step 1: 同样的需要创建一个OkHttpClick对象
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS).build();

		 
		// step 2: 创建 FormBody.Builder
		FormBody formBody = new FormBody.Builder()
				.add("uuid", uuid)
				.add("dSn", dsnCode)
				.add("uploadTime", SystemClock.currentThreadTimeMillis()+"")// 发行商sn
				.add("vId", vidCode)// 发行商id
				.add("log", crashFilelog)
				.add("md5lg", md5SixString)
				.build();

		// step 3: 创建请求
		Request request = new Request.Builder()
				.url(server_url)
				.post(formBody)
				.build();

		// step 4： 建立联系 创建Call对象
		okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.e("zqh", "error = " + e.toString());
				crashFileLog.delete();
			}

			@Override
			public void onResponse(Call call, Response response)
					throws IOException {
				Log.d("zqh", "response = " + response.body().string());
				deleteFile(crashFileLog);
			}
		});
	}

	public static void uploadCommonHttp(final File crashFile, Context mContext,String serverUrl){
		HashMap<String, String> hashMap = new HashMap<String, String>();

		String uuid = CommonUtils.getUUID();
		// 设备sn 122006000075
		String dsnCode = android.os.Build.SERIAL;
		// 发行商id 00100017
		String vidCode = CommonUtils.getStringFromSettings(mContext,
				"publisher_id");

		StringBuilder readFile = FileUtil.readFile(crashFile, "UTF-8");
		//读取完毕就删除文件
		deleteFile(crashFile);

		// Log.e("zqh",
		// "str2HexStr = "+HexUtils.str2HexStr(readFile.toString()));
		String md5SixString = MD5Util.getMD5SixString2(readFile.toString());
		// Log.e("zqh", "md5SixString = "+md5SixString);
		String crashFileLog = getParamsCrashFileLog(uuid, dsnCode, vidCode,
				readFile);
		// Log.d("zqh", "crashFileLog = " + crashFileLog);

		hashMap.put("uuid", uuid);
		hashMap.put("dSn", dsnCode);
		hashMap.put("uploadTime", System.currentTimeMillis() + "");
		hashMap.put("vId", vidCode);
		hashMap.put("log", crashFileLog);
		hashMap.put("md5lg", md5SixString);

		String data = HttpClientUtil.submitPostData(serverUrl, hashMap, "UTF-8");

		Log.d("zqh", "result = "+data);
	}



	/** 参数组织 */
	private static String getParamsCrashFileLog(String uuid, String dsnCode,
			String vidCode, StringBuilder readFile) {
		String oneData = dsnCode + vidCode;
		Log.d("zqh", "oneData = " + oneData);
		String oneDataMD5 = MD5Util.getMD5String(oneData);
		Log.d("zqh", "oneDataMD5 = " + oneDataMD5);
		Log.d("zqh", "uuid = " + uuid);
		String secrurity = xor(oneDataMD5, uuid);
		Log.e("zqh", "secrurity = "+secrurity);
		byte[] hex2Bytes1 = hex2Bytes1(secrurity);
		
		return DES3Utils.get3DESData(hex2Bytes1, readFile.toString());
	}

	protected static void deleteFile(File file) {
		// 上传成功则删除压缩
		file.delete();
	}

	/**
	 * hex字符串转byte数组<br/>
	 * 2个hex转为一个byte
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] hex2Bytes1(String src) {
		byte[] res = new byte[src.length() / 2];
		char[] chs = src.toCharArray();
		int[] b = new int[2];

		for (int i = 0, c = 0; i < chs.length; i += 2, c++) {
			for (int j = 0; j < 2; j++) {
				if (chs[i + j] >= '0' && chs[i + j] <= '9') {
					b[j] = (chs[i + j] - '0');
				} else if (chs[i + j] >= 'A' && chs[i + j] <= 'F') {
					b[j] = (chs[i + j] - 'A' + 10);
				} else if (chs[i + j] >= 'a' && chs[i + j] <= 'f') {
					b[j] = (chs[i + j] - 'a' + 10);
				}
			}

			b[0] = (b[0] & 0x0f) << 4;
			b[1] = (b[1] & 0x0f);
			res[c] = (byte) (b[0] | b[1]);
		}

		return res;
	}

	/***
	 * 两个字符串进行异或处理
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String xor(String str1, String str2) {
		StringBuffer sb = new StringBuffer();
		int len1 = str1.length(), len2 = str2.length();
		int i = 0, index = 0;
		if (len2 > len1) {
			index = len2 - len1;
			while (i++ < len2 - len1) {
				sb.append(str2.charAt(i - 1));
				str1 = "0" + str1;
			}
		} else if (len1 > len2) {
			index = len1 - len2;
			while (i++ < len1 - len2) {
				sb.append(str1.charAt(i - 1));
				str2 = "0" + str2;
			}
		}
		int len = str1.length();
		while (index < len) {
			int j = (Integer.parseInt(str1.charAt(index) + "", 16) ^ Integer
					.parseInt(str2.charAt(index) + "", 16)) & 0xf;
			sb.append(Integer.toHexString(j));
			index++;
		}
		return sb.toString();
	}
}
