package com.zqh.crash.crashhandler.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class HttpClientUtil {

	// post请求
	public static final String HTTP_POST = "POST";
	// get请求
	public static final String HTTP_GET = "GET";
	// utf-8字符编码
	public static final String CHARSET_UTF_8 = "utf-8";
	// HTTP内容类型。如果未指定ContentType，默认为TEXT/HTML
	public static final String CONTENT_TYPE_TEXT_HTML = "text/xml";
	// HTTP内容类型。相当于form表单的形式，提交
	public static final String CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded";
	// 请求超时时间
	public static final int SEND_REQUEST_TIME_OUT = 10000;
	// 将读超时时间
	public static final int READ_TIME_OUT = 10000;

	/**
	 * 
	 * @param strUrlPath
	 *            请求地址
	 * @param params
	 *            params请求体内容
	 * @param encode
	 *            编码
	 * @return 返回内容
	 */
	public static String submitPostData(String strUrlPath,
			Map<String, String> params, String encode) {

		// 是否有http正文提交
		boolean isDoInput = false;
		String body = getRequestData(params, CHARSET_UTF_8);// 获取请求体
		if (body != null && body.length() > 0)
			isDoInput = true;
		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;
		try {
			// 统一资源
			URL url = new URL(strUrlPath);
			// 连接类的父类，抽象类
			URLConnection urlConnection = url.openConnection();
			// http的连接类
			HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true, 默认情况下是false;
			if (isDoInput) {
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestProperty("Content-Length",
						String.valueOf(body.length()));
			}
			// 设置是否从httpUrlConnection读入，默认情况下是true;
			httpURLConnection.setDoInput(true);
			// 设置一个指定的超时值（以毫秒为单位）
			httpURLConnection.setConnectTimeout(SEND_REQUEST_TIME_OUT);
			// 将读超时设置为指定的超时，以毫秒为单位。
			httpURLConnection.setReadTimeout(READ_TIME_OUT);
			// Post 请求不能使用缓存
			httpURLConnection.setUseCaches(false);
			// 设置字符编码
			httpURLConnection.setRequestProperty("Accept-Charset",
					CHARSET_UTF_8);
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			// 设置内容类型
			httpURLConnection.setRequestProperty("Content-Type",
					CONTENT_TYPE_FORM_URL);
			// 设定请求的方法，默认是GET
			httpURLConnection.setRequestMethod(HTTP_POST);

			// 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
			// 如果在已打开连接（此时 connected 字段的值为 true）的情况下调用 connect 方法，则忽略该调用。
			httpURLConnection.connect();

			if (isDoInput) {
				outputStream = httpURLConnection.getOutputStream();
				outputStreamWriter = new OutputStreamWriter(outputStream);
				outputStreamWriter.write(body);
				outputStreamWriter.flush();// 刷新
			}
			int responseCode = httpURLConnection.getResponseCode();
			
			if (responseCode >= 300) {
				Log.e("zqh","HTTP Request Fail, Response code is " + responseCode);
				throw new Exception(
						"HTTP Request Fail, Response code is " + responseCode);
			}

			if (responseCode == HttpURLConnection.HTTP_OK) {
				inputStream = httpURLConnection.getInputStream();
				inputStreamReader = new InputStreamReader(inputStream);
				reader = new BufferedReader(inputStreamReader);

				while ((tempLine = reader.readLine()) != null) {
					resultBuffer.append(tempLine);
					resultBuffer.append("\n");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("zqh","HTTP Request is not success, error is " + e.getMessage());
		} finally {// 关闭流

			try {
				if (outputStreamWriter != null) {
					outputStreamWriter.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultBuffer.toString();
	}

	public static String getRequestData(Map<String, String> params,
			String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		if (params != null) {
			try {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					stringBuffer
							.append(entry.getKey())
							.append("=")
							.append(URLEncoder.encode(entry.getValue(), encode))
							.append("&");
				}
				stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * 将map集合的键值对转化成：key1=value1&key2=value2 的形式
	 * 
	 * @param parameterMap
	 *            需要转化的键值对集合
	 * @return 字符串
	 */
	public static String convertStringParamter(@SuppressWarnings("rawtypes") Map parameterMap) {
		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}
				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}
		return parameterBuffer.toString();
	}
}