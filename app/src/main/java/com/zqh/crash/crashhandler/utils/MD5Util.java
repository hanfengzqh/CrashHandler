package com.zqh.crash.crashhandler.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	String TAG = this.getClass().getSimpleName();

	// /** * 16进制字符集 */
	// private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
	// '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/** * 16进制字符集 */
	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/** * 指定算法为MD5的MessageDigest */
	private static MessageDigest messageDigest = null;

	/** * 初始化messageDigest的加密算法为MD5 */
	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * * 获取文件的MD5值
	 * 
	 * @param file
	 *            目标文件
	 * 
	 * @return MD5字符串
	 */
	public static String getFileMD5String(File file) {
		String ret = "";
		FileInputStream in = null;
		FileChannel ch = null;
		try {
			in = new FileInputStream(file);
			ch = in.getChannel();
			ByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
					file.length());
			messageDigest.update(byteBuffer);
			ret = bytesToHex(messageDigest.digest());
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (ch != null) {
				try {
					ch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret.toLowerCase();// 转小写之后
	}

	/**
	 * * 获取文件的MD5值
	 * 
	 * @param fileName
	 *            目标文件的完整名称
	 * 
	 * @return MD5字符串
	 */
	public static String getFileMD5String(String fileName) {
		return getFileMD5String(new File(fileName));
	}

	/**
	 * * MD5加密字符串
	 * 
	 * @param str
	 *            目标字符串
	 * 
	 * @return MD5加密后的字符串
	 */

	public static String getMD5String(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		return getMD5String(str.getBytes());
	}

	/**
	 * * MD5加密字符串 16位大写加密
	 * 
	 * @param str
	 *            目标字符串
	 * 
	 * @return MD5加密后的字符串
	 */

	public static String getMD5SixString(String str) {
		String md5SixString = "";
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		String md5String = getMD5String(str.getBytes());
		if (!TextUtils.isEmpty(md5String) && md5String.length() >= 24) {
			md5SixString = md5String.substring(8, 24).toUpperCase();
		}

		return md5SixString;
	}

	public static String getMD5SixString2(String str) {
		String md5SixString = "";
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		String md5String = getMD5String(str.getBytes());
//		if (!TextUtils.isEmpty(md5String) && md5String.length() >= 24) {
//			md5SixString = md5String.substring(8, 24).toUpperCase();
//		}

		return md5String;
	}
	
	/**
	 * * MD5加密以byte数组表示的字符串
	 * 
	 * @param bytes
	 *            目标byte数组
	 * 
	 * @return MD5加密后的字符串
	 */

	public static String getMD5String(byte[] bytes) {
		messageDigest.update(bytes);
		return bytesToHex(messageDigest.digest());
	}

	/**
	 * * 校验密码与其MD5是否一致
	 * 
	 * @param pwd
	 *            密码字符串
	 * 
	 * @param md5
	 *            基准MD5值
	 * 
	 * @return 检验结果
	 */
	public static boolean checkPassword(String pwd, String md5) {
		return getMD5String(pwd).equalsIgnoreCase(md5);
	}

	/**
	 * * 校验密码与其MD5是否一致
	 * 
	 * @param pwd
	 *            以字符数组表示的密码
	 * 
	 * @param md5
	 *            基准MD5值
	 * 
	 * @return 检验结果
	 */
	public static boolean checkPassword(char[] pwd, String md5) {
		return checkPassword(new String(pwd), md5);

	}

	/**
	 * * 检验文件的MD5值
	 * 
	 * @param file
	 *            目标文件
	 * 
	 * @param md5
	 *            基准MD5值
	 * 
	 * @return 检验结果
	 */
	public static boolean checkFileMD5(File file, String md5) {
		return getFileMD5String(file).equalsIgnoreCase(md5);

	}

	/**
	 * * 检验文件的MD5值
	 * 
	 * @param fileName
	 *            目标文件的完整名称
	 * 
	 * @param md5
	 *            基准MD5值
	 * 
	 * @return 检验结果
	 */
	public static boolean checkFileMD5(String fileName, String md5) {
		return checkFileMD5(new File(fileName), md5);

	}

	/**
	 * * 将字节数组转换成16进制字符串
	 * 
	 * @param bytes
	 *            目标字节数组
	 * 
	 * @return 转换结果
	 */
	public static String bytesToHex(byte bytes[]) {
		return bytesToHex(bytes, 0, bytes.length);

	}

	/**
	 * * 将字节数组中指定区间的子数组转换成16进制字符串
	 * 
	 * @param bytes
	 *            目标字节数组
	 * 
	 * @param start
	 *            起始位置（包括该位置）
	 * 
	 * @param end
	 *            结束位置（不包括该位置）
	 * 
	 * @return 转换结果
	 */
	public static String bytesToHex(byte bytes[], int start, int end) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < start + end; i++) {
			sb.append(byteToHex(bytes[i]));
		}
		return sb.toString();

	}

	/**
	 * * 将单个字节码转换成16进制字符串
	 * 
	 * @param bt
	 *            目标字节
	 * 
	 * @return 转换结果
	 */
	public static String byteToHex(byte bt) {
		return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];

	}

}